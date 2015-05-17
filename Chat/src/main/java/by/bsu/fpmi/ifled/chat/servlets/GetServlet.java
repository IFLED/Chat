package by.bsu.fpmi.ifled.chat.servlets;

import by.bsu.fpmi.ifled.chat.models.DbStorage;
import by.bsu.fpmi.ifled.chat.models.Storage;
import by.bsu.fpmi.ifled.chat.utils.CommonFunctions;
import by.bsu.fpmi.ifled.chat.utils.LongPolling;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import static by.bsu.fpmi.ifled.chat.servlets.ServletConstants.*;


@WebServlet(urlPatterns = "/Get", asyncSupported = true)
public class GetServlet extends HttpServlet {

    final PrintStream err = System.err;
    final String myName = "GetServlet";
    
    public void init(ServletConfig config) {
        String now = CommonFunctions.nowTime();
        err.println(now + " " + myName + ": init");
    }

    public void doGet(HttpServletRequest request, 
                      HttpServletResponse response)
            throws ServletException, IOException {
        //response.setHeader("Access-Control-Allow-Origin",
        //                   "*");

        String action_id_str  = request.getParameter("action_id");
        int action_id = Integer.parseInt(action_id_str);
        String session_id = request.getParameter("session_id");
        String username = request.getParameter("username");

        Storage storage = new DbStorage(myName, err, DB_DRIVER, DB_NAME,
                                        DB_USERNAME, DB_PASSWORD);

        String result = storage.getMessages(action_id, username);

        if (result.length() > 16) {
            err.println("answering..." + result.length());

            response.setContentType("text/html");
            response.setCharacterEncoding("UTF-8");
            PrintWriter out = response.getWriter();

            out.println(result);
            out.close();
        }
        else {
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
            err.println(asyncContext);
            LongPolling.getInstance().addAsync(storage.getUserId(username),
                                               action_id, asyncContext);
        }

        err.println(myName + ": after all");
    }
}
