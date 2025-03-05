package com.example.taskbazaar.servlet;

import com.example.taskbazaar.utility.PopUpAlert;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public abstract class BaseServlet extends HttpServlet {
    protected void handleError(HttpServletResponse response, String message, HttpServletRequest request) {
        try {
            PopUpAlert.sendAlertAndRedirect(response, message, request.getHeader("Referer"));
        } catch (IOException ex) {
            getServletContext().log("Error redirecting after exception", ex);
        }
    }

    protected void forwardToErrorPage(HttpServletRequest request, HttpServletResponse response) {
        try {
            request.getRequestDispatcher("error.jsp").forward(request, response);
        } catch (Exception ex) {
            getServletContext().log("Error redirecting after exception", ex);
        }
    }
}