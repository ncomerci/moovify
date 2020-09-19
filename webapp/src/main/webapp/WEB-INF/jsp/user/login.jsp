<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%--
  Created by IntelliJ IDEA.
  User: tobias
  Date: 17/9/20
  Time: 11:21
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="user.login"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />

</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
    <div class="uk-container uk-container-xsmall uk-margin-medium-top">
        <form action="${action}" method="post" >
            <fieldset class="uk-fieldset">
                <legend class="uk-legend uk-text-uppercase uk-text-center uk-text-bold uk-text-large uk-text-primary"><spring:message code="user.login.loginTitle"/></legend>
                <div class="uk-margin">
                    <label>
                        <input class="uk-input" name="username" type="text" placeholder="<spring:message code="user.create.Username"/>"/>
                    </label>
                </div>
                <div class="uk-margin">
                    <label>
                        <input class="uk-input" name="password" type="password" placeholder="<spring:message code="user.create.Password"/>" />
                    </label>
                    <c:if test="${param.error != null}">
                        <div id="error">
                            <p class="uk-text-danger"><spring:message code="user.login.badCredentials"/></p>
                        </div>
                    </c:if>
                </div>
                <div class="uk-margin uk-grid-small uk-child-width-auto uk-grid uk-align-center">
                    <label class="uk-text-center">
                        <input class="uk-checkbox" type="checkbox">
                        <span class="uk-text-primary"><spring:message code="user.login.rememberMe"/></span>
                    </label>
                </div>
                <div class="uk-text-center">
                    <input class="uk-button uk-button-primary uk-border-rounded uk-margin-bottom" type="submit" value="<spring:message code="user.login.loginTitle"/>" />
                </div>
            </fieldset>
        </form>
        </div>

</body>
</html>
