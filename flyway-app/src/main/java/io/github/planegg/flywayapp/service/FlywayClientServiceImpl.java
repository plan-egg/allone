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
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlywayClientServiceImpl implements IFlywayClientService {

    private Logger logger = LoggerFactory.getLogger(FlywayClientServiceImpl.class);

    private String activeProfile = System.getProperty("activeProfile","uat");
    /**
     * 匹配配置中${xxx.xxx}的变量
     * 用于从运行时通过参数传入
     */
    private final static Pattern REPLACE_HOLDER_PATTERN = Pattern.compile("\\$\\{([a-zA-Z]\\.?)+}");

    @Override
    public Flyway getFlywayClient(){
        Configuration configure = getConfig(getConfigList());
        Flyway flyway = new Flyway(configure);
        return flyway;
    }

    @Override
    public void execute(Flyway flyway){
        MigrationInfo[] pending = flyway.info().all();
        for (MigrationInfo migrationInfo : pending) {
            logger.info(JSONObject.toJSONString(migrationInfo));
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


    protected void info(Flyway flyway){
        MigrationInfo[] pending = flyway.info().all();
        for (MigrationInfo migrationInfo : pending) {
            logger.info(JSONObject.toJSONString(migrationInfo));
        }
    }
    protected void repair(Flyway flyway){
        flyway.repair();
        logger.info("repair命令执行成功！");
    }
    protected void migrate(Flyway flyway){
        int count = flyway.migrate();
        logger.info("更新条数："+count);
    }

}
