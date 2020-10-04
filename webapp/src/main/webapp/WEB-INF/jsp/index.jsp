<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="index.pagename"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
    <main class="uk-container uk-container-large">

        <div class="uk-column-1-2 uk-padding-large">

            <section id="newest-posts">
                <h1 class="uk-heading-small"><spring:message code="index.newestPost"/></h1>
                <c:set value="${newestPosts}" var="posts" scope="request"/>
                <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>
            </section>
            <section id="hottest-posts">
                <h1 class="uk-heading-small"><spring:message code="index.hottestPost"/></h1>
                <c:set value="${hottestPosts}" var="posts" scope="request"/>
                <jsp:include page="/WEB-INF/jsp/components/postsDisplay.jsp"/>
            </section>
        </div>
    </main>
</body>

</html>