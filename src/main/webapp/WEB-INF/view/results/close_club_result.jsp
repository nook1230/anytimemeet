<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:url var="clubRootUrl" value="/club" />
<c:url var="docRootUrl" value="/" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>로그인 에러</title>
	<link rel="stylesheet" type="text/css" href="/res/css/basic_style.css" media="all" />
	<link rel="stylesheet" type="text/css" href="/res/css/error.css" media="all" />
</head>
<body>
	<div id="error_contents" class="w-100">
		<div class="error_msg">
			<c:choose>
			<c:when test="${errorCode == 'close failure'}">
				폐쇄 실패: 알 수 없는 오류
			</c:when>
			
			<c:when test="${errorCode == 'close success'}">
			<script>alert('동아리가 폐쇄되었습니다.'); location.assign('${docRootUrl}');</script>
			</c:when>
			</c:choose>
		</div>
		
		<div style="margin-top: 2em;"><a href="${docRootUrl}">메인으로</a></div>
	</div>
</body>
</html>