<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><c:out value="${movie.title}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<article class="uk-article">
    <div class="uk-container uk-container-small">
        <div>
            <h2 class="uk-text-bold uk-h1 uk-margin-remove-adjacent uk-margin-remove-top"><c:out value="${movie.title}"/></h2>
            <p class="uk-article-meta"> Premier date: <c:out value="${movie.releaseDate}"/></p>
            <p class="uk-article-meta"> Popularity: <c:out value="${movie.popularity}"/></p>
            <p class="uk-article-meta"> Original language: <c:out value="${movie.originalLanguage}"/></p>
            <p class="uk-article-meta"> Original title: <c:out value="${movie.originalTitle}"/></p>
            <p class="uk-article-meta"> Vote Average: <c:out value="${movie.voteAverage}"/></p>
            <p class="uk-article-meta"> Movie categories: </p>
            <c:forEach items="${movie.categories}" var="category">
                <li>
                    <c:out value="${category.name}"/>
                </li>
            </c:forEach>
            <p class="uk-article-meta"> Overview: <c:out value="${movie.overview}"/></p>
        </div>
        <hr>
        <h1>Posts about this movie</h1>
        <c:forEach items="${posts}" var="post">
            <li>
                <a href="<c:url value="/post/${post.id}"/>">
                    <c:out value="${post.title}"/>
                </a>
            </li>
        </c:forEach>
    </div>
</article>
</body>
</html>