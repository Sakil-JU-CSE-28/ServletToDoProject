package com.example.taskbazaar;

import dao.UserDao;
import data.User;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.sql.*;

@WebServlet(name = "SavePostServlet", value = "/save")
public class SavePostServlet extends HttpServlet {

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setHeader("Cache-Control", "no-cache, no-store, must-revalidate");
        response.setHeader("Pragma", "no-cache");
        response.setHeader("Expires", "0");

        HttpSession session = request.getSession(false);
        String user = (String) session.getAttribute("username");
        System.out.println(user);
        String title = request.getParameter("title");
        String content = request.getParameter("content");

        UserDao userDao = new UserDao();
        Pair<Statement, Connection> con = null;
        try {
            con = userDao.connect();
            Statement statement = con.getLeft();
            Connection connection = con.getRight();
            ResultSet resultSet = statement.executeQuery("select * from users where username = '" + user + "'");
            resultSet.next();
            String role = resultSet.getString("role");

            if (role.equals("buyer")) {
                String insertQuery = "INSERT INTO posts (title, description, author_username) VALUES (?, ?, ?)";
                PreparedStatement stmt = connection.prepareStatement(insertQuery);
                stmt.setString(1, title);
                stmt.setString(2, content);
                stmt.setString(3, user);

                stmt.executeUpdate();

                stmt.close();
                response.sendRedirect("/home");
            }
            else{
                response.setContentType("text/html");
                response.getWriter().println("<script type='text/javascript'>"
                        + "alert('You are not authorized to access this page!');"
                        + "window.location='/home';"
                        + "</script>");
            }

            resultSet.close();


            connection.close();

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
        finally {
            try {
                con.getRight().close();
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }
    }

}
