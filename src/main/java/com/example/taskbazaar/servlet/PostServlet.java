package com.example.taskbazaar.servlet;

import com.example.taskbazaar.model.Post;
import com.example.taskbazaar.service.BidService;
import com.example.taskbazaar.service.PostService;
import com.example.taskbazaar.service.ResponseService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet({"/home","/save","/edit","/deletePost"})
public class PostServlet extends HttpServlet {

    private final PostService postService = PostService.getInstance();
    private final BidService bidService = BidService.getInstance();

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if(request.getServletPath().equals("/home")){
            List<Post> posts = postService.getAllPosts();
            request.setAttribute("posts", posts);
            RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");
            dispatcher.forward(request, response);
        }
    }



    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException {

        String username = request.getSession().getAttribute("username").toString();

        if(request.getServletPath().equals("/save")) {

            String title = request.getParameter("title");
            String content = request.getParameter("content");

            boolean success = postService.createPost(username, title, content);
            if (success) {
                response.sendRedirect("/home");
            } else {
                ResponseService.sendAlertAndRedirect(response,"You are not authorized to access this page!!","/home");
            }
        }
        else if(request.getServletPath().equals("/edit")) {
            String postId = request.getParameter("postId");
            System.out.println("EditPostServlet doPost id: " + postId);
            String title = request.getParameter("title");
            String description = request.getParameter("content");
            System.out.println("EditPostServlet doPost title: " + title);
            System.out.println("EditPostServlet doPost description: " + description);

            try {
                if(bidService.isPostOwnedByUser(username,postId)){
                    ResponseService.sendAlertAndRedirect(response,postService.updatePost(Integer.parseInt(postId),title,description) ?
                            "You have successfully edited this post!" : "There is some error to update the post!!","/home");

                }
                else{
                    ResponseService.sendAlertAndRedirect(response,"You are not authorized to edit this post!!","/home");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
        else if(request.getServletPath().equals("/deletePost")) {
            int postId = Integer.parseInt(request.getParameter("postId"));

            try {
                PostService postService = PostService.getInstance();
                boolean deleted = postService.deletePost(postId);

                if (deleted) {
                    response.sendRedirect("admin.jsp"); // Refresh page after deletion
                } else {
                    response.getWriter().println("<script>alert('Failed to delete post!'); window.location='admin.jsp';</script>");
                }
            } catch (Exception e) {
                throw new ServletException(e);
            }
        }
    }

}
