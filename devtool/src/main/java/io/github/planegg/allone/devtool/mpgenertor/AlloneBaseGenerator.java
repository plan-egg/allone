package io.github.planegg.allone.devtool.mpgenertor;



import com.baomidou.mybatisplus.generator.AutoGenerator;
import com.baomidou.mybatisplus.generator.config.*;
import com.baomidou.mybatisplus.generator.util.ClassUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * 基础测试类
 *
 * @author lanjerry
 * @since 3.5.3
 */
public class AlloneBaseGenerator {

    /**
     * 项目信息
     * 需要代码生成的项目相关信息
     */
    private ProjectInfo projectInfo;


    /**
     * 读取表配置文件
     * @param fileName
     * @return
     */
    protected  List<String> readTableListFromFile(String fileName){
        String prjPath = ClassUtils.getDefaultClassLoader().getResource("").getPath().substring(1);
        prjPath=prjPath.replace("/target/test-classes/","/src/test/resources");
        System.out.println("配置文件的路径="+prjPath);

        List<String> tableList = new LinkedList<>();
        File tblListFile = new File(prjPath+File.separator+fileName);



        if (!tblListFile.exists()){
            try {
                //getParentFile() 获取上级目录（包含文件名时无法直接创建目录的）
                if (!tblListFile.getParentFile().exists()) {
                    //创建上级目录
                    tblListFile.getParentFile().mkdirs();
                }
                tblListFile.createNewFile();
                throw new RuntimeException("没有发现配置文件，系统已自动创建，请在配置文件中配置需要生成代码文件的表名，一行一个表。");
            } catch (IOException e) {
                e.printStackTrace();
                System.err.println("配置文件创建失败！"+fileName);
            }
        }

        try (
            FileReader fileReader = new FileReader(tblListFile);
            BufferedReader reader = new BufferedReader(fileReader);
            ){
            String line = "";
            while (line != null){
                line = reader.readLine();
                if (line != null && !"".equals(line.trim())){
                    tableList.add(line.trim());
                }
            }
        }catch (Exception e){
            throw new RuntimeException(String.format("读取文件%s出错",fileName),e);
        }
        return tableList ;
    }


    /**
     * 策略配置
     */
    protected  StrategyConfig.Builder strategyConfig() {
        StrategyConfig.Builder strategyConfig = new StrategyConfig.Builder();
        List<String> includeTblList = readTableListFromFile("incluedeTableList.txt");
        if (includeTblList.size() == 0){
            throw new RuntimeException("没有发现需要生成代码的表，请在配置表中配置，一行一个表名！");
        }
        strategyConfig.addInclude(includeTblList);
        strategyConfig.entityBuilder()
                //设置父类为通用字段类
                .superClass(projectInfo.getEntitySuperClass())
                //开启字段注解
                .enableTableFieldAnnotation()
                //开启 Boolean 类型字段移除 is 前缀
                .enableRemoveIsPrefix()
                //开启链式模型
                .enableChainModel()
                //格式化文件名称
                .formatFileName("%s"+ projectInfo.getFormatFileName());
//        strategyConfig.enableSchema();
        return strategyConfig;
    }

    /**
     * 全局配置
     */
    protected  GlobalConfig.Builder globalConfig() {
        GlobalConfig.Builder globalConfig = new GlobalConfig.Builder();
        globalConfig.outputDir(getOutputPath());
        globalConfig.fileOverride();
        return globalConfig;
    }

    /**
     * 包配置
     */
    protected  PackageConfig.Builder packageConfig() {

        PackageConfig.Builder pkgBd = new PackageConfig.Builder();
        pkgBd.parent(projectInfo.getParentPkg());
        //自定义Entity 包名
        pkgBd.entity(projectInfo.getEntityPkgName());
        pkgBd.pathInfo(Collections.singletonMap(OutputFile.mapperXml, getOutputPath()));
        return pkgBd;
    }

    /**
     * 模板配置
     */
    protected  TemplateConfig.Builder templateConfig() {
        return new TemplateConfig.Builder();
    }

    /**
     * 注入配置
     */
    protected  InjectionConfig.Builder injectionConfig() {
        // 测试自定义输出文件之前注入操作，该操作再执行生成代码前 debug 查看
        return new InjectionConfig.Builder().beforeOutputFile((tableInfo, objectMap) -> {
            System.out.println("tableInfo: " + tableInfo.getEntityName() + " objectMap: " + objectMap.size());
        });
    }

    /**
     * 获取代码输出路径
     * @return
     */
    protected  String getOutputPath(){
        if (projectInfo.getOutputPath() != null && !"".equals(projectInfo.getOutputPath())){
            return projectInfo.getOutputPath();
        }
        String prjPath = ClassUtils.getDefaultClassLoader().getResource("").getPath().substring(1);
        prjPath = prjPath.replace("test-classes","mp-generator-output");
        String path = prjPath;
        System.out.println("path="+path);
        return path;
    }

    /**
     * 生成代码
     * @param dataSourceConfig
     * @param projectInfo
     */
    protected void generateCodeFile(DataSourceConfig dataSourceConfig, ProjectInfo projectInfo) {
        this.projectInfo = projectInfo;
        AutoGenerator generator = new AutoGenerator(dataSourceConfig);

        StrategyConfig.Builder strategyConfig = strategyConfig();
//        strategyConfig.addInclude(targetTblArr);
        generator.strategy(strategyConfig.build());

        GlobalConfig.Builder globalConfig = globalConfig();
        generator.global(globalConfig.build());

        PackageConfig.Builder pkgConfig = packageConfig();
        generator.packageInfo(pkgConfig.build());
        generator.execute();
    }

}