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
    <h3>${Pack.name}</h3>
    <div class="progress">
        <div class="progress-bar" role="progressbar" aria-valuenow="70"
             aria-valuemin="0" aria-valuemax="100" style="width:${width}%">
            <span class="sr-only">${width}% Complete</span>
        </div>
    </div>
        <c:forEach items="${stats}" var="stats">
            <h4>${stats.key.name}</h4>
            <table class="table table-hover">
            <thead>
                <tr>
                    <th>user</th>
                    <th>progress</th>
                </tr>
            </thead>
            <c:forEach items="${stats.value}" var="statsSubPack">
                    <tbody>
                    <tr>
                        <td>${statsSubPack.key.username}</td>
                        <td>${statsSubPack.value}</td>
                    </tr>
                    </tbody>
            </c:forEach>
            </table>
        </c:forEach>



    <form action="<spring:url value="/"/>">
        <input type="submit" class="btn btn-lg btn-primary btn-block"   value="Go back">
    </form>
</div>
</body>
</html>