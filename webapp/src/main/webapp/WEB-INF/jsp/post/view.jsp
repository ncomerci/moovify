<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><c:out value="${post.title}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/marked/1.1.1/marked.min.js"></script>
    <script src="<c:url value="/resources/js/post/read.js" />"></script>
</head>
<body data-post-body="<c:out value="${post.body}"/>">
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<article class="uk-article">
    <div class="uk-container uk-container-small">
        <div>
            <h2 class="uk-text-bold uk-h1 uk-margin-remove-adjacent uk-margin-medium-top"><c:out value="${post.title}"/></h2>
            <span class="uk-article-meta"> <spring:message code="post.view.written"/>
<%--                TODO: Is there a better way to handle LocalDateTime formatting?    --%>
<%--                We convert LocalDateTime to Date parsing it like a String. Then formatDate formats the Date correctly.    --%>
                    <fmt:parseDate value="${post.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                    <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                </span>
            <span class="uk-article-meta uk-align-right">
                    <span data-uk-icon="icon: future" class="uk-margin-small-right"></span>
                    <spring:message code="post.view.minReading" arguments="${post.readingTimeMinutes}"/>
                </span>
            <span class="uk-article-meta uk-align-right">
                <spring:message code="post.view.writtenBy"/>
                <a href="<c:url value="/user/${post.user.id}"/>">
                    <c:out value="${post.user.name}"/>
                </a>
            </span>
        </div>
        <hr>
        <div>
            <noscript id="unparsedBody">
                <c:out value="${post.body}"/>
            </noscript>
            <div id="parsedBody"></div>
        </div>
        <hr>
        <div>
            <h1 class="uk-text-meta"><spring:message code="post.view.movies"/></h1>
            <c:forEach items="${post.movies}" var="movie" >
                <a class="uk-badge uk-padding-small uk-margin-small-right uk-margin-small-bottom uk-text-normal"
                   href="<c:url value="/movie/${movie.id}"/>">
                        ${movie.title}
                </a>
            </c:forEach>
        </div>
        <div>
            <h1 class="uk-text-meta"><spring:message code="post.view.tags"/></h1>
            <c:forEach items="${post.tags}" var="tag" >
                <c:url var="tagLink" value="/search/posts/?query=${tag}"/>
                <a class="uk-badge uk-padding-small uk-margin-small-right uk-margin-small-bottom uk-text-normal"
                    href="${tagLink}">
                    <c:out value="${tag}"/>
                </a>
            </c:forEach>
        </div>

    </div>
</article>
<hr>
<div class="uk-container uk-container-small">
    <h2>Comments (${post.totalCommentCount})</h2>
    <sec:authorize access="hasAnyRole('USER', 'ADMIN')">
        <div style="padding-bottom: 25px">
            <c:url value="/comment/create" var="action"/>
                <%--@elvariable id="CommentCreateForm" type=""--%>
            <form:form id="spring-form" modelAttribute="CommentCreateForm" action="${action}" method="post">
                <c:set var="parentId" value="${null}" scope="request" />
                <c:set var="placeholder"><spring:message code="comment.create.writeCommentPlaceholder"/></c:set>
                <div class="uk-margin">
                    <form:label path="postId">
                        <form:hidden path="postId" value="${post.id}"/>
                    </form:label>
                    <form:label path="parentId">
                        <form:hidden path="parentId" value="${parentId}"/>
                    </form:label>
                    <form:label path="commentBody">
                        <form:textarea class="uk-textarea" rows="5" path="commentBody" placeholder="${placeholder}" />
                    </form:label>
                </div>
                <div class="uk-margin-large-bottom uk-align-right">
                    <input class="uk-button uk-button-primary uk-border-rounded" type="submit" value="<spring:message code="comment.create.button"/>" />
                </div>
            </form:form>
        </div>
    </sec:authorize>
    <sec:authorize access="hasRole('NOT_VALIDATED')">
        <div class="uk-text-bold uk-text-italic uk-text-secondary uk-text-center"><spring:message code="comment.create.not_validated"/></div>
    </sec:authorize>
    <div class="uk-margin-large-top">
        <hr>
    <c:set var="comments" value="${post.comments}" scope="request" />
    <jsp:include page="/WEB-INF/jsp/components/commentTree.jsp" />
    </div>

    <%-- Comment reply textarea --%>
    <form id="reply-form" class="uk-hidden">
        <fieldset class="uk-fieldset">
            <div class="uk-margin">
                <textarea id="textarea" class="uk-textarea" rows="5" placeholder="<spring:message code="comment.create.replyPlaceholder"/>"></textarea>
            </div>
            <div class="uk-align-right">
                <button id="send-bt" class="uk-button uk-button-primary uk-border-rounded" type="button"><spring:message code="comment.create.replyBtn"/></button>
            </div>
        </fieldset>
    </form>
</div>
</body>
</html>

