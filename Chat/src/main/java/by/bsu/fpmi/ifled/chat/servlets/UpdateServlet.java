package by.bsu.fpmi.ifled.chat.servlets;

import by.bsu.fpmi.ifled.chat.models.DbStorage;
import by.bsu.fpmi.ifled.chat.models.Storage;
import by.bsu.fpmi.ifled.chat.utils.CommonFunctions;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.sql.*;

import static by.bsu.fpmi.ifled.chat.servlets.ServletConstants.*;

@WebServlet("/Update")
public class UpdateServlet extends HttpServlet {

    final PrintStream err = System.err;
    final String myName = "UpdateServlet";
	static int action_id;
	static int message_id;

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


		try {
			Statement statement = connection.createStatement();
			String sql = "SELECT MAX(message_id), MAX(action_id) " +
			             "FROM messages;";
			ResultSet result = statement.executeQuery(sql);

			if (!result.next()) {
				// There is no any messages in db
				action_id = 0;
				message_id = 0;
			}
			else {
				message_id = result.getInt(1) + 1;
				action_id = result.getInt(2) + 1;
			}
		}
		catch (SQLException se) {
			err.println(myName + ": " + se.getMessage());
		}

		err.println(myName + ": init done!");
		//err.println(message_id + " " + action_id);
    }

    public void doPost(HttpServletRequest request, 
                      HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        
        PrintWriter out = response.getWriter();
        
        //response.setHeader("Access-Control-Allow-Origin",
        //                   "*");

        String message  = request.getParameter("message");
        String session_id = request.getParameter("session_id");
		err.println(session_id);

		int result = update(session_id, message, out);
		err.println(result);
		
		out.println(result);
		
		
        
        System.err.println(myName + ": after all");
    }
	
	int update(String sessionId, String jsonMessage, PrintWriter out) {
		JSONParser parser = new JSONParser();
		JSONObject obj;
		try {
			obj = (JSONObject)parser.parse(jsonMessage);
		}
		catch (ParseException pe) {
			err.println(myName + ": " + pe.getMessage());
			return -1;
		}
		
		String username = (String)obj.get("username");
		String room_id = (String)obj.get("room_id");
		String text = CommonFunctions.fixSqlFieldValue((String)obj.get("text"));
		String status = (String)obj.get("status");
		String time = CommonFunctions.nowTime();
		
		
		//err.println(myName + ": " + username + " " + room_id + 
		//            " " + text + " " + status );
		//err.println(sessionId);
		
		try {
			Statement statement = connection.createStatement();
			
			if (status.equals("new")) {
                Storage storage = new DbStorage(myName, err, DB_DRIVER, DB_NAME,
                                                DB_USERNAME, DB_PASSWORD);
                int user_id = storage.getUserId(username);
				//String user_id = CommonFunctions.getUserId(connection,
				//									username, myName, err);
				if (user_id < 0) {
					err.println("there are some errors");
					return -3;
				}
				
				String sql = "INSERT INTO messages VALUES (" +
							 (message_id++) + ", " + (user_id) + ", " +
							 room_id + ", " + (action_id++) + ", '" +
							 text + "', 1, '" + time + "');";
				//err.println(sql);
				statement.executeUpdate(sql);
			}
			else if (status.equals("edit")) {
				//String user_id = CommonFunctions.getUserId(connection, 
				//									username, myName, err);
				//if (user_id.charAt(0) == '-') {
				//	err.println("there are some errors");
				//	return -3;
				//}
				int msg_id = Integer.parseInt((String)obj.get("message_id"));
				
				String sql = "UPDATE messages SET text = '" +
							 text + "', " + "action_id = " +
							 (action_id++) + ", status = 2, time = '" +
							 time + "' WHERE message_id = " +
							 msg_id + ";";
				//err.println(sql);
				statement.executeUpdate(sql);
			}
			else if (status.equals("delete")) {
				int msg_id = Integer.parseInt((String)obj.get("message_id"));
				
				String sql = "UPDATE messages SET text = ''," +
							 "action_id = " +
							 (action_id++) + ", status = 3, time = '" +
							 time + "' WHERE message_id = " +
							 msg_id + ";";
				//err.println(sql);
				statement.executeUpdate(sql);
			}
			else {
				return -10;
			}
			
		}
		catch (SQLException se) {
			//out.println(se.getMessage());
			err.println(myName + ": " + se.getMessage());
			return -2;
		}
		catch (Exception e) {
			err.println(e.getMessage());
			return -5;
		}
		
		return 0;
	}
}