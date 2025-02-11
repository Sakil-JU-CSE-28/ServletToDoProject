package com.example.taskbazaar.servlet;

import com.example.taskbazaar.model.Post;
import com.example.taskbazaar.service.BidService;
import com.example.taskbazaar.service.PostService;
import com.example.taskbazaar.service.ResponseService;
import com.example.taskbazaar.utility.TaskBazaarLogger;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

@WebServlet({"/home","/save","/edit","/delete"})
public class PostServlet extends HttpServlet {

    private final Logger logger = TaskBazaarLogger.getLogger();
    private final PostService postService = PostService.getInstance();
    private final BidService bidService = BidService.getInstance();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if(request.getServletPath().equals("/home")){
            logger.info("retrieving posts for showing home page.....");
            List<Post> posts = postService.getAllPosts();
            request.setAttribute("posts", posts);
            RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");
            dispatcher.forward(request, response);
        }
    }



    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException{

        String username = request.getSession().getAttribute("username").toString();

        if(request.getServletPath().equals("/save")) {

            String title = request.getParameter("title");
            String content = request.getParameter("content");

            logger.info("saving post...");
            boolean success = postService.createPost(username, title, content);

            if (success) {
                logger.info(username + " created post successfully....");
                response.sendRedirect("/home");
            } else {
                logger.info(username + " try to create post but failed....");
                ResponseService.sendAlertAndRedirect(response,"You are not authorized to access this page!!","/home");
            }
        }
        else if(request.getServletPath().equals("/delete")) {
            int postId = Integer.parseInt(request.getParameter("postId"));

            try {
                PostService postService = PostService.getInstance();
                logger.info("deleting post...");
                boolean deleted = postService.deletePost(postId);

                if (deleted) {
                    logger.info(username + " deleted post successfully....");
                    response.sendRedirect("admin.jsp"); // Refresh page after deletion
                } else {
                    logger.info(username + " try to delete post but failed....");
                    response.getWriter().println("<script>alert('Failed to delete post!'); window.location='admin.jsp';</script>");
                }
            } catch (Exception e) {
                logger.info(username + " try to delete post but failed...." + e.getMessage());
                //throw new ServletException(e);
            }
        }
        else if(request.getServletPath().equals("/edit")) {
            String postId = request.getParameter("postId");
            String title = request.getParameter("title");
            String description = request.getParameter("content");
            logger.info("editing post : " + postId);
            boolean isSuccess = false;
            try {
                isSuccess = bidService.isPostOwnedByUser(username,postId);
                logger.info("isSuccess : " + isSuccess);
            } catch (SQLException e) {
                logger.info("SQL query error "+e.getMessage());
                //throw new RuntimeException(e);
            }
            logger.info("updating post...");
            boolean isUpdated = postService.updatePost(Integer.parseInt(postId),title,description);

            if(isSuccess) {
                logger.info(username + " updated post successfully....");
                    ResponseService.sendAlertAndRedirect(response, isUpdated ?
                            "You have successfully edited this post!" : "There is some error to update the post!!","/home");
                }
                else{
                    logger.info(username + " try to update post but failed....");
                    ResponseService.sendAlertAndRedirect(response,"You are not authorized to edit this post!!","/home");
                }

        }

    }

}