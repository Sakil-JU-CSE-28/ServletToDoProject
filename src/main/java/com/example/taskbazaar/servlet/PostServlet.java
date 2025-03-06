/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.servlet;

import com.example.taskbazaar.dto.PostDTO;
import com.example.taskbazaar.dto.UserDTO;
import com.example.taskbazaar.enums.Role;
import com.example.taskbazaar.exception.AuthenticationException;
import com.example.taskbazaar.exception.BidException;
import com.example.taskbazaar.exception.PostException;
import com.example.taskbazaar.exception.ValidationException;
import com.example.taskbazaar.service.PostService;
import com.example.taskbazaar.utility.PopUpAlert;
import com.example.taskbazaar.service.UserService;
import com.example.taskbazaar.utility.Constants;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

@WebServlet({"/home", "/save", "/edit", "/delete", "/view"})
public class PostServlet extends BaseServlet {

    private final Logger logger = LoggerFactory.getLogger(PostServlet.class);
    private final PostService postService = PostService.getInstance();
    private final UserService userService = UserService.getInstance();


    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = request.getServletPath();
            if ("/home".equals(path)) {
                List<PostDTO> posts = postService.getPosts();
                logger.info("Retrieved {} posts successfully.", posts.size());
                request.setAttribute("posts", posts);
                RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");
                dispatcher.forward(request, response);
            }
        } catch (PostException e) {
            logger.error("Error retrieving posts: {}", e.getMessage(), e);
            handleError(response, e.getMessage(), request);
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            forwardToErrorPage(request, response);
        }
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String username = request.getSession().getAttribute("username").toString();
            UserDTO userDTO = userService.getByUsername(username);
            String userRole = userDTO.role();
            String path = request.getServletPath();
            switch (path) {
                case "/save" -> {
                    String title = request.getParameter("title");
                    String content = request.getParameter("content");
                    if (!Role.BUYER.getValue().equals(userRole)) {
                        logger.warn("Unauthorized attempt to create post by user: {}", username);
                        PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.UNAUTHORIZED, "/home");
                        return;
                    }

                    logger.info("User '{}' attempting to create a post with title '{}'.", username, title);
                    boolean isSuccessful = postService.create(username, title, content);

                    if (isSuccessful) {
                        logger.info("User '{}' successfully created post '{}'.", username, title);
                        response.sendRedirect("/home");
                    } else {
                        logger.warn("Post creation failed for user '{}', title '{}'.", username, title);
                        PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.UNAUTHORIZED, "/home");
                    }
                }
                case "/delete" -> {
                    int postId = Integer.parseInt(request.getParameter("postId"));
                    if (!Role.ADMIN.getValue().equals(userRole)) {
                        logger.warn("Unauthorized delete attempt by user '{}' for post '{}'.", username, postId);
                        PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.UNAUTHORIZED, "/home");
                        return;
                    }

                    logger.info("User '{}' attempting to delete post '{}'.", username, postId);
                    boolean isDeleted = postService.delete(postId);
                    if (isDeleted) {
                        logger.info("User '{}' successfully deleted post '{}'.", username, postId);
                        response.sendRedirect("/home");
                    } else {
                        logger.warn("Failed to delete post '{}' by user '{}'.", postId, username);
                        PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.UNAUTHORIZED, "/home");
                    }
                }
                case "/edit" -> {
                    String postId = request.getParameter("postId");
                    String title = request.getParameter("title");
                    String description = request.getParameter("content");
                    logger.info("User '{}' attempting to edit post '{}'.", username, postId);

                    PostDTO postDTO = postService.getById(Integer.parseInt(postId));
                    boolean isUpdated;
                    if (postDTO.author().equals(username)) {
                        isUpdated = postService.update(Integer.parseInt(postId), title, description);
                        logger.info("User '{}' successfully updated post '{}'.", username, postId);
                        PopUpAlert.sendAlertAndRedirect(response, isUpdated ? Constants.Constant.SUCCESS : Constants.Error.ERROR, "/home");
                    } else {
                        logger.warn("User '{}' attempted to edit post '{}' but is not the owner.", username, postId);
                        PopUpAlert.sendAlertAndRedirect(response, Constants.Constant.UNAUTHORIZED, "/home");
                    }

                }
                case "/view" -> {
                    String postId = request.getParameter("postId");
                    logger.info("User '{}' attempting to view post '{}'.", username, postId);
                    if (postId == null) {
                        logger.warn("Post ID is missing in request. Redirecting to home.");
                        response.sendRedirect("/home");
                        return;
                    }

                    PostDTO postDTO = postService.getById(Integer.parseInt(postId));
                    if (postDTO == null) {
                        logger.warn("Post with ID '{}' not found. Redirecting to error page.", postId);
                        request.setAttribute("errorMessage", "Post not found!");
                        request.getRequestDispatcher("error.jsp").forward(request, response);
                        return;
                    }
                    logger.info("User '{}' viewing post '{}'.", username, postId);
                    HttpSession session = request.getSession();
                    session.setAttribute("postId", postId);
                    session.setAttribute("post", postDTO);
                    request.getRequestDispatcher("viewPost.jsp").forward(request, response);
                }
                case null, default -> {
                    logger.warn("Invalid request path: '{}'. Redirecting to pageNotFound.jsp.", path);
                    response.sendRedirect("pageNotFound.jsp");
                }
            }
        } catch (PostException | AuthenticationException | ValidationException | BidException e) {
            logger.error("error occurred:: {}", e.getMessage(), e);
            handleError(response, e.getMessage(), request);
        } catch (Exception e) {
            logger.error("Unexpected error occurred: {}", e.getMessage(), e);
            request.setAttribute("errorMessage", "An unexpected error occurred. Please try again later.");
            forwardToErrorPage(request, response);
        }
    }

}