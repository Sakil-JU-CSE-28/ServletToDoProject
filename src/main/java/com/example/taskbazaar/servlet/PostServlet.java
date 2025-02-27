/*
 * author : Md. Sakil Ahmed
 */

package com.example.taskbazaar.servlet;

import com.example.taskbazaar.dto.PostDTO;
import com.example.taskbazaar.exception.AuthenticationException;
import com.example.taskbazaar.exception.BidException;
import com.example.taskbazaar.exception.PostException;
import com.example.taskbazaar.exception.ValidationException;
import com.example.taskbazaar.service.BidService;
import com.example.taskbazaar.service.PostService;
import com.example.taskbazaar.utility.PopUpAlert;
import com.example.taskbazaar.service.UserService;
import com.example.taskbazaar.utility.Constant;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

@WebServlet({"/home", "/save", "/edit", "/delete", "/view"})
public class PostServlet extends HttpServlet {

    private final Logger logger = LoggerFactory.getLogger(PostServlet.class);
    private final PostService postService = PostService.getInstance();
    private final BidService bidService = BidService.getInstance();
    private final UserService userService = UserService.getInstance();


    public void doGet(HttpServletRequest request, HttpServletResponse response) {
        try {
            String path = request.getServletPath();
            if ("/home".equals(path)) {
                List<PostDTO> posts = postService.getPosts();
                logger.info("total posts:: {}", posts.size());
                request.setAttribute("posts", posts);
                RequestDispatcher dispatcher = request.getRequestDispatcher("home.jsp");
                dispatcher.forward(request, response);
            }
        } catch (PostException e) {
            logger.error("exception in retrieving posts:: {}", e.getMessage());
            try {
                PopUpAlert.sendAlertAndRedirect(response, e.getMessage(), request.getHeader("Referer"));
            } catch (IOException ex) {
                logger.error("Forward error in retrieving post:: {}", ex.getMessage());
            }
        } catch (Exception e) {
            logger.error("error occurred:: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            try {
                request.getRequestDispatcher("error.jsp").forward(request, response);
            } catch (Exception ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        }
    }


    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            String username = request.getSession().getAttribute("username").toString();
            String userRole = userService.getRole(username);
            String path = request.getServletPath();
            switch (path) {
                case "/save" -> {

                    String title = request.getParameter("title");
                    String content = request.getParameter("content");
                    if (!"buyer".equals(userRole)) {
                        PopUpAlert.sendAlertAndRedirect(response, Constant.UNAUTHORIZED, "/home");
                        return;
                    }

                    logger.info("saving post {}", title);
                    boolean isSuccessful = postService.create(username, title, content);

                    if (isSuccessful) {
                        logger.info("{} post {}", username, title);
                        response.sendRedirect("/home");
                    } else {
                        logger.info("{} post {} failed", username, title);
                        PopUpAlert.sendAlertAndRedirect(response, Constant.UNAUTHORIZED, "/home");
                    }
                }
                case "/delete" -> {
                    int postId = Integer.parseInt(request.getParameter("postId"));
                    if (!"admin".equals(userRole)) {
                        PopUpAlert.sendAlertAndRedirect(response, Constant.UNAUTHORIZED, "/home");
                        return;
                    }

                    PostService postService = PostService.getInstance();
                    logger.info("deleting post {}", postId);
                    boolean isDeleted = postService.delete(postId);
                    if (isDeleted) {
                        logger.info("{} deleted post {}", username, postId);
                        response.sendRedirect("/home"); // Refresh page after deletion
                    } else {
                        logger.info("{} deleted post {} failed", username, postId);
                        PopUpAlert.sendAlertAndRedirect(response, Constant.UNAUTHORIZED, "/home");
                    }
                }
                case "/edit" -> {
                    String postId = request.getParameter("postId");
                    String title = request.getParameter("title");
                    String description = request.getParameter("content");
                    logger.info("{} editing post {}", username, postId);
                    boolean isSuccess;
                    logger.info("checking ownership of {}", username);
                    isSuccess = bidService.isPostOwner(username, postId);
                    boolean isUpdated;
                    if (isSuccess) {
                        logger.info("{} updated post successfully", username);
                        isUpdated = postService.update(Integer.parseInt(postId), title, description);
                        PopUpAlert.sendAlertAndRedirect(response, isUpdated ? Constant.SUCCESS : Constant.ERROR, "/home");
                    } else {
                        logger.info("{} is not owner", username);
                        PopUpAlert.sendAlertAndRedirect(response, Constant.UNAUTHORIZED, "/home");
                    }

                }
                case "/view" -> {
                    String postId = request.getParameter("postId");
                    System.out.println(postId);
                    if (postId == null) {
                        response.sendRedirect("/home");
                        return;
                    }

                    PostDTO post = postService.getById(Integer.parseInt(postId));
                    if (post == null) {
                        request.setAttribute("errorMessage", "Post not found!");
                        request.getRequestDispatcher("error.jsp").forward(request, response);
                        return;
                    }
                    System.out.println(post.title() + "::" + post.content() + "::" + post.author());
                    HttpSession session = request.getSession();
                    session.setAttribute("postId", postId);
                    session.setAttribute("post", post);
                    request.getRequestDispatcher("viewPost.jsp").forward(request, response);
                }
                case null, default -> {
                    logger.info("page not found");
                    response.sendRedirect("pageNotFound.jsp");
                }
            }
        } catch (PostException e) {
            logger.error("error in retrieving post:: {}", e.getMessage());
            try {
                PopUpAlert.sendAlertAndRedirect(response, e.getMessage(), request.getHeader("Referer"));
            } catch (IOException ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        } catch (AuthenticationException e) {
            logger.error("error authenticating in postservlet:: {}", e.getMessage());
            try {
                PopUpAlert.sendAlertAndRedirect(response, e.getMessage(), request.getHeader("Referer"));
            } catch (IOException ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        } catch (ValidationException e) {
            logger.error("error validation in postservlet:: {}", e.getMessage());
            try {
                PopUpAlert.sendAlertAndRedirect(response, e.getMessage(), request.getHeader("Referer"));
            } catch (IOException ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        } catch (BidException e) {
            logger.error("error bid:: {}", e.getMessage());
            try {
                PopUpAlert.sendAlertAndRedirect(response, e.getMessage(), request.getHeader("Referer"));
            } catch (IOException ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        } catch (Exception e) {
            logger.error("error occurred: {}", e.getMessage());
            request.setAttribute("errorMessage", e.getMessage());
            try {
                request.getRequestDispatcher("error.jsp").forward(request, response);
            } catch (Exception ex) {
                logger.error(Constant.FORWARD_ERROR, ex.getMessage());
            }
        }
    }
}