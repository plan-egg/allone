package io.github.planegg.allone.devtool.git;

/**
 * gitlab项目信息配置类
 */
public class GitlabPrjInfoDto implements IProjectInfoService{

    private String name;
    private Long prjId;
    private String uatBranch = "uat";
    private String mainBranch = "master";

    public void setName(String name) {
        this.name = name;
    }

    public void setPrjId(Long prjId) {
        this.prjId = prjId;
    }

    public void setUatBranch(String uatBranch) {
        this.uatBranch = uatBranch;
    }

    public void setMainBranch(String mainBranch) {
        this.mainBranch = mainBranch;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public Long getPrjId() {
        return prjId;
    }

    @Override
    public String getUatBranch() {
        return uatBranch;
    }

    @Override
    public String getMainBranch() {
        return mainBranch;
    }
}
