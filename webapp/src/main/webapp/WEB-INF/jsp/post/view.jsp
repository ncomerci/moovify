<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="customTag" uri="http://www.paw.itba.edu.ar/moovify/tags"%>

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><c:out value="${post.title}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <link rel="stylesheet" href="<c:url value="/resources/css/postView.css" />" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/marked/1.1.1/marked.min.js"></script>
    <script src="<c:url value="/resources/js/components/createAndViewComments.js"/>"></script>
    <script src="<c:url value="/resources/js/components/paginationController.js"/>"></script>
</head>
<body data-post-body="<c:out value="${post.body}"/>">
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<main class="uk-article uk-container uk-container-small uk-margin-medium-top">
    <div id="post-metadata" >
        <h1 class="uk-text-bold uk-h1 uk-margin-remove-adjacent "><c:out value="${post.title}"/>

            <c:if test="${!isPostLiked}">
                <a class="uk-padding-remove uk-align-right like-post-button"  data-value="true">
                    <span class="uk-text-right"><c:out value="${post.likes}"/></span>
                    <sec:authorize access="hasRole('USER')">
                        <span class="iconify" data-icon="ant-design:heart-outlined" data-inline="false"></span>
                    </sec:authorize>
                </a>
            </c:if>
            <c:if test="${isPostLiked}">
                <a class="uk-padding-remove uk-align-right like-post-button" data-value="false">
                    <span class="uk-text-right"><c:out value="${post.likes}"/></span>
                    <sec:authorize access="hasRole('USER')">
                        <span class="iconify" data-icon="ant-design:heart-filled" data-inline="false"></span>
                    </sec:authorize>
                </a>
            </c:if>

        </h1>

        <span id="post-creation-date" class="uk-article-meta"> <spring:message code="post.view.written"/>
<%--                TODO: Create a custom taglib  --%>
<%--                We convert LocalDateTime to Date parsing it like a String. Then formatDate formats the Date correctly.    --%>
                <fmt:parseDate value="${post.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
        </span>
        <span id="post-reading-time" class="uk-article-meta uk-align-right uk-margin-remove-bottom">
                <span data-uk-icon="icon: future" class="uk-margin-small-right"></span>
                <spring:message code="post.view.minReading" arguments="${post.readingTimeMinutes}"/>
            </span>
        <span id="post-author" class="uk-article-meta uk-align-right uk-margin-remove-bottom">
            <spring:message code="post.view.writtenBy"/>

            <c:choose>
                <c:when test="${post.user.enabled}">
                    <a href="<c:url value="/user/${post.user.id}"/>">
                        <c:out value="${post.user.name}"/>
                    </a>
                </c:when>
                <c:otherwise>
                    <span class="uk-text-italic">
                        <spring:message code="user.notEnabled.name"/>
                    </span>
                </c:otherwise>
            </c:choose>
        </span>
    </div>

    <hr>
    <article id="post-body">
        <noscript id="unparsedBody">
            <c:out value="${post.body}"/>
        </noscript>
        <div id="parsedBody"></div>
    </article>
    <hr>
    <section id="post-movies">
        <h1 class="uk-text-meta"><spring:message code="post.view.movies"/></h1>
        <c:forEach items="${movies}" var="movie" >
            <a class="uk-badge uk-padding-small uk-margin-small-right uk-margin-small-bottom uk-text-normal"
               href="<c:url value="/movie/${movie.id}"/>">
                <c:out value="${movie.title}"/>
            </a>
        </c:forEach>
    </section>
    <section id="post-tags">
        <h1 class="uk-text-meta"><spring:message code="post.view.tags"/></h1>
        <c:forEach items="${post.tags}" var="tag" >
            <c:url var="tagLink" value="/search/posts/?query=${tag}"/>
            <a class="uk-badge uk-padding-small uk-margin-small-right uk-margin-small-bottom uk-text-normal"
               href="${tagLink}">
                <c:out value="${tag}"/>
            </a>
        </c:forEach>
    </section>
    <hr>
    <c:set var="comments" value="${comments}" scope="request"/>
    <c:set var="postId" value="${post.id}" scope="request"/>
    <c:set var="parentId" value="${0}" scope="request"/>
    <jsp:include page="/WEB-INF/jsp/components/createAndViewComments.jsp"/>
</main>

</body>
</html>

