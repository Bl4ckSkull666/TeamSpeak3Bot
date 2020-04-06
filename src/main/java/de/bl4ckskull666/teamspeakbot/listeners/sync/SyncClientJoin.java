package de.bl4ckskull666.teamspeakbot.listeners.sync;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.exception.TS3Exception;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.bl4ckskull666.teamspeakbot.Main;
import de.bl4ckskull666.teamspeakbot.classes.Database;
import de.bl4ckskull666.teamspeakbot.classes.Utils;

public class SyncClientJoin {
    public static void Do(ClientJoinEvent e) {
        //e.getClientNickname()
        //e.getClientId()
        ClientInfo client;
        try {
            client = Main.getApiSync().getClientInfo(e.getClientId());
            if (!client.isRegularClient())
                return;
        } catch(TS3Exception ex) {
            return;
        }

        int channelID = client.getChannelId();
        ChannelInfo curChannel = Main.getApiSync().getChannelInfo(channelID);

        //For Sync
        while (channelID > 0) {
            int tmp = Main.getApiSync().getChannelInfo(channelID).getParentChannelId();
            if (tmp <= 0)
                break;

            channelID = tmp;
        }
        ChannelInfo channel = Main.getApiSync().getChannelInfo(channelID);

        String seen = Utils.getTimeString();
        Database.execute(Utils.QueryReplacements(Database.getInsertChannelQuery(), client, curChannel, channel, seen));
        if(curChannel != channel)
            Database.execute(Utils.QueryReplacements(Database.getInsertChannelQuery(), client, channel, null, seen));
        Main.getLogger().log("User " + client.getNickname() + " (" + client.getBase64ClientUId() + ") joined in Channel " + curChannel.getName() + " (" + curChannel.getId() + ")");


        Main.addClientInfo(client);
        isUserNew(client);
        UserTotalCheck();
    }

    private static void isUserNew(ClientInfo info) {
        if(Main.addBaseClientId(info.getBase64ClientUId())) {
            Database.execute(Utils.QueryReplacements(Database.getInsertClientQuery(), info.getBase64ClientUId()));
            Main.getApiSync().sendServerMessage("Aufgewacht, Aufgewacht!!! " + info.getNickname() + " ist neu eingetroffen auf dem Server.");
        }
    }

    private static void UserTotalCheck() {
        int i = 0;
        String clients = "";
        for(Client client: Main.getApiSync().getClients()) {
            if(!client.isRegularClient())
                continue;

            i++;
            if(!clients.isEmpty())
                clients += ",";
            clients += client.getNickname();
        }
        Database.execute(Utils.QueryReplacements(Database.getUpdateTopClientsQuery(), i, clients));

        if(Main.setTopOnline(i)) {
            Main.getApiSync().sendServerMessage("[B][I]Es gibt einen neuen Sprecher Rekord auf unserem TeamSpeak 3 Server. Der neue Rekord liegt bei " + i + " Sprechern auf einmal. *hype*[/I][/B]");
        }
    }
}
