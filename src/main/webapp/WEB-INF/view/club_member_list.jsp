<%@page import="com.mamascode.utils.ListPagingHelper"%>
<%@page import="com.mamascode.model.User"%>
<%@ page import="com.mamascode.utils.ListHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%	
	ListHelper<User> clubMembers = null;
	if(request.getAttribute("clubMembers") != null)
		clubMembers = (ListHelper<User>) request.getAttribute("clubMembers");
%>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />
<c:url var="userRootUrl" value="/user" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>동아리 회원 목록</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<style>
		#member_list { width: 700px; margin-top: 30px; }
		#club_member_list_page { text-align: center; margin-top: 15px; }
	</style>
	
	<script type="text/javascript" src=" http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
	<script type="text/javascript" src=" ${resourceRootUrl}/js/custom_js.js"></script>
	<script>
		$(document).ready(function() {
			$('.btn').mouseenter(function() {
				$(this).css('cursor', 'pointer');
			});
		});
	</script>
</head>
<body>
	<%@ include file="components/header.jsp" %>
	
	<div id="allcontents">
			
		<div id="main_contents">
		
			<c:choose>
			
			<c:when test="${checkMember == true}">
			
			<div id="member_list" class="pull-center">
				<table class="basic_table lined_table w-100">
					<tr>
						<th colspan="8">
							동아리 회원 목록
						</th>
					</tr>
					
					<tr class="text-center">
						<td width="20%">프로필 사진</td>
						<td width="20%">#</td>
						<td width="20%">아이디</td>
						<td width="20%">이름</td>
						<td width="20%">별명</td>
					</tr>
					<% if(clubMembers != null && clubMembers.getList() != null
									&& clubMembers.getList().size() != 0) { %>
					<c:forEach var="clubMember" items="${clubMembers.list}" varStatus="status">
					<tr class="font-very-small text-center">
						<c:set var="memberProfilePicture" value="${clubMember.profilePicture}" />
						<c:if test="${memberProfilePicture.picId != 0}">
							<c:set var="memberProfilePictureSrcPath" value="${resourceRootUrl}/user_files/user_profile_pictures/${memberProfilePicture.userName}_${memberProfilePicture.fileName}" />
						</c:if>
						<c:if test="${memberProfilePicture.picId == 0}">
							<c:set var="memberProfilePictureSrcPath" value="${resourceRootUrl}/static_img/default_profile.jpg" />
						</c:if>
						<td>
							<c:set var="userInfoPopupUrl" value="${userRootUrl}/profile/${clubMember.userName}" />
							<img src="${memberProfilePictureSrcPath}" width="30px" height="40px" class="btn" title="클릭! 사용자의 프로필 정보를 보여줍니다"
						 		onclick="popupScroll('${userInfoPopupUrl}', 400, 600, 600, 200, 'new_popup_view_user_profile')" />
						 </td>
						<td>${status.index + 1}</td>
						<td>
							${clubMember.userName}
							<c:if test="${clubMember.userName == club.masterName}">
							(<span class="color-blue">master</span>)
							</c:if>
						</td>
						<td>${clubMember.userRealName}</td>
						<td>${clubMember.nickname}</td>
					</tr>
					</c:forEach>
					<% } else { %>
						<tr><td colspan="7">가입된 동아리 회원이 없습니다</td></tr>
					<% } %>
				</table>
				
				<% ListPagingHelper pagingHelper = new ListPagingHelper(
						clubMembers.getTotalPageCount(), 
						clubMembers.getCurPageNumber(), 10); %>
				<c:set var="pagingHelper" value="<%=pagingHelper%>" />
				<div id="club_member_list_page">
					<c:if test="${pagingHelper.startPage != 1}">
					<a href="${clubRootUrl}/${club.clubName}/clubMemberList?page=${pagingHelper.startPage-pagingHelper.pagePerList}">[prev]</a>
					</c:if>
					
					<c:forEach var="i" begin="${pagingHelper.startPage}" end="${pagingHelper.endPage}">
					
					<c:if test="${i == pagingHelper.curPage}">
					<span class="font-bold">${i}</span>
					</c:if>
					
					<c:if test="${i != pagingHelper.curPage}">
					<a href="${clubRootUrl}/${club.clubName}/clubMemberList?page=${i}">${i}</a>
					</c:if>
					
					</c:forEach>
					<c:if test="${pagingHelper.endPage != pagingHelper.totalPage}">
					<a href="${clubRootUrl}/${club.clubName}/clubMemberList?page=${pagingHelper.endPage+pagingHelper.pagePerList}">[next]</a>
					</c:if>
				</div>
				
				<p class="text-center" style="margin-top: 25px;">
					<a href="${clubRootUrl}/${club.clubName}/clubMain">
						<img src="${resourceRootUrl}/icon/go-icon-32.png" alt="동아리 메인 이동 버튼" title="동아리 메인으로 이동" />
					</a>
				</p>
			</div>
			</c:when>
			
			<c:otherwise>
			<script>alert("잘못된 접근입니다."); location.assign('${DocRootUrl}');</script>
			</c:otherwise>
			
			</c:choose>	
			
		</div>	<!-- main_contents end -->
	</div>	<!-- allcontents end -->
	
	<div>
		<%@ include file="components/footer.jsp" %>
	</div>	
</body>
</html>