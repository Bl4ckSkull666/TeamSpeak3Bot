package de.bl4ckskull666.teamspeakbot.listeners.async;

import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.bl4ckskull666.teamspeakbot.Main;
import de.bl4ckskull666.teamspeakbot.classes.Database;
import de.bl4ckskull666.teamspeakbot.classes.Utils;

public class ASyncClientMove {
    public static void Do(ClientMovedEvent e) throws InterruptedException {
        if(e.getClientId() == Main.getId())
            return;

        int channelID = e.getTargetChannelId();
        ChannelInfo curChannel = Main.getApiASync().getChannelInfo(e.getTargetChannelId()).get();
        //For Sync
        while(channelID > 0) {
            int tmp =  Main.getApiASync().getChannelInfo(channelID).get().getParentChannelId();
            if(tmp <= 0)
                break;

            channelID = tmp;
        }
        ClientInfo client = Main.getApiASync().getClientInfo(e.getClientId()).get();
        ChannelInfo channel = Main.getApiASync().getChannelInfo(channelID).get();

        String seen = Utils.getTimeString();
        Database.execute(Utils.QueryReplacements(Database.getInsertChannelQuery(), client, curChannel, channel, seen));
        if(curChannel != channel)
            Database.execute(Utils.QueryReplacements(Database.getInsertChannelQuery(), client, channel, null, seen));
        Main.getLogger().log("User " + client.getNickname() + " (" + client.getBase64ClientUId() + ") moved to Channel " + curChannel.getName() + " (" + curChannel.getId() + ")");
    }
}