package by.bsu.fpmi.ifled.chat.models;

import by.bsu.fpmi.ifled.chat.utils.CommonFunctions;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.PrintStream;
import java.sql.*;
import java.util.ArrayList;

public class DbStorage extends Storage {
    private String dbDriver;
    private String dbName;
    private String dbUsername;
    private String dbPassword;

    public DbStorage(String servletName, PrintStream console, String dbDriver,
              String dbName, String dbUsername, String dbPassword) {
        this.console = console;
        this.servletName = servletName;
        this.dbDriver = dbDriver;
        this.dbName= dbName;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }


    @Override
    public String getMessages(int action_id, String username) {
        int user_id = getUserId(username);
        if (user_id < 0) {
            console.println(servletName + ": there are some errors");
            return "#3";
        }

        Connection connection = openConnection();

        try {
            Statement statement = connection.createStatement();

            String sql = "SELECT * FROM messages WHERE room_id in " +
                    "(SELECT room_id FROM links WHERE user_id = " +
                    user_id + ") and action_id > " + action_id +
                    " ORDER BY message_id;";
            //err.println(sql);
            ResultSet result = statement.executeQuery(sql);

            JSONObject answer = new JSONObject();
            JSONArray messages = new JSONArray();

            while (result.next()) {
                int user_id_now = result.getInt(2);
                String user = getUsername(user_id_now);
                if (user.charAt(0) == '#') {
                    console.println(servletName + ": invalid user_id " + user);
                    return "#4";
                }
                JSONObject msg = new Message(result, user).toJsonObject();
                messages.add(msg);
            }

            answer.put("messages", messages);

            return answer.toJSONString();

        }
        catch (SQLException se) {
            console.println(servletName + ": " + se.getMessage());
            return "#2";
        }
    }

    @Override
    public int addMessage(String username, int room_id, String text) {
        int user_id = getUserId(username);
        if (user_id < 0) {
            console.println("there are some errors");
            return -3;
        }

        Connection connection = openConnection();
        if (connection == null) {
            return -4;
        }

        int message_id = getMessageId();
        int action_id = getActionId();
        String time = CommonFunctions.nowTime();

        try {
            Statement statement = connection.createStatement();

            String sql = "INSERT INTO messages VALUES (" +
                    (message_id++) + ", " + (user_id) + ", " +
                    room_id + ", " + (action_id++) + ", '" +
                    text + "', 1, '" + time + "');";

            statement.executeUpdate(sql);
            connection.close();

            return 0;
        }
        catch (SQLException se) {
            console.println(servletName + ": " + se.getMessage());
            return -2;
        }
    }

    @Override
    public int editMessage(String text, int message_id) {
        Connection connection = openConnection();
        if (connection == null) {
            return -4;
        }

        int action_id = getActionId();
        String time = CommonFunctions.nowTime();

        try {
            Statement statement = connection.createStatement();

            String sql = "UPDATE messages SET text = '" +
                    text + "', " + "action_id = " +
                    (action_id++) + ", status = 2, time = '" +
                    time + "' WHERE message_id = " +
                    message_id + ";";
            console.println(sql);
            statement.executeUpdate(sql);
            connection.close();

            return 0;
        }
        catch (SQLException se) {
            console.println(servletName + ": " + se.getMessage());
            return -2;
        }
    }

    @Override
    public int deleteMessage(int message_id) {
        Connection connection = openConnection();
        if (connection == null) {
            return -4;
        }

        int action_id = getActionId();
        String time = CommonFunctions.nowTime();

        try {
            Statement statement = connection.createStatement();

            String sql = "UPDATE messages SET text = ''," +
                    "action_id = " +
                    (action_id++) + ", status = 3, time = '" +
                    time + "' WHERE message_id = " +
                    message_id + ";";
            console.println(sql);
            statement.executeUpdate(sql);
            connection.close();

            return 0;
        }
        catch (SQLException se) {
            console.println(servletName + ": " + se.getMessage());
            return -2;
        }
    }

    @Override
    public String getUsername(int user_id) {
        //console.println(servletName + " : getUsername " + user_id);

        Connection con = openConnection();
        if (con == null) {
            return "#4";
        }

        try {
            Statement statement = con.createStatement();

            String sql = "SELECT name FROM users WHERE user_id = " +
                    user_id + ";";
            ResultSet result = statement.executeQuery(sql);

            if (!result.next()) {
                // There is no such username in db
                return "#2";
            }
            if (!result.isLast()) {
                // There is multiple username in db
                return "#3";
            }

            con.close();

            return result.getString(1);
        }
        catch (SQLException se) {
            console.println(servletName + ": " + se.getMessage());
            try {
                if (!con.isClosed()) {
                    con.close();
                }
            }
            catch (SQLException se2) {
                console.println(servletName + ": " + se2.getMessage());
                return "#6";
            }
            return "#5";
        }
    }

    @Override
    public int getUserId(String username) {
        //console.println(servletName + " : getUserId " + username);

        Connection con = openConnection();
        if (con == null) {
            return -4;
        }

        try {
            Statement statement = con.createStatement();

            String sql = "SELECT user_id FROM users WHERE name = '" +
                    username + "';";
            ResultSet result = statement.executeQuery(sql);

            if (!result.next()) {
                // There is no such username in db
                return -2;
            }
            if (!result.isLast()) {
                // There are multiple usernames in db
                return -3;
            }

            con.close();

            return result.getInt(1);
        }
        catch (SQLException se) {
            console.println(servletName + ": " + se.getMessage());
            try {
                if (!con.isClosed()) {
                    con.close();
                }
            }
            catch (SQLException se2) {
                console.println(servletName + ": " + se2.getMessage());
                return -6;
            }
            return -5;
        }
    }

    @Override
    public ArrayList<Integer> getUserIdInRoom(int room_id) {
        Connection connection = openConnection();

        if (connection == null) {
            return new ArrayList<Integer>();
        }

        ArrayList<Integer> ans = new ArrayList<Integer>();

        try {
            Statement statement = connection.createStatement();

            String sql = "SELECT user_id FROM links WHERE room_id =" +
                    room_id + ";";
            //err.println(sql);
            ResultSet result = statement.executeQuery(sql);

            JSONObject answer = new JSONObject();
            JSONArray messages = new JSONArray();

            while (result.next()) {
                int user_id = result.getInt(1);
                ans.add(user_id);
            }

            return ans;

        }
        catch (SQLException se) {
            console.println(servletName + ": " + se.getMessage());
            return new ArrayList<Integer>();
        }
    }

    @Override
    public int getRoomId(int message_id) {
        Connection connection = openConnection();
        if (connection == null) {
            return -4;
        }

        int room_id;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT room_id FROM messages WHERE message_id =" +
                            message_id + " ;";
            ResultSet result = statement.executeQuery(sql);

            if (!result.next()) {
                // There is no such message in db
                room_id = -1;
            }
            else {
                room_id = result.getInt(1);
            }

            connection.close();

            return room_id;
        }
        catch (SQLException se) {
            console.println(servletName + ": " + se.getMessage());
            return -5;
        }
    }

    private int getMessageId() {
        Connection connection = openConnection();
        if (connection == null) {
            return -4;
        }

        int message_id;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT MAX(message_id) " + "FROM messages;";
            ResultSet result = statement.executeQuery(sql);

            if (!result.next()) {
                // There are no any messages in db
                message_id = 0;
            }
            else {
                message_id = result.getInt(1) + 1;
            }

            connection.close();

            return message_id;
        }
        catch (SQLException se) {
            console.println(servletName + ": " + se.getMessage());
            return -5;
        }
    }

    private int getActionId() {
        Connection connection = openConnection();
        if (connection == null) {
            return -4;
        }

        int action_id;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT MAX(action_id) " + "FROM messages;";
            ResultSet result = statement.executeQuery(sql);

            if (!result.next()) {
                // There is no any messages in db
                action_id = 0;
            }
            else {
                action_id = result.getInt(1) + 1;
            }

            connection.close();

            return action_id;
        }
        catch (SQLException se) {
            console.println(servletName + ": " + se.getMessage());
            return -5;
        }
    }

    private Connection openConnection() {

        Connection connection = null;

        try {
            //console.println(servletName + ": try to load " + dbDriver);
            Class.forName(dbDriver);

            //console.println(servletName + ": try to get connection");
            connection = DriverManager.getConnection(dbName, dbUsername,
                    dbPassword);
        }
        catch (ClassNotFoundException cfe) {
            console.println(servletName + ": So sad");
            console.println(cfe.getMessage());

            cfe.printStackTrace();
        }
        catch (SQLException se) {
            console.println(servletName + ": So sad 2");
            console.println(se.getMessage());
            //se.printStackTrace();
            //System.out.println(se.getMessage());
        }
        return connection;
    }
}
