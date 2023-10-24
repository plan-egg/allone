package io.github.planegg.allone.devtool.git;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CommitMsgChker {
    private static final Pattern p = Pattern.compile("^((#[A-Z]+\\-\\d+\\s\\S+\\s\\S+)|((Merge|Revert)\\s.*))");


    public static void main(String[] args) {

        String test1 =  "#xx-999 Merge branch 'xxxxxxxxxxxxx' into 'xxxxxxxxx'";
        String test2 =  "xx-999 Merge branch 'XXXXXXXX' into 'XXXX'";
        String test3 =  "Merge branch 'XXXXXXX' into 'XXXXXXXXX'";
        String test4 =  "Revert #XXXXXX Merge branch 'XXXXXXX' into 'XXXX'";
        String test5 =  " Revert XXX Merge branch 'XXXXXXX' into 'XXXXXX'";
        String test6 =  "Mergebranch 'XXXXX' into 'XXXXX'";
        String test7 =  "Merge";
        Matcher matcher1 = p.matcher(test1);
        Matcher matcher2 = p.matcher(test2);
        Matcher matcher3 = p.matcher(test3);
        Matcher matcher4 = p.matcher(test4);
        Matcher matcher5 = p.matcher(test5);
        Matcher matcher6 = p.matcher(test6);
        Matcher matcher7 = p.matcher(test7);
        System.out.println(matcher1.find());
        System.out.println(matcher2.find());
        System.out.println(matcher3.find());
        System.out.println(matcher4.find());
        System.out.println(matcher5.find());
        System.out.println(matcher6.find());
        System.out.println(matcher7.find());
    }
}
