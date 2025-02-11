package com.example.taskbazaar.servlet;

import com.example.taskbazaar.service.AdminService;
import com.example.taskbazaar.service.ResponseService;
import com.example.taskbazaar.service.UserService;
import com.example.taskbazaar.utility.TaskBazaarLogger;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.taskbazaar.model.Post;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

@WebServlet("/admin")
public class adminServlet extends HttpServlet {

    private final Logger logger = TaskBazaarLogger.getLogger();
    private final AdminService adminService = AdminService.getInstance();
    private final UserService userService = UserService.getInstance();

    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        String username = (String) req.getSession().getAttribute("username");
        logger.info(username + " try to access admin page");

        if(username == null) {
            logger.info("username is null. Redirecting to login page....");
            ResponseService.sendAlertAndRedirect(res,"Error credentials!!","index.jsp");
        }

        String userRole = null;
        try {
            logger.info("Retrieving user role.....");
            userRole = userService.getUserRole(username);
        } catch (SQLException e) {
            logger.info("userRole retrieve error : " + e.getMessage());
            ResponseService.sendAlertAndRedirect(res,"Internal Server Error!!","index.jsp");
        }

        if("admin".equals(userRole)) {
                List<Post> posts = new ArrayList<>();
                try {
                    logger.info("Retrieving posts.....");
                    posts = adminService.getAllPost();
                } catch (Exception e) {
                    logger.info("posts retrieve error" + e.getMessage());
                }

                logger.info("posts retrieved. Redirecting to admin page....");
                req.setAttribute("posts", posts);
                RequestDispatcher dispatcher = req.getRequestDispatcher("admin.jsp");
                dispatcher.forward(req, res);
        }
        else{
                logger.info("unauthorized user try to access admin page");
                ResponseService.sendAlertAndRedirect(res,"You are not authorized to access this page. Redirecting to home....","/home");
        }
    }
}
