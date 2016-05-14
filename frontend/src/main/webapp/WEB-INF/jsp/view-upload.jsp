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
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css"
          integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
    <link type="text/css" rel="stylesheet" href="<c:url value="/resources/css/style.css" />"/>
    <title>Annotation system</title>
</head>
<body>
<div class="container">
    <div id="wrap">
        <form method="post">
            <h2>Upload a new dictionary:</h2>
            <span class="btn btn-default btn-file">
                <label for="uploadDic">Pick dictionary file (CSV)</label>
                <input id="uploadDic" type='file' class="file" name='userFile' required>
            </span>
            <span class="btn btn-default btn-file">
                <label for="uploadNoise">Pick noise file (CSV) - optional</label>
                <input id="uploadNoise" type='file' name='userFile' required>
            </span>
            </br>
            </br>
            <div id="middle">
                <label for="inputRepetition">Enter repetition in %</label>
                <input class="form-control" id="inputRepetition" placeholder="percentage" required>
                </br>
                <label for="inputMax">Enter maximum size of one package</label>
                <input class="form-control" id="inputMax" placeholder="number" required>
                </br>
            </div>
            <input type="submit" class="btn btn-lg btn-primary btn-block" value="Upload">
        </form>
    </div>
    </br>
    <form action="<spring:url value="/"/>">
        <input type="submit" class="btn btn-lg btn-primary btn-block" value="Go back">
    </form>
</div>
</body>
</html>
