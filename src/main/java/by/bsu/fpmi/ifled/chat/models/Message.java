package by.bsu.fpmi.ifled.chat.models;

import org.json.simple.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;

public class Message {
    public String username;
    public int room_id;
    public String text;
    public int status;
    public int message_id;
    public int action_id;
    public String time;

    public Message(ResultSet rs, String username) throws SQLException {
        this.username = username;
        this.message_id = rs.getInt(1);
        this.room_id = rs.getInt(3);
        this.action_id = rs.getInt(4);
        this.text = rs.getString(5);
        this.status = rs.getInt(6);
        this.time = rs.getString(7);
    }

    public JSONObject toJsonObject() {
        JSONObject obj = new JSONObject();
        obj.put("message_id", message_id);
        obj.put("username", username);
        obj.put("room_id", room_id);
        obj.put("action_id", action_id);
        obj.put("text", text);
        obj.put("status", status);
        obj.put("time", time);

        return obj;
    }
}
