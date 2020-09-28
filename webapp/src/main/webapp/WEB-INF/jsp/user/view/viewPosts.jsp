<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="user.view.Profile" arguments="${user.username}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
<div>
    <c:set var="currentState" value="0" scope="request" />
    <jsp:include page="view.jsp"/>

    <div class="uk-container">
        <c:if test="${empty posts}">
            <h2 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="user.view.postsNotFound"/> </h2>
        </c:if>
        <c:set var="posts" value="${posts}" scope="request"/>
        <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>
    </div>
</div>
</body>
</html>