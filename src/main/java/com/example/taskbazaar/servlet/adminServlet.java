package com.example.taskbazaar.servlet;

import com.example.taskbazaar.service.AdminService;
import com.example.taskbazaar.service.ResponseService;
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
    private final AdminService adminService = AdminService.getInstance();
    private final UserService userService = UserService.getInstance();
    public void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException, ServletException {

        String username = (String) req.getSession().getAttribute("username");
        System.out.println(username + "try to access admin page");

        String userRole = null;
        try {
            userRole = userService.getUserRole(username);
        } catch (SQLException e) {
            log("userRole = " + userRole + " retrieve error");
           // throw new RuntimeException(e);
        }

        if("admin".equals(userRole)) {
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
                ResponseService.sendAlertAndRedirect(res,"Unauthorized!!!","/home");

            }


    }
}
