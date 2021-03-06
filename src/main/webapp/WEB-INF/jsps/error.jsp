<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
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
    <title>Find - Error</title>
    <meta http-equiv="content-type" content="text/html; charset=UTF-8"/>
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <base href="${baseUrl}">
    <link rel="icon" type="image/ico" href="static-${applicationVersion}/favicon.ico">
    <!--[if IE 7]>
    <link rel="stylesheet" type="text/css" href="static-${applicationVersion}/bower_components/fontawesome/css/font-awesome-ie7.css"/>
    <![endif]-->
    <link rel="stylesheet" type="text/css" href="static-${applicationVersion}/css/app.css"/>
</head>
<body>
<div class="error-body">
    <h1>
        <c:choose>
            <c:when test="${statusCode == null}">${pageContext.response.status}</c:when>
            <c:otherwise>${statusCode}</c:otherwise>
        </c:choose>
    </h1>
    <h3>${mainMessage}</h3>
    <div>
        <p>${subMessage}</p>
        <c:if test="${contactSupport}"><p><spring:message code="error.contactSupport"/></p></c:if>
    </div>
</div>
</body>
</html>
