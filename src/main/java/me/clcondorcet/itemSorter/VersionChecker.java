package me.clcondorcet.itemSorter;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VersionChecker {

    public static String pluginVersion = "1.3.4";
    public boolean needUpdate;
    public String lastVersion;

    VersionChecker() {
        this.lastVersion = getLastVersion();
        if(lastVersion.equals("Error")){
            needUpdate = false;
        }else needUpdate = !lastVersion.equals(pluginVersion);
    }

    private String getLastVersion(){
        try {
            HttpURLConnection conn = getConnection("https://clcondorcet.me/plugins/api/ItemSorter");
            if (conn.getResponseCode() == 200) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String line = reader.readLine();
                Matcher matches = Pattern.compile("(?<=LastVersion\":\")([^\"]*)").matcher(line);
                if(matches.find()){
                    return matches.group(0);
                }else{
                    return "Error";
                }
            }else{
                return "Error";
            }
        } catch (Exception ex) {
            return "Error";
        }
    }

    private HttpURLConnection getConnection(final String url) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Premium-Checker");
        return connection;
    }
}
