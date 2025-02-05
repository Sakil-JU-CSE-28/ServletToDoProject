package com.example.taskbazaar.servlet;

import com.example.taskbazaar.service.PostService;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebServlet("/deletePost")
public class DeletrePostServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String postId = request.getParameter("postId");

        try {
            PostService postService = new PostService();
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
