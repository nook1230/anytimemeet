<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:url var="docRootUrl" value="/" />
<c:url var="resourceRootUrl" value="/res" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>회원 탈퇴</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/error.css" media="all" />
</head>
<body>
	<div id="error_contents" class="w-100">
		<h2>회원 탈퇴 결과</h2>
		
		<div class="error_msg">
			<c:choose>
			<c:when test="${delete_result == 0}">
				현재 회원님께서 운영하고 있는 동아리가 있습니다.<br />
				마스터 권한을 다른 회원에게 양도한 후 탈퇴하시기 바랍니다.
			</c:when>
			
			<c:when test="${delete_result == 1}">
				탈퇴 처리 실패: 내부 오류
			</c:when>
			
			<c:when test="${delete_result == 2}">
				탈퇴 처리가 완료되었습니다. 그동안 이용해주셔서 감사합니다.
			</c:when>
			
			<c:when test="${delete_result == 3}">
				탈퇴 처리 실패: 알 수 없는 오류
			</c:when>
			
			<c:when test="${delete_result == 4}">
				탈퇴 처리 실패: 잘못된 비밀번호를 입력하였습니다.
			</c:when>
			
			<c:otherwise>
				탈퇴 처리 실패: 알 수 없는 오류
			</c:otherwise>
			</c:choose>
		</div>
		
		<div style="margin-top: 2em;"><a href="${docRootUrl}">메인으로</a></div>
	</div>
</body>
</html>