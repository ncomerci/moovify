<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ taglib prefix="fmt" uri = "http://java.sun.com/jsp/jstl/fmt" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="search.movies.pageTitle"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp"/>
    <script src="<c:url value="/resources/js/components/paginationController.js"/>"></script>
    <script src="<c:url value="/resources/js/search/movies.js"/>"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp"/>

<main class="uk-container-small uk-margin-auto uk-padding-small">
    <c:url value="/search/movies" var="action"/>
    <form:form modelAttribute="searchMoviesForm" method="get" action="${action}">
        <section id="search-controllers">
            <c:set var="query" value="${query}" scope="request"/>
            <c:set var="currentSearch" value="1" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/search/defaultForm.jsp"/>

            <div class="uk-flex uk-margin-small-top">
                <div class="uk-width-1-3 uk-flex uk-flex-wrap uk-flex-baseline">
                    <form:label path="movieCategory" class="uk-form-label uk-margin-small-right uk-width-auto" for="post-category">
                        <spring:message code="search.movies.categories.label"/>
                    </form:label>
                    <form:select path="movieCategory" class="uk-select uk-form-blank uk-width-expand">
                        <form:option value="all"><spring:message code="search.movies.categories.all"/></form:option>
                        <c:forEach items="${categories}" var="category" >
                            <form:option value="${category}"><spring:message code="search.movies.categories.${category}"/></form:option>
                        </c:forEach>
                    </form:select>
                </div>
                <div class="uk-width-1-3 uk-flex uk-flex-wrap uk-flex-baseline">
                    <form:label path="decade" class="uk-padding-small-left uk-form-label uk-margin-small-right uk-width-auto" for="decade">
                        <spring:message code="search.movies.decades.label"/>
                    </form:label>
                    <form:select path="decade" class="uk-select uk-form-blank uk-width-expand">
                        <form:option value="any"><spring:message code="search.movies.decades.any"/></form:option>
                        <c:forEach items="${decades}" var="decade" >
                            <form:option value="${decade}"><spring:message code="search.movies.decades.${decade}"/></form:option>
                        </c:forEach>
                    </form:select>
                </div>
                <div class="uk-width-1-3 uk-flex uk-flex-wrap uk-flex-baseline">
                    <form:label path="sortCriteria" class="uk-form-label uk-margin-small-right uk-width-auto" for="sort-criteria">
                        <spring:message code="search.movies.sortCriteria.label"/>
                    </form:label>
                    <form:select path="sortCriteria" class="uk-select uk-form-blank uk-width-expand">
                        <c:forEach items="${sortCriteria}" var="criteria" >
                            <form:option value="${criteria}"><spring:message code="search.movies.sortCriteria.${criteria}"/></form:option>
                        </c:forEach>
                    </form:select>
                </div>
            </div>
        </section>

        <section id="search-results" class="uk-flex uk-flex-wrap">
            <c:if test="${empty movies.results}">
                <h1 class="uk-text-meta uk-text-center uk-text-bold">
                    <spring:message code="search.notFound" arguments="movies"/></h1>
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
                                    <spring:message code="search.movies.categories.${category.name}"/> -
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
            <c:set var="collection" value="${movies}" scope="request"/>
            <c:url var="baseURL" value="/search/movies" scope="request">
                <c:param name="query" value="${searchMoviesForm.query}"/>
                <c:param name="decade" value="${searchMoviesForm.decade}"/>
                <c:param name="sortCriteria" value="${searchMoviesForm.sortCriteria}"/>
                <c:param name="movieCategory" value="${searchMoviesForm.movieCategory}"/>
            </c:url>
            <c:set var="numberOfInputs" value="${2}" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/components/paginationController.jsp" />
        </c:if>
    </form:form>
</main>
</body>
</html>
