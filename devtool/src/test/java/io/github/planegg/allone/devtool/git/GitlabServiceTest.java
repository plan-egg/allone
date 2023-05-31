package io.github.planegg.allone.devtool.git;

import kong.unirest.HttpResponse;
import kong.unirest.Unirest;

public class GitlabServiceTest {


    public static void main(String[] args) throws Exception {

        GitlabService gitlabService = new GitlabPingcodeServiceImpl();
        String prjInfoConfigFile = "gitlabPrjInfo.ymal";
//        ((GitlabPingcodeServiceImpl)gitlabService).setRspBody(callPingcodeForVersion());
        gitlabService.start(prjInfoConfigFile);

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
