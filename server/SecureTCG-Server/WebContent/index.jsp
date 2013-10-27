<%@ page language="java" contentType="text/html; charset=ISO-8859-1" pageEncoding="ISO-8859-1"%>
<%@taglib uri="http://struts.apache.org/tags-bean" prefix="bean" %>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>

<head>
	<meta charset="ISO-8859-1">
	<title>Secure TCG</title>
</head>

<body>
	Welcome to Secure TCG page. <br/>
	<form action="test.do" method="post">
	Description: <input type="text" name="message"> </br>
	Id: <input type="text" name="searchId"> </br>
	<input type="submit" value="GO!">
	</form>
</body>

</html>