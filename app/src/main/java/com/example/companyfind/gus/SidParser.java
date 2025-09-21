package com.example.companyfind.gus;

// SidParser.java
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SidParser {
    private static final Pattern SID =
            Pattern.compile("<ZalogujResult>(.*?)</ZalogujResult>", Pattern.DOTALL);

    public static String extract(String xml) {
        Matcher m = SID.matcher(xml);
        return m.find() ? m.group(1).trim() : null;
    }
}
