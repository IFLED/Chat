package by.bsu.fpmi.ifled.chat.servlets;

import by.bsu.fpmi.ifled.chat.models.DbStorage;
import by.bsu.fpmi.ifled.chat.models.Storage;
import by.bsu.fpmi.ifled.chat.utils.CommonFunctions;
import by.bsu.fpmi.ifled.chat.utils.LongPolling;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import java.io.PrintWriter;
import java.util.ArrayList;

import static by.bsu.fpmi.ifled.chat.servlets.ServletConstants.*;

@WebServlet("/Update")
public class UpdateServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger(UpdateServlet.class);
   
    public void init(ServletConfig config) {
        logger.entry(config);
		logger.exit();
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

        String message  = request.getParameter("message");
        String session_id = request.getParameter("session_id");
        logger.debug("session_id = ", session_id);

		int result = update(session_id, message);
		logger.debug("result = ", result);

        logger.exit();
    }
	
	int update(String sessionId, String jsonMessage) {
        logger.entry(sessionId, jsonMessage);

		JSONParser parser = new JSONParser();
		JSONObject obj;
		try {
			obj = (JSONObject)parser.parse(jsonMessage);
		}
		catch (ParseException pe) {
			logger.catching(pe);
			return logger.exit(-1);
		}

        int ret = 0;
        int room_id = -1; // will be defined later
		String status = (String)obj.get("status");
        Storage storage = new DbStorage(DB_DRIVER, DB_NAME,
                                        DB_USERNAME, DB_PASSWORD);
		try {
			if (status.equals("new")) {
                String username = (String)obj.get("username");
                String text = CommonFunctions.fixSqlFieldValue((String)obj.get("text"));
                room_id = Integer.parseInt((String)obj.get("room_id"));

                storage.addMessage(username, room_id, text);
			}
			else if (status.equals("edit")) {
                String text = CommonFunctions.fixSqlFieldValue((String)obj.get("text"));
                int msg_id = Integer.parseInt((String)obj.get("message_id"));
                room_id  = storage.getRoomId(msg_id);

                storage.editMessage(text, msg_id);
			}
			else if (status.equals("delete")) {
				int msg_id = Integer.parseInt((String)obj.get("message_id"));
                room_id = storage.getRoomId(msg_id);

                storage.deleteMessage(msg_id);
			}
			else {
				ret = -10;
			}
		}
//		catch (SQLException se) {
//			logger.catching(se);
//			return logger.exit(-2);
//		}
		catch (Exception e) {
			logger.catching(e);
			ret = -5;
		}

        respondToAllInRoom(storage, room_id);
		
		return logger.exit(ret);
	}

    void respondToAllInRoom(Storage storage, int room_id) {
        logger.entry(storage, room_id);

        ArrayList<Integer> users;
        users = storage.getUserIdInRoom(room_id);
        logger.debug("users: ", users);

        for (Integer user_id : users) {
            int action_id = LongPolling.getInstance().getActionId(user_id);
            if (action_id == -1) {
                continue;
            }
            String username =  storage.getUsername(user_id);
            String response = storage.getMessages(action_id, username);
            LongPolling.getInstance().respond(user_id, response);
        }
        logger.exit();
    }
}
