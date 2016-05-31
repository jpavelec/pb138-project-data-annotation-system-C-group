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
    <h2>Assign ${user} to package ${pack}</h2>
    <div id="middle">
        <ul class="list-group">
            <c:forEach var="subpack" items="${subPackList}">
                <li class="list-group-item">
                    <div class="checkbox">
                        <label>
                            <input type="checkbox" value=""><c:out value="${subpack.name}"/>
                        </label>
                    </div>
                </li>
            </c:forEach>
        </ul>
        <form action="<spring:url value="/assign/${pack}"/>">
            <input type="submit" class="btn btn-lg btn-primary btn-block" value="Assign packages">
        </form>
    </div>

</div>
</body>
</html>
