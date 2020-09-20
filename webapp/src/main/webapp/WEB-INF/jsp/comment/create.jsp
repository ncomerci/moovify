<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html lang="en" >
    <head>
        <title><spring:message code="comment.create.title"/></title>

        <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
        <jsp:include page="/WEB-INF/jsp/dependencies/mdEditor.jsp" />
    </head>

    <body>
        <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

        <form method="post" action="<c:url value="/comment/create"/>">
            <input type="number" name="postId" class="uk-input uk-form-width-large" placeholder="<spring:message code="comment.create.postIdPlaceholder"/>" required />
            <input type="number" name="parentId" class="uk-input uk-form-width-large" placeholder="<spring:message code="comment.create.parentIdPlaceholder"/>" />
            <input type="text" name="body" class="uk-input uk-form-width-large" placeholder="<spring:message code="comment.create.writeCommentPlaceholder"/>" required />
            <input type="submit" value="Enviar"/>
        </form>
    </body>
</html>