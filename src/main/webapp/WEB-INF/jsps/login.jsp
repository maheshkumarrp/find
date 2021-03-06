<%@ taglib prefix="spring" uri="http://www.springframework.org/tags" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%--
  ~ Copyright 2014-2015 Hewlett-Packard Development Company, L.P.
  ~ Licensed under the MIT License (the "License"); you may not use this file except in compliance with the License.
  --%>

<spring:eval expression="@dispatcherProperties['application.version']" var="applicationVersion"/>

<!DOCTYPE html>
<html>
<head>
  <meta http-equiv="content-type" content="text/html;charset=UTF-8">

  <title>Find - Login</title>
  <link rel="icon" type="image/ico" href="static-${applicationVersion}/favicon.ico">
  <link rel="stylesheet" type="text/css" href="static-${applicationVersion}/css/bootstrap-custom.css">
  <link rel="stylesheet" href="static-${applicationVersion}/bower_components/fontawesome/css/font-awesome.css">
  <link rel="stylesheet" type="text/css" href="static-${applicationVersion}/bower_components/hp-autonomy-login-page/src/css/login-page.css">
  <script type="text/javascript" src="static-${applicationVersion}/bower_components/requirejs/require.js" data-main="static-${applicationVersion}/js/login.js"></script>
</head>
<body>
</body>
</html>
