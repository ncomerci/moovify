<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>

<html>
<head>
    <title><spring:message code="error.moovifyNotFound"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<main class="uk-container uk-container-large">
    <div class="uk-margin-auto">
        <h1 class="uk-margin-auto uk-text-center uk-text-bold uk-margin-medium-top"><spring:message code="error.error404"/></h1>
        <h1 class="uk-margin-auto uk-text-center uk-text-large uk-margin-remove-top mid-bold"><spring:message code="error.pageNotFound"/></h1>
    </div>
    <br>
    <div class="uk-margin-auto">
        <button class="uk-button uk-button-primary uk-border-rounded uk-margin-auto uk-align-center"
                type="button"
                onclick="goBack()"
        >
            <spring:message code="error.goBack"/>
        </button>
    </div>

</main>

</body>
</html>

<script>
    function goBack() {
        window.history.back();
    }
</script>
