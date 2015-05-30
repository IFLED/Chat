package by.bsu.fpmi.ifled.chat.models;

import by.bsu.fpmi.ifled.chat.utils.CommonFunctions;
import by.bsu.fpmi.ifled.chat.utils.ConnectionPool;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import static by.bsu.fpmi.ifled.chat.utils.CommonFunctions.fixSqlFieldValue;

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
                    "(-1, (SELECT room_id FROM links WHERE user_id = " +
                    user_id + ") ) and action_id > " + action_id +
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
                JSONObject msg = new Message(result).toJsonObject();
                messages.add(msg);
            }

            answer.put("messages", messages);

            if (messages.size() != 0) {
                sql = "SELECT name, user_id FROM users WHERE user_id in " +
                        "(SELECT user_id FROM links WHERE room_id in " +
                        "(SELECT room_id FROM links WHERE user_id = " +
                        "(SELECT user_id FROM users WHERE name = '" +
                        fixSqlFieldValue(username) + "')) );";
                logger.debug(sql);
                result = statement.executeQuery(sql);

                JSONArray usernames = new JSONArray();
                while (result.next()) {
                    int user_id_now = result.getInt(2);
                    String name = result.getString(1);

                    JSONObject user = new JSONObject();
                    user.put("user_id", user_id_now);
                    user.put("name", name);
                    usernames.add(user);
                }

                answer.put("usernames", usernames);
            }

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
                    fixSqlFieldValue(text) + "', 1, '" +
                    fixSqlFieldValue(time) + "');";
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
                    fixSqlFieldValue(text) + "', " + "action_id = " +
                    (action_id++) + ", status = 2, time = '" +
                    fixSqlFieldValue(time) + "' WHERE message_id = " +
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
                    fixSqlFieldValue(time) + "' WHERE message_id = " +
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

            String res = result.getString(1);

            con.close();

            return logger.exit(res);
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
                         fixSqlFieldValue(username) + "';";
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
            int res = result.getInt(1);

            con.close();

            return logger.exit(res);
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
    public ArrayList<Integer> getRoomIdFromUser(int user_id) {
        logger.entry(user_id);

        Connection connection = openConnection();
        if (connection == null) {
            logger.error("can't get connection");
            return logger.exit(new ArrayList<Integer>());
        }

        ArrayList<Integer> ans = new ArrayList<Integer>();

        try {
            Statement statement = connection.createStatement();

            String sql = "SELECT room_id FROM links WHERE user_id =" +
                         user_id + ";";
            logger.debug(sql);
            ResultSet result = statement.executeQuery(sql);

            while (result.next()) {
                int room_id = result.getInt(1);
                ans.add(room_id);
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

    @Override
    public int registerUser(String username, String password) {
        logger.entry(username, password);

        Connection connection = openConnection();
        if (connection == null) {
            logger.error("can't get connection");
            return logger.exit(-4);
        }

        try {
            int user_id = getUserId();
            if (user_id < 0) {
                logger.error("wrong user_id: user_id = " + user_id);
                throw new SQLException("wrong user_id: " + user_id);
            }

            connection.setAutoCommit(false);

            Statement statement = connection.createStatement();
            String sql = "INSERT INTO users VALUES (" + user_id +
                            ", '" + fixSqlFieldValue(username) + "');";
            logger.debug(sql);
            statement.executeUpdate(sql);

            sql = "INSERT INTO links VALUES (" + user_id + ", 1);";
            logger.debug(sql);
            statement.executeUpdate(sql);

            connection.commit();

            connection.close();

            return logger.exit(user_id);
        }
        catch (SQLException se) {
            logger.catching(se);
            return logger.exit(-5);
        }
    }

    @Override
    public int changeName(String old_username, String new_username) {
        logger.entry(old_username, new_username);

        int user_id = getUserId(old_username);
        if (user_id < 0) {
            logger.error("can't get user_id: user_id = ", user_id);
            return logger.exit(user_id - 10);
        }

        Connection connection = openConnection();
        if (connection == null) {
            logger.error("can't get connection");
            return logger.exit(-4);
        }


        int message_id = getMessageId();
        int action_id = getActionId();
        String time = CommonFunctions.nowTime();
        try {
            connection.setAutoCommit(false);

            Statement statement = connection.createStatement();
            String sql = "UPDATE users SET name = '" +
                         fixSqlFieldValue(new_username) +
                         "' WHERE user_id = " + user_id + ";";
            logger.debug(sql);
            statement.executeUpdate(sql);

            sql = "INSERT INTO messages VALUES (" +
                    (message_id++) + ", " + (user_id) + ", " +
                    -1 + ", " + (action_id++) + ", '" +
                    fixSqlFieldValue(new_username) + "', 4, '" +
                    fixSqlFieldValue(time) + "');";
            logger.debug(sql);
            statement.executeUpdate(sql);

            connection.commit();

            connection.close();

            return logger.exit(user_id);
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

    private int getUserId() {
        logger.entry();

        Connection connection = openConnection();
        if (connection == null) {
            logger.error("can't get connection");
            return logger.exit(-4);
        }

        int user_id;
        try {
            Statement statement = connection.createStatement();
            String sql = "SELECT MAX(user_id) " + "FROM users;";
            logger.debug(sql);
            ResultSet result = statement.executeQuery(sql);

            if (!result.next()) {
                // There is no any messages in db
                user_id = 0;
            }
            else {
                user_id = result.getInt(1) + 1;
            }

            connection.close();

            return logger.exit(user_id);
        }
        catch (SQLException se) {
            logger.catching(se);
            return logger.exit(-5);
        }
    }

    private Connection openConnection() {
        logger.entry();
        Connection connection = null;

        connection = ConnectionPool.getConnection(dbDriver, dbName, dbUsername,
                dbPassword);
        return logger.exit(connection);
    }
}
