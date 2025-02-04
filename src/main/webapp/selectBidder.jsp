<%@ page import="java.util.List" %>
<%--
  Created by IntelliJ IDEA.
  User: sakil
  Date: 2/4/25
  Time: 4:00 PM
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
  <title>Select a Bidder</title>
</head>
<body>
<h2>Select a Bidder</h2>
<form action="addBidder" method="post">
  <select name="selectedBidder">
    <%
      List<String> bidders = (List<String>) request.getAttribute("bidders");
      if (bidders != null) {
        for (String bidder : bidders) {
    %>
    <option value="<%= bidder %>"><%= bidder %></option>
    <%
        }
      }
    %>
  </select>
  <input type="submit" value="Add Bidder">
</form>
</body>
</html>