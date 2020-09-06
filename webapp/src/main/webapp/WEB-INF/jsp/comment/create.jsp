<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en" >
    <head>
        <title>New Comment</title>

        <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
        <jsp:include page="/WEB-INF/jsp/dependencies/mdEditor.jsp" />
    </head>

    <body>
        <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

        <form method="post" action="<c:url value="/comment/create"/>">
            <input type="number" name="postId" class="uk-input uk-form-width-large" placeholder="Post ID" required />
            <input type="number" name="parentId" class="uk-input uk-form-width-large" placeholder="Parent ID" />
            <input type="email" name="userEmail" class="uk-input uk-form-width-large" placeholder="Email" required />
            <input type="text" name="body" class="uk-input uk-form-width-large" placeholder="Write your comment here..." required />
            <input type="submit" value="Enviar"/>
        </form>
    </body>
</html>