<%--
  Created by IntelliJ IDEA.
  User: 田淙宇
  Date: 2019/6/9
  Time: 17:33
  To change this template use File | Settings | File Templates.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<html>
<head>
    <title>book details</title>
</head>
<body>
<div id="global">
    <h4>book added</h4>
    <p>
        <h5>details:</h5>
        标题：${book.title}<br/>
        章节数：${book.count}<br/>
        字数：${book.charNum}
    </p>
</div>
</body>
</html>
