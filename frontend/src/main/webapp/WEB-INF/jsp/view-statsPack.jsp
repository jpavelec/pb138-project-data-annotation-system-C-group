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
    <h3>${pack.name}</h3>
    <div class="progress">
        <div class="progress-bar" role="progressbar" aria-valuenow="70"
             aria-valuemin="0" aria-valuemax="100" style="width:${progress}%">
            ${progress}%
        </div>
    </div>
        <c:forEach items="${subpackUserStats}" var="currentSubpackUserStats">
            <h4>${currentSubpackUserStats.key.name}</h4>
            <c:forEach items="${subpackGeneralStats}" var="currentSubpackGeneralStats">
                <c:if test="${currentSubpackUserStats.key.id == currentSubpackGeneralStats.key.id}">
                    <div class="progress">
                        <div class="progress-bar" role="progressbar" aria-valuenow="70"
                             aria-valuemin="0" aria-valuemax="100" style="width:${currentSubpackGeneralStats.value[0]}%">
                                ${currentSubpackGeneralStats.value[0]}%
                        </div>
                    </div>
                    Progress: ${currentSubpackGeneralStats.value[0]}%;
                    Avarage Completion Time:
                    <c:if test="${currentSubpackGeneralStats.value[1] == 0}">
                        not enough data to compute;
                    </c:if>
                    <c:if test="${currentSubpackGeneralStats.value[1] != 0}">
                        ${currentSubpackGeneralStats.value[5]} days
                        ${currentSubpackGeneralStats.value[4]} hours
                        ${currentSubpackGeneralStats.value[3]} minutes;
                    </c:if>

                    Avarage Evaluation Time: ${currentSubpackGeneralStats.value[6]} ms

                </c:if>
            </c:forEach>

            <table class="table table-hover">
            <thead>
                <tr>
                    <th>User</th>
                    <th>Progress</th>
                    <th>Cohen kappa</th>
                    <th>Average evaluation time</th>
                </tr>
            </thead>
            <c:forEach items="${currentSubpackUserStats.value}" var="currentUserStats">
                <tbody>
                    <tr>
                        <td>${currentUserStats.key.username}</td>
                        <td>${currentUserStats.value[0]}%</td>
                        <td>${currentUserStats.value[1]} </td>
                        <td>${currentUserStats.value[2]} ms</td>
                    </tr>
                </tbody>
            </c:forEach>
            </table>
        </c:forEach>



    <form action="<spring:url value="/stats"/>">
        <input type="submit" class="btn btn-lg btn-primary btn-block"   value="Go back">
    </form>
</div>
</body>
</html>