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
        </br>
        <h2>Select the subpack you wish to start marking:</h2>
        <div class="list-group">
            <c:forEach items = "${subpacks}" var="subpack">
                <a href="/packages/${subpack.key.id}" class="list-group-item">
                    ID: <b>${subpack.key.id}</b>  ;  NAME: <b>${subpack.key.name}</b>  ;  ${subpack.value} % finished
                </a>
            </c:forEach>
        </div>
        </br>
        <form action="<spring:url value="/"/>">
            <input type="submit" class="btn btn-lg btn-primary btn-block" value="Go back to main page">
        </form>
        </br>
    </div>
</body>
</html>
