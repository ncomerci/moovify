<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>

<!DOCTYPE html>
<html lang="en">
<head>
    <title><spring:message code="post.create.title"/></title>

    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp"/>
    <jsp:include page="/WEB-INF/jsp/dependencies/mdEditor.jsp"/>

    <script src="<c:url value="/resources/js/post/create.js" />"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp"/>

<div class="uk-margin-auto uk-margin-top">
    <h1 class="uk-article-title uk-margin-auto uk-text-center uk-text-primary"><spring:message
            code="post.create.newPost"/></h1>
    <hr class="uk-divider-icon">
</div>
<div>
    <c:url value="/post/create" var="action"/>
    <form:form modelAttribute="postCreateForm"
               class="uk-form-stacked uk-margin-auto uk-padding-large uk-padding-remove-vertical" method="post"
               action='${action}' id="new-post-form">
        <form:errors path="movies" element="p" cssClass="error"/>
        <form:errors path="tags" element="p" cssClass="error"/>
        <spring:message code="post.create.titlePlaceholder" var="titlePlaceholder"/>
    <div class="uk-margin-left uk-margin-bottom">
        <form:label path="title" class="uk-form-label uk-text-secondary uk-margin-auto" for="title-text"><spring:message
                code="post.create.newPostTitle"/>
            <form:input path="title" class="uk-input uk-form-small" id="title-text" type="text" minlength="6"
                        placeholder="${titlePlaceholder}" required="required"/>
        </form:label>
        <form:errors path="title" element="p" cssClass="error" cssStyle="color:red;"/>
    </div>

    <div class="uk-margin-left uk-margin-bottom">
        <form:label path="category" class="uk-form-label uk-text-secondary uk-margin-auto"
                    for="category-select"><spring:message code="post.create.newPostCategory"/></form:label>
        <div class="uk-form-controls">
            <form:select path="category" class="uk-select" id="category-select" required="required">
                <option hidden disabled selected value><spring:message code="post.create.selectCategory"/></option>
                <c:forEach items="${categories}" var="category">
                    <form:option value="${category.id}"><spring:message code="${category.name}"/></form:option>
                </c:forEach>
            </form:select>
            <form:errors path="category" element="p" cssClass="error" cssStyle="color:red;"/>
        </div>

        <div class="uk-margin-auto">
            <form:errors path="body" element="p" cssClass="error"/>
            <form:label path="body" for="create-post-data"><spring:message code="post.create.body"/>
                <form:textarea path="body" id="create-post-data"/>
            </form:label>
        </div>
        </form:form>
    </div>
    <div class="uk-text-center">
        <button id="open-modal-button" class="uk-button uk-button-primary uk-border-rounded uk-margin-bottom"
                type="button"><spring:message code="post.create.createButton"/></button>
    </div>

    <!-- This is the modal -->
    <div id="movies-modal" class="uk-modal-container" uk-modal>
        <div class="uk-modal-dialog uk-modal-body uk-border-rounded">
            <p class="uk-text-right uk-margin-remove">
                <button class="uk-modal-close" type="button" uk-close></button>
            </p>
            <div>
                <h1 class="uk-h1 uk-margin-remove-top"><spring:message
                        code="post.create.modal.moviesDiscussed.title"/></h1>
                <label for="add-movie-input"><spring:message
                        code="post.create.modal.moviesDiscussed.constrains"/></label>
                <div class="uk-flex">
                    <input id="add-movie-input"
                           class="uk-input uk-margin-right uk-input uk-border-rounded uk-width-expand" list="movie-list"
                           placeholder="<spring:message code="post.create.modal.moviesPlaceholder"/>">
                    <button id="add-movie-button" class="uk-button uk-button-primary uk-border-rounded uk-width-auto"
                            type="button"><spring:message code="post.create.modal.addButton"/></button>
                </div>
                <p id="movie-error" class="uk-margin-remove-top" style="display: none; color: red"><spring:message
                        code="post.create.modal.movieError.client"/></p>
            </div>
            <div id="movies-selected" class="uk-width-3-4 uk-margin"></div>


            <div class="uk-margin-medium">
                <h1 class="uk-h2 uk-margin-remove-top"><spring:message code="post.create.modal.tags"/></h1>
                <label for="add-tag-input" class="uk-margin-bottom"><spring:message
                        code="post.create.modal.tags.constrains"/></label>
                <div class="uk-flex">
                    <input id="add-tag-input"
                           class="uk-input uk-margin-right uk-input uk-border-rounded uk-width-expand"
                           placeholder="<spring:message code="post.create.modal.tagsPlaceholder"/>">
                    <button id="add-tag-button" class="uk-button uk-button-primary uk-border-rounded uk-width-auto"
                            type="button"><spring:message code="post.create.modal.addButton"/></button>
                </div>
            </div>

            <div id="tags-selected" class="uk-margin uk-width-3-4"></div>

            <p class="uk-text-center">
                <button id="submit-form-button" class="uk-button uk-button-primary uk-border-rounded" type="button">
                    <spring:message code="post.create.modal.sendButton"/></button>
            </p>
        </div>
    </div>

    <datalist id="movie-list">
        <c:forEach items="${movies.results}" var="movie">
        <option value="${movie.title}" data-id="${movie.id}">
            </c:forEach>
    </datalist>

</body>
</html>

</jsp:root>