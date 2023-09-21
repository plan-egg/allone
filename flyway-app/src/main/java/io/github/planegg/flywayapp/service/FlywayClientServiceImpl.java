package io.github.planegg.flywayapp.service;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.planegg.flywayapp.util.FileUtil;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.flywaydb.core.api.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.Charset;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlywayClientServiceImpl implements IFlywayClientService {

    private Logger logger = LoggerFactory.getLogger(FlywayClientServiceImpl.class);

    private String activeProfile = System.getProperty("activeProfile","uat");

    protected Flyway flyway;

    protected List<JSONObject> configList;


    protected Map<String, Runnable> actionMap = new HashMap<>(5);

    /**
     * 匹配配置中${xxx.xxx}的变量
     * 用于从运行时通过参数传入
     */
    private final static Pattern REPLACE_HOLDER_PATTERN = Pattern.compile("\\$\\{([a-zA-Z]\\.?)+}");

    @Override
    public void initFlywayClient(){

        actionMap.put("info",this::info);
        actionMap.put("migrate",this::migrate);
        actionMap.put("repair",this::repair);

        configList = getConfigList();
        Configuration configure = getConfig(configList);
        flyway = new Flyway(configure);
    }

    @Override
    public void execute(){
        String cmdList = (String)getProperty(configList,"cmdList");
        if (cmdList == null){
            logger.info("没有命令需要执行！");
            return;
        }
        chkForAction();
        for (String cmd : cmdList.split(",")) {
            Runnable action = actionMap.get(cmd);
            if (action == null){
                throw new RuntimeException("命令不正确，请检查！目前仅支持：info,repair,migrate");
            }
            action.run();
        }
    }

    @Override
    public Configuration getConfig(List<JSONObject> configList){

        ClassicConfiguration configure = new ClassicConfiguration();
/*        if (getProperty(configList,"driver") != null){
            configure.((String)getProperty(configList,"driver"));
        }*/
        configure.setDataSource((String)getProperty(configList,"url"), (String)getProperty(configList,"user")
                , (String)getProperty(configList,"password"));


        if (getProperty(configList,"schemas") != null){
            configure.setSchemas(getProperty(configList,"schemas").toString().split(","));
        }
        if (getProperty(configList,"table") != null){
            configure.setTable((String)getProperty(configList,"table"));
        }
        if (getProperty(configList,"locationRelatePaths") != null){
            String locationrelatePaths = (String) getProperty(configList,"locationRelatePaths");
            String[] locationrelatePathArr = locationrelatePaths.split(",");
            String locationRootPath = (String) getProperty(configList,"locationRootPath");
            if (locationRootPath == null){
                locationRootPath = ".";
            }
            for (int i = 0; i < locationrelatePathArr.length; i++) {
                locationrelatePathArr[i] = locationRootPath + File.separator +locationrelatePathArr[i];
            }
            configure.setLocationsAsStrings(locationrelatePathArr);
        }
        if (getProperty(configList,"outOfOrder") != null){
            configure.setOutOfOrder((Boolean) getProperty(configList,"outOfOrder") );
        }
        if (getProperty(configList,"baselineVersion") != null){
            configure.setBaselineVersionAsString((String)getProperty(configList,"baselineVersion") );
        }
        if (getProperty(configList,"cleanDisabled") != null){
            configure.setCleanDisabled((Boolean) getProperty(configList,"cleanDisabled") );
        }
        if (getProperty(configList,"encoding") != null){
            configure.setEncoding(Charset.forName((String)getProperty(configList,"encoding")));
        }
       return configure;
    }

    @Override
    public JSONObject readFromFile(File configFile){
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        //将yaml文件的内容转换为Java对象
        try {
            logger.info("正在读取配置文件："+configFile.getAbsolutePath());
            JSONObject flywayConfig = mapper.readValue(configFile, JSONObject.class);
            return flywayConfig;
        } catch (Exception e) {
            logger.error("读取配置文件失败！",e);
            throw new RuntimeException("读取配置文件失败！",e);
        }

    }

    @Override
    public List<JSONObject> getConfigList (){
        List<JSONObject> flywayConfigList = new LinkedList<>();

        //读取flyway-config.yaml文件
        String configFileName = "flyway-config%s%s.yml";

//        String configFileDir = FlywayClientServiceImpl.class.getClassLoader().getResource("").getPath();

        File activeConfigFile = getConfigPath(String.format(configFileName,"-",activeProfile));
        File defaultConfigFile = getConfigPath(String.format(configFileName,"",""));


        if (activeConfigFile.exists()){
            JSONObject profileFlywayConfig = readFromFile(activeConfigFile);
            flywayConfigList.add(profileFlywayConfig);
        }
        if (defaultConfigFile.exists()){
            JSONObject defaultFlywayConfig = readFromFile(defaultConfigFile);
            flywayConfigList.add(defaultFlywayConfig);
        }
        if (flywayConfigList.isEmpty()){
            throw new RuntimeException("没有读取到任何配置！");
        }
        return flywayConfigList;
    }

    /**
     *
     * @param jsonObjectList
     * @param name
     * @return
     */
    protected Object getProperty(List<JSONObject> jsonObjectList , String name){
        for (JSONObject jsonObject : jsonObjectList) {
            Object val = jsonObject.get(name);
            if (val != null){
                if (val instanceof String && ((String) val).contains("${")){
                    String config = (String)val;
                    String originConfig = (String)val;
                    Matcher matcher = REPLACE_HOLDER_PATTERN.matcher(originConfig);
                    while (matcher.find()) {
                        String propName = originConfig.substring(matcher.start()+2,matcher.end()-1);
                        logger.info("开始读取运行参数：{}",propName);
                        String propVal = System.getProperty(propName);
                        if (propVal == null){
                            continue;
                        }
                        config = config.replaceAll( "\\$\\{"+propName+"}" , propVal);
                    }
                    return config;
                }
                return val;
            }
        }
        return null;
    }

    /**
     * 获取配置文件
     * 优先级从高到低，运行参数指定 >  jar支行同级配置文件 > 包内默认配置文件
     * @param fileName
     * @return
     */
    protected File getConfigPath(String fileName){
        //从运行参数中获取指定目录
        String configPath = System.getProperty("confPath");
        File configFile = FileUtil.getFile(configPath,fileName);
        if (configFile != null){
            logger.info("已读取运行参数指定的配置文件目录：{}",configPath);
            return configFile;
        }
/** * 获取当前可执行jar包所在目录 */
        Class clz = getClass();
        URL url = clz.getProtectionDomain().getCodeSource().getLocation();
        File jarFile = null;
        try {
            // 转化为utf-8编码，支持中文
            configPath = URLDecoder.decode(url.getPath(), "utf-8");
            jarFile = new File(configPath);
            if (jarFile.isDirectory()) {
                configPath = jarFile.getCanonicalPath();
            }else {
                configPath = jarFile.getParentFile().getCanonicalPath();
            }
        } catch (Exception e) {
            logger.error("获取当前可执行jar包所在目录时出错！",e);
        }

        configFile = FileUtil.getFile(configPath,fileName);
        if (configFile != null){
            logger.info("已读取执行jar所在目录配置，文件目录：{}",configPath);
            return configFile;
        }

        //读取自身默认配置文件
        URL clzUrl = clz.getResource("/" + fileName);
        try {
            configPath = URLDecoder.decode(clzUrl.getPath(), "utf-8");
            configFile = FileUtil.getFile(configPath,fileName);
            if (configFile != null){
                logger.info("已读取执行jar包内默认配置，文件目录：{}",configPath);
                return configFile;
            }
        } catch (Exception e) {
            logger.error("获取当前可执行jar包内默认配置时出错！",e);
        }
        throw new RuntimeException("没有找到配置文件！" + fileName);
    }


    protected void info(){
        logger.info("开始执行 info 命令！");
        String tblFormat = "          |%-20s|%-50s|%-10s|%-25s|%-20s|%-20s|%-80s|";
        MigrationInfo[] pending = flyway.info().pending();
        logger.info("---------------------------------------------------------------------------------------------------" +
                "-------------------------------------------------------------------------------------------------------");
        String header = String.format(tblFormat, "Version"
                , "Description", "Type","Installed On ","State","Checksum","Script");
        logger.info(header);
        logger.info("---------------------------------------------------------------------------------------------------" +
                "-------------------------------------------------------------------------------------------------------");
        for (MigrationInfo migrationInfo : pending) {
            String str = String.format(tblFormat, migrationInfo.getVersion()==null?"":migrationInfo.getVersion().getVersion()
                    , migrationInfo.getDescription(), migrationInfo.getType(), migrationInfo.getInstalledOn()
            ,migrationInfo.getState(),migrationInfo.getChecksum(),migrationInfo.getScript());
            logger.info(str);
        }
    }
    protected void repair(){
        logger.info("开始执行repair命令！");
        flyway.repair();
        logger.info("结束执行repair命令！");
    }
    protected void migrate(){
        logger.info("开始执行migrate命令！");
        int count = flyway.migrate();
        logger.info("结束执行migrate命令！更新条数："+count);
    }

    /**
     * 判断是否获取到正确的执行历史
     * 通过判断是否获取到当前执行记录判断
     */
    protected void chkForAction(){
        MigrationInfo current = flyway.info().current();
        if (current == null){
            String schemaInDb = "";
            try {
                schemaInDb = flyway.getDataSource().getConnection().getSchema();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            throw new RuntimeException(String.format("没有获取到正确的执行历史，请检查!" +
                    "\n 1.是否为flyway未初始化?" +
                    "\n 2.执行历史记录表是否正确创建？要求的执行记录表：%s" +
                    "\n 3.schema配置是否正确？是否区分大小写？当前配置：%s，数据库配置：%s",flyway.getTable()
                    ,JSONObject.toJSONString(flyway.getSchemas()),schemaInDb));
        }
    }
}
