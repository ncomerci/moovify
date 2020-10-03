<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="search.pageTitle" arguments="${query}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
    <script src="<c:url value="/resources/js/search/posts.js"/>"></script>
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
            <c:if test="${empty posts}">
                <h1 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="search.posts.postsNotFound"/> </h1>
            </c:if>
            <c:set var="posts" value="${posts}" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>
        </section>

        <ul class="uk-pagination uk-flex-center" uk-margin>
            <c:if test="${posts.pageNumber > 0}">
                <c:url value = "/search/posts/" var = "pageURL">
                    <c:param name = "query" value = "${searchPostsForm.query}"/>
                    <c:param name = "postCategory" value = "${searchPostsForm.postCategory}"/>
                    <c:param name = "postAge" value = "${searchPostsForm.postAge}"/>
                    <c:param name = "sortCriteria" value = "${searchPostsForm.sortCriteria}"/>
                    <c:param name = "pageNumber" value = "${posts.pageNumber - 1}"/>
                    <c:param name = "pageSize" value = "${posts.pageSize}"/>
                </c:url>
                <li><a href="${pageURL}"><span uk-pagination-previous></span></a></li>
            </c:if>

            <c:set value="${posts.pageNumber - 1 >= 0 ? posts.pageNumber - 1 : 0}" var="firstPage"/>
            <c:forEach begin="0" end="2" var="index">
                <c:if test="${firstPage + index <= posts.lastPageNumber}">
                    <c:url value = "/search/posts/" var = "pageURL">
                        <c:param name = "query" value = "${searchPostsForm.query}"/>
                        <c:param name = "postCategory" value = "${searchPostsForm.postCategory}"/>
                        <c:param name = "postAge" value = "${searchPostsForm.postAge}"/>
                        <c:param name = "sortCriteria" value = "${searchPostsForm.sortCriteria}"/>
                        <c:param name = "pageNumber" value = "${firstPage + index}"/>
                        <c:param name = "pageSize" value = "${posts.pageSize}"/>
                    </c:url>
                    <li class="${ firstPage + index == posts.pageNumber ? 'uk-active' : ''}"><a href="${pageURL}"><c:out value="${firstPage + index + 1}"/></a></li>
                </c:if>
            </c:forEach>

            <c:if test="${posts.pageNumber < posts.lastPageNumber }">
                <c:url value = "/search/posts/" var = "pageURL">
                    <c:param name = "query" value = "${searchPostsForm.query}"/>
                    <c:param name = "postCategory" value = "${searchPostsForm.postCategory}"/>
                    <c:param name = "postAge" value = "${searchPostsForm.postAge}"/>
                    <c:param name = "sortCriteria" value = "${searchPostsForm.sortCriteria}"/>
                    <c:param name = "pageNumber" value = "${posts.pageNumber + 1}"/>
                    <c:param name = "pageSize" value = "${posts.pageSize}"/>
                </c:url>
                <li><a href="${pageURL}"><span uk-pagination-next></span></a></li>
            </c:if>

        </ul>
    </form:form>
</main>
</body>
</html>
