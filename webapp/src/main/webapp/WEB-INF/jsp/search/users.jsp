<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="search.pageTitle" arguments="${query}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<main class="uk-container-small uk-margin-auto uk-padding-small">
    <c:url value="/search/users/" var="action"/>
    <form:form modelAttribute="searchUsersForm" method="get" action="${action}">

        <c:set var="query" value="${query}" scope="request" />
        <c:set var="currentSearch" value="2" scope="request" />
        <jsp:include page="/WEB-INF/jsp/search/defaultForm.jsp"/>
    </form:form>
    <dl class="uk-description-list ">
        <c:if test="${empty users.results}">
            <h2 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="search.user.userNotFound"/> </h2>
        </c:if>
        <c:forEach items="${users.results}" var="user">
            <dt>
                <a href="<c:url value="${'/user/'}${user.id}"/>">
                    <c:out value="${user.username}"/>
                </a>
            </dt>
        </c:forEach>
    </dl>
</main>
</body>
</html>