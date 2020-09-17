
<%--
  Created by IntelliJ IDEA.
  User: tobias
  Date: 30/8/20
  Time: 23:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<!DOCTYPE html>
<html lang="en" >
<head>
    <title>Create Post</title>

    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <jsp:include page="/WEB-INF/jsp/dependencies/mdEditor.jsp" />

    <script src="<c:url value="/resources/js/post/create.js" />"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<div class="uk-margin-auto uk-margin-top">
    <h1 class="uk-article-title uk-margin-auto uk-text-center uk-text-primary">Create a new post</h1>
    <hr class="uk-divider-icon">
</div>

<form method="post" class="uk-form-horizontal uk-margin-auto uk-padding-large uk-padding-remove-vertical" action="<c:url value="/post/create" />" id="new-post-form">

    <div class="uk-margin-left uk-margin-bottom" >
        <label class="uk-form-label uk-text-secondary uk-margin-auto" for="title-text">Choose a title for your post:</label>
        <div class="uk-form-controls">
            <input name="title" class="uk-input uk-form-small" id="title-text" type="text" placeholder="Title" required />
        </div>
    </div>

    <div class="uk-margin-left uk-margin-bottom">
        <label class="uk-form-label uk-text-secondary uk-margin-auto" for="email-text">Insert your email:</label>
        <div class="uk-form-controls">
            <input name="email" class="uk-input uk-form-small" id="email-text" type="email" placeholder="Email" required />
        </div>
    </div>

    <div class="uk-margin-left uk-margin-bottom">
        <label class="uk-form-label uk-text-secondary uk-margin-auto" for="category-select">Select the Post Category</label>
        <div class="uk-form-controls">
            <select name="category" class="uk-select" id="category-select" required>
                <c:forEach items="${categories}" var="category">
<%--                    TODO: Change category name from c:out to spring:value for i18n   --%>
                    <option value="${category.id}"><c:out value="${category.name}" /></option>
                </c:forEach>
            </select>
        </div>
    </div>

    <div class="uk-margin-auto">
        <label for="create-post-data"></label>
        <textarea id="create-post-data" name="body" required ></textarea>
    </div>
</form>
    <div class="uk-text-center">
        <button id="open-modal-button" class="uk-button uk-button-primary uk-border-rounded uk-margin-bottom" type="button">Create</button>
    </div>

<!-- This is the modal -->
<div id="movies-modal" uk-modal>
    <div class="uk-modal-dialog uk-modal-body uk-border-rounded">
        <p class="uk-text-right uk-margin-remove">
            <button class="uk-modal-close" type="button" uk-close></button>
        </p>
        <h2 class="uk-modal-title uk-margin-remove-top">What movies are discussed in this Post?</h2>
        <div>
            <div>
                <label for="add-movie-input" class="uk-margin-bottom" ></label>
                <input id="add-movie-input" class="uk-input uk-margin-right uk-input uk-margin-bottom uk-border-rounded" list="movie-list" placeholder="Insert the name of the movie you want to add">
                <button id="add-movie-button" class="uk-button uk-button-primary uk-border-rounded" type="button">Add</button>
            </div>
        </div>

        <div id="movies-selected" class="uk-margin-top uk-width-3-4"></div>

        <h4 class="uk-modal-title uk-margin-remove-top">Write tags that describe this post (5 max)</h4>
        <div>
            <div>
                <label for="add-tag-input" class="uk-margin-bottom" ></label>
                <input id="add-tag-input" class="uk-input uk-margin-right uk-input uk-margin-bottom uk-border-rounded" placeholder="Write the tag here">
                <button id="add-tag-button" class="uk-button uk-button-primary uk-border-rounded" type="button">Add</button>
            </div>
        </div>

        <div id="tags-selected" class="uk-margin-top uk-width-3-4"></div>

        <p class="uk-text-right">
            <button id="submit-form-button" class="uk-button uk-button-primary uk-border-rounded" type="button">Enviar</button>
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
