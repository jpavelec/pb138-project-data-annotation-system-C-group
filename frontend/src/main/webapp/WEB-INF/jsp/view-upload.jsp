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
    <div class="row">
        <div class="col-md-offset-2 col-md-8">
            <form id="fileuploadForm" method="POST" enctype="multipart/form-data">
                <h2>Upload a new dictionary:</h2>

                <div class="row">
                    <div class="col-sm-6">
                        <label class="btn btn-default btn-block" for="uploadDic">
                            <div class="file-label">Pick dictionary file (CSV)</div>
                            <input id="uploadDic" type='file' name='file' required>
                        </label>
                        <br/>
                    </div>
                    <div class="col-sm-6">
                        <label class="btn btn-default btn-block" for="uploadNoise">
                            <div class="file-label">Pick noise file (CSV) - optional</div>
                            <input id="uploadNoise" type='file' name='file'>
                        </label>
                        <br/>
                    </div>
                </div>
                <label>Enter repetition rate in %</label>
                <input class="form-control" type="number" name="value" value="5" required>
                <br/>
                <label>Enter noise rate in %</label>
                <input class="form-control" type="number" name="value" value="5" required>
                <br/>
                <label>Enter maximum size of one package</label>
                <input class="form-control" type="number" name="value" value="200" required>
                <br/>
                <input type="submit" class="btn btn-lg btn-primary btn-block" value="Upload">
            </form>

            </br>

            <a href="<spring:url value="/"/>" class="btn btn-lg btn-primary btn-block">Go back</a>
        </div>
    </div>
</div>
</body>
</html>
