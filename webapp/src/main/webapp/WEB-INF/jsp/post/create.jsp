
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

    <form method="post" action="<c:url value="/post/create" />" id="new-post-form">
        <input type="text" name="title" class="uk-input uk-form-width-large" placeholder="Titulo" required />
        <input type="text" name="email" class="uk-input uk-form-width-large" placeholder="Email" required />

        <label>
            <textarea id="create-post-data" name="body" required ></textarea>
        </label>
    </form>

    <button id="open-modal-button" type="button">Done</button>

    <!-- This is the modal -->
    <div id="movies-modal" uk-modal>
        <div class="uk-modal-dialog uk-modal-body">
            <h2 class="uk-modal-title">De que peliculas se habla en este Post?</h2>

            <div>
                <div>
                    <label for="add-movie-input"></label>
                    <input id="add-movie-input" list="movie-list" placeholder="Ingrese el nombre de la pelicula que quiera agregar">
                    <button id="add-movie-button" class="uk-button uk-button-primary" type="button">Add</button>
                </div>
            </div>

            <div id="movies-selected"></div>

            <p class="uk-text-right">
                <button class="uk-button uk-button-default uk-modal-close" type="button">Volver</button>
                <button id="submit-form-button" class="uk-button uk-button-primary" type="button">Enviar</button>
            </p>
        </div>
    </div>

    <datalist id="movie-list">
        <c:forEach items="${movies}" var="movie">
            <option value="${movie.title}" data-id="${movie.id}" >
        </c:forEach>
    </datalist>

</body>
</html>
