<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>

<jsp:useBean id="comments" scope="request" type="java.util.Collection"/>
<ul class="uk-comment-list">
    <c:forEach items="${comments}" var="comment" >

        <li>
            <div id="${comment.id}">
                <article class="uk-comment uk-visible-toggle" tabindex="-1">
                    <header class="uk-comment-header uk-position-relative">
                        <div class="uk-grid-medium uk-flex-middle" uk-grid>
                            <div class="uk-width-auto">
                                <img class="uk-comment-avatar" src="<c:url value="/resources/images/avatar.jpg"/>" width="80" height="80" alt="">
                            </div>
                            <div class="uk-width-expand">
                                <h4 class="uk-comment-title uk-margin-remove">
                                    <a href = "<c:url value="/user/${comment.user.id}" />"><c:out value="${comment.user.name}" /> </a>
                                </h4>
                                <p class="uk-comment-meta uk-margin-remove-top">
                                    <fmt:parseDate value="${comment.creationDate}" pattern="yyyy-MM-dd'T'HH:mm" var="parsedDateTime" type="both" />
                                    <fmt:formatDate pattern="dd/MM/yyyy HH:mm" value="${parsedDateTime}" />
                                </p>
                            </div>
                        </div>
                        <sec:authorize access="hasAnyRole('USER', 'ADMIN')">
                            <div class="uk-position-top-right uk-position-small uk-hidden-hover">
                                <a data-id="<c:out value="${comment.id}"/>" class="uk-link-muted reply-button"><spring:message code="comment.create.reply"/></a>
                            </div>
                        </sec:authorize>
                    </header>
                    <div class="uk-comment-body">
                        <p><c:out value="${comment.body}" /></p>
                    </div>
                </article>
                <hr>
            </div>
            <ul class="li">
                    <%--  Recursive Call  --%>
                <c:set var="comments" value="${comment.children}" scope="request"/>
                <jsp:include page="commentTree.jsp" />
            </ul>
        </li>

    </c:forEach>
</ul>
