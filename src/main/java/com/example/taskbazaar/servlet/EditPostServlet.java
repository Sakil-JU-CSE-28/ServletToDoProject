package com.example.taskbazaar.servlet;

import com.example.taskbazaar.service.BidService;
import com.example.taskbazaar.service.PostService;
import com.example.taskbazaar.service.Response;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/edit")
public class EditPostServlet extends HttpServlet {
    private final BidService bidService = new BidService();
    private final PostService postService = new PostService();
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String id = request.getParameter("postId");
        System.out.println("EditPostServlet doPost id: " + id);
        String title = request.getParameter("title");
        String description = request.getParameter("content");
        System.out.println("EditPostServlet doPost title: " + title);
        System.out.println("EditPostServlet doPost description: " + description);
        String username = request.getSession().getAttribute("username").toString();
        try {
            if(bidService.isPostOwnedByUser(username,id)){
                Response.sendAlertAndRedirect(response,postService.updatePost(Integer.parseInt(id),title,description) ?
                        "You have successfully edited this post!" : "There is some error to update the post!!","/home");

            }
            else{
                Response.sendAlertAndRedirect(response,"You are not authorized to edit this post!!","/home");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }
}
