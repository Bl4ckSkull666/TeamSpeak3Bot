package de.bl4ckskull666.teamspeakbot.listeners.async;

import com.github.theholywaffle.teamspeak3.api.event.TextMessageEvent;
import com.github.theholywaffle.teamspeak3.api.wrapper.*;
import de.bl4ckskull666.teamspeakbot.Main;
import de.bl4ckskull666.teamspeakbot.classes.Database;
import de.bl4ckskull666.teamspeakbot.classes.ScheduleTask;
import de.bl4ckskull666.teamspeakbot.classes.Utils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

public class ASyncCommands {
    public static void Do(TextMessageEvent e) throws InterruptedException {
        if(e == null) {
            Main.getLogger().log("Stop command while event is null!");
            return;
        } else if(e.getInvokerId() <= 0 || e.getInvokerId() == 512) {
            Main.getLogger().log("Stop Command while ID is " + String.valueOf(e.getInvokerId()) + "!");
            return;
        } else if(e.getMessage() == null || e.getMessage().isEmpty()) {
            Main.getLogger().log("Stop Command while Message os null or empty!");
            return;
        }

        if(e.getInvokerId() == Main.getId()) {
            return;
        }

        if(!e.getMessage().startsWith("?")) {
            return;
        }

        String msg = e.getMessage().substring(1);
        String[] args = msg.split(Pattern.quote(" "));

        switch(args[0].toLowerCase()) {
            case "ghosts": //List the Ghosts Channels with Name
                new ScheduleTask(new sendGhostCommand(e.getInvokerId()));
                break;
            case "inaktiv": //List all Channels who is longer as 2 Weeks no one in.
                new ScheduleTask(new sendInaktivCommand(e.getInvokerId()));
                break;
            case "shutdown":
                System.exit(0);
                break;
            default:
                new ScheduleTask(new sendUnknownCommand(e.getInvokerId()));
                break;
        }
    }

    public static boolean isAdmin(ClientInfo client) throws InterruptedException {
        if(client.getBoolean("b_channel_create_child")) {
            Main.getApiASync().sendPrivateMessage(client.getId(), "Du hast Properties!").get();
        }
        return false;
    }

    public static class sendGhostCommand implements Runnable {
        private final int _senderID;

        public sendGhostCommand(int senderID) {
            _senderID = senderID;
        }

        @Override
        public void run() {
            try {
                List<Channel> ghosts = new ArrayList<>();
                for (Channel channel : Main.getApiASync().getChannels().get()) {
                    if (channel.getNeededSubscribePower() > 0) {
                        ghosts.add(channel);
                    }
                }

                if (ghosts.isEmpty()) {
                    Main.getApiASync().sendPrivateMessage(_senderID, "No Ghosts Channel found!").get();
                    return;
                }

                Main.getApiASync().sendPrivateMessage(_senderID, "Folgende Channel haben ein Abo Level Höher als 0").get();
                for (Channel channel : ghosts) {
                    Main.getApiASync().sendPrivateMessage(_senderID, "- " + channel.getName() + ", Level " + channel.getNeededSubscribePower()).get();
                }
            } catch(InterruptedException ex) {

            }
        }
    }

    public static class sendInaktivCommand implements Runnable {
        private final int _senderID;

        public sendInaktivCommand(int senderID) {
            _senderID = senderID;
        }

        @Override
        public void run() {
            try {
                List<Map<String, Object>> results = Database.executeMultiSelectQuery(Database.getInaktiveChannelQuery());
                if(results.isEmpty()) {
                    Main.getApiASync().sendPrivateMessage(_senderID, "Keine inaktiven Channel gefunden").get();
                    return;
                }

                Main.getApiASync().sendPrivateMessage(_senderID, results.size() + " inaktive Kanäle gefunden:").get();
                Main.getApiASync().sendPrivateMessage(_senderID, "Kanalname | Benutzername | zuletzt genutzt").get();
                for(Map<String, Object> me: results) {
                    if(!me.containsKey("channelName") || !me.containsKey("lastUse") || !me.containsKey("lastUser"))
                        continue;

                    String channel = String.valueOf(me.get("channelName"));
                    String lastuse = String.valueOf(me.get("lastUse"));
                    String user = String.valueOf(me.get("lastUser"));
                    Main.getApiASync().sendPrivateMessage(_senderID, channel + " | " + user + " | " + Utils.formatSQLDate(lastuse)).get();
                }
            } catch(InterruptedException ex) {

            }
        }
    }

    public static class sendUnknownCommand implements Runnable {
        private final int _senderID;

        public sendUnknownCommand(int senderID) {
            _senderID = senderID;
        }

        @Override
        public void run() {
            try {
                Main.getApiASync().sendPrivateMessage(_senderID, "Unknown Command!!!!").get();
            } catch(InterruptedException ex) {

            }
        }
    }
}
