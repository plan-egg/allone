package io.github.planegg.allone.devtool.git;

import com.alibaba.fastjson2.JSONObject;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import io.github.planegg.allone.devtool.git.filter.GitlabCommitFilter;
import io.github.planegg.allone.devtool.git.filter.NoMergeFilter;
import org.gitlab4j.api.Constants;
import org.gitlab4j.api.GitLabApi;
import org.gitlab4j.api.GitLabApiException;
import org.gitlab4j.api.models.Commit;
import org.gitlab4j.api.models.MergeRequest;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 *
 * @param <T>
 */
public abstract class GitlabService <T extends IProjectInfoService> {
    /**
     * 存放项目名与gitlabApi的关系
     * 用于根据项目名获取gitlabApi调用gitlab接口
     */
    private Map<String,GitLabApi> gitLabApiMap = new HashMap<>();



    protected final static String unknowChgReq = "未归类";
    /**
     * 用于适配单一gitlab来源的情况
     */
    protected final static String DEFAULT_GITLAB_API_KEY = "default";

    protected abstract GitlabService getGitlabService();

    /**
     * 开始检查
     * @param prjInfoClz
     * @throws GitLabApiException
     */
    public void start(String gitlabUrl ,String token,Class<T> prjInfoClz) throws GitLabApiException {
        Map<String,GitLabApi> gitLabApiMap = new HashMap<>();
        gitLabApiMap.put(DEFAULT_GITLAB_API_KEY,new GitLabApi(gitlabUrl,token));
        start(gitLabApiMap , prjInfoClz);
    }
    /**
     * 开始检查
     * @param prjInfoClz
     * @throws GitLabApiException
     */
    public void start(Map<String,GitLabApi> gitLabApiMap,Class<T> prjInfoClz) throws GitLabApiException {
        List<String> chgReqList = getGitlabService().getChgReqList();
        T[] prjInfoArr = prjInfoClz.getEnumConstants();
        List<T> prjInfoList = Arrays.asList(prjInfoArr);
        start(gitLabApiMap , chgReqList,prjInfoList);
    }
    /**
     * 开始检查
     * @param configFile
     * @throws GitLabApiException
     */
    public void start(String configFile) throws GitLabApiException, IOException {
        ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
        //读取example.yaml文件
        String configFilePath = GitlabService.class.getClassLoader().getResource(configFile).getPath();
        File file = new File(configFilePath);
        System.out.println("正在读取配置文件："+file.getAbsolutePath());
        //将yaml文件的内容转换为Java对象
        GitlabConfig gitlabConfig = mapper.readValue(file, GitlabConfig.class);
        List<T> prjInfoList = new ArrayList<>();
        Map<String,GitLabApi> gitLabApiMap = new HashMap<>();

        for (GitlabInfoDto gitlabInfoDto : gitlabConfig.getGitlabInfoList()) {
            if (gitlabInfoDto.getGitlabUrl() == null){
                throw new RuntimeException("gitlabUrl 不能为空！");
            }
            if ( gitlabInfoDto.getGitlabToken() == null){
                throw new RuntimeException("gitlabToken 不能为空！");
            }
            GitLabApi gitLabApi = new GitLabApi(gitlabInfoDto.getGitlabUrl(),gitlabInfoDto.getGitlabToken());
            for (GitlabPrjInfoDto gitlabPrjInfoDto : gitlabInfoDto.getPrjInfoList()) {
                gitLabApiMap.put(gitlabPrjInfoDto.getName(),gitLabApi);
            }
            prjInfoList.addAll((List<T>)gitlabInfoDto.getPrjInfoList());
        }

        List<String> chgReqList = getGitlabService().getChgReqList();
        start(gitLabApiMap,chgReqList,prjInfoList);
    }

    /**
     * 开始检查
     * @param chgReqList
     * @throws GitLabApiException
     */
    public void start(Map<String,GitLabApi> gitLabApiMap,List<String> chgReqList, List<T> prjInfoList) throws GitLabApiException {
        this.gitLabApiMap.putAll(gitLabApiMap);
        if (chgReqList == null || chgReqList.size() == 0){
            throw new RuntimeException("chgReqList 不能为空！");
        }
        if (prjInfoList == null || prjInfoList.size() == 0){
            throw new RuntimeException("prjInfoList 不能为空！");
        }

        Map<String,Set<String>> srcChgReqAndCommit4CompareMap = new HashMap<>();
        Map<String,Set<String>> tgtChgReqAndCommit4CompareMap = new HashMap<>();

        Map<String, GitlabRlseCommpareDiffDto> chkRsMap = new HashMap<>();

        List<String> missChgReqList = new ArrayList<>(chgReqList);

        for (String chgReq : chgReqList) {
            System.out.println("开始从来源分支获取变更需求："+chgReq);
            //根据指定获取
            for (T prjInfo : prjInfoList) {
                GitLabApi gitLabApi = getGitlabService().getGitlabApi(prjInfo.getName());
                Map<String,Set<String>> chgReqAndCommit4CompareMap = getGitlabService().searchCommits(gitLabApi,prjInfo, chgReq, prjInfo.getUatBranch());
                if (chgReqAndCommit4CompareMap.size() == 0){
                    continue;
                }
                missChgReqList.remove(chgReq);
                srcChgReqAndCommit4CompareMap.putAll(chgReqAndCommit4CompareMap);
                System.out.println(String.format("chgReq=%s,projectName=%s",chgReq,prjInfo.getName()
                        ,JSONObject.toJSONString(chgReqAndCommit4CompareMap)));
            }
        }

        for (T prjInfo : prjInfoList) {
            System.out.println("开始从目标分支获取变更需求，当前项目："+prjInfo.getName());
            GitLabApi gitLabApi = getGitlabService().getGitlabApi(prjInfo.getName());
            Long mergeRequestId = getMergeRequest(gitLabApi,prjInfo);
            if (mergeRequestId == null){
                continue;
            }
            Map<String,Set<String>> chgReqAndCommit4CompareMap = getGitlabService().getMergeCommits( gitLabApi, prjInfo, mergeRequestId);
            if (chgReqAndCommit4CompareMap.size() == 0){
                continue;
            }
            for (String compareDiffKey : chgReqAndCommit4CompareMap.keySet()) {
                String chgReqNum = compareDiffKey.split("@")[0];
                missChgReqList.remove(chgReqNum);
            }
            tgtChgReqAndCommit4CompareMap.putAll(chgReqAndCommit4CompareMap);
        }

        Map<String, GitlabRlseCommpareDiffDto> compareDiffMap = getGitlabService().getCompareDiffMap(srcChgReqAndCommit4CompareMap, tgtChgReqAndCommit4CompareMap);


        for (String missChgReq : missChgReqList) {
            GitlabRlseCommpareDiffDto rlseCommpareDiffDto = new GitlabRlseCommpareDiffDto();
            rlseCommpareDiffDto.setMissInSrc(true);
            rlseCommpareDiffDto.setMissInTgt(true);
            chkRsMap.put(missChgReq,rlseCommpareDiffDto);
        }

        chkRsMap.putAll(compareDiffMap);
        System.out.println("检查结果：");
        System.out.println(JSONObject.toJSONString(chkRsMap));
    }


    /**
     *  获取合并请求里的变动提交
     * @param prjInfo
     * @param mrId
     * @return
     * @throws GitLabApiException
     */
    protected Map<String,Set<String>> getMergeCommits(GitLabApi gitLabApi,T prjInfo, Long mrId) throws GitLabApiException {
        String prjId = String.valueOf(prjInfo.getPrjId());
        List<Commit> commits = gitLabApi.getMergeRequestApi().getCommits(prjId, mrId);
        Map<String,Set<String>> chgReqAndCommit4CompareMap = getGitlabService().filterCommit(prjInfo,commits);
        return chgReqAndCommit4CompareMap;
    }

    /**
     * 在指定分支里获取指定变更需求的提交
     * @param prjInfo
     * @param searchKey
     * @param branch
     * @return
     * @throws GitLabApiException
     */
    protected Map<String,Set<String>> searchCommits(GitLabApi gitLabApi, T prjInfo , String searchKey , String branch) throws GitLabApiException {
        String prjId = String.valueOf(prjInfo.getPrjId());
        List<Commit> commitList = (List<Commit>) gitLabApi.getSearchApi()
                .projectSearch(prjId, Constants.ProjectSearchScope.COMMITS, searchKey, branch);
        Map<String,Set<String>> chgReqAndCommit4CompareMap = getGitlabService().filterCommit(prjInfo,commitList);
        return chgReqAndCommit4CompareMap;
    }

    /**
     * 获取待合并到主干的合并请求ID
     * @param prjInfo 项目集信息
     * @return
     * @throws GitLabApiException
     */
    protected Long getMergeRequest(GitLabApi gitLabApi, T prjInfo) throws GitLabApiException {
        List<Long> mgReqIdList = new ArrayList<>();
        String prjId = String.valueOf(prjInfo.getPrjId());
        List<MergeRequest> mergeRequestList = gitLabApi.getMergeRequestApi().getMergeRequests(prjId, Constants.MergeRequestState.OPENED);
        //获取合并到主干的合并请求
        for (MergeRequest mergeRequest : mergeRequestList) {
            String targetBranch = mergeRequest.getTargetBranch();
            if (!prjInfo.getMainBranch().equals(targetBranch)){
                continue;
            }
            Long mgReqId = mergeRequest.getIid();
            mgReqIdList.add(mgReqId);
            System.out.println(String.format("项目【%s】获取到的合并请求标题：%s",prjInfo.getName(),mergeRequest.getTitle()));
        }
        if (mgReqIdList.size() == 0){
            return null;
        }
        if (mgReqIdList.size() > 1){
            throw new RuntimeException(String.format("项目【%s】合并到主干的待合并请求数不是有且只有1个！当前请求数：%S",prjInfo.getName(),mergeRequestList.size()));
        }
        Long mgReqId = mgReqIdList.get(0);
        return mgReqId;
    }

    /**
     * 过滤提交
     * @param prjInfo
     * @param commits
     * @return
     */
    protected Map<String, Set<String>> filterCommit(T prjInfo , List<Commit> commits){
//        System.out.println("++++++++++++++++++++++++++++++++++++");
//        System.out.println(String.format("prjId=%s,commitSize=%s,before start..........",prjId,commits.size()));

        Map<String,Set<String>> chgReqAndCommit4CompareMap = new HashMap<>();

        GitlabCommitFilter gitlabCommitFilter = new NoMergeFilter();
        for (Commit aCommit : commits){

            if (!gitlabCommitFilter.startFilter(aCommit)){
//                System.out.println(String.format("【-】%s@%s",aCommit.getTitle(),aCommit.getShortId()));
                continue;
            }
            String title = aCommit.getTitle();
//            System.out.println(String.format("【+】%s@%s",title,aCommit.getShortId()));
            String chgNum = getGitlabService().getChgReqCode(title);
            if (chgNum == null){
                chgNum = unknowChgReq;
            }
            String commitKey = getGitlabService().getCommitKey(chgNum,prjInfo);
            Set<String> commitIdSet = chgReqAndCommit4CompareMap.get(commitKey);
            if (commitIdSet == null){
                commitIdSet = new HashSet<>();
                chgReqAndCommit4CompareMap.put(commitKey,commitIdSet);
            }
            commitIdSet.add(aCommit.getShortId());
//            idAndCommitMap.put(aCommit.getShortId(),aCommit);
//            System.out.println(aCommit.getTitle()+"@@"+aCommit.getShortId());
        }
//        System.out.println("-------------------------");
        return chgReqAndCommit4CompareMap;
    }


    /**
     * 比较两分支间的提交差异
     * @param srcChgReqAndCommitMap
     * @param tgtChgReqAndCommitMap
     * @return
     */
    private Map<String,GitlabRlseCommpareDiffDto> getCompareDiffMap(Map<String, Set<String>> srcChgReqAndCommitMap, Map<String, Set<String>> tgtChgReqAndCommitMap){
        Map<String,GitlabRlseCommpareDiffDto> compareDiffDtoMap = new HashMap<>();

        Set<String> tgtChgReqSet = tgtChgReqAndCommitMap.keySet();

        for (Map.Entry<String, Set<String>> srcChgReqAndCommit : srcChgReqAndCommitMap.entrySet()){

            GitlabRlseCommpareDiffDto rlseCommpareDiffDto = new GitlabRlseCommpareDiffDto();
            String chgReqNum = srcChgReqAndCommit.getKey();
            Set<String> srcCommitIdSet = srcChgReqAndCommit.getValue();
            Set<String> tgtCommitIdSet = tgtChgReqAndCommitMap.get(chgReqNum);
            if (tgtCommitIdSet == null){
                rlseCommpareDiffDto.setMissInTgt(true);
                compareDiffDtoMap.put(chgReqNum,rlseCommpareDiffDto);
                continue;
            }
            tgtChgReqSet.remove(chgReqNum);

            Set<String> lessInTgtBranch = new HashSet<>();
            Set<String> moreInTgtBranch = new HashSet<>(tgtCommitIdSet);

            for (String srcCommitId : srcCommitIdSet) {
                if (tgtCommitIdSet.contains(srcCommitId)){
                    moreInTgtBranch.remove(srcCommitId);
                }else {
                    lessInTgtBranch.add(srcCommitId);
                }
            }
            rlseCommpareDiffDto.setLessInTarget(lessInTgtBranch);
            rlseCommpareDiffDto.setMoreInTarget(moreInTgtBranch);
            if (lessInTgtBranch.size() > 0 || moreInTgtBranch.size() > 0){
                compareDiffDtoMap.put(chgReqNum,rlseCommpareDiffDto);
            }
        }
        for (String tgtChgReq : tgtChgReqSet) {
            GitlabRlseCommpareDiffDto rlseCommpareDiffDto = new GitlabRlseCommpareDiffDto();
            rlseCommpareDiffDto.setMissInSrc(true);
            compareDiffDtoMap.put(tgtChgReq,rlseCommpareDiffDto);
        }
        return compareDiffDtoMap;
    }

    /**
     * 获取统一的提交展示标题
     * @param chgNum
     * @param prjInfo
     * @return
     */
    private String getCommitKey(String chgNum ,T prjInfo){

        return chgNum+"@" + prjInfo.getName();
    }

    /**
     * 从提交注释里获取变更需求编号
     * @param title
     * @return
     */
    protected abstract String getChgReqCode(String title);

    /**
     * 获取版变更需求列表
     * @return
     */
    public abstract List<String> getChgReqList();

    /**
     * 获取gitlabApi
     * @param prjName
     * @return
     */
    private GitLabApi getGitlabApi(String prjName){
        GitLabApi gitLabApi = gitLabApiMap.get(prjName);
        if (gitLabApi != null){
            return gitLabApi;
        }
        gitLabApi = gitLabApiMap.get(DEFAULT_GITLAB_API_KEY);
        if (gitLabApi == null){
            throw new RuntimeException("请配置好gitLabApi");
        }
        return gitLabApi;
    }

}
