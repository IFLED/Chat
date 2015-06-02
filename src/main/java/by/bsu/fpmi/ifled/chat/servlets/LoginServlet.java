package by.bsu.fpmi.ifled.chat.servlets;

import by.bsu.fpmi.ifled.chat.models.DbStorage;
import by.bsu.fpmi.ifled.chat.models.Storage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;

import static by.bsu.fpmi.ifled.chat.servlets.ServletConstants.*;


@WebServlet("/Login")
public class LoginServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(LoginServlet.class);

    static Connection connection;
    
    public void init(ServletConfig config) {
		logger.entry(config);

        logger.exit();
    }
	
	public void doGet(HttpServletRequest request, 
                      HttpServletResponse response)
            throws ServletException, IOException {
	}

    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        logger.entry(request, response);

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();

        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Storage storage = new DbStorage(DB_DRIVER, DB_NAME,
                                        DB_USERNAME, DB_PASSWORD);

        int session_id = storage.getSession(username, password);

        out.println(session_id);
        out.close();

        logger.debug("session_id: " + session_id);
        
        logger.exit();
    }
}
