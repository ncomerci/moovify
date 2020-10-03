<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="search.pageTitle" arguments="${query}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp"/>
    <script src="<c:url value="/resources/js/search/movies.js"/>"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp"/>

<main class="uk-container-small uk-margin-auto uk-padding-small">
    <c:url value="/search/movies/" var="action"/>
    <form:form modelAttribute="searchMoviesForm" method="get" action="${action}">
        <section id="search-controllers">
            <c:set var="query" value="${query}" scope="request"/>
            <c:set var="currentSearch" value="1" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/search/defaultForm.jsp"/>
        </section>
        <section id="search-results uk-flex uk-flex-wrap">
            <c:if test="${empty movies.results}">
                <h1 class="uk-text-meta uk-text-center uk-text-bold"><spring:message
                        code="search.movies.moviesNotFound"/></h1>
            </c:if>
            <c:forEach items="${movies.results}" var="movie">
                <div class="uk-width-1-1">
                    <div class="uk-flex">
                        <div class="uk-width-expand uk-margin-small-top">
                            <a href="<c:url value="/movie/${movie.id}"/>">
                                <c:out value="${movie.title}"/>
                            </a>
                            <p class="uk-text-capitalize uk-text-meta uk-margin-remove-vertical">
                                <c:forEach items="${movie.categories}" var="category">
                                    <c:out value="${category.name}"/> -
                                </c:forEach>
                                <fmt:parseDate value="${movie.releaseDate}" pattern="yyyy-MM-dd" var="parsedDateTime" />
                                <fmt:formatDate pattern="yyyy" value="${parsedDateTime}" />
                            </p>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </section>
        <c:if test="${not empty movies.results}">
            <div class="uk-flex uk-flex-wrap ">
                <div class="uk-form-horizontal uk-margin-auto-vertical">
                    <form:label path="pageSize" class="uk-form-label" for="page-size" style="width: auto"><spring:message code="search.posts.pagination.pageSize.message"/></form:label>
                    <div class="uk-form-controls" style="margin-left: 100px">
                        <form:select path="pageSize" class="uk-select uk-form-blank">
                            <form:option value="2">2</form:option>
                            <form:option value="5">5</form:option>
                            <form:option value="10">10</form:option>
                            <form:option value="25">25</form:option>
                        </form:select>
                    </div>
                </div>
                <ul id="pagination-page-selector" class="uk-width-expand uk-pagination uk-flex-center" uk-margin>
                    <c:set value="/search/movies/" var="baseURL"/>
                    <c:if test="${movies.pageNumber > 0}">
                        <c:url value = "${baseURL}" var = "pageURL">
                            <c:param name = "query" value = "${searchMoviesForm.query}"/>
                            <c:param name = "pageNumber" value = "${movies.pageNumber - 1}"/>
                            <c:param name = "pageSize" value = "${movies.pageSize}"/>
                        </c:url>
                        <li><a href="${pageURL}"><span uk-pagination-previous></span></a></li>
                    </c:if>

                    <c:set value="${movies.pageNumber - 1 >= 0 ? movies.pageNumber - 1 : 0}" var="firstPage"/>
                    <c:forEach begin="0" end="2" var="index">
                        <c:if test="${firstPage + index <= movies.lastPageNumber}">
                            <c:url value = "${baseURL}" var = "pageURL">
                                <c:param name = "query" value = "${searchMoviesForm.query}"/>
                                <c:param name = "pageNumber" value = "${firstPage + index}"/>
                                <c:param name = "pageSize" value = "${movies.pageSize}"/>
                            </c:url>
                            <li class="${ firstPage + index == movies.pageNumber ? 'uk-active' : ''}">
                                <a href="${pageURL}"><c:out value="${firstPage + index + 1}"/></a>
                            </li>
                        </c:if>
                    </c:forEach>

                    <c:if test="${movies.pageNumber < movies.lastPageNumber }">
                        <c:url value = "${baseURL}" var = "pageURL">
                            <c:param name = "query" value = "${searchMoviesForm.query}"/>
                            <c:param name = "pageNumber" value = "${movies.pageNumber + 1}"/>
                            <c:param name = "pageSize" value = "${movies.pageSize}"/>
                        </c:url>
                        <li><a href="${pageURL}"><span uk-pagination-next></span></a></li>
                    </c:if>
                </ul>
            </div>
        </c:if>
    </form:form>
</main>
</body>
</html>
