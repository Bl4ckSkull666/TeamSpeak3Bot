package de.bl4ckskull666.teamspeakbot.classes;

import de.bl4ckskull666.teamspeakbot.Main;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

public class Config {
    private final HashMap<String, Object> _configs = new HashMap<>();
    private final File _file;
    private long _lastModify = 0;

    public Config() {
        _file = new File("config.bot");
        load();
        _lastModify = _file.lastModified();
    }

    private void createConfig() {
        List<String> lines = new ArrayList<>();
        lines.add("use-sync=[boolean]true");
        lines.add("query-ip=[string]127.0.0.1");
        lines.add("query-port=[int]10011");
        lines.add("query-nickname=[string]Putput Bot");
        lines.add("login-name=[string]serveradmin");
        lines.add("login-password=[string]myPassword");
        lines.add("server-port=[string]9987");
        lines.add("mysql-host=[string]127.0.0.1");
        lines.add("mysql-port=[int]3306");
        lines.add("mysql-user=[string]root");
        lines.add("mysql-password=[string]root");
        lines.add("mysql-database=[string]teamspeak3");

        try {
            BufferedWriter bw = new BufferedWriter(new FileWriter(_file));
            for(String line: lines) {
                bw.write(line);
                bw.newLine();
            }
            bw.close();
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    public void load() {
        _configs.clear();

        if(!_file.exists())
            createConfig();

        BufferedReader reader;
        try {
            reader = new BufferedReader(new FileReader(_file));

            String line = null;
            while((line = reader.readLine()) != null) {
                if (line.isEmpty() || line.startsWith("#"))
                    continue;

                String[] args = line.split("=", 2);
                if(args[1].toLowerCase().startsWith("[list]")) {
                    args[1] = args[1].substring(6);
                    String[] arg = args[1].split(Pattern.quote("|"));
                    List<String> myList = new ArrayList<>();
                    for(String ar : arg)
                        myList.add(formatString(ar));

                    _configs.put(args[0], myList);
                } else if(args[1].toLowerCase().startsWith("[string]")) {
                    args[1] = args[1].substring(8);
                    _configs.put(args[0], formatString(args[1]));
                } else if(args[1].toLowerCase().startsWith("[int]")) {
                    args[1] = args[1].substring(5);
                    try {
                        int i = Integer.parseInt(args[1]);
                        _configs.put(args[0], i);
                    } catch(Exception exInt) {
                        Main.getLogger().log(args[1] + " in " + args[0] + " is not a Integer");
                    }
                } else if(args[1].toLowerCase().startsWith("[double]")) {
                    args[1] = args[1].substring(8);
                    try {
                        double d = Double.parseDouble(args[1]);
                        _configs.put(args[0], d);
                    } catch (Exception exInt) {
                        Main.getLogger().log(args[1] + " in " + args[0] + " is not a Double");
                    }
                } else if(args[1].toLowerCase().startsWith("[long]")) {
                    args[1] = args[1].substring(6);
                    try {
                        long l = Long.parseLong(args[1]);
                        _configs.put(args[0], l);
                    } catch (Exception exInt) {
                        Main.getLogger().log(args[1] + " in " + args[0] + " is not a Long");
                    }
                } else if(args[1].toLowerCase().startsWith("[boolean]")) {
                    args[1] = args[1].substring(9);
                    try {
                        boolean b = Boolean.parseBoolean(args[1]);
                        _configs.put(args[0], b);
                    } catch (Exception exInt) {
                        Main.getLogger().log(args[1] + " in " + args[0] + " is not a boolean");
                    }
                } else {
                    Main.getLogger().log("Wrong config by " + line + " - " + args[0] + " - " + args[1]);
                }
            }
        } catch(IOException ex) {
            ex.printStackTrace();
        }
    }

    private void checkConfig() {
        if(_file.lastModified() > _lastModify) {
            load();
            _lastModify = _file.lastModified();
            System.out.println("Reloaded Configuration File.");
        }
    }

    public String getString(String path, String def) {
        checkConfig();
        if(!_configs.containsKey(path))
            return def;

        Object obj = _configs.get(path);
        if(!(obj instanceof String))
            return def;
        return (String)obj;
    }

    public int getInt(String path, int def) {
        checkConfig();
        if(!_configs.containsKey(path))
            return def;

        Object obj = _configs.get(path);
        if(!(obj instanceof Integer))
            return def;
        return (int)obj;
    }

    public double getDouble(String path, double def) {
        checkConfig();
        if(!_configs.containsKey(path))
            return def;

        Object obj = _configs.get(path);
        if(!(obj instanceof Double))
            return def;
        return (double)obj;
    }

    public long getLong(String path, long def) {
        checkConfig();
        if(!_configs.containsKey(path))
            return def;

        Object obj = _configs.get(path);
        if(!(obj instanceof Long))
            return def;
        return (long)obj;
    }

    public boolean getBoolean(String path, boolean def) {
        checkConfig();
        if(!_configs.containsKey(path))
            return def;

        Object obj = _configs.get(path);
        if(!(obj instanceof Boolean))
            return def;
        return (boolean)obj;
    }

    public List<String> getList(String path, List<String> def) {
        checkConfig();
        if(!_configs.containsKey(path))
            return def;

        Object obj = _configs.get(path);
        if(!(obj instanceof List))
            return def;
        return (List<String>)obj;
    }

    public List<String> getKeys(String path) {
        checkConfig();
        List<String> tmps = new ArrayList<>();
        for(String str: _configs.keySet()) {
            if(str.startsWith(path))
                tmps.add(str);
        }
        return tmps;
    }

    public static String formatString(String str) {
        byte[] ptext = str.getBytes();
        return new String(ptext, StandardCharsets.UTF_8);
    }
}

