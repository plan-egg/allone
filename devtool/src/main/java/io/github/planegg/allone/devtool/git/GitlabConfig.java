package io.github.planegg.allone.devtool.git;

import java.util.List;

/**
 * gitlab项目信息ymal配置文件装载类
 */
public class GitlabConfig {
    /**
     *
     */
    private List<GitlabInfoDto> gitlabInfoList;

    public List<GitlabInfoDto> getGitlabInfoList() {
        return gitlabInfoList;
    }

    public void setGitlabInfoList(List<GitlabInfoDto> gitlabInfoList) {
        this.gitlabInfoList = gitlabInfoList;
    }
}
