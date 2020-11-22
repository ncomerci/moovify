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
    <article id="movie" class="uk-flex uk-flex-wrap">

        <div class="uk-width-1-1">
            <h1 class="uk-text-bold uk-h1 uk-margin-remove-adjacent uk-margin-remove-top">

                <spring:message code="movie.view.pageTitle" arguments="${movie.title};${movie.releaseDate.year}"  htmlEscape="false"
                                argumentSeparator=";"/></h1>
        </div>

        <div class="uk-width-max-content">
            <img class="movie-poster" src="<c:url value="/movie/poster/${movie.posterId}"/>"
                 alt="<spring:message code="movie.view.poster.altText"/>"/>
        </div>
        <div class="uk-width-expand uk-padding-large uk-padding-remove-top uk-padding-remove-bottom">

            <dl class="uk-description-list uk-margin-small-top">
                <dt><spring:message code="movie.view.meta.originalTitle"/></dt>
                <dd>${movie.originalTitle}</dd>
                <dt><spring:message code="movie.view.meta.releaseDate"/></dt>
                <dd>${movie.releaseDate}</dd>
                <dt><spring:message code="movie.view.meta.originalLanguage"/></dt>
                <dd>${movie.originalLanguage}</dd>
                <dt><spring:message code="movie.view.meta.voteAverage"/></dt>
                <dd>${movie.voteAverage}</dd>
                <dt><spring:message code="movie.view.meta.movieCategories.title"/></dt>
                <dd>
                    <ul class="uk-padding-small uk-padding-remove-right uk-padding-remove-bottom">
                        <c:forEach items="${movie.categories}" var="category">
                            <li>
                                <c:url value="/search/movies" var="categoryURL">
                                    <c:param name="query" value=""/>
                                    <c:param name="movieCategory" value="${category.name}"/>
                                </c:url>
                                <a href="${categoryURL}"><spring:message code="search.movies.categories.${category.name}"/></a>
                            </li>
                        </c:forEach>
                    </ul>
                </dd>
            </dl>
        </div>
        <div class="uk-width-1-1 uk-margin-medium-top">
            <h2 class="uk-h2"><spring:message code="movie.view.overview"/> </h2>
            <p class="uk-text-normal">
                <c:out value="${movie.overview}"/>
            </p>
        </div>
    </article>
    <hr>
    <section id="movie-posts">
        <h1 class="uk-h2"><spring:message code="movie.view.postsSection.title"/> </h1>
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