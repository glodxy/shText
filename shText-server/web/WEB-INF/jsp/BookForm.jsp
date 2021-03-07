<%--
  Created by IntelliJ IDEA.
  User: 田淙宇
  Date: 2019/6/9
  Time: 17:24
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>add book</title>
</head>
<body>

<div id="global">
<form action="/book/add" method="post">
    <fieldset>
        <legend>add a book</legend>
        <p>
            标题:
            <input type="text" id="title" name="title" tabindex="1">
        </p>
        <p>
            章节数:
            <input type="number" name="count" tabindex="2">
        </p>
        <p>
            字数:
            <input type="number" name="charNum" tabindex="3">
        </p>
        <p id="buttons">
            <input id="submit" type="submit" tabindex="4" value="Add Product">
        </p>
    </fieldset>
</form>
</div>

</body>
</html>
