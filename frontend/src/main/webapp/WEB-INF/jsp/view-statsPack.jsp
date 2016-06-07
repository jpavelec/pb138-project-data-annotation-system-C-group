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
    <h3>${pack.name}</h3>
    <div class="progress">
        <div class="progress-bar" role="progressbar" aria-valuenow="70"
             aria-valuemin="0" aria-valuemax="100" style="width:${progress}%">
            ${progress}%
        </div>
    </div>
    <c:forEach items="${subpackUserStats}" var="currentSubpackUserStats">
        <div class="panel panel-default">
            <div class="panel-heading">
                <h3 class="panel-title">${currentSubpackUserStats.key.name}</h3>
            </div>
            <c:forEach items="${subpackGeneralStats}" var="currentSubpackGeneralStats">
                <c:if test="${currentSubpackUserStats.key.id == currentSubpackGeneralStats.key.id}">

                    <div class="progress subpack-progress">
                        <div class="progress-bar" role="progressbar" aria-valuenow="70"
                             aria-valuemin="0" aria-valuemax="100" style="width:${currentSubpackGeneralStats.value[0]}%">
                                ${currentSubpackGeneralStats.value[0]}%
                        </div>
                    </div>

                    <div class="panel-body">

                        Avarage Completion Time:
                        <b>
                        <c:if test="${empty currentSubpackGeneralStats.value[1]}">
                            not enough data to compute
                        </c:if>
                        <c:if test="${not empty currentSubpackGeneralStats.value[1]}">
                            ${currentSubpackGeneralStats.value[5]} days
                            ${currentSubpackGeneralStats.value[4]} hours
                            ${currentSubpackGeneralStats.value[3]} minutes
                        </c:if>
                        </b>

                        &nbsp; &nbsp; &nbsp;

                        Avarage Evaluation Time:
                        <b>
                        <c:if test="${empty currentSubpackGeneralStats.value[6]}">
                            not enough data to compute
                        </c:if>
                        <c:if test="${not empty currentSubpackGeneralStats.value[6]}">
                            ${currentSubpackGeneralStats.value[6]} ms
                        </c:if>
                        </b>

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

                    </div>
                </c:if>
            </c:forEach>


        </div>
    </c:forEach>

    <form action="<spring:url value="/stats"/>">
        <input type="submit" class="btn btn-lg btn-primary btn-block"   value="Go back">
    </form>
</div>
</body>
</html>