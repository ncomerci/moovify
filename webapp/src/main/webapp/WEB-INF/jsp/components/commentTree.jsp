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
                        <div class="uk-grid-medium uk-flex-middle" uk-grid>
                            <c:if test="${comment.enabled}">
                            <div class="uk-width-auto">
                                <img class="uk-border-circle uk-comment-avatar" src="<c:url value="/resources/images/avatar.jpg"/>" width="80" height="80" alt="">
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

                        <c:if test="${comment.enabled}">
                        <div class="uk-position-top-right">
                            <div class="uk-flex uk-flex-column">
                                <sec:authorize access="hasRole('USER')">
                                    <div>
                                            <%--TODO no se como hacer para que scrollee automaticamente a los comentarios que son hijos--%>
                                        <c:if test="${!customTag:hasUserLikedComment(loggedUser,comment.id )}">
                                            <a class="uk-padding-remove uk-align-right uk-margin-remove like-comment-button" data-id="${comment.id}" data-value="true">
                                                <span class="uk-text-right"><c:out value="${comment.likes}"/></span>
                                                <sec:authorize access="hasRole('USER')">
                                                    <span class="iconify" data-icon="ant-design:heart-outlined" data-inline="false"></span>
                                                </sec:authorize>
                                            </a>
                                        </c:if>
                                        <c:if test="${customTag:hasUserLikedComment(loggedUser, comment.id)}">
                                            <a class="uk-padding-remove uk-align-right uk-margin-remove like-comment-button" data-id="${comment.id}" data-value="false">
                                                <span class="uk-text-right"><c:out value="${comment.likes}"/></span>
                                                <sec:authorize access="hasRole('USER')">
                                                    <span class="iconify" data-icon="ant-design:heart-filled" data-inline="false"></span>
                                                </sec:authorize>
                                            </a>
                                        </c:if>
                                        <a data-id="<c:out value="${comment.id}"/>" class="uk-link-muted reply-button uk-position-small uk-hidden-hover"><spring:message code="comment.create.reply"/></a>
                                    </div>
                                </sec:authorize>
                                <div>
                                    <a class="uk-link-muted uk-position-small uk-hidden-hover" href="<c:url value="/comment/${comment.id}"/>">
                                        <spring:message code="comment.viewComment"/>
                                    </a>
                                </div>
                            </div>
                        </div>
                        </c:if>
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
