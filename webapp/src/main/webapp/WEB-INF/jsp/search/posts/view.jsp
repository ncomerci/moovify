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
            <ol>
                <c:forEach items="${posts}" var="item">
                    <li>
                        <h2 class="uk-text-bold uk-h1 uk-margin-remove-adjacent uk-margin-remove-top"><c:out value="${item.title}"/></h2>
                        <p class="uk-article-meta"> Written on <c:out value="${item.creationDate}"/>.</p>
                        <p class="uk-article-meta"> <span data-uk-icon="icon: future"></span> Takes <c:out value="${item.readingTimeMinutes}"/> min reading.</p>
                    </li>
                </c:forEach>
            </ol>
        </div>
        <hr>
    </div>
</article>
</body>
</html>