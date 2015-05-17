package by.bsu.fpmi.ifled.chat.utils;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class LongPolling {

    private HashMap<Integer, AsyncContext> poll;
    private HashMap<Integer, Integer> action_id;
    static LongPolling instance;

    private LongPolling() {
        poll = new HashMap<Integer, AsyncContext>();
        action_id = new HashMap<Integer, Integer>();
    }

    public static LongPolling getInstance() {
        if (instance == null) {
            instance = new LongPolling();
        }
        return instance;
    }

    public void addAsync(int user_id, int action_id,
                         AsyncContext asyncContext) {
        poll.put(user_id, asyncContext);
        System.err.println("user_id: " + user_id + " action_id: " + action_id);
        this.action_id.put(user_id, action_id);
    }

    public int getActionId(int user_id) {
        System.err.println(action_id + " " + user_id);
        if (action_id.containsKey(user_id)) {
            return action_id.get(user_id);
        }
        else {
            return -1;
        }
    }

    public boolean respond(int user_id, String toSend) {
        AsyncContext asyncContext = poll.get(user_id);
        if (asyncContext != null) {
            ServletResponse response = asyncContext.getResponse();
            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");

            try {
                PrintWriter out = response.getWriter();
                out.print(toSend);
                out.flush();
                out.close();
            }
            catch (IOException e) {
            }
            asyncContext.complete();
            poll.remove(user_id);

            return true;
        }
        return false;
    }
}
