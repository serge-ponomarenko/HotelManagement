<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>welcome page</title>
</head>
<body bgcolor="silver">
Welcome page
<form action="/logoutAction" method="post">
    <input type="submit" value="logout">
</form>
<%
    if(session.getAttribute("user")==null)
    {
        response.sendRedirect("sign-in.jsp");
    }

%>
${sessionScope.user.getId()}

<form action="FileUploadServlet" method="post" enctype="multipart/form-data">
    Select File to Upload:<input type="file" name="fileName">
    <br>
    <input type="submit" value="Upload">
</form>


</body>
</html>