package de.bl4ckskull666.teamspeakbot;

import com.github.theholywaffle.teamspeak3.TS3ApiAsync;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Bot {
    /*private TS3Config _tsConfig = null;
    private TS3Query _tsQuery = null;
    private TS3ApiAsync _tsApiAsync = null;

    private static Bot _bot = null;
    private static int _id = 0;

    private static Map<Integer, ClientInfo> _clients = new HashMap<>();
    private static List<String> _baseClientIDs = new ArrayList<>();
    private static int _topOnline = 0;*/

    public Bot() {
        /*String ip = Main.getConfig().getString("query-ip", "127.0.0.1");
        int port = Main.getConfig().getInt("query-port", 10011);
        String loginName = Main.getConfig().getString("login-name", "");
        String loginPass = Main.getConfig().getString("login-password", "");
        String queryNick = Main.getConfig().getString("query-nickname", "A Bot");
        int serverPort = Main.getConfig().getInt("server-port", 9987);

        if (loginName.isEmpty() || loginPass.isEmpty()) {
            Main.getLogger().log("Login Name und Passwort können nicht leer sein.");
            return;
        }

        _tsConfig = new TS3Config();
        _tsConfig.setHost(ip);
        _tsConfig.setQueryPort(port);
        _tsConfig.setEnableCommunicationsLogging(true);
        _tsConfig.setFloodRate(TS3Query.FloodRate.UNLIMITED);
        _tsQuery = new TS3Query(_tsConfig);
        _tsQuery.connect();

        if (!_tsQuery.isConnected()) {
            Main.getLogger().log("Kann nicht verbunden werden.");
            return;
        } else {
            Main.getLogger().log("Bot connected successful!");
        }

        _tsApiAsync = _tsQuery.getAsyncApi();
        //For Sync
        _tsApiAsync.login(loginName, loginPass);
        _tsApiAsync.selectVirtualServerByPort(serverPort);
        _tsApiAsync.setNickname(queryNick);


        //_tsApiAsync.sendServerMessage(queryNick + " is here!");

        _tsApiAsync.registerAllEvents();
        _tsApiAsync.addTS3Listeners(new Register());

        _bot = this;
        try {
            setMyID();
        } catch(InterruptedException ex) {

        }
        Utils.loadTopOnlineFromDB();
        Utils.loadClientsFromDB();*/
    }

    /*private void setMyID() throws InterruptedException {
        _id = _tsApiAsync.whoAmI().get().getId();
    }

    public TS3ApiAsync getAPI() {
        return _tsApiAsync;
    }

    public static Main getBot() {
        return Main.getInstance();
    }

    public static int getId() {
        return _id;
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
    }*/
}
