package by.bsu.fpmi.ifled.chat.models;

import by.bsu.fpmi.ifled.chat.utils.CommonFunctions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.*;
import java.util.ArrayList;

public class DbStorage extends Storage {
    private String dbDriver;
    private String dbName;
    private String dbUsername;
    private String dbPassword;

    private static final Logger logger = LogManager.getLogger(DbStorage.class);

    public DbStorage(String dbDriver, String dbName, String dbUsername,
                     String dbPassword) {
        this.dbDriver = dbDriver;
        this.dbName= dbName;
        this.dbUsername = dbUsername;
        this.dbPassword = dbPassword;
    }


    @Override
    public String getMessages(int action_id, String username) {
        logger.entry(action_id, username);
        int user_id = getUserId(username);
        if (user_id < 0) {
            logger.error("can't get user_id");
            return logger.exit("#3");
        }

        Connection connection = openConnection();
        if (connection == null) {
            logger.error("can't open connection");
            return logger.exit("#4");
        }

        try {
            Statement statement = connection.createStatement();

            String sql = "SELECT * FROM messages WHERE room_id in " +
                    "(SELECT room_id FROM links WHERE user_id = " +
                    user_id + ") and action_id > " + action_id +
                    " ORDER BY message_id;";
            logger.debug(sql);
            ResultSet result = statement.executeQuery(sql);

            JSONObject answer = new JSONObject();
            JSONArray messages = new JSONArray();

            while (result.next()) {
                int user_id_now = result.getInt(2);
                String user = getUsername(user_id_now);
                if (user.charAt(0) == '#') {
                    logger.error("invalid user_id " + user);
                    return logger.exit("#4");
                }
                JSONObject msg = new Message(result, user).toJsonObject();
                messages.add(msg);
            }

            answer.put("messages", messages);

            return logger.exit(answer.toJSONString());

        }
        catch (SQLException se) {
            logger.catching(se);
            return logger.exit("#2");
        }
    }

    @Override
    public int addMessage(String username, int room_id, String text) {
        logger.entry(username, room_id, text);
        int user_id = getUserId(username);
        if (user_id < 0) {
            logger.error("cant't get user_id");
            return logger.exit(-3);
        }

        Connection connection = openConnection();
        if (connection == null) {
            logger.error("can't open connection");
            return logger.exit(-4);
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
            logger.debug(sql);
            statement.executeUpdate(sql);
            connection.close();

            return logger.exit(0);
        }
        catch (SQLException se) {
            logger.catching(se);

            return logger.exit(-2);
        }
    }

    @Override
    public int editMessage(String text, int message_id) {
        logger.entry(text, message_id);

        Connection connection = openConnection();
        if (connection == null) {
            logger.error("can't get connection");
            return logger.exit(-4);
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
            logger.debug(sql);
            statement.executeUpdate(sql);
            connection.close();

            return logger.exit(0);
        }
        catch (SQLException se) {
            logger.catching(se);
            return logger.exit(-2);
        }
    }

    @Override
    public int deleteMessage(int message_id) {
        Connection connection = openConnection();
        if (connection == null) {
            logger.error("can't get connection");
            return logger.exit(-4);
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
            logger.debug(sql);
            statement.executeUpdate(sql);
            connection.close();

            return logger.exit(0);
        }
        catch (SQLException se) {
            logger.catching(se);
            return logger.exit(-2);
        }
    }

    @Override
    public String getUsername(int user_id) {
        logger.entry(user_id);

        Connection con = openConnection();
        if (con == null) {
            logger.error("can't get connection");
            return logger.exit("#4");
        }

        try {
            Statement statement = con.createStatement();

            String sql = "SELECT name FROM users WHERE user_id = " +
                    user_id + ";";
            logger.debug(sql);
            ResultSet result = statement.executeQuery(sql);

            if (!result.next()) {
                // There is no such username in db
                return logger.exit("#2");
            }
            if (!result.isLast()) {
                // There are multiple usernames in db
                return logger.exit("#3");
            }

            con.close();

            return logger.exit(result.getString(1));
        }
        catch (SQLException se) {
            logger.catching(se);
            return logger.exit("#5");
        }
    }

    @Override
    public int getUserId(String username) {
        logger.entry(username);

        Connection con = openConnection();
        if (con == null) {
            logger.error("can't get connection");
            return logger.exit(-4);
        }

        try {
            Statement statement = con.createStatement();

            String sql = "SELECT user_id FROM users WHERE name = '" +
                    username + "';";
            logger.debug(sql);
            ResultSet result = statement.executeQuery(sql);

            if (!result.next()) {
                // There is no such username in db
                return logger.exit(-2);
            }
            if (!result.isLast()) {
                // There are multiple usernames in db
                return logger.exit(-3);
            }

            con.close();

            return logger.exit(result.getInt(1));
        }
        catch (SQLException se) {
            logger.catching(se);
            return logger.exit(-5);
        }
    }

    @Override
    public ArrayList<Integer> getUserIdInRoom(int room_id) {
        logger.entry(room_id);

        Connection connection = openConnection();
        if (connection == null) {
            logger.error("can't get connection");
            return logger.exit(new ArrayList<Integer>());
        }

        ArrayList<Integer> ans = new ArrayList<Integer>();

        try {
            Statement statement = connection.createStatement();

            String sql = "SELECT user_id FROM links WHERE room_id =" +
                    room_id + ";";
            logger.debug(sql);
            ResultSet result = statement.executeQuery(sql);

            JSONObject answer = new JSONObject();
            JSONArray messages = new JSONArray();

            while (result.next()) {
                int user_id = result.getInt(1);
                ans.add(user_id);
            }

            return logger.exit(ans);

        }
        catch (SQLException se) {
            logger.catching(se);
            return logger.exit(new ArrayList<Integer>());
        }
    }

    @Override
    public int getRoomId(int message_id) {
        logger.entry(message_id);

        Connection connection = openConnection();
        if (connection == null) {
            logger.error("can't get connection");
            return logger.exit(-4);
        }

        int room_id;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT room_id FROM messages WHERE message_id =" +
                            message_id + " ;";
            logger.debug(sql);
            ResultSet result = statement.executeQuery(sql);

            if (!result.next()) {
                // There is no such message in db
                room_id = -1;
            }
            else {
                room_id = result.getInt(1);
            }

            connection.close();

            return logger.exit(room_id);
        }
        catch (SQLException se) {
            logger.catching(se);
            return logger.exit(-5);
        }
    }

    private int getMessageId() {
        logger.entry();
        Connection connection = openConnection();
        if (connection == null) {
            logger.error("can't get connection");
            return logger.exit(-4);
        }

        int message_id;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT MAX(message_id) " + "FROM messages;";
            logger.debug(sql);
            ResultSet result = statement.executeQuery(sql);

            if (!result.next()) {
                // There are no any messages in db
                message_id = 0;
            }
            else {
                message_id = result.getInt(1) + 1;
            }

            connection.close();

            return logger.exit(message_id);
        }
        catch (SQLException se) {
            logger.catching(se);
            return logger.exit(-5);
        }
    }

    private int getActionId() {
        logger.entry();

        Connection connection = openConnection();
        if (connection == null) {
            logger.error("can't get connection");
            return logger.exit(-4);
        }

        int action_id;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT MAX(action_id) " + "FROM messages;";
            logger.debug(sql);
            ResultSet result = statement.executeQuery(sql);

            if (!result.next()) {
                // There is no any messages in db
                action_id = 0;
            }
            else {
                action_id = result.getInt(1) + 1;
            }

            connection.close();

            return logger.exit(action_id);
        }
        catch (SQLException se) {
            logger.catching(se);
            return logger.exit(-5);
        }
    }

    private Connection openConnection() {
        logger.entry();
        Connection connection = null;

        try {
            logger.trace("try to load " + dbDriver);
            Class.forName(dbDriver);

            logger.trace("try to get connection");
            connection = DriverManager.getConnection(dbName, dbUsername,
                    dbPassword);
            logger.trace("done!");
        }
        catch (ClassNotFoundException cfe) {
            logger.catching(cfe);
        }
        catch (SQLException se) {
            logger.catching(se);
        }
        return logger.exit(connection);
    }
}
