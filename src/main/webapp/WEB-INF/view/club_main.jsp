<%@page import="com.mamascode.utils.DateFormatUtil"%>
<%@page import="java.util.List"%>
<%@page import="com.mamascode.model.ClubArticle"%>
<%@page import="com.mamascode.model.Meeting"%>
<%@page import="com.mamascode.utils.ListHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="s" uri="http://www.springframework.org/tags"%>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	ListHelper<Meeting> meetingListHelper = null;

	if(request.getAttribute("meetingListHelper") != null &&
			request.getAttribute("meetingListHelper") instanceof ListHelper) {
		meetingListHelper = (ListHelper) request.getAttribute("meetingListHelper");
	}
	
	ListHelper<ClubArticle> articleListHelper = null;
	
	if(request.getAttribute("articleListHelper") != null &&
			request.getAttribute("articleListHelper") instanceof ListHelper) {
		articleListHelper = (ListHelper) request.getAttribute("articleListHelper");
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
	<title>동아리</title>

	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/club.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/custom_js.js"></script>	
	<script>
		$(document).ready(function() {
			$divs = $('.meeting_intro');
			
			for(var i = 0; i < $divs.size(); i++) {
				$div = $divs.eq(i);
				
				abbreviateText($div, 200);
			}
			
			$('.meeting_notice').eq(0).addClass('first');
		});
	</script>
</head>
<body>
	<%@ include file="components/header.jsp" %>
	
	<div id="allcontents">
		<div id="main_contents">
			
			<c:choose>
			
			<c:when test="${club.active == true && (club.type == 1 || (club.type == 2 && checkClubMember == true))}">
			
			<div id="club_title">
				<span class="font-header2">${club.clubTitle}</span>
				
				<span class="pull-right">
				
					<c:if test="${checkMaster == true || checkCrew == true}">
					<a class="btn" href="${clubRootUrl}/admin/${club.clubName}/main">
						<img src="${resourceRootUrl}/icon/gear-icon-24.png" alt="동아리 관리 버튼" title="동아리 관리" />
					</a>
					</c:if>
					<c:if test="${checkClubMember == false}">
						<a class="btn" href="${clubRootUrl}/joinClub?clubName=${club.clubName}">
							<img src="${resourceRootUrl}/icon/join-icon-24.png" alt="동아리 가입 버튼" title="동아리 가입" />
						</a>
					</c:if>
					<c:if test="${checkClubMember == true}">
						<c:set var="popupUrl" value="${clubRootUrl}/${club.clubName}/leaveClub" />
						<a class="btn" onclick="popup('${popupUrl}', 400, 200, 600, 200, 'new_popup_apprv');">
							<img src="${resourceRootUrl}/icon/leave-icon-24.png" alt="동아리 달퇴 버튼" title="동아리  탈퇴" />
						</a>
					</c:if>
				</span>
				<br /><br />
				
				<span class="small pull-right">
					<c:set var="userInfoPopupUrl" value="${userRootUrl}/profile/${club.masterName}" />
					<span class="btn" onclick="popupScroll('${userInfoPopupUrl}', 400, 600, 600, 200, 'new_popup_view_user_profile')">
					마스터: ${club.masterName}</span><br />
					인원수: <a href="${clubRootUrl}/${club.clubName}/clubMemberList" style="color: blue;" title="동아리 회원 목록 보기">${club.numberOfClubMember}명</a>(최대: ${club.maxMemberNum}명)<br />
					타입: <c:choose><c:when test="${club.type == 1}">승인형</c:when>
					<c:when test="${club.type == 2}">초대형</c:when></c:choose><br />
					개설일: ${club.dateOfCreated}<br />
				</span>
			</div>
			
			<div id="meeting_list">
				<img src="${resourceRootUrl}/icon/clipboard-icon-16.png" />&nbsp;
				<span class="font-header2">우리 동아리 모임</span>
				<c:if test="${checkMaster == true || checkCrew == true}">
				<span class="pull-right">
					<a href="${clubRootUrl}/meeting/${club.clubName}/openMeeting">
						<img src="${resourceRootUrl}/icon/users-icon-24.png" alt="모임 열기 버튼" title="모임 열기" />
					</a>
				</span>
				</c:if>
				<br /><br />
				
				<% if(meetingListHelper != null && meetingListHelper.getList() != null &&
						meetingListHelper.getList().size() != 0) { %>
				
				<% for(Meeting meeting : meetingListHelper.getList()) { %>
				<c:set var="meeting" value="<%=meeting%>" />
				<div class="meeting_notice">
					<p style="font-size: 1.2em;">
						<a class="font-bold" href="${clubRootUrl}/meeting/${meeting.clubName}/meetingDetail?id=${meeting.meetingId}">${meeting.title}</a>
						<span class="font-very-small color-orange">[${meeting.repliesCount}]</span>
						<span class="pull-right" style="font-size: 0.7em">
							<c:set var="nicknameCheck" value="${meeting.administratorNickname != null && meeting.administratorNickname != ''}" />
							<c:if test="${nicknameCheck}">${meeting.administratorNickname}(</c:if>${meeting.administratorName}<c:if test="${nicknameCheck}">)</c:if> | <%=DateFormatUtil.getDatetimeFormat(meeting.getRegDate())%> 
						</span>
					</p>
					<div class="content_text">
						<c:if test="${checkClubMember == true}"><div class="meeting_intro">${meeting.introduction}</div></c:if>
						<c:if test="${checkClubMember == false}">동아리 회원만 읽을 수 있는 정보입니다</c:if>
					</div>
				</div>
				
				<% } %>
				
				<p class="font-small text-right">
					<a href="${clubRootUrl}/meeting/${club.clubName}/meetingList">[더 보기]</a>
				</p>
				
				<% } else { %>
				<div>아직 개설된 모임이 없습니다.</div>
				<% } %>
			</div> <!-- meeting_list END  -->
						
			<div id="club_article">
				<img src="${resourceRootUrl}/icon/clipboard-icon-16.png" />&nbsp;
				<span class="font-header2">우리 동아리 게시물
					<span class="pull-right">
						<a href="${clubRootUrl}/article/${club.clubName}/writeNewArticle">
							<img src="${resourceRootUrl}/icon/pencil-icon-24.png" title="글 작성" alt="글 작성 버튼" />
						</a>
					</span>
				</span><br /><br /><br />
				
				<% if(articleListHelper != null && articleListHelper.getList() != null &&
						articleListHelper.getList().size() != 0) {
					List<ClubArticle> articleList = articleListHelper.getList();
				%>
				<table class="basic_table lined_table w-90">
					<tr>
						<th width="15%">#</th>
						<th width="40%">제목</th>
						<th width="15%">작성자</th>
						<th width="20%">작성일시</th>
						<th width="10%">조회수</th>
					</tr>
				<% for(int i = 0;  i < articleList.size(); i++) {
					ClubArticle article = articleList.get(i);
				%>
				<c:set var="articleId" value="<%=article.getArticleId()%>" />
				<tr>
					<td class="text-center">${articleId}</td>
					<td>
						<a href="${clubRootUrl}/article/${club.clubName}/readArticle/${articleId}"><%=article.getTitle()%></a>
						<span class="font-small color-orange">[<%=article.getRepliesCount()%>]</span>
					</td>
					<td class="font-very-small text-center">
						<%
							String writeName = 
								(article.getWriterNickname() != null && !article.getWriterNickname().equals("")) ?
								 article.getWriterNickname() : article.getWriterName();
						%>
						<%=writeName%>
					</td>
					<td class="font-very-small text-center"><%=DateFormatUtil.getDatetimeFormat(article.getWriteDate())%></td>
					<td class="text-center"><%=article.getViewCount()%></td>
				</tr>
				<% } %>
				</table>
				<% } else { %>
				작성된 동아리 게시물이 없습니다
				<% } %>
				<p class="font-small text-right">
					<a href="${clubRootUrl}/article/${club.clubName}/articleList">[더 보기]</a>
				</p>
			</div>
			</c:when>
			
			<c:when test="${club.active == true && (club.type == 2 && checkClubMember == false)}">
				<script>alert('이 동아리는 비공개 동아리입니다.'); location.assign('${docRootUrl}');</script>
			</c:when>
			
			<c:when test="${club.active == false}">
			<div class="error_msg_box">
				<img src="${resourceRootUrl}/static_img/error1.jpg" width="300px" />
				<p class="error">존재하지 않거나 비활성화된 동아리입니다.</p>
				<c:if test="${checkMaster == true}">
				<p>혹시 동아리가 비활성화되어 있나요?&nbsp;<a href="${clubRootUrl}/admin/${club.clubName}/setting/activateClub" style="color: skyblue;">동아리 활성화 하기</a></p>
				</c:if>
				<p><a href="${docRootUrl}">홈으로 가기</a></p>
			</div>
			</c:when>
			
			</c:choose>	<!-- end of choose tag -->
		</div>	<!-- main_content End -->
	</div>	<!-- allcontents end -->

	<%@ include file="components/footer.jsp" %>
</body>
</html>