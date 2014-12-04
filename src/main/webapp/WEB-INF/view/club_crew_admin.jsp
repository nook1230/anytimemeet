<%@page import="com.mamascode.utils.DateFormatUtil"%>
<%@page import="java.util.List"%>
<%@page import="com.mamascode.utils.ListPagingHelper"%>
<%@page import="com.mamascode.model.User"%>
<%@page import="com.mamascode.utils.ListHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	ListHelper<User> clubCrewListHelper = null;
	if(request.getAttribute("clubCrewListHelper") != null)
		clubCrewListHelper = (ListHelper<User>) request.getAttribute("clubCrewListHelper");
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
		#crew_list { width: 700px; margin-top: 30px; }
		#crew_list_page { text-align: center; margin-top: 15px; }
		#search_form_area { width: 700px; text-align: right; margin-top: 10px; }
		#search_content_area { width: 700px; margin-top: 15px; }
	</style>
	
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/custom_js.js"></script>
	<script>
		$(document).ready(function() {
			$('.a_btn').mouseenter(function() {
				$(this).css('cursor', 'pointer');
			});
		});
		
		function getSearchResult(clubName) {
			// 검색 결과 리셋
			$('#search_content_area').children().remove();
			
			// 검색 키워드
			var keyword = $('#search_keyword').attr('value');
			var searchType = $('#search_opt').attr('value');
			
			// Ajax url
			var ajaxUrl = '${clubRootUrl}/admin/' + clubName + '/searchClubMember';
			var popupUrl = '${clubRootUrl}/admin/' + clubName + '/appointClubCrew/';
			
			// 검색 결과 테이블
			var $table = $('<table class="basic_table lined_table w-100"></table>');
			$table.appendTo($('#search_content_area'));
			$('<tr><th colspan="5">검색 결과</th></tr>').appendTo($table);
			$('<tr><td>#</td><td>아이디</td><td>별명</td><td>운영진 임명</td><td>회원정보</td></tr>').appendTo($table);
			
			// Ajax 요청 데이터(검색 키워드, 검색 타입) - keyword는 한글 검색을 위해 utf-8로 인코딩
			var requestData = {"keyword": encodeURIComponent(keyword), 
					"searchType": searchType};
			
			// 검색 결고 가져오기(Ajax)			
			$.ajax({url: ajaxUrl, data: requestData, method: 'get',
				success:function(result) {
					for(var i = 0; i < result.length; i++) {
						var $tr = $('<tr></tr>');
						var $tds = $('<td>' + (i+1) + '</td><td>' + result[i].userName + '</td><td>' 
								+ result[i].nickname + 
								'</td><td><button onclick="popupForm(\'' + popupUrl + result[i].userName + '\', \'appoint_club_crew\');">임명</button></td>' + 
								'<td><a onclick="popupScroll(\'${userRootUrl}/profile/' + result[i].userName + '\', 400, 600, 600, 200, \'new_popup_view_user_profile\');">[보기]</a></td>');
						$tds.appendTo($tr);
						$tr.appendTo($table);
					}
				}, error:function() {
				   alert("실패");
				}
			});
		}
		
		function popupForm(popupUrl, aPopupName) {
			var popupName = "new_popup_";
			if(aPopupName == null) {
				for(var i = 0; i < 5; i++) {
					popupName += Math.round(Math.random() * 10);
				}
			} else {
				popupName += aPopupName;
			}
			
			popup(popupUrl , 400, 200, 600, 200, popupName);
		}
	</script>
</head>
<body>
	<%@ include file="components/header.jsp" %>
	
	<div id="allcontents">
			
		<div id="main_contents">
			<c:choose>
			
			<c:when test="${checkMaster == true}">
			
			<div id="crew_list" class="pull-center">
			<% if(clubCrewListHelper != null) { %>
				<table class="basic_table lined_table w-100">
					<tr>
						<th colspan="6">
							운영진 목록
						</th>
					</tr>
					
					<tr>
						<td width="10%">#</td>
						<td width="15%">아이디</td>
						<td width="15%">이름</td>
						<td width="20%">별명</td>
						<td width="25%">임명날짜</td>
						<td width="15%"></td>
					</tr>
					<% if(clubCrewListHelper != null && clubCrewListHelper.getList() != null
									&& clubCrewListHelper.getList().size() != 0) { 
						List<User> clubCrewList = clubCrewListHelper.getList(); 
						
						for(int i = 0; i < clubCrewList.size(); i++) {
							User clubCrew = clubCrewList.get(i);
						%>
					<c:set var="clubCrew" value="<%=clubCrew%>" />
					<tr>
						<td><%=(i+1)%></td>
						<td>${clubCrew.userName}</td>
						<td>${clubCrew.userRealName}</td>
						<td>${clubCrew.nickname}</td>
						<td class="font-very-small"><%=DateFormatUtil.getDatetimeFormat(clubCrew.getClubCrewAppointedDate())%></td>
						<td><button onclick="popupForm('${clubRootUrl}/admin/${club.clubName}/dismissClubCrew/${clubCrew.userName}', 'dismiss_club_crew');">해임</button></td>
					</tr>
					
						<% } %>
					<% } else { %>
						<tr><td colspan="6">동아리 운영진이 없습니다</td></tr>
					<% } %>
				</table>
				
				<% ListPagingHelper pagingHelper = new ListPagingHelper(
							clubCrewListHelper.getTotalPageCount(), 
							clubCrewListHelper.getCurPageNumber(), 10); %>
				<c:set var="pagingHelper" value="<%=pagingHelper%>" />
				<div id="crew_list_page">
					<c:if test="${pagingHelper.startPage != 1}">
					<a href="${clubRootUrl}/admin/${club.clubName}/adminClubCrew?page=${pagingHelper.startPage-pagingHelper.pagePerList}">[prev]</a>
					</c:if>
					
					<c:forEach var="i" begin="${pagingHelper.startPage}" end="${pagingHelper.endPage}">
					
					<c:if test="${i == pagingHelper.curPage}">
					<span class="font-bold">${i}</span>
					</c:if>
					
					<c:if test="${i != pagingHelper.curPage}">
					<a href="${clubRootUrl}/admin/${club.clubName}/adminClubCrew?page=${i}">${i}</a>
					</c:if>
					
					</c:forEach>
					<c:if test="${pagingHelper.endPage != pagingHelper.totalPage}">
					<a href="${clubRootUrl}/admin/${club.clubName}/adminClubCrew?page=${pagingHelper.endPage+pagingHelper.pagePerList}">[next]</a>
					</c:if>
				</div>
					
			<% } %>	
			</div>
			
			<div id="search_form_area" class="pull-center">
				<select id="search_opt">
					<option value="0">아이디</option>
					<option value="1">별명</option>
					<option value="2">모두</option>
				</select>
				<input type="text" id="search_keyword" />
				<button id="search_btn" onclick="getSearchResult('${club.clubName}')">검색</button>
			</div>
			
			<div id="search_content_area" class="pull-center"></div>
			
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