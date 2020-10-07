<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><c:out value="Comment"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <script src="<c:url value="/resources/js/components/paginationController.js"/>"></script>
    <script src="<c:url value="/resources/js/components/createAndViewComments.js"/>"></script>
    <link rel="stylesheet" href="<c:url value="/resources/css/postView.css"/>"/>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
<main class="uk-article uk-container uk-container-small uk-margin-medium-top">
    <a class="uk-text-bold uk-h1" href="<c:url value="/post/${comment.post.id}"/>">
        <c:out value="${comment.post.title}"/>
    </a>

    <div id="${comment.id}">
        <div id="main-comment" class="uk-comment uk-visible-toggle uk-margin-medium-bottom uk-margin-medium-top">
            <header class="uk-comment-header uk-position-relative">
                <div class="uk-grid-medium uk-flex-middle" uk-grid>
                    <c:if test="${comment.enabled}">
                        <div class="uk-width-auto">
                            <img class="uk-border-circle uk-comment-avatar" src="<c:url value="/resources/images/avatar.jpg"/>" width="80" height="80" alt="">
                        </div>
                    </c:if>
                    <div class="uk-width-expand" >
                        <c:if test="${comment.enabled}">
                            <h4 class="uk-comment-title uk-margin-remove">
                                <c:choose>
                                    <c:when test="${comment.user.enabled}">
                                        <a href="<c:url value="/user/${comment.user.id}"/>" class="comment-user-name <c:out value="${comment.user.admin ? 'uk-text-primary':''}"/>">
                                            <c:out value="${comment.user.name}" />
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
                        <p class="uk-comment-meta uk-margin-remove-top">
                            <fmt:parseDate value="${comment.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                            <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                        </p>
                    </div>
                </div>
                <sec:authorize access="hasRole('USER')">
                    <div class="uk-position-top-right">
                        <a class="uk-padding-remove uk-align-right uk-margin-remove like-comment-button" data-id="${comment.id}" data-value="${!loggedUser.getLikedComments().contains(comment.getId()) ? 'true' : 'false'}">
                            <span class="uk-text-right"><c:out value="${comment.likes}"/></span>
                            <sec:authorize access="hasRole('USER')">
                                <span class="iconify" data-icon="ant-design:${!loggedUser.getLikedComments().contains(comment.getId()) ? 'heart-outlined' : 'heart-filled'}" data-inline="false"></span>
                            </sec:authorize>
                        </a>
                    </div>
                </sec:authorize>
            </header>
            <div class="uk-comment-body">
                <c:choose>
                    <c:when test="${comment.enabled}">
                        <span style="white-space: pre-line"><c:out value="${comment.body}"/></span>
                    </c:when>
                    <c:otherwise>
                        <span class="uk-text-italic">
                            <spring:message code="comment.notEnabled.fullMessage"/>
                            <sec:authorize access="hasRole('ADMIN')">
                                <br><br><hr>
                                [ ${comment.user.username}: ${comment.body} ]
                            </sec:authorize>
                        </span>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
    </div>
    <sec:authorize access="hasRole('ADMIN')">
        <c:if test="${comment.enabled}">
        <div class="uk-flex uk-flex-right">
            <button id="cmt-delete-btn"
                    class="uk-button uk-button-default logout-button uk-border-rounded delete-comment-button"
                    data-id="<c:out value="${comment.id}"/>"
                    type="button"
                    uk-toggle="target: #delete-modal"
            >
                <spring:message code="comment.delete.button"/>
            </button>
        </div>
        </c:if>
    </sec:authorize>

    <c:set var="comments" value="${children}" scope="request"/>
    <c:set var="postId" value="${comment.post.id}" scope="request"/>
    <c:set var="parentId" value="${comment.id}" scope="request"/>
    <c:set var="enableReplies" value="${comment.enabled}" scope="request"/>
    <jsp:include page="/WEB-INF/jsp/components/createAndViewComments.jsp"/>
</main>
</body>
</html>