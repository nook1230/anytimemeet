<%@page import="com.mamascode.utils.DateFormatUtil"%>
<%@page import="com.mamascode.model.Reply"%>
<%@page import="java.util.Calendar"%>
<%@page import="java.util.GregorianCalendar"%>
<%@page import="java.util.List"%>
<%@page import="com.mamascode.model.MeetingDate"%>
<%@page import="com.mamascode.model.Meeting"%>
<%@page import="com.mamascode.model.Club"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<% 
	Club club = (Club) request.getAttribute("club"); 
	Meeting meeting = (Meeting) request.getAttribute("meeting");
	List<Reply> meetingReplyList = null;
	if(request.getAttribute("meetingReplyList") != null)
		meetingReplyList = (List<Reply>) request.getAttribute("meetingReplyList");
%>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>모임 공지: ${meeting.meetingId}</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/club.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/custom_js.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/article_reply.js"></script>
	<style>
		#content_area { margin-top: 80px; }
		
		#reply_area { margin-top: 50px; min-height: 200px; padding: 10px; border: 1px solid gray; background-color: #E9E9E9; }
		#txtReplyContent { width: 450px; height: 100px}
		#replies_list { margin-top: 10px;}
		.reply-header td { padding-top: 15px; }
		.reply-body td { border-bottom: 1px solid black; padding-top: 10px; padding-bottom: 15px; }
		#reply_list_page { text-align: center; margin-top: 15px; font-size: 0.9em; }
		.btn { cursor: pointer; }
	</style>
	
	<script>
		$(document).ready(function() {
			// write Ajax url
			var writeAjaxUrl = '${clubRootUrl}/meeting/${club.clubName}/writeReply/${meeting.meetingId}';
			// read Ajax url
			var readAjaxUrl = '${clubRootUrl}/meeting/${club.clubName}/readReplies/${meeting.meetingId}';
			// delete url
			var deleteUrl = '${clubRootUrl}/meeting/${club.clubName}/deleteReply/';
			
			$('#btnReplyWrite').click(function(event) {
				event.preventDefault();
				event.stopPropagation();
				
				writeReply(writeAjaxUrl, '${sessionScope.loginUserName}', readAjaxUrl, deleteUrl);
			});
		});
	</script>
</head>
<body onload="resizeImagesWidth($('.meeting_content'), 800);">
	<%@ include file="components/header.jsp" %>
	
	<div id="allcontents">
		<div id="main_contents">
			<c:if test="${meeting.meetingId == 0 || meeting.meetingId == -1 || meeting.title == ''}">
			<script>
				alert('해당 모임 정보가 존재하지 않습니다'); 
				location.assign('${clubRootUrl}/${club.clubName}/clubMain');
			</script>
			</c:if>
			
			<c:choose>
			
			<c:when test="${club.active == true && checkClubMember == true}">
			<div id="content_area">
				<table class="basic_table lined_table w-80">
					<tr>
						<th colspan="2">[${club.clubTitle}]: 동아리 모임</th>
					</tr>
					
					<tr>
						<td width="20%" class="font-bold">모임 타이틀</td>
						<td width="80%">
							${meeting.title}
						</td>
					</tr>
					
					<tr>
						<td class="font-bold">개최인</td>
						<td>
							<c:set var="nicknameCheck" value="${meeting.administratorNickname != null && meeting.administratorNickname != ''}" />
							<c:if test="${nicknameCheck}">${meeting.administratorNickname}(</c:if>${meeting.administratorName}<c:if test="${nicknameCheck}">)</c:if>
						</td>
					</tr>
					
					<tr>
						<td class="font-bold">모임 날짜</td>
						<td>
						<% 
							List<MeetingDate> dates = meeting.getMeetingDates();
							if(dates != null) {
								for(int i = 0; i < dates.size(); i++) {
									MeetingDate date = dates.get(i);
									int dayOfWeek = date.getRecommendedDate().getDay();
						%>
							<c:set var="date" value="<%=date%>" />
							
							<c:if test="${date.dateStatus != 2}">
							#<%=(i+1)%>&nbsp;&nbsp;<%=date.getRecommendedDate()%>&nbsp;<%=date.getRecommendedTime()%>
							&nbsp;(<%=MeetingDate.getDayString(dayOfWeek)%>)
							&nbsp;&nbsp;
							참가 <span class="font-bold color-skyblue"><%=date.getCountParticipants()%></span>
							<c:set var="participantPopupUrl" value="${clubRootUrl}/meeting/${club.clubName}/participants?dateId=${date.dateId}&page=1" />
							<span class="btn" onclick="popup('${participantPopupUrl}', 400, 500, 600, 200, 'new_popup_participants')">
								<img src="${resourceRootUrl}/icon/clipboard-icon-16.png" 
									style="vertical-align: bottom;" alt="참가자 명단 보기 버튼" title="참가자 명단 보기" />
							</span>
							
							<c:set var="popupUrl" value="${clubRootUrl}/meeting/${club.clubName}/participate?id=${meeting.meetingId}&dateId=${date.dateId}" />
							<span class="btn" onclick="popup('${popupUrl}', 400, 200, 600, 200, 'new_popup_participate_date')">
								<img src="${resourceRootUrl}/icon/ok-mark-icon-16.png" 
									style="vertical-align: bottom;" alt="참가 버튼" title="이 날짜에 참석하고 싶습니다" />
							</span>
							
							&nbsp;&nbsp;&nbsp;
							<c:if test="${checkAdmin == true && date.dateStatus == 0}">
							<c:set var="popupUrl" value="${clubRootUrl}/meeting/${club.clubName}/decide?id=${meeting.meetingId}&dateId=${date.dateId}" />
							<span class="btn" onclick="popup('${popupUrl}', 400, 200, 600, 200, 'new_popup_decide_date')">
								<img src="${resourceRootUrl}/icon/check-mark-icon-16.png" 
									style="vertical-align: bottom;" alt="모임 날짜 확정 버튼" title="이 날짜로 모임을 확정합니다" />
							</span>
							</c:if>
							
							<c:if test="${date.dateStatus == 1}">
							<span style="color: orange">확정</span>
							</c:if>
							
								<% if(i != dates.size()-1) { %>
							<br />
								<% } %>
							</c:if>
							<% } %>	
						<%} %>
						</td>
					</tr>
					
					<tr>
						<td class="font-bold">장소</td>
						<td>
							${meeting.location}
						</td>
					</tr>
					
					<tr>
						<td colspan="2" class="meeting_content">
							${meeting.introduction}
						</td>
					</tr>
					
					<c:if test="${checkAdmin == true}">
					<tr>
						<td colspan="2" class="text-right">
							<c:set var="deletePopupUrl" value="${clubRootUrl}/meeting/${club.clubName}/deleteMeeting/${meeting.meetingId}" />
							<span class="btn" onclick="popup('${deletePopupUrl}', 400, 200, 600, 200, 'new_popup_delete_meeting')">
								<img src="${resourceRootUrl}/icon/trash-can-icon-16.png" alt="모임 글 삭제 버튼" title="모임 글 삭제" />
							</span>&nbsp;
							<c:if test="${meeting.administratorName == sessionScope.loginUserName}">
							<span class="btn"><a href="${clubRootUrl}/meeting/${club.clubName}/modifyMeeting/${meeting.meetingId}">
								<img src="${resourceRootUrl}/icon/pencil-icon-16.png" alt="모임 글 수정 버튼" title="모임 글 수정" />
							</a></span>&nbsp;
							</c:if>
						</td>
					</tr>
					</c:if>
				</table>
				
				<div id="reply_area" class="w-80 pull-center">
				<div id="write_reply_form">
					<form>
						<textarea id="txtReplyContent" name="replyContent"></textarea><br />
						<button id="btnReplyWrite" onclick="">댓글쓰기</button>
					</form>
				</div>
				
				<div id="replies_list">
					<table id="replies_table" class="w-100">
						<% for(Reply meetingReply : meetingReplyList) { %>
						<c:set var="meetingReply" value="<%=meetingReply%>" />
						<tr class="font-small reply-header">
							<td width="15%" class="font-bold"><c:set var="nicknameCheck" value="${meetingReply.writerNickname != null && meetingReply.writerNickname != ''}" />
								<c:if test="${nicknameCheck == true}">${meetingReply.writerNickname}</c:if>
								<c:if test="${nicknameCheck == false}">${meetingReply.writerName}</c:if>
							</td>
							<td width="20%" class="font-very-small">
								<%=DateFormatUtil.getDatetimeFormat(meetingReply.getWriteDate())%>
							</td>
							<td width="5%" class="font-very-small btn" 
								onclick="deleteReply('${clubRootUrl}/meeting/${club.clubName}/deleteReply/', ${meetingReply.replyId}, '${clubRootUrl}/meeting/${club.clubName}/readReplies/${meeting.meetingId}');">[삭제]</td>
							<td></td>
						</tr>
						<tr class="reply-body">
							<td colspan="4">${meetingReply.content}</td>
						</tr>
						<% } %>
					</table>
				</div>
			</div>
				
				<br /><br />
				<p class="text-center">
					<a href="${clubRootUrl}/${club.clubName}/clubMain">
						<img src="${resourceRootUrl}/icon/go-icon-32.png" alt="동아리 메인 이동 버튼" title="동아리 메인으로 이동" />
					</a>
				</p>
			</div>
			</c:when>
			
			<c:when test="${club.active == false && checkClubMember == true}">
			<script>
				alert('비활성화된 동아리입니다. 게시물을 볼 수 없습니다.');
				location.assign('${DocRootUrl}');
			</script>
			</c:when>
			
			<c:otherwise>
			<div class="error_msg_box">
				<img src="${resourceRootUrl}/static_img/error1.jpg" width="300px" />
				<p class="error">동아리 회원만 볼 수 있는 정보입니다.</p>
				
				<p>
					<a href="${clubRootUrl}/joinClub?clubName=${club.clubName}">가입 신청하기</a>&nbsp;
					<a href="${docRootUrl}">홈으로 가기</a>
				</p>
			</div>
			</c:otherwise>
			
			</c:choose>	<!-- end of choose tag -->
		</div>	<!-- main_content End -->
	</div>	<!-- allcontents end -->

	<%@ include file="components/footer.jsp" %>
</body>
</html>