package de.bl4ckskull666.teamspeakbot;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.reconnect.ReconnectStrategy;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.bl4ckskull666.teamspeakbot.classes.*;
import de.bl4ckskull666.teamspeakbot.listeners.async.ASyncRegister;
import de.bl4ckskull666.teamspeakbot.listeners.sync.SyncRegister;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {
    private static Config _conf = null;
    private static MyLogger _logger = null;

    private static Thread _botThread = null;
    private static TS3Config _tsConfig = null;
    private static TS3Query _tsQuery = null;
    private static TS3Api _tsApiSync = null;
    private static TS3ApiAsync _tsApiAsync = null;

    private static ScheduleTask _reconnectTask = null;

    public static void main(String[] args) {
        _logger = new MyLogger();
        _conf = new Config();
        if(!Database.hasMySQLDriver())
            _logger.log("Can't connect to MySQL Database or missing MySQL Driver!");

        Thread ramChecker = new Thread(new ThreadChecker());
        ramChecker.start();
        Thread console = new Thread(new ConsoleReader());
        console.start();

        Utils.loadTopOnlineFromDB();
        Utils.loadClientsFromDB();

        //Start Bot now here
        _botThread = new Thread(new RunBot());
        _botThread.start();
    }

    private static void startSyncBot() {
        String loginName = _conf.getString("login-name", "");
        String loginPass = _conf.getString("login-password", "");
        String queryNick = _conf.getString("query-nickname", "A Bot");
        int serverPort = _conf.getInt("server-port", 9987);

        _tsApiSync = _tsQuery.getApi();
        _tsApiSync.login(loginName, loginPass);
        _tsApiSync.selectVirtualServerByPort(serverPort);
        _tsApiSync.setNickname(queryNick);

        _tsApiSync.registerAllEvents();
        _tsApiSync.addTS3Listeners(new SyncRegister());

        _botId = _tsApiSync.whoAmI().getId();
    }

    private static void startASyncBot() throws InterruptedException {
        String loginName = _conf.getString("login-name", "");
        String loginPass = _conf.getString("login-password", "");
        String queryNick = _conf.getString("query-nickname", "A Bot");
        int serverPort = _conf.getInt("server-port", 9987);

        _tsApiAsync = _tsQuery.getAsyncApi();
        _tsApiAsync.login(loginName, loginPass).get();
        _tsApiAsync.selectVirtualServerByPort(serverPort).get();
        _tsApiAsync.setNickname(queryNick).get();

        _tsApiAsync.registerAllEvents().get();
        _tsApiAsync.addTS3Listeners(new ASyncRegister());

        _botId = _tsApiAsync.whoAmI().get().getId();
    }

    public static TS3ApiAsync getApiASync() {
        return _tsApiAsync;
    }

    public static TS3Api getApiSync() {
        return _tsApiSync;
    }

    private static int _botId = 0;
    private static Map<Integer, ClientInfo> _clients = new HashMap<>();
    private static List<String> _baseClientIDs = new ArrayList<>();
    private static int _topOnline = 0;

    public static int getId() {
        return _botId;
    }

    public static int getTopOnline() {
        return _topOnline;
    }

    public static boolean setTopOnline(int i) {
        if(_topOnline < i) {
            _topOnline = i;
            return true;
        }
        return false;
    }

    public static void addClientInfo(ClientInfo info) {
        _clients.put(info.getId(), info);
    }

    public static ClientInfo getClientInfo(int id, boolean remove) {
        if(!_clients.containsKey(id))
            return null;

        ClientInfo info = _clients.get(id);
        if(remove)
            _clients.remove(id);
        return info;
    }

    public static boolean addBaseClientId(String id) {
        if(_baseClientIDs.contains(id))
            return false;

        _baseClientIDs.add(id);
        return true;
    }

    public static Config getConfig() {
        return _conf;
    }

    public static MyLogger getLogger() {
        return _logger;
    }

    public static class RunBot implements Runnable {
        @Override
        public void run() {
            String ip = Main.getConfig().getString("query-ip", "127.0.0.1");
            int port = Main.getConfig().getInt("query-port", 10011);
            String loginName = Main.getConfig().getString("login-name", "");
            String loginPass = Main.getConfig().getString("login-password", "");

            if (loginName.isEmpty() || loginPass.isEmpty()) {
                Main.getLogger().log("Login Name und Passwort können nicht leer sein.");
                return;
            }

            _tsConfig = new TS3Config();
            _tsConfig.setHost(ip);
            _tsConfig.setQueryPort(port);
            _tsConfig.setEnableCommunicationsLogging(true);
            _tsConfig.setFloodRate(TS3Query.FloodRate.UNLIMITED);
            _tsConfig.setReconnectStrategy(ReconnectStrategy.constantBackoff());
            _tsQuery = new TS3Query(_tsConfig);
            _tsQuery.connect();

            if (!_tsQuery.isConnected()) {
                Main.getLogger().log("Kann nicht verbunden werden.");
                return;
            } else {
                Main.getLogger().log("Bot connected successful!");
            }

            if(_conf.getBoolean("use-sync", true)) {
                startSyncBot();
                Main.getLogger().log("Bot started Syncron!");
            } else {
                try {
                    startASyncBot();
                    Main.getLogger().log("Bot started Asyncron!");
                } catch (InterruptedException ex) {
                }
            }

            //_reconnectTask = new ScheduleTask(new BotReload(), 60000,60000);
        }
    }

    public static class BotReload implements Runnable {
        @Override
        public void run() {
            if(_tsQuery.isConnected())
                _tsQuery.exit();

            _tsQuery = new TS3Query(_tsConfig);
            _tsQuery.connect();
            if(_conf.getBoolean("use-sync", true)) {
                startSyncBot();
            } else {
                try {
                    startASyncBot();
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    public static class ConsoleReader implements Runnable {
        @Override
        public void run() {
            String line;
            while(!(line = System.console().readLine()).isEmpty()) {
                Main.getLogger().log("Input: " + line);
                if(line.toLowerCase().equalsIgnoreCase("?shutdown")) {
                    System.exit(0);
                }
            }
        }
    }

    public static class ThreadChecker implements Runnable {
        int _i = 0;

        @Override
        public void run() {
            while(true) {
                if(_i == 30) {
                    System.gc();
                    Runtime rt = Runtime.getRuntime();
                    _logger.log("Memory usage " + getMB(rt.maxMemory() - rt.freeMemory()) + " of " + getMB(rt.maxMemory()));
                    if(((100/rt.maxMemory())*rt.freeMemory()) > 5)
                        System.exit(0);
                    _i = 0;
                }

                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                _i++;
            }
        }


        private String getMB(long b) {
            double mb = b/(1024*1024);
            DecimalFormat df = new DecimalFormat("#,##0");
            return df.format(mb) + " MB";
        }
    }
}
