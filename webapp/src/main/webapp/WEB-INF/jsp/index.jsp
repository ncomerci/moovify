<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="index.pagename"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<div class="uk-height-medium uk-background-cover uk-light uk-flex uk-flex-top" uk-parallax="bgy: -50" style="background-image: url(<c:url value="/resources/images/banner.jpg"/>);">
    <h1 class="uk-width-1-2@m uk-text-center uk-margin-auto uk-margin-auto-vertical uk-text-primary uk-text-bold"><spring:message code="index.welcome"/></h1>
</div>

<main class="uk-container uk-container-large main-page">
    <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center uk-margin-bottom uk-margin-top" uk-grid>
        <sec:authorize access="isAuthenticated()">
            <div class="uk-width-1-3">
                <section id="followed-users-posts">
                    <h2><spring:message code="index.myFeed"/></h2>
                    <c:if test="${empty followedUsersPosts.results}">
                        <h2 class="uk-text-meta uk-text-center uk-text-bold"><spring:message code="index.myFeedEmpty"/> </h2>
                    </c:if>
                    <c:set value="${followedUsersPosts}" var="posts" scope="request"/>
                    <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>
                </section>
            </div>
            <div class="uk-width-1-3">
                <section id="newest-posts">
                    <h2><spring:message code="index.newestPost"/></h2>
                    <c:set value="${newestPosts}" var="posts" scope="request"/>
                    <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>
                </section>
            </div>
            <div class="uk-width-1-3">
                <section id="hottest-posts">
                    <h2><spring:message code="index.hottestPost"/></h2>
                    <c:set value="${hottestPosts}" var="posts" scope="request"/>
                    <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>
                </section>
            </div>
        </sec:authorize>
        <sec:authorize access="!isAuthenticated()">
            <div class="uk-width-1-2">
                <section id="newest-posts">
                    <h2><spring:message code="index.newestPost"/></h2>
                    <c:set value="${newestPosts}" var="posts" scope="request"/>
                    <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>
                </section>
            </div>
            <div class="uk-width-1-2">
                <section id="hottest-posts">
                    <h2><spring:message code="index.hottestPost"/></h2>
                    <c:set value="${hottestPosts}" var="posts" scope="request"/>
                    <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>
                </section>
            </div>
        </sec:authorize>

    </div>
</main>
</body>

</html>