package de.bl4ckskull666.teamspeakbot.listeners.async;

import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.*;
import de.bl4ckskull666.teamspeakbot.Main;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class ASyncCommands {
    public static void Do(TextMessageEvent e) throws InterruptedException {
        ClientInfo client = Main.getApiASync().getClientInfo(e.getInvokerId()).get();
        Main.getApiASync().sendPrivateMessage(client.getId(), "Incoming Message: " + e.getMessage()).get();
        if(e.getInvokerId() == Main.getId()) {
            Main.getApiASync().sendPrivateMessage(client.getId(), "Invoker (" + e.getInvokerId() + "/" + client.getId() + ") has Bot ID (" + Main.getId() + ")").get();
            return;
        }

        if(!e.getMessage().startsWith("?")) {
            Main.getLogger().log(e.getInvokerName() + " write " + e.getMessage() + " in " + e.getTargetMode().name());
            return;
        }

        String msg = e.getMessage().substring(1);
        Main.getApiASync().sendPrivateMessage(client.getId(), "Cleared Message: " + msg).get();
        String[] args = msg.split(Pattern.quote(" "));
        Main.getApiASync().sendPrivateMessage(client.getId(), "Using " + args[0]).get();
        Main.getLogger().log(e.getInvokerName() + " use " + msg);
        switch(args[0].toLowerCase()) {
            case "ghosts": //List the Ghosts Channels with Name
                isAdmin(client);
                List<Channel> ghosts = new ArrayList<>();
                for(Channel channel: Main.getApiASync().getChannels().get()) {
                    if(channel.getNeededSubscribePower() > 0) {
                        ghosts.add(channel);
                    }
                }

                if(ghosts.isEmpty()) {
                    Main.getApiASync().sendPrivateMessage(client.getId(), "No Ghosts Channel found!").get();
                    break;
                }

                Main.getApiASync().sendPrivateMessage(client.getId(), "Folgende Channel haben ein Abo Level Höher als 0").get();
                for(Channel channel: ghosts) {
                    Main.getApiASync().sendPrivateMessage(client.getId(), "- " + channel.getName() + ", Level " + channel.getNeededSubscribePower()).get();
                }
                break;
            case "inaktiv": //List all Channels who is longer as 2 Weeks no one in.
                Main.getApiASync().sendPrivateMessage(client.getId(), "inaktiv ist noch inaktiv ^^").get();
                break;
            case "shutdown":
                System.exit(0);
                break;
            default:
                Main.getApiASync().sendPrivateMessage(client.getId(), "Unknown Command!!!!").get();
                break;
        }
    }

    public static boolean isAdmin(ClientInfo client) throws InterruptedException {
        if(client.getBoolean("b_channel_create_child")) {
            Main.getApiASync().sendPrivateMessage(client.getId(), "Du hast Properties!").get();
        }
        return false;
    }
}
