package io.github.planegg.allone.devtool.git;

/**
 * 定义项目信息枚举必填值
 */
public interface IProjectInfoService {
    /**
     * 获取项目名称
     * @return
     */
    String getName();
    /**
     * 获取项目id
     * @return
     */
    Long getPrjId();
    /**
     * 获取uat分支
     * @return
     */
    String getUatBranch();

    /**
     * 获取主干分支
     * @return
     */
    String getMainBranch();

    /**
     * 获取合并请求ID
     * @return
     */
    Long getMergeReqId();
}
