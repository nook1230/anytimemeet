<%@page import="com.mamascode.model.utils.ProcessingResult"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	ProcessingResult result = (ProcessingResult) request.getAttribute("result");
%>
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
	<script>
		alert('<%=result.getSuccess()%>');
		location.assign('${DocRootUrl}<%=result.getSuccessUrl()%>');
	</script>
	</c:when>
		
	<c:otherwise>
	<script>
		alert('<%=result.getFail()%>: ' + '<%=result.getErrorCause()%>');
		location.assign('${DocRootUrl}<%=result.getFailUrl()%>');
	</script>
	</c:otherwise>
	
	</c:choose>		<!-- c:choose end -->
</body>
</html>