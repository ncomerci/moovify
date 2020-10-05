<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<jsp:useBean id="currentSearch" scope="request" type="java.lang.String"/>
<jsp:useBean id="query" scope="request" type="java.lang.String"/>

<div class="uk-search uk-search-large">
    <span uk-search-icon></span>
    <form:input path="query" class="uk-search-input" type="search"/>
</div>
<p class="uk-text-meta">
    <spring:message code="search.searchResults"/>
</p>
<div class="uk-margin-medium-top">
    <ul class="uk-child-width-expand uk-tab">
        <li class="${currentSearch == 0 ? 'uk-active' : ''}">
            <c:choose>
                <c:when test="${currentSearch == 0}">
                    <a href="#"><spring:message code="search.posts"/></a>
                </c:when>
                <c:otherwise>
                    <a href="<c:url value="${'/search/posts/?query='}${query}"/>"><spring:message code="search.posts"/></a>
                </c:otherwise>
            </c:choose>
        </li>
        <li class="${currentSearch == 1 ? 'uk-active' : ''}">
            <c:choose>
                <c:when test="${currentSearch == 1}">
                    <a href="#"><spring:message code="search.movies"/></a>
                </c:when>
                <c:otherwise>
                    <a href="<c:url value="${'/search/movies/?query='}${query}"/>"><spring:message code="search.movies"/></a>
                </c:otherwise>
            </c:choose>
        </li>
        <li class="${currentSearch == 2 ? 'uk-active' : ''}">
            <c:choose>
                <c:when test="${currentSearch == 2}">
                    <a href="#"><spring:message code="search.users"/></a>
                </c:when>
                <c:otherwise>
                    <a href="<c:url value="${'/search/users/?query='}${query}"/>"><spring:message code="search.users"/></a>
                </c:otherwise>
            </c:choose>
        </li>
    </ul>
</div>
