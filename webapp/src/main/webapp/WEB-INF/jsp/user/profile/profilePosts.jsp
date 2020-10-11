<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
    <title>
        <spring:message code="user.profile.Profile" arguments="${loggedUser.username}"/>
        <c:if test="${loggedUser.admin}">
            <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false"></span>
        </c:if>
    </title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <script src="<c:url value="/resources/js/components/paginationController.js"/>"></script>
    <script src="<c:url value="/resources/js/user/profile.js" />"></script>
</head>
<body class="min-height-1000">
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<div>
    <c:set var="currentState" value="0" scope="request" />
    <jsp:include page="profile.jsp"/>

    <div class="uk-container">
        <c:if test="${loggedUser.validated}">
            <c:if test="${empty posts.results}">
                <h2 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="user.profile.PostsNotFound"/> </h2>
            </c:if>
            <c:set var="posts" value="${posts}" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>

            <c:if test="${not empty posts.results}">
                <c:set var="collection" value="${posts}" scope="request"/>
                <c:url var="baseURL" value="/user/profile/posts" scope="request"/>
                <c:set var="numberOfInputs" value="${2}" scope="request"/>
                <form action="${baseURL}" method="get">
                    <jsp:include page="/WEB-INF/jsp/components/paginationController.jsp" />
                </form>
            </c:if>
        </c:if>
    </div>

</body>
</html>