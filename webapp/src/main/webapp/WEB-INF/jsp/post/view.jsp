<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title>Title</title>
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

                <p class="uk-article-meta"> Written on
<%--                TODO: Is there a better way to handle LocalDateTime formatting?    --%>
<%--                We convert LocalDateTime to Date parsing it like a String. Then formatDate formats the Date correctly.    --%>
                    <fmt:parseDate value="${post.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                    <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                </p>
                <p class="uk-article-meta"> <span data-uk-icon="icon: future" class="uk-margin-small-right"></span> Takes <c:out value="${post.readingTimeMinutes}"/> min reading.</p>
            </div>
            <hr>
            <div>
                <noscript id="unparsedBody">
                    <c:out value="${post.body}"/>
                </noscript>
                <div id="parsedBody"></div>
            </div>
            <div class="uk-width-3-4">
                <c:forEach items="${post.movies}" var="movie" >
<%--                TODO: Add search path with correct query  --%>
                    <a class="uk-badge uk-padding-small uk-margin-small-right uk-margin-small-bottom" href="#" >
                            ${movie.title}
                    </a>
                </c:forEach>
            </div>
        </div>
    </article>

</body>
</html>
