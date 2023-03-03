package io.github.planegg.allone.devtool.mpgenertor;

/**
 *
 */
public class ProjectInfo {
    /**
     * 父包名(全路径)
     * 必填
     * 如：订单模块 order
     * io.github.planegg.allone.order
     */
    private String parentPkg;
    /**
     * 通用字段实体类
     * 必填
     * 用于被其他实体类继承
     */
    private Class entitySuperClass;
    /**
     * 生成代码输出路径
     * 默认项目targt/mp-generator-output
     */
    private String outputPath;
    /**
     * 实体类包名
     * 默认 po
     */
    private String entityPkgName = "po";
    /**
     * 实体类后缀
     * 默认Entity
     */
    private String formatFileName = "Entity";

    public ProjectInfo(String parentPkg, Class entitySuperClass) {
        this.parentPkg = parentPkg;
        this.entitySuperClass = entitySuperClass;
    }

    public String getParentPkg() {
        return parentPkg;
    }

    public void setParentPkg(String parentPkg) {
        this.parentPkg = parentPkg;
    }

    public Class getEntitySuperClass() {
        return entitySuperClass;
    }

    public void setEntitySuperClass(Class entitySuperClass) {
        this.entitySuperClass = entitySuperClass;
    }

    public String getOutputPath() {
        return outputPath;
    }

    public void setOutputPath(String outputPath) {
        this.outputPath = outputPath;
    }

    public String getEntityPkgName() {
        return entityPkgName;
    }

    public void setEntityPkgName(String entityPkgName) {
        this.entityPkgName = entityPkgName;
    }

    public String getFormatFileName() {
        return formatFileName;
    }

    public void setFormatFileName(String formatFileName) {
        this.formatFileName = formatFileName;
    }
}
