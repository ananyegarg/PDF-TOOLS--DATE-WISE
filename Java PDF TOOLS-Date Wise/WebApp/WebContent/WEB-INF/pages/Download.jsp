<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>
	<form action="/WebApp/download" method="post">
		<input type="hidden" name="filepath" value=${file}>
		<button type="submit">DOWNLOAD</button>
	</form>
</body>
</html>