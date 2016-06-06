<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true" session="false" %>


<!DOCTYPE html>
<html lang="${pageContext.request.locale}">
<head>
    <meta charset="utf-8">
    <meta http-equiv="X-UA-Compatible" content="IE=edge">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
    <title>Annotation system</title>
</head>
<body>
    <div class="container">
        </br>
        <center><h2>Choose your package</h2></center>
        </br>
            <c:forEach items="${allPacks}" var="Pack">
                <form action="<spring:url value="/stats/${Pack.id}"/>">
                    <input type="submit" class="btn btn-lg btn-primary btn-block"   value="${Pack.name}">
                    </br>
                </form>

            </c:forEach>



        <form action="<spring:url value="/"/>">
            </br>
            <input type="submit" class="btn btn-lg btn-primary btn-block"   value="Go back">
        </form>
    </div>
</body>
</html>
