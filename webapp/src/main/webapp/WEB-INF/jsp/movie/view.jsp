<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="movie.view.title" arguments="${movie.title}" /></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <script src="<c:url value="/resources/js/components/paginationController.js"/>"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<main class="uk-article uk-container uk-container-small uk-margin-medium-top">
    <article id="movie">
        <h1 class="uk-text-bold uk-h1 uk-margin-remove-adjacent uk-margin-remove-top"><c:out value="${movie.title}"/></h1>
        <img src="<c:url value="/movie/poster/${movie.posterId}"/>"/>
        <hr class="uk-divider-icon">
        <p class="uk-article-meta"><spring:message code="movie.view.meta.releaseDate" arguments="${movie.releaseDate}"/></p>
        <p class="uk-article-meta"><spring:message code="movie.view.meta.popularity" arguments="${movie.popularity}"/></p>
        <p class="uk-article-meta"><spring:message code="movie.view.meta.originalLanguage" arguments="${movie.originalLanguage}"/></p>
        <p class="uk-article-meta"><spring:message code="movie.view.meta.originalTitle" arguments="${movie.originalTitle}"/></p>
        <p class="uk-article-meta"><spring:message code="movie.view.meta.voteAverage" arguments="${movie.voteAverage}"/></p>
        <p class="uk-article-meta"><spring:message code="movie.view.meta.movieCategories.title"/></p>
        <ul>
            <c:forEach items="${movie.categories}" var="category">
                <li>
                    <spring:message code="search.movies.categories.${category.name}"/>
                </li>
            </c:forEach>
        </ul>
        <h2 class="uk-h4"><spring:message code="movie.view.overview"/> </h2>
        <p class="uk-text-normal">
            <c:out value="${movie.overview}"/>
        </p>
    </article>
    <hr>
    <section id="movie-posts">
        <h1 class="uk-h2">Posts about this movie</h1>
        <c:set var="posts" value="${posts}" scope="request"/>
        <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>
        <c:if test="${empty posts.results}">
            <h1 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="movie.view.posts.postsNotFound"/> </h1>
        </c:if>
        <c:if test="${not empty posts.results}">
            <c:set var="collection" value="${posts}" scope="request"/>
            <c:url var="baseURL" value="/movie/${movieId}" context="/" scope="request"/>
            <c:set var="numberOfInputs" value="${2}" scope="request"/>
            <form action="<c:url value="${baseURL}"/>" method="get">
                <jsp:include page="/WEB-INF/jsp/components/paginationController.jsp" />
            </form>
        </c:if>

    </section>
</main>
</body>
</html>