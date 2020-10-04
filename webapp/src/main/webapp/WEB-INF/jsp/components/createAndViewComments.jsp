<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags"%>

<%@ page contentType="text/html;charset=UTF-8"%>

<jsp:useBean id="comments" scope="request" type="ar.edu.itba.paw.models.PaginatedCollection<ar.edu.itba.paw.models.Comment>"/>
<jsp:useBean id="postId" scope="request" type="java.lang.Long"/>
<jsp:useBean id="parentId" scope="request" type="java.lang.Long"/>

<c:if test="${parentId == 0}">
    <c:set var="parentId" value="${null}"/>
</c:if>

<section id="post-comments" class="uk-container uk-container-small">
    <h1 class="uk-h2"><spring:message code="post.view.comments.title" arguments="${comments.totalCount}"/></h1>
    <sec:authorize access="hasRole('USER')">
        <div style="padding-bottom: 25px">
            <c:url value="/comment/create" var="action"/>
            <%--@elvariable id="CommentCreateForm" type="ar.edu.itba.paw.webapp.form.CommentCreateForm"--%>
            <form:form id="spring-form" modelAttribute="CommentCreateForm" action="${action}" method="post">
                <c:set var="placeholder"><spring:message code="comment.create.writeCommentPlaceholder"/></c:set>
                <div class="uk-margin">
                    <form:label path="postId">
                        <form:hidden path="postId" value="${postId}"/>
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
        <c:set var="paginatedComments" value="${comments}" scope="request"/>
        <c:set var="comments" value="${comments.results}" scope="request"/>
        <jsp:include page="/WEB-INF/jsp/components/commentTree.jsp"/>
    </div>
    <c:if test="${not empty paginatedComments.results}">

        <c:set var="collection" value="${paginatedComments}" scope="request"/>
        <c:url var="baseURL" value="/post/${postId}" scope="request"/>
        <c:set var="numberOfInputs" value="${2}" scope="request"/>
        <form action="${baseURL}" method="get">
            <jsp:include page="/WEB-INF/jsp/components/paginationController.jsp" />
        </form>
    </c:if>

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

    <%-- Comment like form --%>
    <form method="post" action="<c:url value="/comment/like"/>" id="comment-like-form">
        <label>
            <input hidden type="number" name="post_id" value="${postId}"/>
        </label>
        <label>
            <input hidden type="number" id="comment-id" name="comment_id"/>
        </label>
        <label>
            <input hidden type="checkbox" id="like-value" name="value"/>
        </label>
    </form>

<sec:authorize access="hasRole('ADMIN')">
    <%--  Delete form  --%>
    <form method="post" action="" id="delete-form">
        <label>
            <input hidden type="number" name="postId" value="${postId}"/>
        </label>
    </form>

    <!-- delete confirmation modal -->
    <div id="delete-modal" uk-modal>
        <div class="uk-modal-dialog uk-modal-body">
            <h2 class="uk-modal-title"><spring:message code="comment.delete.confirmTitle"/></h2>
            <p id="modal-body" class="uk-text-italic"></p>
            <p class="uk-text-right">
                <button class="uk-button uk-button-default uk-modal-close uk-border-rounded" type="button"><spring:message code="comment.delete.cancelButton"/></button>
                <button id="modal-confirm" class="uk-button uk-button-primary uk-border-rounded" type="button"><spring:message code="comment.delete.confirmButton"/></button>
            </p>
        </div>
    </div>
</sec:authorize>
</section>

