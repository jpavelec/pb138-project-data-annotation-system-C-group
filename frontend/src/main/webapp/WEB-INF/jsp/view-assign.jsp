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
    <div class="row">
        <div class="col-md-offset-2 col-md-8">
            <h2>Assign users to package ${pack.name}</h2>
            <form id="fileAssignForm" method="POST" enctype="multipart/form-data">
            <div id="middle">
                <div class="list-group">
                    <c:forEach items="${users}" var="person">
                        <a href="<spring:url value="/assign/${pack.id}/${person.id}" />" class="list-group-item" type="submit">
                            <b>${person.username}</b>
                        </a>
                    </c:forEach>
                </div>
            </div>
            </form>
            <c:if test="${allAssigned == true}">
                <h3>All packages are assigned</h3>
            </c:if>
            <form action="<spring:url value="/"/>">
                </br>
                <input type="submit" class="btn btn-lg btn-primary btn-block"   value="Go back to main menu">
            </form>
        </div>
    </div>
</div>
</body>
</html>
