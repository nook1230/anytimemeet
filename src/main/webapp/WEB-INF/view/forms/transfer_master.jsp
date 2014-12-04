<%@page import="com.mamascode.utils.DateFormatUtil"%>
<%@page import="java.util.List"%>
<%@page import="com.mamascode.utils.ListPagingHelper"%>
<%@page import="com.mamascode.model.User"%>
<%@page import="com.mamascode.utils.ListHelper"%>
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
	<title>동아리 권한 양도</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<style>
		#crew_list { width: 700px; margin-top: 30px; }
		#crew_list_page { text-align: center; margin-top: 15px; }
		#search_form_area { width: 700px; text-align: center; margin-top: 10px; }
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
			var popupUrl = '${clubRootUrl}/admin/' + clubName + '/setting/transferMaster';
			
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
								'</td><td><button onclick="popupForm(\'' + popupUrl + '?newMasterName=' + result[i].userName + '\', \'transfer_master\');">권한 양도</button></td>' + 
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
	<%@ include file="../components/header.jsp" %>
	
	<div id="allcontents">
			
		<div id="main_contents">
			<c:choose>
			
			<c:when test="${checkMaster == true}">
						
			<div id="search_form_area" class="pull-center">
				<span class="font-bold" style="margin-right: 25px;">회원 검색</span>
				<select id="search_opt">
					<option value="0">아이디</option>
					<option value="1">별명</option>
					<option value="2">모두</option>
				</select>
				<input type="text" id="search_keyword" />
				<button id="search_btn" onclick="getSearchResult('${club.clubName}')">검색</button>
			</div>
			
			<div id="search_content_area" class="pull-center"></div>
			
			<p class="text-center" style="margin-top: 50px;">
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
		<%@ include file="../components/footer.jsp" %>
	</div>
</body>
</html>