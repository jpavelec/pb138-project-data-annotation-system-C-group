<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true" session="false" %>


<!DOCTYPE html>
<html lang="${pageContext.request.locale}">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
          integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
    <link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/style.css" />"/>
    <title>Annotation system</title>
</head>
<body>
    <div class="container">
        <ul>
            <li>package 1 (60% finished)</li>
            <li>package 2 (100% finished)</li>
            <li>package 3 (100% finished)</li>
            <li>package 4 (100% finished)</li>
        </ul>
        <form action="<spring:url value="/mark"/>">
            <input type="submit" class="btn btn-lg btn-primary btn-block" value="Pick this package">
        </form>
        </br>
        <!--- show the following buttong only for administrator -->
        <form action="<spring:url value="/"/>">
            <input type="submit" class="btn btn-lg btn-primary btn-block" value="Go back">
        </form>
    </div>
</body>
</html>