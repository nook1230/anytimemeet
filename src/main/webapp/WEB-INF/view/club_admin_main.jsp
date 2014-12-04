<%@page import="com.mamascode.utils.ListPagingHelper"%>
<%@page import="com.mamascode.utils.DateFormatUtil"%>
<%@page import="java.util.List"%>
<%@page import="com.mamascode.model.User"%>
<%@ page import="com.mamascode.utils.ListHelper"%>
<%@ page import="com.mamascode.model.ClubJoinInfo"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	ListHelper<ClubJoinInfo> clubJoinListHelper = null;
	if(request.getAttribute("clubJoinListHelper") != null)
		clubJoinListHelper = (ListHelper<ClubJoinInfo>) request.getAttribute("clubJoinListHelper");
	
	ListHelper<ClubJoinInfo> clubInvListHelper = null;
	if(request.getAttribute("clubInvListHelper") != null)
		clubInvListHelper = (ListHelper<ClubJoinInfo>) request.getAttribute("clubInvListHelper");
	
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
	<title>동아리 관리 메인</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<style>
		#member_list { width: 700px; margin-top: 30px; }
		#applications { width: 700px; margin-top: 35px; }
		#invitations { width: 700px; margin-top: 25px; }
		#master_controll { width: 700px; height: 20px; margin-top: 25px; padding: 5px; }
		#club_member_list_page { text-align: center; margin-top: 15px; }
	</style>
	
	<script type="text/javascript" src=" http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
	<script type="text/javascript" src=" ${resourceRootUrl}/js/custom_js.js"></script>
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
		
			<c:choose>
			
			<c:when test="${checkMaster == true || checkCrew == true}">
			
			<div id="member_list" class="pull-center">
				<table class="basic_table lined_table w-100">
					<tr>
						<th colspan="8">
							회원 목록
						</th>
					</tr>
					
					<tr>
						<td width="10%"></td>
						<td width="5%">#</td>
						<td width="20%">아이디</td>
						<td width="20%">이름</td>
						<td width="15%">별명</td>
						<td width="15%">가입날짜</td>
						<td width="15%"></td>
					</tr>
					<% if(clubMembers != null && clubMembers.getList() != null
									&& clubMembers.getList().size() != 0) {
						List<User> clubMemberList = clubMembers.getList(); 
						
						for(int i = 0; i < clubMemberList.size(); i++) {
							User clubMember = clubMemberList.get(i);
						%>
					<c:set var="clubMember" value="<%=clubMember%>" />
					<tr class="font-very-small">
						<c:set var="memberProfilePicture" value="${clubMember.profilePicture}" />
						<c:if test="${memberProfilePicture.picId != 0}">
							<c:set var="memberProfilePictureSrcPath" value="${resourceRootUrl}/user_files/user_profile_pictures/${memberProfilePicture.userName}_${memberProfilePicture.fileName}" />
						</c:if>
						<c:if test="${memberProfilePicture.picId == 0}">
							<c:set var="memberProfilePictureSrcPath" value="${resourceRootUrl}/static_img/default_profile.jpg" />
						</c:if>
						<td><img src="${memberProfilePictureSrcPath}" width="30px" height="40px" /></td>
						<td><%=(i+1)%></td>
						<td>${clubMember.userName}</td>
						<td>${clubMember.userRealName}</td>
						<td>${clubMember.nickname}</td>
						<td><%=DateFormatUtil.getDateFormat(clubMember.getDateOfClubJoin())%></td>
						<td>
							<c:if test="${checkMaster}">
							<c:set var="releaseMemberPopupUrl" value="${clubRootUrl}/admin/${club.clubName}/releaseMember?userName=${clubMember.userName}" />
							<a class="a_btn" onclick="popup('${releaseMemberPopupUrl}', 400, 200, 600, 200, 'new_popup_apprv');">
								<span>[강제탈퇴]</span>
							</a>
							</c:if>
						</td>
					</tr>
						<% } %>
					<% } else { %>
						<tr><td colspan="7">가입된 동아리 회원이 없습니다</td></tr>
					<% } %>
				</table>
			</div>
			
			<% ListPagingHelper pagingHelper = new ListPagingHelper(
					clubMembers.getTotalPageCount(), 
					clubMembers.getCurPageNumber(), 10); %>
				<c:set var="pagingHelper" value="<%=pagingHelper%>" />
				<div id="club_member_list_page">
					<c:if test="${pagingHelper.startPage != 1}">
					<a href="${clubRootUrl}/admin/${club.clubName}/main?page=${pagingHelper.startPage-pagingHelper.pagePerList}">[prev]</a>
					</c:if>
					
					<c:forEach var="i" begin="${pagingHelper.startPage}" end="${pagingHelper.endPage}">
					
					<c:if test="${i == pagingHelper.curPage}">
					<span class="font-bold">${i}</span>
					</c:if>
					
					<c:if test="${i != pagingHelper.curPage}">
					<a href="${clubRootUrl}/admin/${club.clubName}/main?page=${i}">${i}</a>
					</c:if>
					
					</c:forEach>
					<c:if test="${pagingHelper.endPage != pagingHelper.totalPage}">
					<a href="${clubRootUrl}/admin/${club.clubName}/main?page=${pagingHelper.endPage+pagingHelper.pagePerList}">[next]</a>
					</c:if>
				</div>
			
			<c:if test="${checkMaster == true}">
			<!-- 동아리 회원 가입 신청 & 초대 관리: 동아리 master만 접근 가능 -->
			<div id="applications" class="pull-center">
				
				<table class="basic_table lined_table w-100">
					<tr>
						<th colspan="4" class="text-center">
							동아리 가입 신청 목록
							<span class="pull-right font-normal"><a href="${clubRootUrl}/admin/${club.clubName}/joinClubAdmin" style="font-size: 0.8em;">[더 보기]</a></span>
						</th>
					</tr>
					<% if(clubJoinListHelper != null && clubJoinListHelper.getList() != null
										&& clubJoinListHelper.getList().size() != 0) { %>
					<c:forEach var="application" items="${clubJoinListHelper.list}">
					<tr>
						<td>${application.userName}</td>
						<td>${application.comment}</td>
						<td>
							<c:set var="popupUrl" value="${clubRootUrl}/admin/${application.clubName}/clubJoin?userName=${application.userName}" />
							<a class="a_btn" onclick="popup('${popupUrl}', 400, 200, 600, 200, 'new_popup_apprv');">
								<span class="small">[가입승인]</span>
							</a>
						</td>
						<td><a class="a_btn"><span class="small">[승인거절]</span></a></td>
					</tr>
					</c:forEach>
					<% } else { %>
					<tr><td colspan="4" class="text-center">신청 내역이 없습니다</td></tr>
					<% } %>
				</table>
			</div>
			
			<div id="invitations" class="pull-center">
				<table class="basic_table lined_table w-100">
					<tr>
						<th colspan="3" class="text-center">
							동아리 가입 초대 목록
							<span class="pull-right font-normal"><a href="${clubRootUrl}/admin/${club.clubName}/joinClubAdmin" style="font-size: 0.8em;">[더 보기]</a></span>
						</th>
					</tr>
					<% if(clubInvListHelper != null && clubInvListHelper.getList() != null
						&& clubInvListHelper.getList().size() != 0) { %>
					<c:forEach var="invitation" items="${clubInvListHelper.list}">
					<tr>
						<td>${invitation.userName}</td>
						<td>${invitation.comment}</td>
						<td><span class="small"><a class="a_btn">[취소]</a></span></td>
					</tr>
					</c:forEach>
					<% } else { %>
					<tr><td colspan="3" class="text-center">초대 내역이 없습니다</td></tr>
					<% } %>
				</table>
			</div>
			<!-- 끝: 동아리 회원 가입 신청 & 초대 관리 -->
			</c:if>
			
			<c:if test="${checkMaster == true}">
			<div id="master_controll" class="pull-center">
				<span class="pull-right font-normal">
					<a href="${clubRootUrl}/admin/${club.clubName}/setting" style="color: blue;">[동아리 정보 변경]</a>
				</span>&nbsp;&nbsp;
				
				<span class="pull-right font-normal">
					<a href="${clubRootUrl}/admin/${club.clubName}/adminClubCrew" style="color: blue;">[운영진 관리]</a>
				</span>
				
				<span class="pull-right font-normal">
					<a href="${clubRootUrl}/admin/${club.clubName}/inviteMember" style="color: blue;">[회원 초대]</a>
				</span>
			</div>
			</c:if>
			
			<p class="text-center" style="margin-top: 25px;">
				<a href="${clubRootUrl}/${club.clubName}/clubMain">
					<img src="${resourceRootUrl}/icon/go-icon-32.png" alt="동아리 메인 이동 버튼" title="동아리 메인으로 이동" />
				</a>
			</p>
			
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