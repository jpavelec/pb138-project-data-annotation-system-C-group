<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true" session="false" %>

<!--
@author Kristýna Pavlíčková <422537@mail.muni.cz>
-->

<!DOCTYPE html>
<html lang="${pageContext.request.locale}">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
    <link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/style.css" />"/>
    <title>Annotation system</title>
</head>
<body>
<div class="container">
    <div class="row">
        <div class="col-md-offset-2 col-md-8">
            </br>
            <h2 align="center">Choose your package</h2>
            </br>
            <div class="list-group">
                <c:forEach items="${allPacks}" var="Pack">

                    <a href="<spring:url value="/stats/${Pack.id}"/>" class="list-group-item text-center">
                        <b>${Pack.name}</b>
                    </a>

                </c:forEach>
            </div>

            <form action="<spring:url value="/"/>">
                </br>
                <input type="submit" class="btn btn-lg btn-primary btn-block"   value="Go back">
            </form>
        </div>
    </div>
</div>
</body>
</html>
