package com.example.taskbazaar.servlet;

import com.example.taskbazaar.service.AlertService;
import com.example.taskbazaar.service.PostService;
import com.example.taskbazaar.service.UserService;
import com.example.taskbazaar.validation.Validator;
import com.example.taskbazaar.utility.Constants;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.taskbazaar.model.Post;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.List;

@WebServlet("/admin")
public class AdminServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(AdminServlet.class);
    private static final PostService postService = PostService.getInstance();
    private final UserService userService = UserService.getInstance();

    public void doGet(HttpServletRequest req, HttpServletResponse res) {

        try {
            String username = (String) req.getSession().getAttribute("username");
            logger.info("{} try to access admin page", username);
            String userRole = userService.getUserRole(username);
            logger.info("{} is log as {}", username, userRole);
            if ("admin".equals(userRole)) {
                List<Post> posts = postService.getAllPosts();
                logger.info("Total {} posts", posts.size());
                req.setAttribute("posts", posts);
                RequestDispatcher dispatcher = req.getRequestDispatcher("admin.jsp");
                dispatcher.forward(req, res);
            } else {
                logger.info("{} is not admin", username);
                AlertService.sendAlertAndRedirect(res, Constants.UNAUTHORIZED, "/home");
            }
        } catch (Exception e) {
            logger.error("error occurred: {}", e.getMessage());
            req.setAttribute("errorMessage", e.getMessage());
            try {
                req.getRequestDispatcher("error.jsp").forward(req, res);
            } catch (Exception ex) {
                logger.error("Error forwarding to error page: {}", ex.getMessage());
            }
        }
    }
}