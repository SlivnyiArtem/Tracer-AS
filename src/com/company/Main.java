package com.company;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.*;


public class Main {

    public static void main(String[] args) throws Exception {
        String encoding = System.getProperty("console.encoding", "utf-8");
        var input = new Scanner(System.in, encoding);
        String domenOrIp = input.nextLine();
        input.close();
        Process process = Runtime.getRuntime().exec("tracert -d " + domenOrIp);
        var reader = new BufferedReader(new InputStreamReader(process.getInputStream(), encoding));
        var currentLine = "";
        var cnt = 1;

        System.out.println("№    IP-Address    AS   Lang   Provider");
        while((currentLine = reader.readLine()) != null) {
            var splitInt = currentLine.split(" ");
            try {
                var last = splitInt[splitInt.length - 1];
                if (last.equals(""))
                    continue;
                printTable(cnt, last, getAS(last));
            } catch (FileNotFoundException | ArrayIndexOutOfBoundsException el) {
                if (currentLine.contains("Превышен интервал")) {
                    printTable(cnt, "***", "time limit");
                }
                else
                    continue;
            }
            cnt++;
        }
        System.out.println("end of the table");
    }

    public static String getAS(String urlEnd) throws Exception {
        String url = "https://whois.ru/" + urlEnd;
        String AS = null;
        String country = null;
        String provider = null;
        try (InputStream is = new URL(url).openStream()) {
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8));
            while (true) {
                var line = rd.readLine();
                if (line == null)
                    break;
                if (line.contains("origin") || line.contains("OriginAS"))
                    AS = line.substring(line.lastIndexOf(" ")+1);
                if (line.contains("country") || line.contains("Country"))
                    country = line.substring(line.lastIndexOf(" ")+1);
                if (line.contains("netname") || line.contains("NetName"))
                    provider = line.substring(line.lastIndexOf(" ")+1);
            }
        }
        if (AS==null && country == null && provider == null) {
            return "domain is not supported";
        }
        return AS + "    " + country + "    " + provider;
    }

    private static void printTable(int number, String ip, String AdditionalInformation) {
        System.out.println(number + "    " + ip + "    " + AdditionalInformation);
    }
}
