<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
    <h2>Assign ${user.username} to package ${pack.name}</h2>
    <div id="middle">
        <form id="assi" method="POST" enctype="multipart/form-data">

            <ul class="list-group">
                <c:forEach var="subpack" items="${subpackMap}">
                    <li class="list-group-item">
                        <div class="checkbox">
                            <label>
                                <input id="${subpack.key.id}" name="value" type="checkbox" value="${subpack.key.id}">
                                <c:out value="${subpack.key.name}"/>
                                <c:if test="${subpack.value == 0}">
                                    &nbsp;&nbsp;<span class="label label-warning">Not assigned!</span>
                                </c:if>
                                <c:if test="${subpack.value == 1}">
                                    &nbsp;&nbsp;<span class="label label-default">Assigned to one user.</span>
                                </c:if>
                                <c:if test="${subpack.value > 1}">
                                    &nbsp;&nbsp;<span class="label label-default">Assigned to ${subpack.value} users.</span>
                                </c:if>
                            </label>
                        </div>
                    </li>
                </c:forEach>
            </ul>
            <input type="submit" class="btn btn-lg btn-primary btn-block" value="Assign packages">
        </form>
        <c:if test="${allAssigned == true}">
            <center>
                <h3>All packages are assigned</h3>
            </center>
            <form action="<spring:url value="/"/>">
                </br>
                <input type="submit" class="btn btn-lg btn-primary btn-block"   value="Go back to main menu">
            </form>
        </c:if>
    </div>

</div>
</body>
</html>
