package by.bsu.fpmi.ifled.chat.servlets;

import by.bsu.fpmi.ifled.chat.models.DbStorage;
import by.bsu.fpmi.ifled.chat.models.Storage;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static by.bsu.fpmi.ifled.chat.servlets.ServletConstants.*;

@WebFilter("/index.jsp")
public class LoginFilter implements Filter {

    private static final Logger logger = LogManager.getLogger(LoginFilter.class);

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain filterChain) throws IOException, ServletException {
        logger.info(((HttpServletRequest)request).getRequestURI());

        Cookie[] cookies = ((HttpServletRequest)request).getCookies();
        String username = null;
        int session_id = -1;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if ("username".equals(cookie.getName())) {
                    username = cookie.getValue();
                }
                if ("session_id".equals(cookie.getName())) {
                    session_id = Integer.parseInt(cookie.getValue());
                }
            }
        }

        Storage storage = new DbStorage(DB_DRIVER, DB_NAME,
                                        DB_USERNAME, DB_PASSWORD);

        if (storage.checkSession(username, session_id)) {
            logger.info(username + " " + session_id);
            filterChain.doFilter(request, response);
            return ;
        }
        else {
            ((HttpServletResponse)response).sendRedirect("/homepage.htm");
        }
    }

    @Override
    public void destroy() {
    }
}
