<%--
  Created by IntelliJ IDEA.
  User: sakil
  Date: 1/29/25
  Time: 7:10 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>

<%
  // Redirect if user is not logged in
  if (session.getAttribute("username") == null) {
    response.sendRedirect("index.jsp");
  }
%>

<!DOCTYPE html>
<html>
<head>
  <title>Create Post</title>
</head>
<body>
<h2>Create a New Post</h2>
<form action="save" method="post">
  <label for="title">Title:</label><br>
  <input type="text" id="title" name="title" required><br><br>

  <label for="content">Content:</label><br>
  <textarea id="content" name="content" rows="5" required></textarea><br><br>

  <button type="submit">Submit</button>
</form>
</body>
</html>

