<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="search.posts.pageTitle"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <script src="<c:url value="/resources/js/search/posts.js"/>"></script>
    <script src="<c:url value="/resources/js/components/paginationController.js"/>"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<main class="uk-container-small uk-margin-auto uk-padding-small">
    <c:url value="/search/posts/" var="action"/>
    <form:form modelAttribute="searchPostsForm" method="get" action="${action}">
        <section id="search-controllers">
            <c:set var="query" scope="request"><c:out value="${query}"/></c:set>
            <c:set var="currentSearch" value="0" scope="request" />
            <div>
                <jsp:include page="defaultForm.jsp"/>
            </div>
            <div class="uk-form-horizontal uk-grid-small" uk-grid>
                <div class="uk-width-1-3">
                    <div class="uk-margin">
                        <form:label path="postCategory" class="uk-form-label" for="post-category" style="width: auto"><spring:message code="search.posts.categories.label"/></form:label>
                        <div class="uk-form-controls" style="margin-left: 100px">
                            <form:select path="postCategory" class="uk-select uk-form-blank">
                                <form:option value="all"><spring:message code="search.posts.categories.all"/></form:option>
                                <form:option value="critique"><spring:message code="search.posts.categories.critique"/></form:option>
                                <form:option value="watchlist"><spring:message code="search.posts.categories.watchlist"/></form:option>
                                <form:option value="news"><spring:message code="search.posts.categories.news"/></form:option>
                                <form:option value="debate"><spring:message code="search.posts.categories.debate"/></form:option>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="uk-width-1-3">
                    <div class="uk-margin">
                        <form:label path="postAge" class="uk-form-label" for="post-age" style="width: auto"><spring:message code="search.posts.postAge.label"/></form:label>
                        <div class="uk-form-controls" style="margin-left: 100px">
                            <form:select path="postAge" class="uk-select uk-form-blank">
                                <form:option value="all-time"><spring:message code="search.posts.postAge.allTime"/></form:option>
                                <form:option value="past-year"><spring:message code="search.posts.postAge.pastYear"/></form:option>
                                <form:option value="past-month"><spring:message code="search.posts.postAge.pastMonth"/></form:option>
                                <form:option value="past-day"><spring:message code="search.posts.postAge.pastDay"/></form:option>
                            </form:select>
                        </div>
                    </div>
                </div>
                <div class="uk-width-1-3">
                    <div class="uk-margin">
                        <form:label path="sortCriteria" class="uk-form-label" for="sort-criteria" style="width: auto"><spring:message code="search.posts.sortCriteria.label"/></form:label>
                        <div class="uk-form-controls" style="margin-left: 100px">
                            <form:select path="sortCriteria" class="uk-select uk-form-blank">
                                <form:option value="newest"><spring:message code="search.posts.sortCriteria.newest"/></form:option>
                                <form:option value="hottest"><spring:message code="search.posts.sortCriteria.hottest"/></form:option>
                                <form:option value="oldest"><spring:message code="search.posts.sortCriteria.oldest"/></form:option>
                            </form:select>
                        </div>
                    </div>
                </div>
            </div>
        </section>
        <section id="search-results" class="uk-margin-top">
            <c:if test="${empty posts.results}">
                <h1 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="search.posts.postsNotFound"/> </h1>
            </c:if>
            <c:set var="posts" value="${posts}" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>
        </section>
        <c:if test="${not empty posts.results}">
            <c:set var="collection" value="${posts}" scope="request"/>
            <c:url var="baseURL" value="/search/posts/" scope="request">
                <c:param name="query" value="${searchPostsForm.query}"/>
                <c:param name="postAge" value="${searchPostsForm.postAge}"/>
                <c:param name="postCategory" value="${searchPostsForm.postCategory}"/>
                <c:param name="sortCriteria" value="${searchPostsForm.sortCriteria}"/>
            </c:url>
            <c:set var="numberOfInputs" value="${2}" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/components/paginationController.jsp" />
        </c:if>
    </form:form>
</main>
</body>
</html>
