package io.github.planegg.allone.devtool.git;

import java.util.List;

/**
 * gitlab项目信息ymal配置文件装载类
 */
public class GitlabConfigDto {
    /**
     * gitlab 服务地址
     */
    private String gitlabUrl;
    /**
     * gitlab 连接token
     */
    private String gitlabToken;
    /**
     * gitlab项目信息
     */
    private List<GitlabPrjInfoDto> prjInfoList;

    public String getGitlabUrl() {
        return gitlabUrl;
    }

    public void setGitlabUrl(String gitlabUrl) {
        this.gitlabUrl = gitlabUrl;
    }

    public String getGitlabToken() {
        return gitlabToken;
    }

    public void setGitlabToken(String gitlabToken) {
        this.gitlabToken = gitlabToken;
    }

    public List<GitlabPrjInfoDto> getPrjInfoList() {
        return prjInfoList;
    }

    public void setPrjInfoList(List<GitlabPrjInfoDto> prjInfoList) {
        this.prjInfoList = prjInfoList;
    }


}
