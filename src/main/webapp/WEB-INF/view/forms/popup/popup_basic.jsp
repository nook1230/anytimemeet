<%@page import="java.util.Map"%>
<%@page import="com.mamascode.model.utils.PopupInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	PopupInfo popunInfo = (PopupInfo) request.getAttribute("popupInfo");
%>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>${popupInfo.title}</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
</head>
<body>
	<c:choose>
			
	<c:when test="${popupInfo.access}">
	<div class="pull-center text-center" style="width: 350px; margin-top: 20px;">
		<h3>${popupInfo.title}</h3>
		<form action="${DocRootUrl}${popupInfo.url}" method="post">
			<% for(Map.Entry<String, Object> hidden : popunInfo.getHiddens().entrySet()) { %>
			<input type="hidden" name="<%=hidden.getKey()%>" value="<%=hidden.getValue()%>" />
			<% } %>
			<p>
				${popupInfo.comment}
			</p>
			<input type="submit" value="확인" />
			<input type="button" value="취소" onclick="window.close('window.self');" />
		</form>
	</div>
	</c:when>
	
	<c:otherwise>
	<div class="pull-center text-center" style="width: 350px; margin-top: 20px;">
		잘못된 접근입니다.
		<button onclick="window.close('window.self');">확인</button>
	</div>
	</c:otherwise>
	
	</c:choose>
</body>
</html>