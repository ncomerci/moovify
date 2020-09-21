<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="search.pageTitle" arguments="${query}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<main class="uk-container-small uk-margin-auto uk-padding-small">
    <c:url value="/search/movies/" var="action"/>
    <form:form modelAttribute="searchMoviesForm" method="get" action="${action}">

        <c:set var="query" value="${query}" scope="request" />
        <c:set var="currentSearch" value="1" scope="request" />
        <jsp:include page="/WEB-INF/jsp/search/defaultForm.jsp"/>
    </form:form>
    <dl class="uk-description-list ">
        <c:forEach items="${movies}" var="movie">
            <dt>
                <a href="<c:url value="/movie/${movie.id}"/>">
                    <c:out value="${movie.title}"/>
                </a>
            </dt>
            <dd>
                    <span class="uk-text-light uk-text-muted uk-text-small">
                       Fecha de estreno: <c:out value="${movie.premierDate}"/>
                    </span>
            </dd>
        </c:forEach>
    </dl>
</main>
</body>
</html>
