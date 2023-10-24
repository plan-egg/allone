package io.github.planegg.allone.devtool.git;

//import com.alibaba.fastjson2.JSONObject;
import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

import java.util.List;

public class GitlabServiceTest {


    public static void main(String[] args) throws Exception {

        GitlabService gitlabService = new GitlabPingcodeServiceImpl();
        String prjInfoConfigFile = "gitlabPrjInfo-ae.ymal";
//        ((GitlabPingcodeServiceImpl)gitlabService).setRspBody(callPingcodeForVersion());
        //获取版本中的变更请求列表
//        List<String> chgReqList = ((GitlabPingcodeServiceImpl) gitlabService).getChgReqList();
        //指定版本中不需要检查的需求
//                gitlabService.setExcludeChgReqSet("xxxx-787","xxxx-797","xxxx-741","xxxx-747","xxxx-746","xxxx-746","xxxx-737");
//        检查整个版本
//        gitlabService.start(prjInfoConfigFile);
        //检查单个需求变更
        gitlabService.compareInOneReqChg(prjInfoConfigFile,"xxxx-790");

    }

    private static String callPingcodeForVersion(){
        HttpResponse<String> response = Unirest.post("https://xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx")
                .header("Pragma", "no-cache")
                .header("Cookie", "xxxxxxxxxxxxxxxxxxx")
                .header("Content-Type", "application/json")
                .header("Accept", "*/*")
                .header("Host", "xxxxxxxxxxxxxxxxx")
                .header("Connection", "keep-alive")
                .body("{\"querystring\":\"xxxxxxxxxxxxxxx\"}")
                .asString();
        return response.getBody();
    }

}
