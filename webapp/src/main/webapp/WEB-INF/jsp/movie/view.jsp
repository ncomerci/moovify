<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Title</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<article class="uk-article">
    <div class="uk-container uk-container-small">
        <div>
            <h2 class="uk-text-bold uk-h1 uk-margin-remove-adjacent uk-margin-remove-top"><c:out value="${movie.title}"/></h2>
            <p class="uk-article-meta"> Premier date: <c:out value="${movie.premierDate}"/></p>
        </div>
        <hr>
    </div>
</article>
</body>
</html>