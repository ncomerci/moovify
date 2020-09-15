<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><c:out value="Comment ID = ${comment.id}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<article class="uk-article">
    <div class="uk-container uk-container-small">
        <div>
            <h2 class="uk-text-bold uk-h1 uk-margin-remove-adjacent uk-margin-remove-top"><c:out value="${movie.title}"/></h2>
            <p class="uk-article-meta"> <spring:message code="comment.view.premierDate" arguments="${movie.premierDate}"/></p>
        </div>
        <hr>
        <h1><spring:message code="comment.view.postsAbout"/></h1>
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