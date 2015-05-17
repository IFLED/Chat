package by.bsu.fpmi.ifled.chat.models;

import java.io.PrintStream;
import java.util.ArrayList;

abstract public class Storage {
    protected String servletName;
    protected PrintStream console;

    abstract public String getMessages(int action_id, String username);
    abstract public int addMessage(String username, int room_id, String text);
    abstract public int editMessage(String text, int message_id);
    abstract public int deleteMessage(int message_id);
    abstract public String getUsername(int user_id);
    abstract public int getUserId(String username);
    abstract public ArrayList<Integer> getUserIdInRoom(int room_id);
    abstract public int getRoomId(int message_id);
}
