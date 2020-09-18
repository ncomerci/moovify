<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <title>Login</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />

</head>
<body>
<jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

<form action="<c:url value="/login" />" method="post" >
    <div>
        <label>
            Enter Username:
            <input name="username" type="text" placeholder="Username" />
        </label>
    </div>

    <div>
        <label>
            Enter Password:
            <input name="password" type="password" placeholder="Password" />
        </label>
    </div>

    <div>
        <label>
            Remember Me
            <input name="remember-me" type="checkbox" />
        </label>
    </div>

    <div>
        <input type="submit" value="Login!" />
    </div>
</form>

</body>
</html>
