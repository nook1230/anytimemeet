<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:url var="docRootUrl" value="/" />
<c:url var="resourceRootUrl" value="/res" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>로그인 에러</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/error.css" media="all" />
</head>
<body>
	<div id="error_contents" class="w-100">
		<h2>:( error! ${errorCode}</h2>
		
		<div class="error_msg">
			<c:choose>
			<c:when test="${errorCode == 'login failure'}">
				아이디와 비밀번호가 일치하지 않습니다.
			</c:when>
			
			<c:when test="${errorCode == 'logout failure'}">
				로그아웃 실패: 이미 로그아웃 상태이거나<br />
				로그아웃 처리 중 알 수 없는 오류가 발생하였습니다.
			</c:when>
			</c:choose>
		</div>
		
		<div style="margin-top: 2em;"><a href="${docRootUrl}">메인으로</a></div>
	</div>
</body>
</html>