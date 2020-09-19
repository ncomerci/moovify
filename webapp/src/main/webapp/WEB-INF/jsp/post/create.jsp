<%--
  Created by IntelliJ IDEA.
  User: tobias
  Date: 30/8/20
  Time: 23:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<!DOCTYPE html>
<html lang="en" >
<head>
    <title><spring:message code="post.create.title"/></title>

    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <jsp:include page="/WEB-INF/jsp/dependencies/mdEditor.jsp" />

    <script src="<c:url value="/resources/js/post/create.js" />"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<div class="uk-margin-auto uk-margin-top">
    <h1 class="uk-article-title uk-margin-auto uk-text-center uk-text-primary"><spring:message code="post.create.newPost"/></h1>
    <hr class="uk-divider-icon">
</div>
<div>
    <jsp:include page="form.jsp"/>
</div>
    <div class="uk-text-center">
        <button id="open-modal-button" class="uk-button uk-button-primary uk-border-rounded uk-margin-bottom" type="button"><spring:message code="post.create.createButton"/></button>
    </div>

<!-- This is the modal -->
<div id="movies-modal" uk-modal>
    <div class="uk-modal-dialog uk-modal-body uk-border-rounded">
        <p class="uk-text-right uk-margin-remove">
            <button class="uk-modal-close" type="button" uk-close></button>
        </p>
        <h2 class="uk-modal-title uk-margin-remove-top"><spring:message code="post.create.modal.moviesDiscussed"/></h2>
        <div>
            <div>
                <label for="add-movie-input" class="uk-margin-bottom" ></label>
                <input id="add-movie-input" class="uk-input uk-margin-right uk-input uk-margin-bottom uk-border-rounded" list="movie-list" placeholder="<spring:message code="post.create.modal.moviesPlaceholder"/>">
                <button id="add-movie-button" class="uk-button uk-button-primary uk-border-rounded" type="button"><spring:message code="post.create.modal.addButton"/></button>
            </div>
        </div>

        <div id="movies-selected" class="uk-margin-top uk-width-3-4"></div>

        <h4 class="uk-modal-title uk-margin-remove-top"></h4>
        <div>
            <div>
                <label for="add-tag-input" class="uk-margin-bottom" ><spring:message code="post.create.modal.tags"/></label>
                <input id="add-tag-input" class="uk-input uk-margin-right uk-input uk-margin-bottom uk-border-rounded" placeholder="<spring:message code="post.create.modal.tagsPlaceholder"/>">
                <button id="add-tag-button" class="uk-button uk-button-primary uk-border-rounded" type="button"><spring:message code="post.create.modal.addButton"/></button>
            </div>
        </div>

        <div id="tags-selected" class="uk-margin-top uk-width-3-4"></div>

        <p class="uk-text-right">
            <button id="submit-form-button" class="uk-button uk-button-primary uk-border-rounded" type="button"><spring:message code="post.create.modal.sendButton"/></button>
        </p>
    </div>
</div>

<datalist id="movie-list">
    <c:forEach items="${movies}" var="movie">
        <option value="${movie.title}" data-id="${movie.id}">
    </c:forEach>
</datalist>

</body>
</html>
