package com.example.taskbazaar.servlet;

import com.example.taskbazaar.model.Post;
import com.example.taskbazaar.service.PostService;
import com.example.taskbazaar.service.Response;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet({"/home","/save"})
public class PostServlet extends HttpServlet {

    private final PostService postService = new PostService();
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        if(request.getServletPath().equals("/home")){
            List<Post> posts = postService.getAllPosts();
            request.setAttribute("posts", posts);
            RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");
            dispatcher.forward(request, response);
        }

    }



    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        if(request.getServletPath().equals("/save")) {
            HttpSession session = request.getSession(false);
            String user = (session != null) ? (String) session.getAttribute("username") : null;
            String title = request.getParameter("title");
            String content = request.getParameter("content");

            boolean success = postService.createPost(user, title, content);
            if (success) {
                response.sendRedirect("/home");
            } else {
                Response.sendAlertAndRedirect(response,"You are not authorized to access this page!!","/home");
            }
        }
    }

}
