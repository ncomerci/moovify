<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>

<!DOCTYPE html>
<html lang="en" >
<head>
    <title><spring:message code="movie.title"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>

<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<form method="post" action="<c:url value="/movie/create"/>">
    <label>
        <input type="text" name="title" class="uk-input uk-form-width-large" placeholder="Titulo" required />
    </label>
    <label>
        <input type="text" name="originalTitle" class="uk-input uk-form-width-large" placeholder="Original Title" required />
    </label>
    <label>
        <input type="number" name="tmdbId" class="uk-input uk-form-width-large" placeholder="The movie data base ID" required />
    </label>
    <label>
        <input type="text" name="imdbId" class="uk-input uk-form-width-large" placeholder="TIMDB ID" required />
    </label>
    <label>
        <input type="text" name="originalLanguage" class="uk-input uk-form-width-large" placeholder="originalLanguage" required />
    </label>
    <label>
        <textarea name="overview" class="uk-input uk-form-width-large" placeholder="overview" required></textarea>
    </label>
    <label>
        <input type="text" name="popularity" class="uk-input uk-form-width-large" placeholder="Popularity" required />
    </label>
    <label>

        <input type="text" name="runtime" class="uk-input uk-form-width-large" placeholder="Runtime" required />
    </label>
    <label>
        <input type="text" name="voteAverage" class="uk-input uk-form-width-large" placeholder="Vote Average" required />
    </label>
    <label>
        <input type="date" name="releaseDate" class="uk-input uk-form-width-large" placeholder="Release Date" required />
    </label>
    <br>
    <c:forEach items="${categories}" var="category">
        <input type="checkbox" id="${category.name}" name="categories" value="${category.tmdbCategoryId}">
        <label for="${category.name}"><c:out value="${category.name}"/></label><br>
    </c:forEach>
    <spring:message var="submit" code="movie.create.submit"/>
    <input type="submit" value="${submit}">

</form>
</body>
</html>