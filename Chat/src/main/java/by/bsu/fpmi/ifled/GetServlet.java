package by.bsu.fpmi.ifled;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

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


@WebServlet("/Get")
public class GetServlet extends HttpServlet {

    final PrintStream err = System.err;
    final String myName = "GetServlet";

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
        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");

        PrintWriter out = response.getWriter();

        //response.setHeader("Access-Control-Allow-Origin",
        //                   "*");

        String action_id  = request.getParameter("action_id");
        String session_id = request.getParameter("session_id");
        String username = request.getParameter("username");

		String result = getMessages(session_id, action_id, username);

		out.println(result);
		
        err.println(myName + ": after all");
    }
	
	public String getMessages(String session_id, String action_id, 
							 String username) {
		String user_id = CommonFunctions.getUserId(connection,
												   username, myName, err);
		if (user_id.charAt(0) == '-') {
			err.println(myName + ": there are some errors");
			return "-3";
		}

		try {
			Statement statement = connection.createStatement();

			if (user_id.charAt(0) == '-') {
				err.println("there are some errors");
				return "-3";
			}

			String sql = "SELECT * FROM messages WHERE room_id in " +
						 "(SELECT room_id FROM links WHERE user_id = " +
						 user_id + ") and action_id > " + action_id +
						 " ORDER BY message_id;";
			//err.println(sql);
			JSONObject answer = new JSONObject();
			JSONArray messages = new JSONArray();
			ResultSet result = statement.executeQuery(sql);

            while (result.next()) {
				int message_id = result.getInt(1);
				int user_id_now = result.getInt(2);
				String user = CommonFunctions.getUsername(connection,
									String.valueOf(user_id_now), myName, err);
				int room_id = result.getInt(3);
				int action_id_now = result.getInt(4);
				String text = result.getString(5);
				int status = result.getInt(6);
				String time = result.getString(7);

				//err.println(message_id + " " + user + " " + room_id + " " +
				//		    action_id_now + " " + text + " " + status + " " + time);

				JSONObject msg = getJsonMessage(message_id, user, room_id,
												action_id_now, text, status, time);
				messages.add(msg);
			}

			answer.put("messages", messages);

			return answer.toJSONString();

		}
		catch (SQLException se) {
			//out.println(se.getMessage());
			err.println(myName + ": " + se.getMessage());
			return "-2";
		}
	}
	
	JSONObject getJsonMessage(int message_id, String username, int room_id,
	                          int action_id, String text, int status, String time) {
		JSONObject obj = new JSONObject();
		obj.put("message_id", message_id);
		obj.put("username", username);
		obj.put("room_id", room_id);
		obj.put("action_id", action_id);
		obj.put("text", text);
		obj.put("status", status);
		obj.put("time", time);
		
		return obj;
	}
}