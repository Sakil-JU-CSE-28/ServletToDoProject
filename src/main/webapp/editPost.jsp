<%--
  Created by IntelliJ IDEA.
  User: sakil
  Date: 2/6/25
  Time: 3:16 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
    // Redirect if user is not logged in
    if (session.getAttribute("username") == null) {
        response.sendRedirect("index.jsp");
    }
    String postId = request.getParameter("postId");
    System.out.println("Inside editPost.jsp id " + postId);
%>

<!DOCTYPE html>
<html>
<head>
    <title>Create Post</title>
</head>
<body>
<h2>Edit Post</h2>
<form action="edit?postId=<%= postId %>" method="post">
    <label for="title">Title:</label><br>
    <input type="text" id="title" name="title" required><br><br>

    <label for="content">Content:</label><br>
    <textarea id="content" name="content" rows="5" required></textarea><br><br>

    <button type="submit">Submit</button>
</form>
</body>
</html>
