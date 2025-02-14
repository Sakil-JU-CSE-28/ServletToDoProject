<%--
  Created by IntelliJ IDEA.
  User: sakil
  Date: 2/12/25
  Time: 10:12 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Page Not Found</title>
    <style>
        body {
            font-family: Arial, sans-serif;
            background-color: #f8f9fa;
            color: #333;
            text-align: center;
            padding: 50px;
        }
        h1 {
            font-size: 50px;
            color: #e74c3c;
        }
        p {
            font-size: 20px;
        }
    </style>
</head>
<body>
<h1>404 - Page Not Found</h1>
<p>Sorry, the page you are looking for does not exist.</p>
<p><a href="${pageContext.request.contextPath}/home">Go back to the homepage</a></p>
</body>
</html>

