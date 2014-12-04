<%@page import="com.mamascode.utils.DateFormatUtil"%>
<%@page import="com.mamascode.model.ClubJoinInfo"%>
<%@page import="java.util.List"%>
<%@page import="com.mamascode.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	User user = (User) request.getAttribute("user");
	List<ClubJoinInfo> applyingClubs = null;
	List<ClubJoinInfo> invitedClubs = null;
	
	if(user != null) {
		applyingClubs = user.getApplyingClubs();
		invitedClubs = user.getInvitedClubs();
	}
%>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />
<c:url var="userRootUrl" value="/user" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>회원 정보</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<style>
		#body-content { width: 700px; min-height: 500px; border: 1px solid gray; }
		h2 { margin-top: 5px; margin-bottom: 25px; text-align: center; clear: both; }
	</style>
	
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/custom_js.js"></script>
	<script>
		$(document).ready(function() {
			$('.a_btn').mouseenter(function() {
				$(this).css('cursor', 'pointer');
			});
		});
	</script>
</head>
<body>
	<%@ include file="components/header.jsp" %>
	
	<div id="allcontents">
			
		<div id="main_contents">
			<h2>회원 정보</h2>
			
			<%@ include file="components/left_nav.jsp" %>
			
			<div id="body_content">
			<c:choose>
				<c:when test="${checkValidUser == true}">
				<table class="basic_table lined_table w-66">
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
						<td>가입날짜</td>
						<td><%=DateFormatUtil.getDateFormat(user.getDateOfJoin())%></td>
					</tr>
					
					<tr>
						<td>생년월일</td>
						<td><%=DateFormatUtil.getDateFormat(user.getDateOfBirth())%></td>
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
				</table>
				<br /><br />
				<table class="basic_table lined_table w-100">
					<tr>
						<td width="50%">
							가입 신청
						</td>
						
						<td width="50%">
							동아리 초대
						</td>
					</tr>
					
					<tr>
						<td class="small">
							<% if(applyingClubs.size() == 0) { %>
							가입 신청 내역이 없습니다.
							<% } else { %>
							<table class="basic_table none_lined_table w-100">
							<% for(ClubJoinInfo clubJoinInfo : applyingClubs) { %>
								<tr>
									<td width="70%">
										<a href="${clubRootUrl}/<%=clubJoinInfo.getClubName()%>/clubMain">
											<%=clubJoinInfo.getClubTitle()%>
										</a>
									</td>
									<td width="30%">
										<c:set var="clubName" value="<%=clubJoinInfo.getClubName()%>" />
										<c:set var="userName" value="<%=user.getUserName()%>" />
										<c:set var="popupUrl" value="${userRootUrl}/cancelAppl?clubName=${clubName}&userName=${userName}" />
										<a class="a_btn" onclick="popup('${popupUrl}', 400, 200, 600, 200, 'new_popup_cancel_appl');">
											[취소]
										</a>
									</td>
								</tr>
							<% } %>
							</table>
							<% } %>
						</td>
						
						<td class="small">
							<% if(invitedClubs.size() == 0) { %>
							초대 내역이 없습니다.
							<% } else { %>
							<table class="basic_table none_lined_table w-100">
							<% for(ClubJoinInfo clubJoinInfo : invitedClubs) { %>
								<tr>
									<td width="70%">
										<a href="${clubRootUrl}/<%=clubJoinInfo.getClubName()%>/clubMain">
										<%=clubJoinInfo.getClubTitle()%></a>
									</td>
									<td width="30%">
										<c:set var="clubName" value="<%=clubJoinInfo.getClubName()%>" />
										<c:set var="userName" value="<%=user.getUserName()%>" />
										<c:set var="popupUrl" value="${userRootUrl}/${userName}/confirmInvitation/${clubName}" />
										<a class="a_btn" onclick="popup('${popupUrl}', 400, 200, 600, 200, 'new_popup_confirm_inv');">
											[승낙]
										</a>
									</td>
								</tr>
							<% } %>
							</table>
							<% } %>
						</td>
					</tr>
				</table>
				
				<br /><br />
				<table class="basic_table lined_table w-100">
					<tr class="text-center">
						<td>
							<c:choose>
							<c:when test="${user.certified == false}">
							인증 기능은 사용하지 않습니다. 그러나 다음 링크를 클릭하면 인증 절차를 완료할 수 있습니다. ---&gt;
							<a style="color: blue;" href="${userRootUrl}/certify?userName=${sessionScope.loginUserName}&certiKey=${user.certificationKey}">[인증하기]</a>
							</c:when>
							<c:otherwise>
							인증 절차가 완료됨
							</c:otherwise>
							</c:choose>
						</td>
					</tr>
				</table>
				
				</c:when>
				
				<c:otherwise>
				<script>alert('잘못된 접근입니다.'); location.assign('${DocRootUrl}');</script>
				</c:otherwise>	
			</c:choose>
			</div>	<!-- end -->
		</div>	<!-- main_contents end -->
	</div>	<!-- allcontents end -->
	
	<div>
		<%@ include file="components/footer.jsp" %>
	</div>
</body>
</html>