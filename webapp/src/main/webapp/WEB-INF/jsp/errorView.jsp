<%@ page contentType="text/html;charset=UTF-8"%>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="message" scope="request" type="java.lang.String"/>
<jsp:useBean id="code" scope="request" type="java.lang.String"/>

<html>
<head>
    <title><spring:message code="error.moovifyErrorTitle" arguments="${code}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<%-- La saco porque explota cuando hay errores. De ultima hay que hacer una especial que no pueda fallar, que sea estatica --%>
<%--<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />--%>

<main class="uk-container uk-container-large">
    <div class="uk-margin-auto">
        <h1 class="uk-margin-auto uk-text-center uk-text-bold uk-margin-medium-top"><spring:message code="error.errorWithCode" arguments="${code}"/></h1>
        <h1 class="uk-margin-auto uk-text-center uk-text-large uk-margin-remove-top mid-bold">
            <c:out value="${message}"/>
    </div>
    <br>
    <div class="uk-margin-auto">
        <%-- TODO: Cambiar por boton que vaya al home. Hay veces donde goBack() no tiene sentido --%>
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
