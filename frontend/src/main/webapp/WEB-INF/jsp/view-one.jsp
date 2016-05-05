<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true" session="false" %>


<!DOCTYPE html>
<html lang="${pageContext.request.locale}">
<head>
    <meta charset="utf-8">
    <link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/3.3.6/css/bootstrap.min.css" integrity="sha384-1q8mTJOASx8j1Au+a5WDVnPi2lkFfwwEAa8hDDdjZlpLegxhjVME1fgjWPGmkzs7" crossorigin="anonymous">
    <title>Annotation system</title>
</head>
<body>
    <div class="container">
        <form class="form-signin">
            <h2>Upload a new dictionary</h2>
            <label for="uploadDic">Pick dictionary file (CSV)</label>
            <input id="uploadDic" type='file' name='userFile'>
            </br>
            <label for="uploadNoise">Pick noise file (CSV) - optional</label>
            <input id="uploadNoise" type='file' name='userFile'>
            </br>
            <label for="inputRepetition">Enter repetition in %</label>
            <input id="inputRepetition"placeholder="percentage" required>
            </br>
            <label for="inputMax">Enter maximum size of one package</label>
            <input id="inputMax"placeholder="number" required>
            </br>
            </br>
            <button type="submit" class="btn btn-lg btn-primary btn-block">Upload</button>
        </form>
        </br>
        <form action="<spring:url value="/"/>">
            <input type="submit" class="btn btn-lg btn-primary btn-block"   value="Go back">
        </form>
        </br>
        <div>

            ${yellow}

        </div>
    </div>
</body>
</html>
