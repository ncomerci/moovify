<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><c:out value="${movie.title}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<main class="uk-article uk-container uk-container-small uk-margin-medium-top">
    <article id="movie">
        <h1 class="uk-text-bold uk-h1 uk-margin-remove-adjacent uk-margin-remove-top"><c:out value="${movie.title}"/></h1>
        <p class="uk-article-meta"><spring:message code="movie.view.meta.releaseDate" arguments="${movie.releaseDate}"/></p>
        <p class="uk-article-meta"><spring:message code="movie.view.meta.popularity" arguments="${movie.popularity}"/></p>
        <p class="uk-article-meta"><spring:message code="movie.view.meta.originalLanguage" arguments="${movie.originalLanguage}"/></p>
        <p class="uk-article-meta"><spring:message code="movie.view.meta.originalTitle" arguments="${movie.originalTitle}"/></p>
        <p class="uk-article-meta"><spring:message code="movie.view.meta.voteAverage" arguments="${movie.voteAverage}"/></p>
        <p class="uk-article-meta"><spring:message code="movie.view.meta.movieCategories.title"/></p>
        <ul>
            <c:forEach items="${movie.categories}" var="category">
                <li>
                    <c:out value="${category.name}"/>
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
    </section>
</main>
</body>
</html>