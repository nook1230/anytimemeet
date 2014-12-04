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
		#join_appl_list_page { text-align: center; margin-top: 15px; }
		#join_inv_list_page { text-align: center; margin-top: 15px; }
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
					
			<c:when test="${checkMaster == true}">
			<!-- 동아리 회원 가입 신청 & 초대 관리: 동아리 master만 접근 가능 -->
			<div id="applications" class="pull-center">
				
				<table class="basic_table lined_table w-100">
					<tr>
						<th colspan="4" class="text-center">
							동아리 가입 신청 목록
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
				
				<% ListPagingHelper applPagingHelper = new ListPagingHelper(
						clubJoinListHelper.getTotalPageCount(), 
						clubJoinListHelper.getCurPageNumber(), 10); %>
				<c:set var="applPagingHelper" value="<%=applPagingHelper%>" />
				<div id="join_appl_list_page">
					<c:if test="${applPagingHelper.startPage != 1}">
					<a href="${clubRootUrl}/admin/${club.clubName}/joinClubAdmin?cj_page=${pagingHelper.startPage-applPagingHelper.pagePerList}">[prev]</a>
					</c:if>
					
					<c:forEach var="i" begin="${applPagingHelper.startPage}" end="${applPagingHelper.endPage}">
					
					<c:if test="${i == applPagingHelper.curPage}">
					<span class="font-bold">${i}</span>
					</c:if>
					
					<c:if test="${i != applPagingHelper.curPage}">
					<a href="${clubRootUrl}/admin/${club.clubName}/joinClubAdmin?cj_page=${i}">${i}</a>
					</c:if>
					
					</c:forEach>
					<c:if test="${applPagingHelper.endPage != applPagingHelper.totalPage}">
					<a href="${clubRootUrl}/admin/${club.clubName}/joinClubAdmin?cj_page=${applPagingHelper.endPage+applPagingHelper.pagePerList}">[next]</a>
					</c:if>
				</div>
			</div>
			
			<div id="invitations" class="pull-center">
				<table class="basic_table lined_table w-100">
					<tr>
						<th colspan="3" class="text-center">
							동아리 가입 초대 목록
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
				
				<% ListPagingHelper invPagingHelper = new ListPagingHelper(
						clubJoinListHelper.getTotalPageCount(), 
						clubJoinListHelper.getCurPageNumber(), 10); %>
				<c:set var="invPagingHelper" value="<%=invPagingHelper%>" />
				<div id="join_inv_list_page">
					<c:if test="${invPagingHelper.startPage != 1}">
					<a href="${clubRootUrl}/admin/${club.clubName}/joinClubAdmin?ci_page=${invPagingHelper.startPage-invPagingHelper.pagePerList}">[prev]</a>
					</c:if>
					
					<c:forEach var="i" begin="${invPagingHelper.startPage}" end="${invPagingHelper.endPage}">
					
					<c:if test="${i == invPagingHelper.curPage}">
					<span class="font-bold">${i}</span>
					</c:if>
					
					<c:if test="${i != invPagingHelper.curPage}">
					<a href="${clubRootUrl}/admin/${club.clubName}/joinClubAdmin?ci_page=${i}">${i}</a>
					</c:if>
					
					</c:forEach>
					<c:if test="${invPagingHelper.endPage != invPagingHelper.totalPage}">
					<a href="${clubRootUrl}/admin/${club.clubName}/joinClubAdmin?ci_page=${invPagingHelper.endPage+invPagingHelper.pagePerList}">[next]</a>
					</c:if>
				</div>
			</div>
			<!-- 끝: 동아리 회원 가입 신청 & 초대 관리 -->			
			
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