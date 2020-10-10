<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:useBean id="currentSearch" scope="request" type="java.lang.String"/>
<jsp:useBean id="query" scope="request" type="java.lang.String"/>

<div class="uk-flex uk-flex-middle">
    <div class="uk-search uk-search-large uk-width-expand">
        <span uk-search-icon></span>
        <form:input path="query" class="uk-search-input search-query-input" type="search" placeholder="Search .."/>
    </div>
    <div class="uk-width-small">
        <button class="uk-button uk-button-default uk-border-rounded search-button extended-button" type="submit"><spring:message code="navbar.search"/></button>
    </div>
</div>
<p class="uk-text-meta">
    <spring:message code="search.searchResults"/>
</p>
<div class="uk-margin-medium-top">
    <ul class="uk-child-width-expand uk-tab">
        <li class="${currentSearch == 0 ? 'uk-active' : ''}">
            <c:url value="/search/posts" var="postsURL">
                <c:param name="query" value="${query}"/>
            </c:url>
            <a href="${currentSearch == 0 ? '' : postsURL}"><spring:message code="search.posts"/></a>
        </li>
        <li class="${currentSearch == 1 ? 'uk-active' : ''}">
            <c:url value="/search/movies" var="moviesURL">
                <c:param name="query" value="${query}"/>
            </c:url>
            <a href="${currentSearch == 1 ? '' : moviesURL}"><spring:message code="search.movies"/></a>
        </li>
        <li class="${currentSearch == 2 ? 'uk-active' : ''}">
            <c:url value="/search/users" var="usersURL">
                <c:param name="query" value="${query}"/>
            </c:url>
            <a href="${currentSearch == 2 ? '' : usersURL}"><spring:message code="search.users"/></a>
        </li>
    </ul>
</div>
