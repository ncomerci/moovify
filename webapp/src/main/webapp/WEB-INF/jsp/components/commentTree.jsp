<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="customTag" uri="http://www.paw.itba.edu.ar/moovify/tags"%>

<jsp:useBean id="comments" scope="request" type="java.util.Collection"/>
<ul class="uk-comment-list" id="comment-section">
    <c:forEach items="${comments}" var="comment" >

        <li class="uk-margin-remove">
            <div id="${comment.id}">
                <article class="uk-comment uk-visible-toggle" tabindex="-1">
                    <header class="uk-comment-header uk-position-relative <c:out value="${comment.enabled ? '':'uk-margin-remove'}"/>">
                        <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center uk-margin-bottom" uk-grid>
                            <div class="uk-width-2-3">
                                <div class="uk-grid-medium uk-flex-middle" uk-grid>
                                    <c:if test="${comment.enabled}">
                                        <div class="uk-width-auto">
                                            <img class="uk-border-circle uk-comment-avatar" src="<c:url value="/user/avatar/${comment.user.avatarId}"/>" width="80" height="80" alt="">
                                        </div>
                                    </c:if>
                                    <div class="uk-width-expand">
                                        <c:if test="${comment.enabled}">
                                            <h4 class="uk-comment-title uk-margin-remove">
                                                <c:choose>
                                                    <c:when test="${comment.user.enabled}">
                                                        <a class="comment-user-name <c:out value="${comment.user.admin ? 'uk-text-primary':''}"/>" href = "<c:url value="/user/${comment.user.id}" />">
                                                            <c:out value="${comment.user.name}"/>
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
                                                <sec:authorize access="hasRole('ADMIN')">
                                                    <a href="#delete-modal"
                                                       data-id="<c:out value="${comment.id}"/>"
                                                       class="uk-link-muted delete-comment-button uk-position-small uk-hidden-hover"
                                                       uk-toggle
                                                    >
                                                        <spring:message code="comment.delete.button"/>
                                                    </a>
                                                </sec:authorize>
                                            </h4>
                                        </c:if>
                                        <c:if test="${!comment.enabled}">
                                            <br><br>
                                        </c:if>
                                        <p class="uk-comment-meta uk-margin-remove-top">
                                            <fmt:parseDate value="${comment.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                                            <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                                        </p>
                                    </div>
                                </div>
                            </div>
                            <c:if test="${comment.enabled}">
                            <div class="uk-width-1-3 uk-text-center uk-padding-remove uk-margin-remove ">
                                <div class="uk-position-top-right">
                                    <div class="uk-flex">
                                        <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center uk-margin-top" uk-grid>
                                            <div class="uk-width-auto uk-text-center uk-padding-remove uk-margin-remove">
                                                <a data-id="<c:out value="${comment.id}"/>" class="uk-link-muted reply-button uk-position-small uk-hidden-hover"><spring:message code="comment.create.reply"/></a>
                                            </div>
                                            <div class="uk-width-auto uk-text-center uk-padding-remove uk-margin-remove">
                                                <a class="uk-link-muted reply-button uk-position-small uk-hidden-hover" href="<c:url value="/comment/${comment.id}"/>">
                                                    <spring:message code="comment.viewComment"/>
                                                </a>
                                            </div>
                                            <sec:authorize access="isAnonymous() or hasRole('NOT_VALIDATED')">
                                                <div class="uk-text-center uk-padding-remove uk-margin-remove">
                                                    <p class="like-post-button uk-text-center uk-align-center uk-text-lead">
                                                        <spring:message code="post.view.likes" arguments="${comment.likes}"/>
                                                    </p>
                                                </div>
                                            </sec:authorize>
                                            <sec:authorize access="hasRole('USER')">
                                                <div class="uk-width-auto uk-text-center uk-padding-remove uk-align-right ">
                                                    <sec:authorize access="hasRole('USER')">
                                                        <c:if test="${!customTag:hasUserVotedComment(comment, loggedUser.id) or !customTag:hasUserLikedComment(comment,loggedUser.id)}">
                                                            <a class="like-comment-button" data-id="${comment.id}" data-value="${ 1 }">
                                                                <span class="iconify" data-icon="cil:chevron-top" data-inline="false"></span>
                                                            </a>
                                                        </c:if>
                                                        <c:if test="${customTag:hasUserVotedComment(comment, loggedUser.id) and customTag:hasUserLikedComment(comment,loggedUser.id)}">
                                                            <a class="like-comment-button" data-id="${comment.id}" data-value="${ 0 }">
                                                                <span class="iconify" data-icon="el:chevron-up" data-inline="false"></span>
                                                            </a>
                                                        </c:if>
                                                    </sec:authorize>
                                                </div>
                                                <div class="uk-width-auto uk-text-center uk-padding-remove uk-margin-small-left uk-margin-small-right">
                                                    <p class="like-post-button uk-text-center uk-align-center uk-text-lead">
                                                        <c:out value="${comment.likes}"/>
                                                    </p>
                                                </div>
                                                <div class="uk-width-auto uk-text-center uk-padding-remove uk-align-right uk-margin-remove">
                                                    <sec:authorize access="hasRole('USER')">
                                                        <c:if test="${!customTag:hasUserVotedComment(comment, loggedUser.id) or customTag:hasUserLikedComment(comment,loggedUser.id)}">
                                                            <a class=" like-comment-button" data-id="${comment.id}"  data-value="${ -1 }">
                                                                <span class="iconify" data-icon="cil:chevron-bottom" data-inline="true"></span>
                                                            </a>
                                                        </c:if>
                                                        <c:if test="${customTag:hasUserVotedComment(comment, loggedUser.id) and !customTag:hasUserLikedComment(comment,loggedUser.id)}">
                                                            <a class="like-comment-button" data-id="${comment.id}" data-value="${ 0 }">
                                                                <span class="iconify" data-icon="el:chevron-down" data-inline="true"></span>
                                                            </a>
                                                        </c:if>
                                                    </sec:authorize>
                                                </div>
                                            </sec:authorize>
                                        </div>
                                    </div>
                                </div>
                                </c:if>
                            </div>
                    </header>
                    <div class="uk-comment-body">
                        <c:choose>
                            <c:when test="${comment.enabled}">
                                <span style="white-space: pre-line"><c:out value="${comment.body}"/></span>
                            </c:when>
                            <c:otherwise>
                                <div class="uk-text-italic"><spring:message code="comment.notEnabled.message"/></div>
                                <sec:authorize access="hasRole('ADMIN')">
                                <div class="uk-text-italic"><c:out value="[${comment.user.username}: ${comment.body}]"/></div>
                                </sec:authorize>
                            </c:otherwise>
                        </c:choose>
                    </div>
                </article>
                <hr>
            </div>
            <div class="replies-show uk-margin-bottom" id="${comment.id}-replies-show" data-id="${comment.id}" data-amount="${comment.descendantCount}">
                <a class="uk-link-muted"><spring:message code="comment.replies.show" arguments="${comment.descendantCount}"/></a>
            </div>
            <ul id="${comment.id}-children" class="li uk-hidden">
                    <%--  Recursive Call  --%>
                <c:set var="comments" value="${comment.children}" scope="request"/>
                <jsp:include page="commentTree.jsp" />
            </ul>
        </li>

    </c:forEach>
</ul>
