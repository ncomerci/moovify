<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://www.springframework.org/tags"%>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form"%>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Create User</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />

</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<div>
    <c:url value="/user/create" var="action"/>
    <%--@elvariable id="userCreateForm" type=""--%>
    <form:form modelAttribute="userCreateForm" action="${action}" method="post">
        <div>
           <%-- <spring:hasBindErrors name="userCreateForm">
                <c:forEach var="error" items="${errors.globalErrors}">
                    <b><spring:message message="${error}" /></b>
                    <br/>
                </c:forEach>
            </spring:hasBindErrors>--%>
        </div>
        <div>
            <form:label path="username">
                <spring:message code="user.create.Username"/>
                <spring:message code="user.create.Username" var="username"/>
                <form:input path="username" placeholder="${username}" />
            </form:label>
            <form:errors path="username" element="p" cssClass="error" cssStyle="color:red;" />
        </div>

        <div>
            <form:label path="password">
                <spring:message code="user.create.Password"/>
                <spring:message code="user.create.Password" var="password"/>
                <form:password path="password"  placeholder="${password}" />
            </form:label>
            <form:errors path="password" element="p" cssClass="error" cssStyle="color:red;" />
            <form:errors element="p" cssClass="error" cssStyle="color:red;"/>
        </div>

        <div>
            <form:label path="repeatPassword">
                <spring:message code="user.create.repeatPassword"/>
                <spring:message code="user.create.repeatPassword" var="repeatPassword"/>
                <form:password path="repeatPassword" placeholder="${repeatPassword}" />
            </form:label>
            <form:errors path="repeatPassword" element="p" cssClass="error" cssStyle="color:red;" />
        </div>

        <div>
            <form:label path="name">
                <spring:message code="user.create.Name"/>
                <spring:message code="user.create.Name" var="name"/>
                <form:input path="name" placeholder="${name}" />
            </form:label>
            <form:errors path="name" element="p" cssClass="error" cssStyle="color:red;"/>
        </div>

        <div>
            <form:label path="email">
                <spring:message code="user.create.Email"/>
                <spring:message code="user.create.Email" var="email"/>
                <form:input path="email" type="email" placeholder="${email}" />
            </form:label>
            <form:errors path="email" element="p" cssClass="error" cssStyle="color:red;"/>
        </div>

        <div>
            <input type="submit" value="Create!" />
        </div>
    </form:form>
</div>

</body>
</html>
