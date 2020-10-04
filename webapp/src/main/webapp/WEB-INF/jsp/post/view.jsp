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
    <script src="https://cdnjs.cloudflare.com/ajax/libs/marked/1.1.1/marked.min.js"></script>
    <script src="<c:url value="/resources/js/post/read.js"/>"></script>
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
            <a href="<c:url value="/user/${post.user.id}"/>">
                <c:out value="${post.user.name}"/>
            </a>
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
    <section class="uk-container uk-container-small">
        <h1 class="uk-h2"><spring:message code="post.view.comments.title" arguments="${customTag:totalComments(comments)}"/></h1>
        <sec:authorize access="hasRole('USER')">
            <div style="padding-bottom: 25px">
                <c:url value="/comment/create" var="action"/>
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
            <c:set var="comments" value="${comments}" scope="request" />
            <jsp:include page="/WEB-INF/jsp/components/commentTree.jsp" />
        </div>

        <%-- Comment reply textarea --%>
        <form id="reply-form" class="uk-hidden">
            <fieldset class="uk-fieldset">
                <div class="uk-margin">
                    <label for="textarea"></label>
                    <textarea id="textarea" class="uk-textarea" rows="5" placeholder="<spring:message code="comment.create.replyPlaceholder"/>"></textarea>
                </div>
                <div class="uk-align-right">
                    <button id="send-bt" class="uk-button uk-button-primary uk-border-rounded" type="button"><spring:message code="comment.create.replyBtn"/></button>
                </div>
            </fieldset>
        </form>
    </section>
    <form class="uk-margin-remove" action="<c:url value="/post/like"/>" method="post" id="post-like-form">
        <label>
            <input hidden name="postId" type="number" value="${post.id}"/>
        </label>
        <label>
            <input hidden name="value" id="post-like-value" type="checkbox"/>
        </label>
    </form>
    <form method="post" action="<c:url value="/comment/like"/>" id="comment-like-form">
        <label>
            <input hidden type="number" name="post_id" value="${post.id}"/>
        </label>
        <label>
            <input hidden type="number" id="comment-id" name="comment_id"/>
        </label>
        <label>
            <input hidden type="checkbox" id="like-value" name="value"/>
        </label>
    </form>
</main>

</body>
</html>

