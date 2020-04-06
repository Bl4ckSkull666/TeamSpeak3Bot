package de.bl4ckskull666.teamspeakbot.listeners.async;

import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.bl4ckskull666.teamspeakbot.Main;

public class ASyncClientLeave {
    public static void Do(ClientLeaveEvent e) {
        ClientInfo info = Main.getClientInfo(e.getClientId(), true);
        //Channel ID --> e.getClientFromId()
        if(info != null)
            Main.getLogger().log("User " + info.getNickname() + " leave the Server.");
    }
}
