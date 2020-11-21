<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Title</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<c:url value="/movie/${movie.id}/poster/update" var="action"/>
<form:form modelAttribute="updateMoviePosterForm" action="${action}" method="post" enctype="multipart/form-data">

    <div class="uk-container uk-margin-auto">
        <h1 class="uk-width-1-1"><spring:message code="movie.updatePoster.pageTitle"/></h1>
        <div class="uk-flex">
            <div class="uk-width-max-content">
                <img width="300px" src="<c:url value="/movie/poster/${movie.posterId}"/>"
                     alt="<spring:message code="movie.updatePoster.previousPoster.altText" arguments="${movie.title}"/>"/>
            </div>
            <div class="uk-width-max-content uk-padding-large">
                <h3><spring:message code="movie.updatePoster.newPoster.title" arguments="${movie.title}"/></h3>
                <form:label path="poster">
                    <form:input path="poster" type="file" accept="image/*"/>
                </form:label>
                <form:errors path="poster" element="p" cssClass="error uk-margin-remove-top"/>
                <div class="uk-text-center uk-margin-medium-top">
                    <input class="uk-button uk-button-primary uk-border-rounded extended-button" type="submit"
                           value="<spring:message code="comment.create.button"/>"/>
                </div>
            </div>
        </div>
    </div>

</form:form>

</body>
</html>
