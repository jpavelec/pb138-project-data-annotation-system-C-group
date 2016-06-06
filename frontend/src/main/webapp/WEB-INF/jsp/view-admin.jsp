<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true" session="false" %>

<!--
@author Matej Rajtár <matej.rajtar@gmail.com>
-->


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
                <br/>
                <h2 align="center">What would you like to do?</h2>
                <br/>
                <a href="<spring:url value="/upload"/>" class="btn btn-lg btn-primary btn-block" >Upload a new dictionary</a>
                <br/>
                <a href="<spring:url value="/assignMenu"/>" class="btn btn-lg btn-primary btn-block" >Assign users to dictionaries</a>
                <br/>
                <a href="<spring:url value="/packages"/>" class="btn btn-lg btn-primary btn-block" >Mark words</a>
                <br/>
                <a href="<spring:url value="/stats"/>" class="btn btn-lg btn-primary btn-block" >View statistics</a>
                <br/>
            </div>
        </div>
    </div>
</body>
</html>
