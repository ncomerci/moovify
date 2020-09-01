
<%--
  Created by IntelliJ IDEA.
  User: tobias
  Date: 30/8/20
  Time: 23:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en" >
<head>
    <title>Create Post</title>

    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <jsp:include page="/WEB-INF/jsp/dependencies/mdEditor.jsp" />

    <script src="<c:url value="/resources/js/post/create.js" />"></script>
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

    <form method="post" action="<c:url value="/post/create" />">
        <input type="text" name="title" class="uk-input uk-form-width-large" placeholder="Titulo" required />

        <label>
            <textarea id="createPostData" name="postData" required ></textarea>
        </label>

        <input type="submit" value="Enviar">

    </form>
</body>
</html>
