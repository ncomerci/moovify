<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title><spring:message code="user.create.signUpTitle"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />

</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<div class="uk-container uk-container-xsmall uk-margin-medium-top">
    <c:url value="/user/create" var="action"/>
    <%--@elvariable id="userCreateForm" type=""--%>
    <legend class="uk-legend uk-text-uppercase uk-text-center uk-text-bold uk-text-large uk-text-primary"><spring:message code="user.create.signUpTitle"/></legend>
    <p class="uk-text-center uk-text-normal uk-text-italic uk-text-bold"><spring:message code="user.create.signUpDesc"/></p>
    <form:form modelAttribute="userCreateForm" action="${action}" method="post" enctype="multipart/form-data">

        <div class="uk-margin">
            <c:set var="nameError"><form:errors path="name"/></c:set>
            <div class="uk-inline">
                <form:label path="name">
                    <spring:message code="user.create.Name" var="name"/>
                    <c:choose>
                        <c:when test="${not empty nameError}">
                            <span class="uk-form-icon icon-error" uk-icon="icon: info"></span>
                            <form:input class="uk-input uk-form-danger" path="name" placeholder="${name}" />
                        </c:when>
                        <c:otherwise>
                            <span class="uk-form-icon" uk-icon="icon: info"></span>
                            <form:input class="uk-input" path="name" placeholder="${name}" />
                        </c:otherwise>
                    </c:choose>
                </form:label>
            </div>
            <form:errors path="name" element="p" cssClass="error" cssStyle="color:red;"/>
        </div>

        <div class="uk-margin">
            <c:set var="mailError"><form:errors path="email"/></c:set>
            <div class="uk-inline">
                <form:label path="email">
                    <spring:message code="user.create.Email" var="email"/>
                    <c:choose>
                        <c:when test="${not empty mailError}">
                            <span class="uk-form-icon icon-error" uk-icon="icon: mail"></span>
                            <form:input class="uk-input uk-form-danger" path="email" type="email" placeholder="${email}" />
                        </c:when>
                        <c:otherwise>
                            <span class="uk-form-icon" uk-icon="icon: mail"></span>
                            <form:input class="uk-input" path="email" type="email" placeholder="${email}" />
                        </c:otherwise>
                    </c:choose>
                </form:label>
            </div>
            <form:errors path="email" element="p" cssClass="error" cssStyle="color:red;"/>
        </div>

        <div class="uk-margin">
            <c:set var="userError"><form:errors path="username"/></c:set>
            <div class="uk-inline">
                <form:label path="username">
                    <spring:message code="user.create.Username" var="username"/>
                    <c:choose>
                    <c:when test="${not empty userError}">
                        <span class="uk-form-icon icon-error" uk-icon="icon: user"></span>
                        <form:input class="uk-input uk-form-danger" path="username" placeholder="${username}" />
                    </c:when>
                    <c:otherwise>
                    <span class="uk-form-icon" uk-icon="icon: user"></span>
                    <form:input class="uk-input" path="username" placeholder="${username}" />
                    </c:otherwise>
                    </c:choose>
                </form:label>
            </div>
            <form:errors path="username" element="p" cssClass="error" cssStyle="color:red;" />
        </div>

        <div class="uk-margin">
            <c:set var="passError"><form:errors path="password"/></c:set>
            <div class="uk-inline">
                <form:label path="password">
                    <spring:message code="user.create.Password" var="password"/>
                    <c:choose>
                    <c:when test="${not empty passError}">
                        <span class="uk-form-icon icon-error" uk-icon="icon: lock"></span>
                        <form:password class="uk-input uk-form-danger"  path="password"  placeholder="${password}" />
                    </c:when>
                    <c:otherwise>
                    <span class="uk-form-icon" uk-icon="icon: lock"></span>
                    <form:password class="uk-input"  path="password"  placeholder="${password}" />
                    </c:otherwise>
                    </c:choose>
                </form:label>
            </div>
            <form:errors path="password" element="p" cssClass="error" cssStyle="color:red;" />
            <form:errors element="p" cssClass="error" cssStyle="color:red;"/>
        </div>

        <div class="uk-margin">
            <c:set var="repPassError"><form:errors path="repeatPassword"/></c:set>
            <div class="uk-inline">
                <form:label path="repeatPassword">
                    <spring:message code="user.create.repeatPassword" var="repeatPassword"/>
                    <c:choose>
                    <c:when test="${not empty repPassError}">
                        <span class="uk-form-icon icon-error" uk-icon="icon: lock"></span>
                        <form:password class="uk-input uk-form-danger"  path="repeatPassword" placeholder="${repeatPassword}" />
                    </c:when>
                    <c:otherwise>
                    <span class="uk-form-icon" uk-icon="icon: lock"></span>
                    <form:password class="uk-input"  path="repeatPassword" placeholder="${repeatPassword}" />
                    </c:otherwise>
                    </c:choose>
                </form:label>
            </div>
            <form:errors path="repeatPassword" element="p" cssClass="error" cssStyle="color:red;" />
        </div>

        <form:label path="avatar">
            <form:input path="avatar" type="file" />
            <form:errors path="avatar" element="p" cssClass="error" cssStyle="color:red;" />
        </form:label>

        <div class="uk-text-center uk-margin-medium-top">
            <input class="uk-button uk-button-primary uk-border-rounded signup-login-button" type="submit" value="<spring:message code="user.create.button"/>" />
        </div>
        <div class="uk-text-center uk-text-bold uk-text-muted uk-margin"><spring:message code="user.create.alreadyAccount"/> <a href="<c:url value="/login"/>"><spring:message code="user.login.loginTitle"/></a></div>
    </form:form>
</div>
</body>
</html>
