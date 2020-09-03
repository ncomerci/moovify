<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><c:out value="${post.title}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/marked/1.1.1/marked.min.js" ></script>
    <script src="<c:url value="/resources/js/post/read.js" />"></script>
</head>
<body data-post-body="<c:out value="${post.body}"/>">
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

    <article class="uk-article">
        <div class="uk-container uk-container-small">
            <div>
                <h2 class="uk-text-bold uk-h1 uk-margin-remove-adjacent uk-margin-remove-top"><c:out value="${post.title}"/></h2>
                <p class="uk-article-meta"> Written on <c:out value="${post.creationDate}"/>.</p>
                <p class="uk-article-meta"> <span data-uk-icon="icon: future"></span> Takes <c:out value="${post.readingTimeMinutes}"/> min reading.</p>
            </div>
            <hr>
            <div>
                <noscript id="unparsedBody">
                    <c:out value="${post.body}"/>
                </noscript>
                <div id="parsedBody"></div>
            </div>
        </div>
    </article>

</body>
</html>
