package by.bsu.fpmi.ifled.chat.servlets;

import by.bsu.fpmi.ifled.chat.models.DbStorage;
import by.bsu.fpmi.ifled.chat.models.Storage;
import by.bsu.fpmi.ifled.chat.utils.LongPolling;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.AsyncContext;
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

        if (result.length() > 16) {
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
            asyncContext.setTimeout(1000000);
//            asyncContext.addListener(new AsyncListener() {
//                public void onComplete(AsyncEvent asyncEvent) throws IOException {
//
//                }
//
//                public void onTimeout(AsyncEvent asyncEvent) throws IOException {
//
//                }
//
//                public void onError(AsyncEvent asyncEvent) throws IOException {
//
//                }
//
//                public void onStartAsync(AsyncEvent asyncEvent) throws IOException {
//
//                }
//            });
            LongPolling.getInstance().addAsync(storage.getUserId(username),
                                               action_id, asyncContext);
        }

        logger.exit();
    }
}
