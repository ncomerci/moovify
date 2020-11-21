<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="customTag" uri="http://www.paw.itba.edu.ar/moovify/tags"%>

<sec:authorize access="isAuthenticated()">
    <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
</sec:authorize>

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="post.view.title" arguments="${post.title}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <link rel="stylesheet" href="<c:url value="/resources/css/postView.css" />" />
    <script src="https://cdnjs.cloudflare.com/ajax/libs/marked/1.1.1/marked.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/dompurify/2.1.1/purify.min.js"
            integrity="sha512-MyuIiR29IQaNvgQIvGVvOwtphjY82+ZoeopFcOyXrdsFbIiU6Sc3MRvpXRzOYtihMs83vT/rz8ArCM53l5Onqg=="
            crossorigin="anonymous"></script>

    <script src="<c:url value="/resources/js/components/createAndViewComments.js"/>"></script>
    <script src="<c:url value="/resources/js/post/view.js"/>"></script>
    <c:if test="${not empty loggedUser and loggedUser.admin}">
        <script src="<c:url value="/resources/js/post/delete.js"/>"></script>
    </c:if>
    <script src="<c:url value="/resources/js/components/paginationController.js"/>"></script>
</head>
<body data-post-body="<c:out value="${post.body}"/>">
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<main class="uk-article uk-container uk-container-small uk-margin-medium-top">

    <div id="post-metadata" class="uk-child-width-expand" uk-grid>
        <div class="uk-grid-small uk-flex uk-flex-row uk-flex-between uk-margin-bottom" uk-grid>
            <div class="uk-flex uk-flex-column uk-width-expand">
                <div class="uk-flex uk-flex-between uk-margin-bottom" uk-grid>
                    <h1 class="uk-text-bold uk-h1 uk-margin-remove-adjacent uk-width-4-5">
                        <sec:authorize access="hasRole('USER')">
                            <c:set var="bookmarked" value="${customTag:hasUserBookmarkedPost(loggedUser, post)}"/>
                            <c:choose>
                                <c:when test="${bookmarked}">
                                    <a id="bookmark" title="<spring:message code="post.remove.bookmark"/>"><span class="iconify" data-icon="mdi-bookmark-check" data-inline="false"></span></a>
                                </c:when>
                                <c:otherwise>
                                    <a id="no-bookmark" title="<spring:message code="post.add.bookmark"/>"><span class="iconify" data-icon="mdi-bookmark-check-outline" data-inline="false"></span></a>
                                </c:otherwise>
                            </c:choose>
                        </sec:authorize>
                        <c:out value="${post.title}"/>
                    </h1>
                    <sec:authorize access="isAnonymous() or hasRole('NOT_VALIDATED')">
                        <div class="uk-text-center uk-padding-remove uk-margin-remove uk-flex uk-flex-middle">
                            <p class="uk-text-center uk-align-center uk-text-lead">
                                <spring:message code="post.view.votes" arguments="${post.totalLikes}"/>
                            </p>
                        </div>
                    </sec:authorize>
                </div>
                <div class="uk-flex uk-flex-between uk-margin-remove uk-margin-bottom" uk-grid>
                <span id="post-author" class="uk-article-meta uk-align-right uk-margin-remove uk-padding-remove">
                    <spring:message code="post.view.writtenBy"/>
                    <c:choose>
                        <c:when test="${post.user.enabled}">
                            <a href="<c:url value="/user/${post.user.id}"/>">
                                <c:out value="${post.user.username}"/>
                                <c:if test="${post.user.admin}">
                                    <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false" title="<spring:message code="admin.title"/>"></span>
                                </c:if>
                            </a>
                        </c:when>
                        <c:otherwise>
                            <span class="uk-text-italic">
                                <spring:message code="user.notEnabled.name"/>
                            </span>
                        </c:otherwise>
                    </c:choose>
                    <spring:message code="post.view.writtenSeparator"/>
                    <fmt:parseDate value="${post.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                    <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                    <c:out value="-"/>
                    <span data-uk-icon="icon: future"></span>
                    <spring:message code="post.view.minReading" arguments="${post.readingTimeMinutes}"/>
                </span>
                    <span>
                        <c:set var="editable" value="${not empty loggedUser and loggedUser.validated and post.user.id == loggedUser.id}"/>
                        <c:choose>
                            <c:when test="${editable}" >
                        <div class="uk-flex uk-flex-middle">
                            <a href="<c:url value="/post/edit/${post.id}"/>">
                                <span uk-icon="icon: pencil; ratio: 1.2"  data-inline="false"></span><spring:message code="post.view.edit"/>
                            </a>
                        </div>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${post.edited}">
                                    <span id="post-creation-date" class="uk-article-meta"> <spring:message code="post.view.lastEdited"/>
                                            <fmt:parseDate value="${post.lastEditDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                                            <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                                    </span>
                                </c:if>
                            </c:otherwise>
                        </c:choose>
                    </span>
                </div>
                <div class="uk-flex uk-flex-right uk-margin-remove uk-margin-bottom" uk-grid>
                    <c:if test="${editable and post.edited}">
                    <span id="post-creation-date" class="uk-article-meta"> <spring:message code="post.view.lastEdited"/>
                            <fmt:parseDate value="${post.lastEditDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                            <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                    </span>
                    </c:if>
                </div>
            </div>
        </div>
        <c:if test="${not empty loggedUser and loggedUser.validated}">
            <c:set var="likeValue" value="${ customTag:getPostLikeValue(post, loggedUser) }" />
            <div class="uk-width-auto uk-text-center uk-padding-remove uk-align-right uk-margin-left uk-flex uk-flex-middle">
                <div class="uk-flex uk-flex-column uk-width-expand">
                    <a class="like-post-button" data-value="${ likeValue == 1 ? 0 : 1 }">
                        <span class="iconify" data-icon="<c:out value="${ likeValue == 1 ? 'el:chevron-up' : 'cil:chevron-top' }" />" data-inline="false"></span>
                    </a>
                    <p class="uk-text-center uk-align-center uk-text-lead uk-margin-remove"><c:out value="${post.totalLikes}"/></p>
                    <a class=" like-post-button"  data-value="${ likeValue == -1 ? 0 : -1 }">
                        <span class="iconify" data-icon="<c:out value="${ likeValue == -1 ? 'el:chevron-down' : 'cil:chevron-bottom' }" />" data-inline="true"></span>
                    </a>
                </div>
            </div>
        </c:if>
    </div>

    <%--    TODO: Arreglar -Tobi :)--%>
    <hr class="uk-margin-remove-top">
    <article id="post-body">
        <noscript id="unparsedBody" class="m-long-text">
            <c:out value="${post.body}"/>
        </noscript>
        <div id="parsedBody" class="m-long-text"></div>
    </article>
    <hr>
    <section id="post-movies">
        <h1 class="uk-text-meta"><spring:message code="post.view.movies"/></h1>
        <c:forEach items="${post.movies}" var="movie" >
            <a class="uk-badge uk-padding-small uk-margin-small-right uk-margin-small-bottom uk-text-normal"
               href="<c:url value="/movie/${movie.id}"/>">
                <c:out value="${movie.title}"/>
            </a>
        </c:forEach>
    </section>
    <section id="post-tags">
        <h1 class="uk-text-meta"><spring:message code="post.view.tags"/></h1>
        <c:forEach items="${post.tags}" var="tag" >
            <c:url var="tagLink" value="/search/posts">
                <c:param name="query" value="${tag}"/>
            </c:url>
            <a class="uk-badge uk-padding-small uk-margin-small-right uk-margin-small-bottom uk-text-normal"
               href="${tagLink}">
                <c:out value="${tag}"/>
            </a>
        </c:forEach>
    </section>
    <hr>
    <c:if test="${not empty loggedUser and loggedUser.admin}">
        <div class="uk-flex uk-flex-right">
            <button id="post-delete-btn"
                    class="uk-button uk-button-default logout-button uk-border-rounded"
                    data-id="${post.id}"
                    type="button"
                    uk-toggle="target: #delete-post-modal"
            >
                <spring:message code="post.delete.button"/>
            </button>
        </div>
    </c:if>
    <c:set var="comments" value="${comments}" scope="request"/>
    <c:set var="postId" value="${post.id}" scope="request"/>
    <c:set var="parentId" value="${0}" scope="request"/>
    <c:set var="enableReplies" value="${true}" scope="request"/>
    <c:set var="maxDepth" value="${maxDepth}" scope="request"/>
    <jsp:include page="/WEB-INF/jsp/components/createAndViewComments.jsp"/>
</main>
</body>
</html>

<%-- Post like form --%>
<form class="uk-margin-remove" action="<c:url value="/post/like"/>" method="post" id="post-like-form">
    <label>
        <input hidden name="postId" type="number" value="${postId}"/>
    </label>
    <label>
        <input hidden name="value" id="post-like-value" type="number"/>
    </label>
</form>

<c:if test="${not empty loggedUser}">
    <c:if test="${loggedUser.admin}">
        <%--  Delete form  --%>
        <form method="post" action="<c:url value="/"/>" id="delete-post-form"></form>

        <!-- delete confirmation modal -->
        <div id="delete-post-modal" uk-modal>
            <div class="uk-modal-dialog uk-modal-body">
                <h2 class="uk-modal-title"><spring:message code="post.delete.modalTitle"/></h2>
                <p class="uk-text-right">
                    <button class="uk-button uk-button-default uk-modal-close uk-border-rounded" type="button"><spring:message code="comment.delete.cancelButton"/></button>
                    <button id="modal-post-confirm" class="uk-button uk-button-primary uk-border-rounded" type="button"><spring:message code="comment.delete.confirmButton"/></button>
                </p>
            </div>
        </div>
    </c:if>

    <form method="post" action="<c:url value="/user/favourite/posts/add"/>" id="add-bookmark-form">
        <label>
            <input hidden name="postId" type="number" value="${post.id}">
        </label>
    </form>

    <form method="post" action="<c:url value="/user/favourite/posts/remove"/>" id="remove-bookmark-form">
        <label>
            <input hidden name="postId" type="number" value="${post.id}">
        </label>
    </form>
</c:if>