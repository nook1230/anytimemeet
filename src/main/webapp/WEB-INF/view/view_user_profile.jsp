<%@page import="com.mamascode.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />
<c:url var="userRootUrl" value="/user" />
<%
	User user = (User) request.getAttribute("user");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>회원 정보</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<style>
	</style>
	
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/custom_js.js"></script>
	<script>
		$(document).ready(function() {
			$('.btn').mouseenter(function() {
				$(this).css('cursor', 'pointer');
			});
		});
	</script>
</head>
<body>
	<div id="user_profile">
		<h2 class="text-center">회원 정보</h2>
		
		<c:choose>
			<c:when test="${loginCheck == true}">
			<table class="basic_table lined_table w-80">
				<tr>
					<td>
						프로필 사진
					</td>
					<td>
						<%
							String profilePictureName;
							if(user != null && user.getProfilePicture() != null) {
								profilePictureName = "/user_files/user_profile_pictures/" + user.getProfilePicture().getUserProfilePictureName();
							} else {
								profilePictureName = "/static_img/default_profile.jpg";
							}
						%>
						<c:set var="profilePictureName" value="<%=profilePictureName%>" />
						<img src="${resourceRootUrl}${profilePictureName}" width="90px" height="120px" alt="user profile picture" />
					</td>
				</tr>
				
				<tr>
					<td width="40%">아이디</td>
					<td width="60%">${user.userName}</td>
				</tr>
					
				<tr>
					<td>이메일</td>
					<td>${user.email}</td>
				</tr>
					
				<tr>
					<td>별명</td>
					<td>${user.nickname}</td>
				</tr>
					
				<tr>
					<td>이름</td>
					<td>${user.userRealName}</td>
				</tr>
				
				<tr>
					<%
						String userIntroduction = "";
						
						if(user != null && user.getUserIntroduction() != null)
							userIntroduction = user.getUserIntroduction().replaceAll("\n", "<br />"); 
					%>
					<c:set var="userIntroduction" value="<%=userIntroduction%>" />
					<td>자기소개</td>
					<td class="userIntroduction">${userIntroduction}</td>
				</tr>
				
				<tr class="text-center">
					<td colspan="2">
						<button onclick="window.close('window.self')">닫기</button>
					</td>
				</tr>
			</table>
				
			</c:when>
				
			<c:otherwise>
			<script>alert('잘못된 접근입니다.'); location.assign('${DocRootUrl}');</script>
			</c:otherwise>	
		</c:choose>
	</div>	<!-- main_contents end -->
</body>
</html>
			