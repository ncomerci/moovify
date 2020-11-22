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
<sec:authorize access="isAuthenticated()">
    <div class="uk-height-medium uk-background-cover uk-light uk-flex uk-flex-top uk-background-fixed" uk-parallax="bgy: -50" style="background-image: url(<c:url value="/resources/images/banner.jpg"/>);">
        <div class="uk-width-1-2@m uk-text-center uk-margin-auto uk-margin-auto-vertical">
            <h1 class="m-text-white uk-text-bold"><spring:message code="index.slogan"/></h1>
            <h1 class="m-text-white uk-text-bold"><spring:message code="index.welcome"/></h1>
        </div>
    </div>
</sec:authorize>
<sec:authorize access="!isAuthenticated()">
<div class="uk-height-large uk-background-cover uk-overflow-hidden uk-flex uk-flex-top uk-background-fixed" uk-parallax="bgy: -50" style="background-image: url(<c:url value="/resources/images/banner.jpg"/>);">
    <div class="uk-width-1-2@m uk-text-center uk-margin-auto uk-margin-auto-vertical">
        <h1 class="m-text-white uk-text-bold uk-animation-scale-up"><spring:message code="index.slogan"/></h1>
        <h1 class="m-text-white uk-text-bold" uk-parallax="opacity: 0,8; y: 100,0; scale: 1,1; viewport: 0.3;"><spring:message code="index.welcome"/></h1>
    </div>
</div>
</sec:authorize>

</body>
</html>