<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en" >
<head>
    <title>New Movie</title>

    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <jsp:include page="/WEB-INF/jsp/dependencies/mdEditor.jsp" />

    <script src="<c:url value="/resources/js/post/create.js" />"></script>

</head>

<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<form method="post" action="<c:url value="/movie/register"/>">
    <input type="text" name="title" class="uk-input uk-form-width-large" placeholder="Titulo" required />
    <input type="date" name="premierDate" class="uk-input uk-form-width-large" placeholder="Premier Date" required />

    <input type="submit" value="Enviar">

</form>
</body>
</html>