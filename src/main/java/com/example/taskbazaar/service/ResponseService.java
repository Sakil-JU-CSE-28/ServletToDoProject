package com.example.taskbazaar.service;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ResponseService {
    public static void sendAlertAndRedirect(HttpServletResponse response, String message, String redirectUrl) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<script type='text/javascript'>"
                + "alert('" + message + "');"
                + "window.location='" + redirectUrl + "';"
                + "</script>");
        out.flush();
        out.close();
    }
}
