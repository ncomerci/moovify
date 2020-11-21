<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="customTag" uri="http://www.paw.itba.edu.ar/moovify/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib uri = "http://java.sun.com/jsp/jstl/functions" prefix = "fn" %>

<%@ page contentType="text/html;charset=UTF-8" %>

<c:set var="commentMaxLength" value="400"/>

<html>
<head>
    <title><spring:message code="comment.title"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <link rel="stylesheet" href="<c:url value="/resources/css/postView.css"/>"/>
    <script src="<c:url value="/resources/js/components/paginationController.js"/>"></script>
    <script src="<c:url value="/resources/js/components/createAndViewComments.js"/>"></script>
    <script src="<c:url value="/resources/js/comment/edit.js"/>"></script>

    <sec:authorize access="isAuthenticated()">
        <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
    </sec:authorize>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
<main class="uk-article uk-container uk-container-small uk-margin-medium-top">
    <c:if test="${comment.post.enabled}">
        <c:url value="/post/${comment.post.id}" var="postURL"/>
        <a class="uk-text-bold uk-h1" href="${postURL}">
            <c:out value="${comment.post.title}"/>
        </a>
    </c:if>
    <c:if test="${!comment.post.enabled}">
        <p class="uk-text-bold uk-h1">
            <spring:message code="comment.view.post.removed"/>
        </p>
    </c:if>

    <div id="${comment.id}">
        <div id="main-comment" class="uk-comment uk-visible-toggle uk-margin-medium-bottom uk-margin-medium-top">
            <header class="uk-comment-header uk-position-relative uk-margin-remove">
                <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center uk-margin-bottom" uk-grid>
                    <div class="uk-width-5-6">
                        <div class="uk-grid-medium uk-flex-middle" uk-grid>
                            <c:if test="${comment.enabled}">
                                <div class="uk-width-auto">
                                    <img class="circle-comment uk-comment-avatar" src="<c:url value="/user/avatar/${comment.user.avatarId}"/>" width="80" height="80" alt="">
                                </div>
                            </c:if>
                            <div class="uk-width-3-5" >
                                <c:if test="${comment.enabled}">
                                    <h4 class="uk-comment-title uk-margin-remove">
                                        <c:choose>
                                            <c:when test="${comment.user.enabled}">
                                                <a href="<c:url value="/user/${comment.user.id}"/>" class="comment-user-name <c:out value="${comment.user.admin ? 'uk-text-primary':''}"/>">
                                                    <c:out value="${comment.user.username}" />
                                                    <c:if test="${comment.user.admin}">
                                                        <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false"></span>
                                                    </c:if>
                                                </a>
                                            </c:when>
                                            <c:otherwise>
                                        <span class="uk-text-italic comment-user-name">
                                            <spring:message code="user.notEnabled.name"/>
                                        </span>
                                            </c:otherwise>
                                        </c:choose>
                                    </h4>
                                </c:if>
                                <p class="uk-comment-meta uk-margin-remove-top uk-margin-remove-bottom">
                                    <fmt:parseDate value="${comment.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                                    <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                                </p>
                                <c:if test="${comment.edited}">
                                    <p class="uk-comment-meta uk-margin-remove">
                                        <fmt:parseDate value="${comment.lastEditDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                                        <fmt:formatDate var="lastEditedDate" pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                                        <spring:message code="comment.lastEditDate.message" arguments="${lastEditedDate}"/>
                                    </p>
                                </c:if>
                            </div>
                        </div>
                    </div>
                    <div class="uk-width-1-6 uk-text-center uk-padding-remove uk-margin-top">
                        <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center" uk-grid>
                            <sec:authorize access="isAnonymous() or hasRole('NOT_VALIDATED')" var="notAbleToLike"/>
                            <c:if test="${notAbleToLike or not comment.enabled}">
                                <div class="uk-text-center uk-padding-remove uk-margin-remove">
                                    <p class="like-post-button uk-text-center uk-align-center uk-text-lead">
                                        <spring:message code="comment.view.votes" arguments="${comment.totalLikes}"/>
                                    </p>
                                </div>
                            </c:if>
                            <c:if test="${loggedUser.validated and comment.enabled}">
                                <div class="uk-width-auto uk-text-center uk-padding-remove uk-align-right uk-margin-remove">
                                    <c:set var="hasUserVoted" value="${ customTag:getCommentLikeValue(comment, loggedUser) != 0 }" />
                                    <c:set var="likeValue" value="${ hasUserVoted and customTag:getCommentLikeValue(comment,loggedUser) > 0 }" />
                                    <a class="like-comment-button" data-id="${comment.id}" data-value="${ likeValue ? 0 : 1 }">
                                        <span class="iconify" data-icon="<c:out value="${ likeValue ? 'el:chevron-up' : 'cil:chevron-top' }" />" data-inline="false" ></span>
                                    </a>
                                    <p class="uk-text-center uk-align-center uk-text-lead uk-margin-remove">
                                        <c:out value="${comment.totalLikes}"/>
                                    </p>
                                    <a class="like-comment-button" data-id="${comment.id}"  data-value="${ !hasUserVoted or likeValue ? -1 : 0 }">
                                        <span class="iconify" data-icon="<c:out value="${ !hasUserVoted or likeValue ? 'cil:chevron-bottom' : 'el:chevron-down'}" />" data-inline="true"></span>
                                    </a>
                                </div>
                            </c:if>
                        </div>
                    </div>
                </div>
            </header>
            <div class="uk-comment-body">
                <c:choose>
                    <c:when test="${comment.enabled}">
                        <span id="comment-body" class="pre-line"><c:out value="${comment.body}"/></span>

                        <div id="comment-edit-form" class="hidden">
                            <c:url value="/comment/edit/${comment.id}" var="action"/>
                                <%--@elvariable id="commentEditForm" type="ar.edu.itba.paw.webapp.form.CommentEditForm"--%>
                            <form:form id="spring-form" modelAttribute="commentEditForm" action="${action}" method="post">
                                <div class="uk-margin">
                                    <form:label path="commentBody">
                                        <form:textarea id="commentEditBody" data-maxlength="${commentMaxLength}" class="uk-textarea" rows="5" path="commentBody" />
                                    </form:label>
                                </div>
                                <p id="edit-counter" class="uk-text-muted uk-align-left">${fn:length(comment.body)}/${commentMaxLength}</p>
                                <div class="uk-margin-large-bottom uk-align-right">
                                    <input id="submit-edt-btn" class="uk-button uk-button-primary uk-border-rounded" type="submit" value="<spring:message code="comment.create.button"/>" />
                                </div>
                            </form:form>
                        </div>
                    </c:when>
                    <c:otherwise>
                            <span class="uk-text-italic">
                                <spring:message code="comment.notEnabled.fullMessage"/>
                                <c:if test="${loggedUser.admin}">
                                    <br><br><hr>
                                    [ <c:out value="${comment.user.username}: ${comment.body}" /> ]
                                </c:if>
                            </span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>

    <div class="uk-flex uk-flex-right" uk-grid>
        <c:if test="${not empty loggedUser}">
            <div>
                <c:if test="${comment.enabled and loggedUser.id == comment.user.id}">
                    <button id="cmt-edit-btn"
                            class="uk-button uk-button-default uk-button-primary uk-border-rounded"
                            type="button">
                        <spring:message code="comment.edit.button"/>
                    </button>
                </c:if>
                <c:if test="${loggedUser.admin}">
                    <c:if test="${comment.enabled}">
                        <button id="cmt-delete-btn"
                                class="uk-button uk-button-default logout-button uk-border-rounded delete-comment-button"
                                data-id="<c:out value="${comment.id}"/>"
                                type="button"
                                uk-toggle="target: #delete-comment-modal">
                            <spring:message code="comment.delete.button"/>
                        </button>
                    </c:if>
                </c:if>
            </div>
        </c:if>
    </div>

    <c:set var="comments" value="${children}" scope="request"/>
    <c:set var="postId" value="${comment.post.id}" scope="request"/>
    <c:set var="parentId" value="${comment.id}" scope="request"/>
    <c:set var="enableReplies" value="${comment.enabled}" scope="request"/>
    <c:set var="maxDepth" value="${maxDepth}" scope="request"/>
    <jsp:include page="/WEB-INF/jsp/components/createAndViewComments.jsp"/>
</main>
</body>
</html>