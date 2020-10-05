<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>

<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="user.login"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />
<div class="uk-container uk-container-xsmall uk-margin-medium-top">
    <form method="post">
        <fieldset class="uk-fieldset">
            <legend class="uk-legend uk-text-uppercase uk-text-center uk-text-bold uk-text-large uk-text-primary"><spring:message code="user.login.loginTitle"/></legend>
            <div class="uk-margin">
                <div class="uk-inline">
                    <label>
                        <c:choose>
                            <c:when test="${param.error != null}">
                                <span class="uk-form-icon icon-error"  uk-icon="icon: user"></span>
                                <input class="uk-input uk-form-danger" name="username" type="text" placeholder="<spring:message code="user.create.Username"/>"/>
                            </c:when>
                            <c:otherwise>
                                <span class="uk-form-icon" uk-icon="icon: user"></span>
                                <input class="uk-input" name="username" type="text" placeholder="<spring:message code="user.create.Username"/>"/>
                            </c:otherwise>
                        </c:choose>
                    </label>
                </div>
            </div>
            <div class="uk-margin">
                <div class="uk-inline">
                    <label>
                        <c:choose>
                            <c:when test="${param.error != null}">
                        <span class="uk-form-icon icon-error"  uk-icon="icon: lock"></span>
                        <input class="uk-input uk-form-danger" name="password" type="password" placeholder="<spring:message code="user.create.Password"/>" />
                            </c:when>
                            <c:otherwise>
                                <span class="uk-form-icon" uk-icon="icon: lock"></span>
                                <input class="uk-input" name="password" type="password" placeholder="<spring:message code="user.create.Password"/>" />
                            </c:otherwise>
                        </c:choose>
                    </label>
                </div>
                <c:if test="${param.error != null}">
                    <div id="error">
                        <p class="uk-text-danger uk-text-center uk-text-bold uk-margin-small-top">
                            <spring:message code="user.login.badCredentials"/>
                        </p>
                    </div>
                </c:if>
            </div>
            <div class="uk-margin uk-grid-small uk-child-width-auto uk-grid uk-align-center">
                <label class="uk-text-center">
                    <input name="remember-me" class="uk-checkbox" type="checkbox">
                    <span class="uk-text-primary"><spring:message code="user.login.rememberMe"/></span>
                </label>
            </div>
            <div class="uk-text-center">
                <input class="uk-button uk-button-primary uk-border-rounded signup-login-button" type="submit" value="<spring:message code="user.login.loginTitle"/>" />
            </div>
            <div class="uk-text-center uk-text-bold uk-text-muted uk-margin"><spring:message code="user.login.noAccount"/> <a href="<c:url value="/user/create"/>"><spring:message code="user.create.signUpTitle"/></a></div>
        </fieldset>
    </form>

    <div class="uk-text-center">
        <a href="<c:url value="/user/resetPassword" /> "><spring:message code="user.login.resetPassword"/></a>
    </div>
</div>

</body>
</html>
