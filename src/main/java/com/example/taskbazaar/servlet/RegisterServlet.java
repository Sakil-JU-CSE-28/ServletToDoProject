package com.example.taskbazaar.servlet;

import com.example.taskbazaar.model.User;
import com.example.taskbazaar.service.RegisterService;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;


@WebServlet(name = "RegisterServlet", value = "/reg")
public class RegisterServlet  extends HttpServlet {
    private final RegisterService registerService = new RegisterService();
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
       String name = request.getParameter("username");
       String password = request.getParameter("password");
       String role = request.getParameter("role");

       User user = new User(name, password, role);

        try {
            boolean registerd = registerService.register(user);
            if(registerd){
                response.sendRedirect("index.jsp");
            }
            else{
                response.sendRedirect("register.jsp");
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}
