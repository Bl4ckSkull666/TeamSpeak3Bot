package de.bl4ckskull666.teamspeakbot.listeners.async;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.exception.TS3Exception;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.bl4ckskull666.teamspeakbot.Main;
import de.bl4ckskull666.teamspeakbot.classes.Database;
import de.bl4ckskull666.teamspeakbot.classes.Utils;

public class ASyncClientJoin {
    public static void Do(ClientJoinEvent e) throws InterruptedException {
        //e.getClientNickname()
        //e.getClientId()
        ClientInfo client;
        try {
            client = Main.getApiASync().getClientInfo(e.getClientId()).get();
            if (!client.isRegularClient())
                return;
        } catch(TS3Exception ex) {
            return;
        }

        int channelID = client.getChannelId();
        ChannelInfo curChannel = Main.getApiASync().getChannelInfo(channelID).get();

        //For Sync
        while (channelID > 0) {
            int tmp = Main.getApiASync().getChannelInfo(channelID).get().getParentChannelId();
            if (tmp <= 0)
                break;

            channelID = tmp;
        }
        ChannelInfo channel = Main.getApiASync().getChannelInfo(channelID).get();

        String seen = Utils.getTimeString();
        Database.execute(Utils.QueryReplacements(Database.getInsertChannelQuery(), client, curChannel, channel, seen));
        if(curChannel != channel)
            Database.execute(Utils.QueryReplacements(Database.getInsertChannelQuery(), client, channel, null, seen));
        Main.getLogger().log("User " + client.getNickname() + " (" + client.getBase64ClientUId() + ") joined in Channel " + curChannel.getName() + " (" + curChannel.getId() + ")");


        Main.addClientInfo(client);
        isUserNew(client);
        UserTotalCheck();
    }

    private static void isUserNew(ClientInfo info) throws InterruptedException {
        if(Main.addBaseClientId(info.getBase64ClientUId())) {
            Database.execute(Utils.QueryReplacements(Database.getInsertClientQuery(), info.getBase64ClientUId()));
            Main.getApiASync().sendServerMessage("Aufgewacht, Aufgewacht!!! " + info.getNickname() + " ist neu eingetroffen auf dem Server.").get();
        }
    }

    private static void UserTotalCheck() throws InterruptedException {
        int i = 0;
        String clients = "";
        for(Client client: Main.getApiASync().getClients().get()) {
            if(!client.isRegularClient())
                continue;

            i++;
            if(!clients.isEmpty())
                clients += ",";
            clients += client.getNickname();
        }
        Database.execute(Utils.QueryReplacements(Database.getUpdateTopClientsQuery(), i, clients));

        if(Main.setTopOnline(i)) {
            Main.getApiASync().sendServerMessage("[B][I]Es gibt einen neuen Sprecher Rekord auf unserem TeamSpeak 3 Server. Der neue Rekord liegt bei " + i + " Sprechern auf einmal. *hype*[/I][/B]").get();
        }
    }
}
