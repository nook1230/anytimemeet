<%@page import="com.mamascode.utils.DateFormatUtil"%>
<%@ page import="com.mamascode.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	User user = null;
	user = (User) request.getAttribute("user");
%>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />
<c:url var="userRootUrl" value="/user" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>회원 정보 수정</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<style>
		#body-content { width: 700px; min-height: 500px; border: 1px solid gray; }
		h2 { margin-top: 5px; margin-bottom: 25px; text-align: center; clear: both; }
		textarea { width: 300px; height: 150px; }
	</style>
</head>
<body>
	<%@ include file="../components/header.jsp" %>
	
	<div id="allcontents">	
		<div id="main_contents">
			<h2>회원 정보 수정</h2>
			
			<%@ include file="../components/left_nav.jsp" %>
			
			<c:if test="${sizeCheck == false}">
			<script>alert('파일 크기가 500KB를 초과하였습니다.');</script>
			</c:if>
			
			<c:if test="${formatCheck == false}">
			<script>alert('허용되는 업로드 파일의 형식이 아닙니다.');</script>
			</c:if>
			
			<c:if test="${fileUploadResult == false}">
			<script>alert('파일 업로드 실패: 알수 없는 오류.');</script>
			</c:if>
			
			<div id="body_content">
			<c:choose>
				<c:when test="${checkValidUser == true}">
				<sf:form method="post" action="${userRootUrl}/update_myinfo/${user.userName}" modelAttribute="user" enctype="multipart/form-data">
				<table class="basic_table lined_table w-66">
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
						<td>
							<sf:input path="nickname" /><br />
							<sf:errors path="nickname" cssClass="error" />
						</td>
					</tr>
					
					<tr>
						<td>이름</td>
						<td>
							<sf:input path="userRealName" /><br />
							<sf:errors path="userRealName" cssClass="error" />
						</td>
					</tr>
					
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
							<img src="${resourceRootUrl}${profilePictureName}" width="90px" height="120px" alt="user profile picture" /><br />
							<input type="file" name="profile_pic" /><br />
							<span class="font-small" style="color: orange;">프로필 이미지는 가로:세로 3:4의 비율로 표시됩니다</span>
						</td>
					</tr>
					
					<tr>
						<td>가입날짜</td>
						<td><%=DateFormatUtil.getDateFormat(user.getDateOfJoin())%></td>
					</tr>
					
					<tr>
						<td>생년월일</td>
						<td><%=DateFormatUtil.getDateFormat(user.getDateOfBirth())%></td>
					</tr>
					
					<tr>
						<td>자기소개</td>
						<td>
							<sf:textarea path="userIntroduction" style="resize: none; "></sf:textarea>
							<span><sf:errors path="userIntroduction" cssClass="error" /></span>
						</td>
					</tr>
					
					<tr>
						<td colspan="2" class="text-right"><input type="submit" value="수정" /></td>
					</tr>
				</table>
				</sf:form>
				</c:when>
				
				<c:otherwise>
				<script>alert('잘못된 접근입니다.'); location.assign('${DocRootUrl}');</script>
				</c:otherwise>	
			</c:choose>
			</div>	<!-- body_content end -->
		</div>	<!-- main_contents end -->
	</div>	<!-- allcontents end -->
	
	<div>
		<%@ include file="../components/footer.jsp" %>
	</div>
</body>
</html>