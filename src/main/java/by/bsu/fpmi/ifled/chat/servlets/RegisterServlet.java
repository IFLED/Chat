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

import static by.bsu.fpmi.ifled.chat.servlets.ServletConstants.*;

@WebServlet("/Register")
public class RegisterServlet extends HttpServlet {
    private static final Logger logger =
            LogManager.getLogger(RegisterServlet.class);

    public void init(ServletConfig config) {
        logger.entry(config);
        logger.exit();
    }

    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
            throws ServletException, IOException {
        logger.entry();
        //response.setHeader("Access-Control-Allow-Origin",
        //                   "*");

        String password = request.getParameter("password");
        String username = request.getParameter("username");

        Storage storage = new DbStorage(DB_DRIVER, DB_NAME,
                                        DB_USERNAME, DB_PASSWORD);

        int result = storage.registerUser(username, password);

        response.setContentType("text/html");
        response.setCharacterEncoding("UTF-8");
        PrintWriter out = response.getWriter();

        out.println(result);
        out.close();

        logger.exit();
    }
}
