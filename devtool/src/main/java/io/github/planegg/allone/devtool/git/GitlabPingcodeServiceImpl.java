package io.github.planegg.allone.devtool.git;

import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class GitlabPingcodeServiceImpl extends GitlabService {

    private String rspBody;
    /**
     * pingcode 编号格式
     */
    protected Pattern pingcodeNumPtn = Pattern.compile("\\w+\\-\\d+");

    @Override
    protected GitlabService getGitlabService() {
        return this;
    }

    @Override
    protected String getChgReqCode(String title){
        if (title.indexOf(" ") <= 0){
            System.err.println("提交没有按照规范注释："+title);
            return title;
        }
        String chgNum = title.substring(0,title.indexOf(" "));
        Matcher pingcodeMatcher = pingcodeNumPtn.matcher(chgNum);
        // "#xxxx-1234    这是这一个测试例子，包含了特殊的空格-ASCII码值160"
        if (!pingcodeMatcher.find()) {
            System.err.println("提交的pingcode编号不正确："+title);
            return chgNum;
        }
        chgNum = pingcodeMatcher.group();
        return chgNum;
    }


    @Override
    public List<String> getChgReqList() {
        List<String> chgReqList = new ArrayList<>();

        JSONObject rspJObj = JSONObject.parse(rspBody);
        JSONObject dataJObj = JSONObject.parse(rspJObj.getString("data"));
        JSONArray valueJArr = JSONArray.parse(dataJObj.getString("value"));
        for (Object valObj : valueJArr) {
            String pingcodeCode = ((JSONObject)valObj).getString("whole_identifier");
            chgReqList.add(pingcodeCode);
        }
        System.out.println("获取到的版本变更需求列表：");
        System.out.println(JSONObject.toJSONString(chgReqList));
        return chgReqList;
    }

    public String getRspBody() {
        return rspBody;
    }

    public void setRspBody(String rspBody) {
        this.rspBody = rspBody;
    }


}
