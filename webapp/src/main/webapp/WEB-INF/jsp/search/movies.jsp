<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="search.pageTitle" arguments="${query}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp"/>

<main class="uk-container-small uk-margin-auto uk-padding-small">
    <section id="search-controllers">
        <c:url value="/search/movies/" var="action"/>
        <form:form modelAttribute="searchMoviesForm" method="get" action="${action}">
            <c:set var="query" value="${query}" scope="request"/>
            <c:set var="currentSearch" value="1" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/search/defaultForm.jsp"/>
        </form:form>
    </section>
    <section id="search-results uk-flex uk-flex-wrap">
        <c:if test="${empty movies}">
            <h1 class="uk-text-meta uk-text-center uk-text-bold"><spring:message
                    code="search.movies.moviesNotFound"/></h1>
        </c:if>
        <c:forEach items="${movies}" var="movie">
            <div class="uk-width-1-1">
                <div class="uk-flex">
                    <div class="uk-width-expand uk-margin-small-top">
                        <a href="<c:url value="/movie/${movie.id}"/>">
                            <c:out value="${movie.title}"/>
                        </a>
                        <p class="uk-text-capitalize uk-text-meta uk-margin-remove-vertical">
                            <c:forEach items="${movie.categories}" var="category">
                                <c:out value="${category.name}"/> -
                            </c:forEach>
                            <fmt:parseDate value="${movie.releaseDate}" pattern="yyyy-MM-dd" var="parsedDateTime" />
                            <fmt:formatDate pattern="yyyy" value="${parsedDateTime}" />
                        </p>
                    </div>
                </div>
            </div>
        </c:forEach>
    </section>
</main>
</body>
</html>
