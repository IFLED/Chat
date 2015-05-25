package by.bsu.fpmi.ifled.chat.servlets;

import by.bsu.fpmi.ifled.chat.models.DbStorage;
import by.bsu.fpmi.ifled.chat.models.Storage;
import by.bsu.fpmi.ifled.chat.utils.LongPolling;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.AsyncContext;
import javax.servlet.AsyncListener;
import javax.servlet.AsyncEvent;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

import static by.bsu.fpmi.ifled.chat.servlets.ServletConstants.*;


@WebServlet(urlPatterns = "/Get", asyncSupported = true)
public class GetServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(GetServlet.class);

    public static final int EMPTY_MESSAGE_LENGTH = 16;
    public static final int REQUESTS_TIMEOUT = 30000;
    
    public void init(ServletConfig config) {
        logger.entry(config);
        logger.exit();
    }

    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        logger.entry();
        //response.setHeader("Access-Control-Allow-Origin",
        //                   "*");

        String action_id_str  = request.getParameter("action_id");
        int action_id = Integer.parseInt(action_id_str);
        String session_id = request.getParameter("session_id");
        String username = request.getParameter("username");

        Storage storage = new DbStorage(DB_DRIVER, DB_NAME,
                                        DB_USERNAME, DB_PASSWORD);

        String result = storage.getMessages(action_id, username);

        if (result.length() > EMPTY_MESSAGE_LENGTH) {
            logger.trace("answering");

            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            out.println(result);
            out.close();

            logger.debug("sending ", result);
        }
        else {
            logger.trace("there are not any messages");
            AsyncContext asyncContext = request.startAsync();
            asyncContext.setTimeout(REQUESTS_TIMEOUT);

            asyncContext.addListener(
                    new MyAsyncListener(storage.getUserId(username), storage)
            );
            LongPolling.getInstance().addAsync(storage.getUserId(username),
                                               action_id, asyncContext);
        }

        logger.exit();
    }
}


class MyAsyncListener implements AsyncListener {
    int user_id;
    Storage storage;

    public MyAsyncListener(int user_id, Storage storage) {
        this.user_id = user_id;
        this.storage = storage;
    }

    public void onComplete(AsyncEvent asyncEvent) throws IOException {
    }

    public void onTimeout(AsyncEvent asyncEvent) throws IOException {
        int action_id = LongPolling.getInstance().getActionId(user_id);
        if (action_id == -1) {
            return ;
        }
        String username =  storage.getUsername(user_id);
        String response = storage.getMessages(action_id, username);

        if (response.length() < GetServlet.EMPTY_MESSAGE_LENGTH) {
            LongPolling.getInstance().respond(user_id, response);
        }
        else {
            LongPolling.getInstance().respond(user_id, response);
        }
    }

    public void onError(AsyncEvent asyncEvent) throws IOException {
    }

    public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
    }
}
