
<%--
  Created by IntelliJ IDEA.
  User: tobias
  Date: 30/8/20
  Time: 23:54
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<html>
<head>
    <title>Create Post</title>
    <%-- TODO: Refactor EasyMD and DOMPurify dependencies  --%>
    <link rel="stylesheet" href="https://unpkg.com/easymde/dist/easymde.min.css">
    <script src="https://unpkg.com/easymde/dist/easymde.min.js"></script>
    <script src="https://cdnjs.cloudflare.com/ajax/libs/dompurify/2.0.14/purify.js" integrity="sha512-k1Cz/QJmmxQHFJhI9dAjibYnBjZbkQVrCiGFTGs6UDek/zjbd6DZrnrPQAnZREr6hx0wyQRDOfWpzA/t5xQKjw==" crossorigin="anonymous"></script>

    <script src="<c:url value="/resources/js/post/create.js" />"></script>
</head>
<body>
    <form method="post" action="<c:url value="/post/create" />">
        <label>
            Username:
            <input type="text" required />
        </label>

        <label>
            Email:
            <input type="email" />
        </label>


        <label for="createPostData">
            <textarea id="createPostData" name="postData" required ></textarea>
        </label>

        <input type="submit" value="Enviar">

    </form>
</body>
</html>
