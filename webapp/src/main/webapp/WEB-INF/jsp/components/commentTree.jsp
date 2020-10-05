<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="customTag" uri="http://www.paw.itba.edu.ar/moovify/tags"%>

<jsp:useBean id="comments" scope="request" type="java.util.Collection"/>
<ul class="uk-comment-list" id="comment-section">
    <c:forEach items="${comments}" var="comment" >

        <li>
            <div id="${comment.id}">
                <c:choose>
                <c:when test="${comment.enabled}">
                <article class="uk-comment uk-visible-toggle" tabindex="-1">
                    <header class="uk-comment-header uk-position-relative">
                        <div class="uk-grid-medium uk-flex-middle" uk-grid>
                            <div class="uk-width-auto">
                                <img class="uk-border-circle uk-comment-avatar" src="<c:url value="/resources/images/avatar.jpg"/>" width="80" height="80" alt="">
                            </div>
                            <div class="uk-width-expand" >
                                <h4 class="uk-comment-title uk-margin-remove">
                                    <c:choose>
                                        <c:when test="${comment.user.enabled}">
                                            <a class="comment-user-name" href = "<c:url value="/user/${comment.user.id}" />">
                                                <c:out value="${comment.user.name}" />
                                            </a>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="comment-user-name" class="uk-text-italic">
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
                                <p class="uk-comment-meta uk-margin-remove-top">
                                    <fmt:parseDate value="${comment.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                                    <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                                </p>
                            </div>
                        </div>

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
                                        <a class="uk-link-muted reply-button uk-position-small uk-hidden-hover" href="<c:url value="/comment/${comment.id}"/>">
                                            <spring:message code="comment.viewComment"/>
                                        </a>
                                    </div>
                                </div>
                            </div>

                    </header>
                    <div class="uk-comment-body">
                        <span style="white-space: pre-line"><c:out value="${comment.body}"/></span>
                    </div>
                </article>
                </c:when>
                <c:otherwise>
                    <article class="uk-comment uk-visible-toggle" tabindex="-1">
                        <header class="uk-comment-header uk-position-relative uk-margin-remove-bottom">
                            <div class="uk-grid-medium uk-flex-middle" uk-grid>
                                <div class="uk-width-expand">
                                    <p class="uk-comment-meta uk-margin-remove-vertical uk-text-italic">
                                        <fmt:parseDate value="${comment.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                                        <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                                    </p>
                                </div>
                            </div>
                            <div class="uk-position-top-right">
                                <div class="uk-flex uk-flex-column">
                                    <div>
                                        <a class="uk-link-muted reply-button uk-position-small uk-hidden-hover" href="<c:url value="/comment/${comment.id}"/>"><spring:message code="comment.viewComment"/></a>
                                    </div>
                                </div>
                            </div>
                        </header>
                        <div class="uk-comment-body">
                            <span class="uk-text-italic"><spring:message code="comment.notEnabled.message"/></span>
                        </div>
                    </article>
                </c:otherwise>
                </c:choose>
                <hr>
            </div>
            <div class="replies-show" id="${comment.id}-replies-show" data-id="${comment.id}" data-amount="${comment.descendantCount}">
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
