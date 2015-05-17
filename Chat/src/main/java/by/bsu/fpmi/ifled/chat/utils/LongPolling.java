package by.bsu.fpmi.ifled.chat.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.ServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

public class LongPolling {

    private static final Logger logger = LogManager.getLogger(LongPolling.class);

    private HashMap<Integer, AsyncContext> poll;
    private HashMap<Integer, Integer> action_id;
    static LongPolling instance;

    private LongPolling() {
        logger.entry();
        poll = new HashMap<Integer, AsyncContext>();
        action_id = new HashMap<Integer, Integer>();
        logger.exit();
    }

    public static LongPolling getInstance() {
        logger.entry();
        if (instance == null) {
            instance = new LongPolling();
        }
        return logger.exit(instance);
    }

    public void addAsync(int user_id, int action_id,
                         AsyncContext asyncContext) {
        logger.entry(user_id, action_id, asyncContext);
        poll.put(user_id, asyncContext);
        this.action_id.put(user_id, action_id);
        logger.exit();
    }

    public int getActionId(int user_id) {
        logger.entry(user_id);
        if (action_id.containsKey(user_id)) {
            return logger.exit(action_id.get(user_id));
        }
        else {
            return logger.exit(-1);
        }
    }

    public boolean respond(int user_id, String toSend) {
        logger.entry(user_id, toSend);
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

            return logger.exit(true);
        }
        return logger.exit(false);
    }
}
