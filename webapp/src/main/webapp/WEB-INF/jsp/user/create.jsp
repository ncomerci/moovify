<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" %>
<html>
<head>
    <title>Create User</title>
    <jsp:include page="/WEB-INF/jsp/dependencies/global.jsp" />

</head>
<body>
    <jsp:include page="/WEB-INF/jsp/components/navBar.jsp" />

    <form action="<c:url value="/user/create" />" method="post">
        <div>
            <label>
                Username:
                <input name="username" type="text" placeholder="Username" />
            </label>
        </div>

        <div>
            <label>
                Password:
                <input name="password" type="password" placeholder="Password" />
            </label>
        </div>

        <div>
            <label>
                Name:
                <input name="name" type="text" placeholder="Name" />
            </label>
        </div>

        <div>
            <label>
                E-Mail:
                <input name="email" type="email" placeholder="E-Mail" />
            </label>
        </div>

        <div>
            <input type="submit" value="Create!" />
        </div>
    </form>

</body>
</html>
