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
    <div>
    <form action="${action}" method="post" >
        <div>
            <label >
                <spring:message code="user.login.enterUsername"/>
                <input name="username"  type="text" placeholder="<spring:message code="user.create.Username"/>"/>
            </label>
        </div>

        <div>
            <label>
                <spring:message code="user.login.enterPassword"/>
                <input name="password" type="password" placeholder="<spring:message code="user.create.Password"/>" />
            </label>
            <c:if test="${param.error != null}">
                <div id="error">
                    <p class="uk-text-danger"><spring:message code="user.login.badCredentials"/></p>
                </div>
            </c:if>
        </div>

        <div>
            <label>
                <spring:message code="user.login.rememberMe"/>
                <input name="remember-me" type="checkbox" />
            </label>
        </div>

        <div>
            <input type="submit" value="Login!" />
        </div>
    </form>
    </div>

    </body>
    </html>
