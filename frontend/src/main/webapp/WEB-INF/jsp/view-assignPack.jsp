<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="form" uri="http://www.springframework.org/tags/form" %>
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
            <h2>Assign ${user.username} to package ${pack.name}</h2>
            <form id="assi" method="POST">

                <div class="btn-group-vertical btn-block" data-toggle="buttons">
                    <c:forEach var="subpack" items="${subpackMap}">
                        <%-- check if contains current user and set boolean value into "contains" variable --%>
                        <c:set var="contains" value="false" />
                        <c:forEach var="subpackUserList" items="${subpackUserMap}">
                            <c:if test="${subpack.key eq subpackUserList.key}">
                                <c:forEach var="userIn" items="${subpackUserList.value}">
                                    <c:if test="${userIn eq user}">
                                        <c:set var="contains" value="true" />
                                    </c:if>
                                </c:forEach>
                            </c:if>
                        </c:forEach>

                        <label class="btn btn-default btn-lg assign-checkbox ${(contains) ? 'active' : ''}">

                            <input id="${subpack.key.id}" name="value" type="checkbox"
                                   value="${subpack.key.id}" autocomplete="off" ${(contains) ? 'checked' : ''}>

                            ${subpack.key.name}&nbsp;

                            <c:if test="${subpack.value == 0}">
                                &nbsp;<span class="label label-warning">not assigned</span>
                            </c:if>
                            <c:if test="${subpack.value > 0}">
                                <c:forEach var="subpackUserList" items="${subpackUserMap}">
                                    <c:if test="${subpack.key.id == subpackUserList.key.id}">
                                        <c:forEach var="user" items="${subpackUserList.value}">
                                            &nbsp;<span class="label label-default">${user.username}</span>
                                        </c:forEach>
                                    </c:if>
                                </c:forEach>
                            </c:if>
                        </label>
                    </c:forEach>
                </div>
                <br/>
                <br/>
                <input type="submit" class="btn btn-lg btn-primary btn-block" value="Assign packages">
            </form>
            <c:if test="${allAssigned == true}">
                <h3>All packages are assigned</h3>
            </c:if>
            <br/>
            <form action="<spring:url value="/"/>">
                <input type="submit" class="btn btn-lg btn-primary btn-block"   value="Go back to main menu">
            </form>

        </div>
    </div>
</div>

<script src="https://ajax.googleapis.com/ajax/libs/jquery/1.11.3/jquery.min.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/js/bootstrap.min.js" integrity="sha384-0mSbJDEHialfmuBBQP6A4Qrprq5OVfW37PRR3j5ELqxss1yVqOtnepnHVP9aJ7xS" crossorigin="anonymous"></script>
</body>
</html>
