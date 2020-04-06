package de.bl4ckskull666.teamspeakbot.listeners.sync;

import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.Channel;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.bl4ckskull666.teamspeakbot.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class SyncCommands {
    public static void Do(TextMessageEvent e) {
        ClientInfo client = Main.getApiSync().getClientInfo(e.getInvokerId());
        Main.getApiSync().sendPrivateMessage(client.getId(), "Incoming Message: " + e.getMessage());
        if(e.getInvokerId() == Main.getId()) {
            Main.getApiSync().sendPrivateMessage(client.getId(), "Invoker (" + e.getInvokerId() + "/" + client.getId() + ") has Bot ID (" + Main.getId() + ")");
            return;
        }

        if(!e.getMessage().startsWith("?")) {
            Main.getLogger().log(e.getInvokerName() + " write " + e.getMessage() + " in " + e.getTargetMode().name());
            return;
        }

        String msg = e.getMessage().substring(1);
        Main.getApiSync().sendPrivateMessage(client.getId(), "Cleared Message: " + msg);
        String[] args = msg.split(Pattern.quote(" "));
        Main.getApiSync().sendPrivateMessage(client.getId(), "Using " + args[0]);
        Main.getLogger().log(e.getInvokerName() + " use " + msg);
        switch(args[0].toLowerCase()) {
            case "ghosts": //List the Ghosts Channels with Name
                isAdmin(client);
                List<Channel> ghosts = new ArrayList<>();
                for(Channel channel: Main.getApiSync().getChannels()) {
                    if(channel.getNeededSubscribePower() > 0) {
                        ghosts.add(channel);
                    }
                }

                if(ghosts.isEmpty()) {
                    Main.getApiSync().sendPrivateMessage(client.getId(), "No Ghosts Channel found!");
                    break;
                }

                Main.getApiSync().sendPrivateMessage(client.getId(), "Folgende Channel haben ein Abo Level Höher als 0");
                for(Channel channel: ghosts) {
                    Main.getApiSync().sendPrivateMessage(client.getId(), "- " + channel.getName() + ", Level " + channel.getNeededSubscribePower());
                }
                break;
            case "inaktiv": //List all Channels who is longer as 2 Weeks no one in.
                Main.getApiSync().sendPrivateMessage(client.getId(), "inaktiv ist noch inaktiv ^^");
                break;
            case "shutdown":
                System.exit(0);
                break;
            default:
                Main.getApiSync().sendPrivateMessage(client.getId(), "Unknown Command!!!!");
                break;
        }
    }

    public static boolean isAdmin(ClientInfo client) {
        if(client.getBoolean("b_channel_create_child")) {
            Main.getApiSync().sendPrivateMessage(client.getId(), "Du hast Properties!");
        }
        return false;
    }
}
