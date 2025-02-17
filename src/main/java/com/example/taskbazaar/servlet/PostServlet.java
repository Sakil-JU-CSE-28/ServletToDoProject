package com.example.taskbazaar.servlet;

import com.example.taskbazaar.model.Post;
import com.example.taskbazaar.service.BidService;
import com.example.taskbazaar.service.PostService;
import com.example.taskbazaar.service.AlertService;
import com.example.taskbazaar.service.UserService;
import com.example.taskbazaar.utility.Constants;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@WebServlet({"/home", "/save", "/edit", "/delete"})
public class PostServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(PostServlet.class);
    private final PostService postService = PostService.getInstance();
    private final BidService bidService = BidService.getInstance();
    private final UserService userService = UserService.getInstance();

    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = request.getServletPath();
            if ("/home".equals(path)) {
                List<Post> posts = postService.getAllPosts();
                logger.info("total posts:: {}", posts.size());
                request.setAttribute("posts", posts);
                RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");
                dispatcher.forward(request, response);
            }
        } catch (Exception e) {
            logger.error("error occurred:: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            try {
                request.getRequestDispatcher("error.jsp").forward(request, response);
            } catch (Exception ex) {
                logger.error("Error forwarding to error page: {}", ex.getMessage());
            }
        }
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String username = request.getSession().getAttribute("username").toString();
            String userRole = userService.getUserRole(username);
            String path = request.getServletPath();
            if ("/save".equals(path)) {

                String title = request.getParameter("title");
                String content = request.getParameter("content");
                if (!"buyer".equals(userRole)) {
                    AlertService.sendAlertAndRedirect(response, Constants.UNAUTHORIZED, "/home");
                    return;
                }

                logger.info("saving post {}", title);
                boolean isSuccess = postService.createPost(username, title, content);

                if (isSuccess) {
                    logger.info("{} post {}", username, title);
                    response.sendRedirect("/home");
                } else {
                    logger.info("{} post {} failed", username, title);
                    AlertService.sendAlertAndRedirect(response, Constants.UNAUTHORIZED, "/home");
                }
            } else if ("/delete".equals(path)) {
                int postId = Integer.parseInt(request.getParameter("postId"));
                if (!"admin".equals(userRole)) {
                    AlertService.sendAlertAndRedirect(response, Constants.UNAUTHORIZED, "/home");
                    return;
                }

                PostService postService = PostService.getInstance();
                logger.info("deleting post {}", postId);
                boolean isDeleted = postService.deletePost(postId);
                if (isDeleted) {
                    logger.info("{} deleted post {}", username, postId);
                    response.sendRedirect("/admin"); // Refresh page after deletion
                } else {
                    logger.info("{} deleted post {} failed", username, postId);
                    AlertService.sendAlertAndRedirect(response, Constants.UNAUTHORIZED, "/home");
                }
            } else if ("/edit".equals(path)) {
                String postId = request.getParameter("postId");
                String title = request.getParameter("title");
                String description = request.getParameter("content");
                logger.info("{} editing post {}", username, postId);
                boolean isSuccess;
                logger.info("checking ownership of {}", username);
                isSuccess = bidService.isPostOwnedByUser(username, postId);
                boolean isUpdated;
                if (isSuccess) {
                    logger.info("{} updated post successfully", username);
                    isUpdated = postService.updatePost(Integer.parseInt(postId), title, description);
                    AlertService.sendAlertAndRedirect(response, isUpdated ? Constants.SUCCESS : Constants.ERROR, "/home");
                } else {
                    logger.info("{} is not owner", username);
                    AlertService.sendAlertAndRedirect(response, Constants.UNAUTHORIZED, "/home");
                }

            } else {
                logger.info("page not found");
                response.sendRedirect("pageNotFound.jsp");
            }
        } catch (Exception e) {
            logger.error("error occurred: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            try {
                request.getRequestDispatcher("error.jsp").forward(request, response);
            } catch (Exception ex) {
                logger.error("Error forwarding to error page: {}", ex.getMessage());
            }
        }
    }
}