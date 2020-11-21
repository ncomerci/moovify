<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<c:url value="/movie/${movie.id}/poster/update" var="action"/>
<form:form modelAttribute="updateMoviePosterForm" action="${action}" method="post" enctype="multipart/form-data">

    <div class="uk-width-1-2 uk-text-right">
            <form:label path="poster">
                <form:input path="poster" type="file" accept="image/*"/>
            </form:label>
        <form:errors path="poster" element="p" cssClass="error uk-margin-remove-top"/>
    </div>

    <div class="uk-text-center uk-margin-medium-top">
        <input class="uk-button uk-button-primary uk-border-rounded extended-button" type="submit" value="<spring:message code="comment.create.button"/>" />
    </div>

</form:form>

</body>
</html>
