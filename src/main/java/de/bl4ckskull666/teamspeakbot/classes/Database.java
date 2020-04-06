package de.bl4ckskull666.teamspeakbot.classes;

import de.bl4ckskull666.teamspeakbot.Main;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.mysql.jdbc.Driver;

public final class Database {
    private static boolean _use = false;
    private static String _host = "";
    private static int _port = 0;
    private static String _user = "";
    private static String _pass = "";
    private static String _data = "";
    private static String _prefix = "";

    public static boolean hasMySQLDriver() {
        try {
            Driver.getPlatform();
            Connection con = getConnection();
            if(con != null) {
                checkTables(con);
                _use = true;
                close(con);
                return true;
            }
            return false;
        } catch (Exception t) {
            Main.getLogger().log("Can't find MySQL Driver!");
            Main.getLogger().log(t.getLocalizedMessage());
            return false;
        }
    }

    public static Connection getConnection() {
        if(_host.isEmpty() || _port == 0 || _user.isEmpty() || _pass.isEmpty() || _data.isEmpty()) {
            _host = Main.getConfig().getString("mysql-host", "127.0.0.1");
            _port = Main.getConfig().getInt("mysql-port", 0);
            _user = Main.getConfig().getString("mysql-user", "root");
            _pass = Main.getConfig().getString("mysql-password", "root");
            _data = Main.getConfig().getString("mysql-database", "teamspeak");
            _prefix = Main.getConfig().getString("mysql-prefix", "ts3bot_");
        }

        if(_host.isEmpty() || _port == 0 || _user.isEmpty() || _pass.isEmpty() || _data.isEmpty())
            return null;

        try {
            return DriverManager.getConnection("jdbc:mysql://" + _host + ":" + _port + "/" + _data, _user, _pass);
        } catch (SQLException e) {
            Main.getLogger().log("Can't connect to MySQL Server!");
            Main.getLogger().log(e.getLocalizedMessage());
            return null;
        }
    }

    public static boolean execute(String query) {
        if(!_use)
            return false;

        Connection con = getConnection();
        if(con == null)
            return false;

        boolean bol = executeInsUpDelQuery(con, query);
        if(bol)
            close(con);
        return bol;
    }

    private static boolean executeInsUpDelQuery(Connection con, String query) {
        try (PreparedStatement statement = con.prepareStatement(query.replaceAll("%prefix%", _prefix))) {
            statement.execute();
            statement.close();
        } catch (SQLException e) {
            Main.getLogger().log("Error on running insert/update/delete query " + query + ".");
            Main.getLogger().log(e.getLocalizedMessage());
            return false;
        } finally {
            return true;
        }
    }

    public static List<Map<String, Object>> executeMultiSelectQuery(String query) {
        if(!_use)
            return null;

        Connection con = getConnection();
        if(con == null)
            return null;

        ArrayList<Map<String, Object>> tmp = new ArrayList<>();
        try {
            PreparedStatement statement = con.prepareStatement(query.replaceAll("%prefix%", _prefix));
            ResultSet rs = statement.executeQuery();
            ResultSetMetaData rsmd = rs.getMetaData();
            int cc = rsmd.getColumnCount();
            while(rs.next()) {
                Map<String, Object> t = new HashMap<>();
                for(int r = 1; r <= cc; r++)
                    t.put(rsmd.getColumnName(r), rs.getObject(r));
                tmp.add(t);
            }
            rs.close();
            statement.close();
        } catch (SQLException e) {
            Main.getLogger().log("Error on running multiple select query " + query + ".");
            Main.getLogger().log(e.getLocalizedMessage());
        }
        close(con);
        return tmp;
    }

    public static void close(Connection con) {
        try {
            con.close();
        } catch (SQLException e) {
            Main.getLogger().log("Error on close MySQL connection.");
            Main.getLogger().log(e.getLocalizedMessage());
        }
    }

    private static void checkTables(Connection con) {
        try {
            PreparedStatement statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + _prefix + "channels` ("
                 + "`channelID` int(11) NOT NULL,"
                 + "`channelName` varchar(255) DEFAULT NULL,"
                 + "`lastUse` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                 + "`lastUser` varchar(255) DEFAULT NULL,"
                 + "`parentChannelID` int(11) DEFAULT NULL,"
                 + "`parentChannelName` varchar(255) DEFAULT NULL,"
                 + "PRIMARY KEY (`channelID`)"
                 + ") ENGINE=MyISAM DEFAULT CHARSET=latin1;"
            );
            statement.execute();
            statement.close();
        } catch(SQLException ex) {
            Main.getLogger().log("Cant create Table " + _prefix + "channels");
            Main.getLogger().log(ex.getMessage());
        }

        try {
            PreparedStatement statement = con.prepareStatement("CREATE TABLE `" + _prefix + "topClients` ("
                    + "`topUsers` int(11) NOT NULL,"
                    + "`userList` longtext NOT NULL,"
                    + "`seen` datetime DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,"
                    + "PRIMARY KEY (`topUsers`)"
                    + ") ENGINE=MyISAM DEFAULT CHARSET=latin1;"
            );
            statement.execute();
            statement.close();
            executeInsUpDelQuery(con, "INSERT INTO `" + _prefix + "topClients` (`topUsers`, `userList`) VALUES ('0', '')");
        } catch(SQLException ex) {
            if(ex.getErrorCode() != 1050) {
                Main.getLogger().log("Cant create Table " + _prefix + "topClients");
                Main.getLogger().log(ex.getMessage());
            }
        }

        try {
            PreparedStatement statement = con.prepareStatement("CREATE TABLE IF NOT EXISTS `" + _prefix + "clients` ("
                    + "`id` varchar(64) NOT NULL,"
                    + "PRIMARY KEY (`id`(40))"
                    + ") ENGINE=MyISAM DEFAULT CHARSET=latin1;"
            );
            statement.execute();
            statement.close();
        } catch(SQLException ex) {
            Main.getLogger().log("Cant create Table " + _prefix + "clients");
            Main.getLogger().log(ex.getMessage());
        }
    }

    public static String getInsertChannelQuery() {
        String query = "INSERT INTO `%prefix%channels` ";
        query += "(`channelID`,`channelName`,`lastUse`,`lastUser`,`parentChannelID`,`parentChannelName`)";
        query += " VALUES ";
        query += "('%channelID','%channelName','%lastUse','%lastUser','%parentID','%parentName')";
        query += " on DUPLICATE KEY UPDATE `channelName` = '%channelName', `lastUse` = '%lastUse', `lastUser` = '%lastUser', `parentChannelID` = '%parentID', `parentChannelName` = '%parentName'";
        return query;
    }

    public static String getUpdateTopClientsQuery() {
        String query = "UPDATE `%prefix%topClients` ";
        query += "SET `topUsers` = '%topUsers',`userList` = '%userList'";
        query += " WHERE `topUsers` <= '%topUsers'";
        return query;
    }

    public static String getTopClientsQuery() {
        String query = "SELECT `topUsers` FROM `%prefix%topClients` ORDER BY `topUsers` ASC LIMIT 0,1";
        return query;
    }

    public static String getInsertClientQuery() {
        String query = "INSERT INTO `%prefix%clients` (`id`) VALUES ('%id') ON DUPLICATE KEY UPDATE `id` = '%id'";
        return query;
    }

    public static String getAllClientQuery() {
        String query = "SELECT `id` FROM `%prefix%clients`";
        return query;
    }
}
