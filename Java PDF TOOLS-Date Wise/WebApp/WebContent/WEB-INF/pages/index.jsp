<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
	pageEncoding="ISO-8859-1"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="ISO-8859-1">
<title>Insert title here</title>
</head>
<body>Hello This is your first request!!

<form action="/WebApp/test" method="post" enctype="multipart/form-data">
	<!--  Name: <input type="text" name=userName><br>
	Phone: <input type="tel" name=mobile><br>
	Email: <input type="email" name=email><br>
	 Password: <input type="password" name=Password><br>-->
	 <input type="file" name="userfiles">
	 <input type="file" name="userfiles">
	 <input type="text" name=userName>
	 <input type="submit">
</form>
<br>
Hello this is Dynamic value<br>
${ abc }
</body>
</html>