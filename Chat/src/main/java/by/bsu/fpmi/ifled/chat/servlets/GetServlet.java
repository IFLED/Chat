package by.bsu.fpmi.ifled.chat.servlets;

import by.bsu.fpmi.ifled.chat.models.DbStorage;
import by.bsu.fpmi.ifled.chat.models.Storage;
import by.bsu.fpmi.ifled.chat.utils.CommonFunctions;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import static by.bsu.fpmi.ifled.chat.servlets.ServletConstants.*;


@WebServlet("/Get")
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
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        //response.setHeader("Access-Control-Allow-Origin",
        //                   "*");

        String action_id  = request.getParameter("action_id");
        String session_id = request.getParameter("session_id");
        String username = request.getParameter("username");

        Storage storage = new DbStorage(myName, err, DB_DRIVER, DB_NAME,
                                        DB_USERNAME, DB_PASSWORD);

		//String result = getMessages(session_id, action_id, username);
        String result = storage.getMessages(Integer.parseInt(action_id),
                                            username);

		out.println(result);
		
        err.println(myName + ": after all");
    }
}