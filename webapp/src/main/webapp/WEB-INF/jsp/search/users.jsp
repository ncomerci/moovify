<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="search.users.pageTitle"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp"/>
    <link rel="stylesheet" href="<c:url value="/resources/css/extraStyle.css"/>"/>
    <script src="<c:url value="/resources/js/search/user.js"/>"></script>
    <script src="<c:url value="/resources/js/components/paginationController.js"/>"></script>
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<main class="uk-container-small uk-margin-auto uk-padding-small">
    <c:url value="/search/users" var="action"/>
    <form:form modelAttribute="searchUsersForm" method="get" action="${action}">
        <section id="search-controllers">
            <c:set var="query" value="${query}" scope="request" />
            <c:set var="currentSearch" value="2" scope="request" />
            <jsp:include page="/WEB-INF/jsp/search/defaultForm.jsp"/>

            <div class="uk-form-horizontal uk-grid-small" uk-grid>
                <div class="uk-width-1-2">
                    <div class="uk-margin">
                        <form:label path="role" class="uk-form-label" for="role" style="width: auto">
                            <spring:message code="search.user.role.label"/>
                        </form:label>
                        <div class="uk-form-controls" style="margin-left: 100px">
                            <form:select path="role" class="uk-select uk-form-blank">
                                <form:option value="any"><spring:message code="search.user.roles.all"/></form:option>
                                <c:forEach items="${roleOptions}" var="role" >
                                    <form:option value="${role}"><spring:message code="search.user.roles.${role}"/></form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                </div>

                <div class="uk-width-1-2">
                    <div class="uk-margin">
                        <form:label path="sortCriteria" class="uk-form-label" for="sort-criteria" style="width: auto">
                            <spring:message code="search.user.sortCriteria.label"/>
                        </form:label>
                        <div class="uk-form-controls" style="margin-left: 100px">
                            <form:select path="sortCriteria" class="uk-select uk-form-blank">
                                <c:forEach items="${sortCriteria}" var="criteria" >
                                    <form:option value="${criteria}"><spring:message code="search.user.sortCriteria.${criteria}"/></form:option>
                                </c:forEach>
                            </form:select>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <section id="search-results" class="uk-flex uk-flex-wrap">
            <c:if test="${empty users.results}">
                <h1 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="search.notFound" arguments="users"/> </h1>
            </c:if>
            <c:forEach items="${users.results}" var="user">
                <div class="uk-width-1-1">
                    <div class="uk-flex">
                        <div class="uk-width-expand uk-margin-small-top">
                            <a href="<c:url value="/user/${user.id}"/>" <c:out value="${user.admin ? 'class=uk-text-primary uk-text-middle': ''}"/>>
                                <c:out value="${user.username}"/>
                                <c:if test="${user.admin}">
                                    <span class="iconify admin-badge" data-icon="entypo:shield" data-inline="false"></span>
                                </c:if>
                            </a>
                        </div>
                    </div>
                </div>
            </c:forEach>
        </section>

        <c:if test="${not empty users.results}">
            <c:set var="collection" value="${users}" scope="request"/>
            <c:url var="baseURL" value="/search/users" scope="request">
                <c:param name="query" value="${searchUsersForm.query}"/>
                <c:param name="role" value="${searchUsersForm.role}"/>
                <c:param name="sortCriteria" value="${searchUsersForm.sortCriteria}"/>
            </c:url>
            <c:set var="numberOfInputs" value="${2}" scope="request"/>
            <jsp:include page="/WEB-INF/jsp/components/paginationController.jsp" />
        </c:if>
    </form:form>
</main>
</body>
</html>