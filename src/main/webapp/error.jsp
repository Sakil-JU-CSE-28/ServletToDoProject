<%--
  Created by IntelliJ IDEA.
  User: sakil
  Date: 2/13/25
  Time: 12:37 AM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <title>Error - Something Went Wrong</title>
    <style>
        body { font-family: Arial, sans-serif; text-align: center; padding: 50px; }
        .error-container { max-width: 500px; margin: auto; padding: 20px; border-radius: 10px; background: #ffebee; }
        h1 { color: #d32f2f; }
        p { color: #555; }
    </style>
</head>
<body>
<div class="error-container">
    <h1>Oops! Something went wrong</h1>
    <p><%= request.getAttribute("errorMessage") != null ? request.getAttribute("errorMessage") : "An unexpected error occurred." %></p>
    <a href="<%= request.getHeader("Referer") %>">Go Back</a>
</div>
</body>
</html>
