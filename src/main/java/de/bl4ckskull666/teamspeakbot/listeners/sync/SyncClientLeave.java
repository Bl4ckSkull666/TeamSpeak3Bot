package de.bl4ckskull666.teamspeakbot.listeners.sync;

import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.bl4ckskull666.teamspeakbot.Main;

public class SyncClientLeave {
    public static void Do(ClientLeaveEvent e) {
        ClientInfo info = Main.getClientInfo(e.getClientId(), true);
        //Channel ID --> e.getClientFromId()
        if(info != null)
            Main.getLogger().log("User " + info.getNickname() + " leave the Server.");
    }
}
