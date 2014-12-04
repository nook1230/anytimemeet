<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>${result.title}</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
</head>
<body>
	<c:choose>
	
	<c:when test="${result.result == true}">
	<div class="pull-center text-center" style="width: 350px; margin-top: 20px;">
		<h3>${result.heading}</h3>
		<p>${result.success}</p>
		<input type="button" value="확인" onclick="window.opener.location.reload();window.close('window.self');"/>
	</div>
	</c:when>
		
	<c:otherwise>
	<div class="pull-center text-center" style="width: 350px; margin-top: 20px;">
		<h3>${result.heading}</h3>
		<p>${result.fail}: ${result.errorCause}</p>
		<input type="button" value="확인" onclick="window.close('window.self');"/>
	</div>
	</c:otherwise>
	
	</c:choose>		<!-- c:choose end -->
</body>
</html>