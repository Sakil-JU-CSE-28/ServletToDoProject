package com.example.taskbazaar.servlet;

import com.example.taskbazaar.service.AdminService;
import com.example.taskbazaar.service.Response;
import com.example.taskbazaar.service.UserService;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.example.taskbazaar.model.Post;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

@WebServlet("/admin")
public class adminServlet extends HttpServlet {
    private final AdminService adminService = new AdminService();
    private final UserService userService = new UserService();
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
        String username = (String) req.getSession().getAttribute("username");
        System.out.println(username);

        try {
            if(userService.getUserRole(username).equals("admin")) {
                List<Post> posts = null;
                try {
                    posts = adminService.getAllPost();
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
                req.setAttribute("posts", posts);
                RequestDispatcher dispatcher = req.getRequestDispatcher("admin.jsp");
                dispatcher.forward(req, res);
            }
            else{
                System.out.println("adminservlet : unauthorized user try to access this page");
                Response.sendAlertAndRedirect(res,"Unauthorized!!!","/home");


            }
        } catch (SQLException | ServletException e) {
            throw new RuntimeException(e);
        }

    }
}
