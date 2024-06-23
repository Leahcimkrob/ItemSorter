package me.clcondorcet.itemsorter.utils;

import com.google.gson.Gson;
import me.clcondorcet.itemsorter.ItemSorter;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author clcondorcet
 */
public class VersionChecker {
    public static String pluginVersion = "1.4.4";
    public static int apiVersion = 2;
    public boolean needUpdate;
    public String lastVersion;

    public VersionChecker() {
        ItemSorter.getInstance().getLogger().info("Checking for updates ...");
        this.lastVersion = getLastVersion();
        if (lastVersion.equals("Error")) {
            needUpdate = false;
        } else needUpdate = !lastVersion.equals(pluginVersion);
        if (needUpdate) {
            ItemSorter.getInstance().getLogger().info("An update is available! Please upgrade ItemSorter here https://www.spigotmc.org/resources/itemsorter.85370/");
        } else {
            ItemSorter.getInstance().getLogger().info("No updates found. You are up to date!");
        }
    }

    private String getLastVersion(){
        try {
            Gson gson = new Gson();
            PluginData data = new PluginData(pluginVersion, Bukkit.getVersion(), Bukkit.getName());
            String jsonResponse = getConnection("https://clcondorcet.me/api/plugins/ItemSorter/v" + apiVersion, gson.toJson(data));
            APIData apiData = gson.fromJson(jsonResponse, APIData.class);
            return apiData.lastversion;
        } catch (Throwable ex) {
            return "Error";
        }
    }

    private String getConnection(final String url, final String jsonData) throws IOException {
        final HttpURLConnection connection = (HttpURLConnection)new URL(url).openConnection();
        connection.setConnectTimeout(1000);
        connection.setReadTimeout(1000);
        connection.setRequestMethod("POST");
        connection.setRequestProperty("Content-Type", "application/json");
        connection.setRequestProperty("User-Agent", "Premium-Checker");
        connection.setDoOutput(true);
        OutputStreamWriter out = new OutputStreamWriter(connection.getOutputStream());
        out.write(jsonData);
        out.flush();
        out.close();
        if (connection.getResponseCode() != 200) {
            throw new ConnectException();
        }
        InputStreamReader in = new InputStreamReader(connection.getInputStream());
        BufferedReader reader = new BufferedReader(in);
        String data = reader.readLine();
        in.close();
        return data;
    }

    private static class APIData {
        private String name;
        private String author;
        private String lastversion;
        private String[] versions;

        public APIData(String name, String author, String lastversion, String[] versions) {
            this.name = name;
            this.author = author;
            this.lastversion = lastversion;
            this.versions = versions;
        }

        public String getName() {
            return name;
        }

        public String getAuthor() {
            return author;
        }

        public String getLastVersion() {
            return lastversion;
        }

        public String[] getAllVersions() {
            return versions;
        }
    }

    private static class PluginData {
        private String currentPluginVersion;
        private String currentMinecraftVersion;
        private String serverName;

        public PluginData(String currentPluginVersion, String currentMinecraftVersion, String serverName) {
            this.currentPluginVersion = currentPluginVersion;
            this.currentMinecraftVersion = currentMinecraftVersion;
            this.serverName = serverName;
        }

        public String getCurrentMinecraftVersion() {
            return currentMinecraftVersion;
        }

        public String getCurrentPluginVersion() {
            return currentPluginVersion;
        }

        public String getServerName() {
            return serverName;
        }
    }
}
