<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="spring" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page contentType="text/html;charset=UTF-8" pageEncoding="utf-8" trimDirectiveWhitespaces="true" session="false" %>


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
    <div id="main1">
        <div id="main2">
            <div id="left">
                <div class="column-in">
                    <div class="next" id="minus-next">
                        <img src="<c:url value="/resources/images/minus.svg" />" width="100%"/>
                    </div>
                </div>
            </div>
            <div id="right">
                <div class="column-in">
                    <div class="next" id="plus-next">
                        <img src="<c:url value="/resources/images/plus.svg" />" width="100%"/>
                    </div>
                </div>
            </div>
            <div id="middle">
                <div class="column-in">
                    <div id="counter"></div>
                    <div>
                        <span id="filename">name of the package thats being solved</span>
                    </div>
                    <div id="np">word</div>
                    <div id="ontology">Is the word an animal?</div>
                    <input type="button" value="Previous word" name="btnPrevious" />
                </div>
            </div>
        </div>
        <form action="<spring:url value="/packages"/>">
            <input type="submit" class="btn btn-lg btn-primary btn-block" value="Go back">
        </form>
    </div>
</body>
</html>
