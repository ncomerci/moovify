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
    <form method="get" id="search-form" action="<c:url value="/search/posts/"/>">

        <c:set var="query" value="${query}" scope="request" />
        <c:set var="currentSearch" value="0" scope="request" />
        <jsp:include page="/WEB-INF/jsp/search/defaultForm.jsp"/>

        <div class="uk-form-horizontal uk-grid-small" uk-grid>
            <div class="uk-width-1-3">
                <div class="uk-margin">
                    <label class="uk-form-label" for="post-category" style="width: auto"><spring:message code="search.posts.categories.label"/> </label>
                    <div class="uk-form-controls" style="margin-left: 100px">
                        <select class="uk-select uk-form-blank" id="post-category" name="post-category">
                            <option value="all"><spring:message code="search.posts.categories.all"/></option>
                            <option value="critique"><spring:message code="search.posts.categories.critique"/></option>
                            <option value="watchlist"><spring:message code="search.posts.categories.watchlist"/></option>
                            <option value="news"><spring:message code="search.posts.categories.news"/></option>
                            <option value="debate"><spring:message code="search.posts.categories.debate"/></option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="uk-width-1-3">
                <div class="uk-margin">
                    <label class="uk-form-label" for="post-age" style="width: auto"><spring:message code="search.posts.postAge.label"/> </label>
                    <div class="uk-form-controls" style="margin-left: 100px">
                        <select class="uk-select uk-form-blank" id="post-age" name="post-age">
                            <option value="all-time"><spring:message code="search.posts.postAge.allTime"/></option>
                            <option value="past-year"><spring:message code="search.posts.postAge.pastYear"/></option>
                            <option value="past-month"><spring:message code="search.posts.postAge.pastMonth"/></option>
                            <option value="past-day"><spring:message code="search.posts.postAge.pastDay"/></option>
                        </select>
                    </div>
                </div>
            </div>
            <div class="uk-width-1-3">
                <div class="uk-margin">
                    <label class="uk-form-label" for="sort-criteria" style="width: auto"><spring:message code="search.posts.sortCriteria.label"/></label>
                    <div class="uk-form-controls" style="margin-left: 100px">
                        <select class="uk-select uk-form-blank" id="sort-criteria" name="sort-criteria">
                            <option value="newest"><spring:message code="search.posts.sortCriteria.newest"/></option>
                            <option value="hottest"><spring:message code="search.posts.sortCriteria.hottest"/></option>
                            <option value="oldest"><spring:message code="search.posts.sortCriteria.oldest"/></option>
                        </select>
                    </div>
                </div>
            </div>

        </div>
    </form>
    <dl class="uk-description-list ">
            <c:forEach items="${posts}" var="post">
                <dt>
                    <a href="<c:url value="/post/${post.id}"/>">
                        <c:out value="${post.title}"/>
                    </a>
                </dt>
            </c:forEach>
        </dl>
</main>
</body>
</html>
