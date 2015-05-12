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
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static by.bsu.fpmi.ifled.chat.servlets.ServletConstants.*;


@WebServlet("/First")
public class FirstServlet extends HttpServlet {

    final PrintStream err = System.err;
    final String myName = "FirstServlet";

    static Connection connection;
    
    public void init(ServletConfig config) {

		String now = CommonFunctions.nowTime();
        err.println(now + " " + myName + ": init");

        try {
            err.println(myName + ": try to load " + ServletConstants.DB_DRIVER);
            Class.forName(ServletConstants.DB_DRIVER);
        }
        catch (ClassNotFoundException cfe) {
            err.println(myName + ": So sad");
            err.println(cfe.getMessage());

            cfe.printStackTrace();
            System.exit(1);
        }

        try {
			err.println(myName + ": try to get connection");
            connection = DriverManager.getConnection(ServletConstants.DB_NAME,
                    ServletConstants.DB_USERNAME,
                    ServletConstants.DB_PASSWORD);
        }
        catch (SQLException se) {
            err.println(myName + ": So sad 2");
            err.println();
            se.printStackTrace();
            //System.out.println(se.getMessage());
            System.exit(2);
        }
    }
	
	public void doGet(HttpServletRequest request, 
                      HttpServletResponse response)
            throws ServletException, IOException {
	}

    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        //response.setHeader("Access-Control-Allow-Origin", 
        //                   "*");

        String username = request.getParameter("username");
		
		String userId;

        Storage storage = new DbStorage(myName, err, DB_DRIVER, DB_NAME,
                                        DB_USERNAME, DB_PASSWORD);

        //userId = CommonFunctions.getUserId(connection, username,
		//                                   myName, err);

        userId = new Integer(storage.getUserId(username)).toString();
        storage.getUsername(storage.getUserId(username));

        out.println(userId);

        err.println(myName + " - userId: " + userId);
        
        err.println(myName + ": after all");
    }
}