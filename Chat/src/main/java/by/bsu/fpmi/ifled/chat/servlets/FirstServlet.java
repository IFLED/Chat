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


@WebServlet("/First")
public class FirstServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(FirstServlet.class);

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
        
        //response.setHeader("Access-Control-Allow-Origin", 
        //                   "*");

        String username = request.getParameter("username");
		
		String userId;

        Storage storage = new DbStorage(DB_DRIVER, DB_NAME,
                                        DB_USERNAME, DB_PASSWORD);

        //userId = CommonFunctions.getUserId(connection, username,
		//                                   myName, err);

        userId = new Integer(storage.getUserId(username)).toString();
        storage.getUsername(storage.getUserId(username));

        out.println(userId);
        out.close();

        logger.debug("userId: " + userId);
        
        logger.exit();
    }
}
