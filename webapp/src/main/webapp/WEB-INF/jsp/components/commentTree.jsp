<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="customTag" uri="http://www.paw.itba.edu.ar/moovify/tags"%>

<jsp:useBean id="comments" scope="request" type="java.util.Collection"/>
<jsp:useBean id="maxDepth" scope="request" type="java.lang.Long"/>
<jsp:useBean id="maxDepth_unmodified" scope="request" type="java.lang.Long"/>
<sec:authorize access="isAuthenticated()">
    <jsp:useBean id="loggedUser" scope="request" type="ar.edu.itba.paw.models.User"/>
</sec:authorize>

<ul class="uk-comment-list" id="comment-section">
    <c:forEach items="${comments}" var="comment" >

        <li class="uk-margin-remove">
            <div id="${comment.id}">
                <article class="uk-comment uk-visible-toggle" tabindex="-1">
                    <header class="uk-comment-header uk-position-relative uk-margin-remove">
                        <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center uk-margin-bottom" uk-grid>
                            <div class="uk-width-2-3">
                                <div class="uk-grid-medium uk-flex-middle" uk-grid>
                                    <div class="uk-width-1-5">
                                        <c:choose>
                                            <c:when test="${comment.enabled and comment.user.enabled}">
                                                <c:set var="avatarUrl"><c:url value="/user/avatar/${comment.user.avatarId}"/></c:set>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="avatarUrl"><c:url value="/resources/images/avatar.jpg"/></c:set>
                                            </c:otherwise>
                                        </c:choose>
                                        <img class="circle-comment uk-comment-avatar" src="${avatarUrl}" alt="">
                                    </div>
                                    <div class="uk-width-auto">
                                        <c:if test="${comment.enabled}">
                                            <h4 class="uk-comment-title uk-margin-remove">
                                                <c:choose>
                                                    <c:when test="${comment.user.enabled}">
                                                        <a class="comment-user-name <c:out value="${comment.user.admin ? 'uk-text-primary':''}"/>" href = "<c:url value="/user/${comment.user.id}" />">
                                                            <c:out value="${comment.user.username}"/>
                                                            <c:if test="${comment.user.admin}">
                                                                <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false"></span>
                                                            </c:if>
                                                        </a>
                                                    </c:when>
                                                    <c:otherwise>
                                                        <span class="comment-user-name uk-text-italic">
                                                            <spring:message code="user.notEnabled.name"/>
                                                        </span>
                                                    </c:otherwise>
                                                </c:choose>
                                            </h4>
                                        </c:if>
                                        <c:if test="${!comment.enabled}">
                                            <p class="uk-text-danger"><spring:message code="comment.deleted"/></p>
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
                                    <div class="uk-width-auto uk-padding-remove">
                                        <c:if test="${not empty loggedUser and loggedUser.admin and comment.enabled}">
                                            <a href="#delete-comment-modal"
                                               data-id="<c:out value="${comment.id}"/>"
                                               class="uk-link-muted delete-comment-button uk-position-small uk-hidden-hover"
                                               uk-toggle>
                                                <span class="iconify" data-icon="ic:baseline-delete-forever" data-inline="false"></span>
                                            </a>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                            <div class="uk-width-1-3 uk-text-center uk-padding-remove uk-margin-remove ">
                                <c:if test="${comment.enabled}">
                                <div class="uk-position-top-right">
                                    <div class="uk-flex">
                                        <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center uk-margin-top" uk-grid>
                                            <sec:authorize access="hasRole('USER')">
                                                <div class="uk-width-auto uk-text-center uk-padding-remove uk-margin-remove">
                                                    <a data-id="<c:out value="${comment.id}"/>" class="uk-link-muted reply-button uk-position-small uk-hidden-hover">
<%--                                                        <spring:message code="comment.create.reply"/>--%>
                                                            <span class="iconify" data-icon="octicon:reply-16" data-inline="false"></span>
                                                    </a>
                                                </div>
                                            </sec:authorize>
                                            <div class="uk-width-auto uk-text-center uk-padding-remove">
                                                <a class="uk-link-muted reply-button uk-position-small uk-hidden-hover" href="<c:url value="/comment/${comment.id}"/>">
                                                    <spring:message code="comment.viewComment"/>
                                                </a>
                                            </div>
                                            <sec:authorize access="isAnonymous() or hasRole('NOT_VALIDATED')">
                                                <div class="uk-text-center uk-padding-remove uk-margin-remove">
                                                    <p class="uk-text-center uk-align-center uk-text-lead">
                                                        <spring:message code="comment.view.votes" arguments="${comment.totalLikes}"/>
                                                    </p>
                                                </div>
                                            </sec:authorize>
                                            <c:if test="${not empty loggedUser and loggedUser.validated}">
                                                <c:set var="likeValue" value="${ customTag:getCommentLikeValue(comment,loggedUser) }" />
                                                <div class="uk-width-auto uk-text-center uk-padding-remove uk-align-right ">
                                                    <a class="like-comment-button" data-id="${comment.id}" data-value="${ likeValue > 0 ? 0 : 1 }">
                                                        <span class="iconify" data-icon="<c:out value="${ likeValue > 0 ? 'el:chevron-up' : 'cil:chevron-top' }" />" data-inline="false"></span>
                                                    </a>
                                                    <p class="uk-text-center uk-align-center uk-text-lead uk-margin-remove">
                                                        <c:out value="${ comment.totalLikes }"/>
                                                    </p>
                                                    <a class=" like-comment-button" data-id="${comment.id}"  data-value="${ likeValue < 0 ? 0 : -1 }">
                                                        <span class="iconify" data-icon="<c:out value="${ likeValue < 0 ? 'el:chevron-down' : 'cil:chevron-bottom' }" />" data-inline="true"></span>
                                                    </a>
                                                </div>
                                            </c:if>
                                        </div>
                                    </div>
                                    </c:if>
                                </div>
                            </div>
                        </div>
                    </header>
                    <div class="uk-comment-body uk-margin-small-left">
                        <c:choose>
                            <c:when test="${comment.enabled}">
                                <span class="pre-line"><c:out value="${comment.body}"/></span>
                            </c:when>
                            <c:when test="${not empty loggedUser and loggedUser.admin}">
                                <div class="uk-text-italic"><c:out value="[${comment.user.username}: ${comment.body}]"/></div>
                            </c:when>
                            <c:otherwise>
                                <div class="uk-text-italic"><spring:message code="comment.notEnabled.message"/></div>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </article>
                <hr>
            </div>
            <c:if test="${maxDepth == 0}">
                <c:set var="descendants" value="${customTag:descendantCount(comment, maxDepth_unmodified)}"/>
                <c:if test="${descendants > 0}">
                    <a class="uk-link-muted" href="<c:url value="/comment/${comment.id}"/>">
                        <spring:message code="comment.replies.show" arguments="${descendants}"/>
                    </a>
                </c:if>
            </c:if>
                <%--            TODO: si este código comentado se borra, hay que sacar el código de javascript también--%>
                <%--            <div class="replies-show uk-margin-bottom" id="${comment.id}-replies-show" data-id="${comment.id}" data-amount="${customTag:descendantCount(comment, maxDepth)}">
                                <a class="uk-link-muted"><spring:message code="comment.replies.show" arguments="${customTag:descendantCount(comment, maxDepth)}"/></a>
                            </div>--%>
                <%--            class="li uk-hidden"--%>
            <ul id="${comment.id}-children">
                <c:if test="${maxDepth > 0}">
                    <%--  Recursive Call  --%>
                    <c:set var="comments" value="${comment.children}" scope="request" />
                    <c:set var="maxDepth" value="${maxDepth - 1}" scope="request"/>
                    <jsp:include page="commentTree.jsp" />

                    <%--      Restoring maxDepth value      --%>
                    <c:set var="maxDepth" value="${maxDepth + 1}" scope="request"/>
                </c:if>
            </ul>
        </li>

    </c:forEach>
</ul>
