<%--
  Created by IntelliJ IDEA.
  User: sakil
  Date: 2/4/25
  Time: 5:28 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Work History</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            margin: 20px;
        }
        h2 {
            color: #333;
        }
        ul {
            list-style-type: none;
            padding: 0;
        }
        li {
            padding: 8px;
            background: #f4f4f4;
            margin-bottom: 5px;
            border-radius: 5px;
        }
    </style>
</head>
<body>

<h2>Work History</h2>

<%
    List<String> works = (List<String>) request.getAttribute("works");
    if (works != null && !works.isEmpty()) {
%>
<ul>
    <% for (String work : works) { %>
    <li><%= work %></li>
    <% } %>
</ul>
<%
} else {
%>
<p>No work history found.</p>
<%
    }
%>

</body>
</html>

