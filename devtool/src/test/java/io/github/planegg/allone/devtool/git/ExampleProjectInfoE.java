package io.github.planegg.allone.devtool.git;

/**
 *
 */
public enum ExampleProjectInfoE implements IProjectInfoService{
    testSys_1(1)
    ,testSys_2(2)
    ,testSys_3(3)
    ,testSys_4(4)
    ;

    private Long prjId;
    private String uatBranch;
    private String mainBranch;

    ExampleProjectInfoE(int prjId) {
        this.prjId = Long.valueOf(prjId);
        this.uatBranch = "uat";
        this.mainBranch = "master";
    }

    @Override
    public String getName() {
        return name();
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
