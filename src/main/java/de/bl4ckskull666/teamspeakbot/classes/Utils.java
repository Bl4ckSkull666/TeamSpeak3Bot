package de.bl4ckskull666.teamspeakbot.classes;

import com.github.theholywaffle.teamspeak3.api.wrapper.ChannelInfo;
import com.github.theholywaffle.teamspeak3.api.wrapper.ClientInfo;
import de.bl4ckskull666.teamspeakbot.Main;

import java.util.Calendar;
import java.util.List;
import java.util.Map;

public class Utils {
    public static String QueryReplacements(String query, ClientInfo client, ChannelInfo channel, ChannelInfo parent, String seen) {
        query = query.replaceAll("%channelID", String.valueOf(channel.getId()));
        query = query.replaceAll("%channelName", channel.getName());
        query = query.replaceAll("%lastUser", client.getNickname());
        query = query.replaceAll("%lastUse", seen);
        int parentID = 0;
        String parentName = "";
        if(parent != null && channel != parent) {
            parentID = parent.getId();
            parentName = parent.getName();
        }
        query = query.replaceAll("%parentID", String.valueOf(parentID));
        query = query.replaceAll("%parentName", parentName);
        return query;
    }

    public static String QueryReplacements(String query, int count, String userlist) {
        query = query.replaceAll("%topUsers", String.valueOf((count)));
        query = query.replaceAll("%userList", userlist);
        return query;
    }

    public static String QueryReplacements(String query, String id) {
        query = query.replaceAll("%id", id);
        return query;
    }

    public static String getTimeString() {
        Calendar cal = Calendar.getInstance();
        String mon = String.valueOf(cal.get(Calendar.MONTH) + 1);
        if((cal.get(Calendar.MONTH) + 1) < 10)
            mon = "0" + mon;

        String day = String.valueOf(cal.get(Calendar.DAY_OF_MONTH));
        if(cal.get(Calendar.DAY_OF_MONTH) < 10)
            day = "0" + day;

        String hou = String.valueOf(cal.get(Calendar.HOUR_OF_DAY));
        if(cal.get(Calendar.HOUR_OF_DAY) < 10)
            hou = "0" + hou;

        String min = String.valueOf(cal.get(Calendar.MINUTE));
        if(cal.get(Calendar.MINUTE) < 10)
            min = "0" + min;

        String sec = String.valueOf(cal.get(Calendar.SECOND));
        if(cal.get(Calendar.SECOND) < 10)
            sec = "0" + sec;

        return cal.get(Calendar.YEAR) + "-" + mon + "-" + day + " " + hou + ":" + min + ":" + sec;
    }

    public static void loadTopOnlineFromDB() {
        List<Map<String, Object>> tmp = Database.executeMultiSelectQuery(Database.getTopClientsQuery());
        if(!tmp.isEmpty()) {
            for(Map<String, Object> me: tmp) {
                if(!me.containsKey("topUsers"))
                    continue;

                try {
                    Main.setTopOnline((int)me.get("topUsers"));
                } catch(Exception ex) { }
            }
        }
    }

    public static void loadClientsFromDB() {
        List<Map<String, Object>> tmp = Database.executeMultiSelectQuery(Database.getAllClientQuery());
        if(tmp.isEmpty())
            return;

        for(Map<String, Object> me: tmp) {
            if(!me.containsKey("id"))
                continue;

            Main.addBaseClientId((String)me.get("id"));
        }
    }
}
