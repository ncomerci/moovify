<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="sec" uri="http://www.springframework.org/security/tags" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" %>

<html>
<head>
    <title><spring:message code="user.profile.editingProfile" arguments="${loggedUser.username}"/></title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />
</head>
<body style="min-height: 1000px">

<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<div class="uk-container uk-margin-medium-top">
    <c:url value="/user/edit/name" var="actionName"/>
    <c:url value="/user/edit/username" var="actionUsername"/>
    <c:url value="/user/edit/description" var="actionDescription"/>
    <legend class="uk-legend uk-text-uppercase uk-text-center uk-text-bold uk-text-large uk-text-primary"><spring:message code="user.profile.EditProfile"/></legend>
    <p class="uk-text-center uk-text-normal uk-text-italic uk-text-bold"><spring:message code="user.profile.editProfileDesc"/></p>
    <div class="uk-container uk-text-center">
        <%--@elvariable id="nameEditForm" type=""--%>
        <form:form modelAttribute="nameEditForm" class="uk-form-horizontal uk-margin-large" action="${actionName}" method="post">
            <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center" uk-grid>
                <div class="uk-width-1-4">
                    <h3 class="uk-margin-bottom uk-text-left"><spring:message code="user.profile.edit.currentName" arguments="${loggedUser.name}"/></h3>
                </div>
                <div class="uk-width-1-2">
                    <c:set var="nameError"><form:errors path="name"/></c:set>
                    <div class="uk-inline">
                        <form:label path="name">
                            <spring:message code="user.profile.edit.newName" var="newName"/>
                            <c:choose>
                                <c:when test="${not empty nameError}">
                                    <span class="uk-form-icon icon-error" uk-icon="icon: info"></span>
                                    <form:input class="uk-input uk-form-danger" path="name" placeholder="${newName}" />
                                </c:when>
                                <c:otherwise>
                                    <span class="uk-form-icon" uk-icon="icon: info"></span>
                                    <form:input class="uk-input" path="name" placeholder="${newName}" />
                                </c:otherwise>
                            </c:choose>
                        </form:label>
                    </div>
                    <form:errors path="name" element="p" cssClass="error" cssStyle="color:red;"/>
                </div>
                <div class="uk-width-1-6">
                    <div class="uk-text-center">
                        <input class="uk-button uk-button-primary uk-border-rounded signup-login-button" type="submit" value="<spring:message code="user.profile.edit.name"/>" />
                    </div>
                </div>
            </div>
        </form:form>
        <%--@elvariable id="usernameEditForm" type=""--%>
        <form:form modelAttribute="usernameEditForm" class="uk-form-horizontal uk-margin-large" action="${actionUsername}" method="post">
            <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center" uk-grid>
                <div class="uk-width-1-4">
                    <h3 class="uk-margin-bottom uk-text-left"><spring:message code="user.profile.edit.currentUsername" arguments="${loggedUser.username}"/></h3>
                </div>
                <div class="uk-width-1-2">
                    <c:set var="userError"><form:errors path="username"/></c:set>
                    <div class="uk-inline">
                        <form:label path="username">
                            <spring:message code="user.profile.edit.newUsername" var="newUsername"/>
                            <c:choose>
                                <c:when test="${not empty userError}">
                                    <span class="uk-form-icon icon-error" uk-icon="icon: user"></span>
                                    <form:input class="uk-input uk-form-danger" path="username" placeholder="${newUsername}" />
                                </c:when>
                                <c:otherwise>
                                    <span class="uk-form-icon" uk-icon="icon: user"></span>
                                    <form:input class="uk-input" path="username" placeholder="${newUsername}"/>
                                </c:otherwise>
                            </c:choose>
                        </form:label>
                    </div>
                    <form:errors path="username" element="p" cssClass="error" cssStyle="color:red;" />
                </div>
                <div class="uk-width-1-6">
                    <div class="uk-text-center">
                        <input class="uk-button uk-button-primary uk-border-rounded signup-login-button" type="submit" value="<spring:message code="user.profile.edit.username"/>" />
                    </div>
                </div>
            </div>
        </form:form>
        <%--@elvariable id="descriptionEditForm" type=""--%>
        <form:form modelAttribute="descriptionEditForm" class="uk-form-horizontal uk-margin-large" action="${actionDescription}" method="post">
            <div class="uk-grid-small uk-flex uk-flex-wrap uk-flex-row uk-flex-center" uk-grid>
                <div class="uk-width-1-4">
                    <h3 class="uk-margin-bottom uk-text-left"><spring:message code="user.profile.edit.newDescription" /></h3>
                </div>
                <div class="uk-width-1-2">
                    <c:set var="descriptionError"><form:errors path="description"/></c:set>
                    <div class="uk-inline">
                        <form:label path="description">
                            <spring:message code="user.create.newDescription" var="description"/>
                            <c:choose>
                                <c:when test="${not empty descriptionError}">
                                    <span class="uk-form-icon icon-error" uk-icon="icon: file-text"></span>
                                    <form:textarea class="uk-input uk-form-danger" path="description" type="text" placeholder="${description}" />
                                </c:when>
                                <c:otherwise>
                                    <span class="uk-form-icon" uk-icon="icon: file-text"></span>
                                    <form:textarea class="uk-input" path="description" type="text" placeholder="${description}" />
                                </c:otherwise>
                            </c:choose>
                        </form:label>
                    </div>
                    <form:errors path="description" element="p" cssClass="error" cssStyle="color:red;"/>
                </div>
                <div class="uk-width-1-6">
                    <div class="uk-text-center">
                        <input class="uk-button uk-button-primary uk-border-rounded signup-login-button" type="submit" value="<spring:message code="user.profile.edit.description"/>" />
                    </div>
                </div>
            </div>
        </form:form>

        <div class="uk-margin-large">
            <div class="uk-grid-small uk-flex uk-flex-wrap  uk-flex-row uk-flex-center" uk-grid>
                <div class="uk-width-1-4">
                    <h3 class="uk-margin-bottom uk-text-left"><spring:message code="user.profile.edit.wantChangePassword"/></h3>
                </div>
                <div class="uk-width-1-2">
                    <a class="uk-text-center" href="<c:url value="/user/changePassword"/>">
                        <button class="uk-button uk-button-primary uk-border-rounded user-profile-button" type="button"><spring:message code="user.profile.edit.changePassword"/></button>
                    </a>
                </div>
            </div>
        </div>
    </div>
</div>

</body>
</html>