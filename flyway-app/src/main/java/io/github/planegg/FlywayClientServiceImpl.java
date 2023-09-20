package io.github.planegg;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import org.flywaydb.core.Flyway;
import org.flywaydb.core.api.MigrationInfo;
import org.flywaydb.core.api.configuration.ClassicConfiguration;
import org.flywaydb.core.api.configuration.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
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

        String configFileDir = FlywayClientServiceImpl.class.getClassLoader().getResource("").getPath();

        logger.info("正在读取的配置文件目录：{}",configFileDir);

        File activeConfigFile = new File(configFileDir + File.separator + String.format(configFileName,"-",activeProfile));
        File defaultConfigFile = new File(configFileDir + File.separator + String.format(configFileName,"",""));


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
                        config = config.replaceAll( "\\$\\{"+propName+"}" , System.getProperty(propName));
                    }
                    return config;
                }
                return val;
            }
        }
        return null;
    }

}
