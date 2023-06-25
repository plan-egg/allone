package io.github.planegg.allone.devtool.mpgenertor;

import com.baomidou.mybatisplus.generator.config.DataSourceConfig;
import com.baomidou.mybatisplus.generator.config.converts.OracleTypeConvert;
import com.baomidou.mybatisplus.generator.keywords.MySqlKeyWordsHandler;

public class AlloneOracleGenerator extends AlloneBaseGenerator{


    /**
     * 获取mysql数据库连接配置
     * @param url
     * @param username
     * @param pwd
     * @param dbName
     * @return
     */
    protected DataSourceConfig getDataSourceConfig(String url, String username, String pwd , String dbName){
        DataSourceConfig dataSourceConfig = new DataSourceConfig
                .Builder(url, username, pwd)
                //数据库类型转换器
                .typeConvert(new OracleTypeConvert())
//            数据库关键字处理器
                .keyWordsHandler(new MySqlKeyWordsHandler())
                .build();
        return dataSourceConfig;
    }
}
