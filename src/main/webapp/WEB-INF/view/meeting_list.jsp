<%@page import="com.mamascode.utils.DateFormatUtil"%>
<%@page import="com.mamascode.model.Meeting"%>
<%@page import="com.mamascode.utils.ListPagingHelper"%>
<%@page import="java.util.List"%>
<%@page import="com.mamascode.utils.ListHelper"%>
<%@page import="com.mamascode.model.ClubArticle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	ListHelper<Meeting> meetingListHelper = null;
	
	if(request.getAttribute("meetingListHelper") != null &&
			request.getAttribute("meetingListHelper") instanceof ListHelper) {
		meetingListHelper = (ListHelper) request.getAttribute("meetingListHelper");
	}
%>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>동아리 모임 목록</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<style>
		.article_content { min-height: 200px; padding: 15px; }
		#article_list_page { text-align: center; margin-top: 15px; }
	</style>
</head>
<body>
	<%@ include file="components/header.jsp" %>
	
	<div id="allcontents">
		
		<div id="main_contents">
			<c:choose>
			
			<c:when test="${checkLogin ==true && checkClubMember == true}">
			<div>
				<h3 class="text-center" style="margin-top: 30px;">
					동아리 [<a href="${clubRootUrl}/${club.clubName}/clubMain">${club.clubTitle}</a>]의 모임 목록:&nbsp;
					페이지 ${meetingListHelper.curPageNumber}
				</h3>
				
				<% if(meetingListHelper != null && meetingListHelper.getList() != null &&
						meetingListHelper.getList().size() != 0) {
					List<Meeting> meetingList = meetingListHelper.getList();
				%>
				<table class="basic_table lined_table w-80">
					<tr>
						<th width="10%">#</th>
						<th width="35%">제목</th>
						<th width="20%">개최자</th>
						<th width="20%">장소</th>
						<th width="15%">작성일</th>
					</tr>
					
					<% for(int i = 0;  i < meetingList.size(); i++) {
						Meeting meeting = meetingList.get(i);
					%>
					<c:set var="meeting" value="<%=meeting%>" />
					<tr class="text-center">
						<td>${meeting.meetingId}</td>
						<td class="text-left"><a href="${clubRootUrl}/meeting/${club.clubName}/meetingDetail?id=${meeting.meetingId}">${meeting.title}</a>
							<span class="font-very-small color-orange">[${meeting.repliesCount}]</span>
						</td>
						<td>
							<c:choose>
							<c:when test="${meeting.administratorNickname != null && meeting.administratorNickname != ''}">
								<c:set var="nickname" value="${meeting.administratorNickname}" />
							</c:when>
							<c:otherwise><c:set var="nickname" value="${meeting.administratorName}" /></c:otherwise>
							</c:choose>
							${nickname}
						</td>
						<td>${meeting.location}</td>
						<td class="font-very-small"><%=DateFormatUtil.getDateFormat(meeting.getRegDate())%></td>
					</tr>
					<% } %>
				</table>
				
				<% ListPagingHelper pagingHelper = new ListPagingHelper(
						meetingListHelper.getTotalPageCount(), 
						meetingListHelper.getCurPageNumber(), 10); %>
				<c:set var="pagingHelper" value="<%=pagingHelper%>" />
				<div id="article_list_page">
					<c:if test="${pagingHelper.startPage != 1}">
					<a href="${clubRootUrl}/meeting/${club.clubName}/meetingList?page=${pagingHelper.startPage-pagingHelper.pagePerList}">[prev]</a>
					</c:if>
					
					<c:forEach var="i" begin="${pagingHelper.startPage}" end="${pagingHelper.endPage}">
					
					<c:if test="${i == pagingHelper.curPage}">
					<span class="font-bold">${i}</span>
					</c:if>
					
					<c:if test="${i != pagingHelper.curPage}">
					<a href="${clubRootUrl}/meeting/${club.clubName}/meetingList?page=${i}">${i}</a>
					</c:if>
					
					</c:forEach>
					<c:if test="${pagingHelper.endPage != pagingHelper.totalPage}">
					<a href="${clubRootUrl}/meeting/${club.clubName}/meetingList?page=${pagingHelper.endPage+pagingHelper.pagePerList}">[next]</a>
					</c:if>
				</div>
				
				<% } else { %>
				개설된 동아리 모임이 없습니다.
				<% } %>
			</div>
			
			<p class="text-center" style="margin-top: 25px;">
				<a href="${clubRootUrl}/${club.clubName}/clubMain">
					<img src="${resourceRootUrl}/icon/go-icon-32.png" alt="동아리 메인 이동 버튼" title="동아리 메인으로 이동" />
				</a>
				&nbsp;&nbsp;
				<a href="${clubRootUrl}/meeting/${club.clubName}/openMeeting">
					<img src="${resourcRootUrl}/icon/users-icon-32.png" alt="모임 열기 버튼" title="모임 열기" />
				</a>
			</p>
			</c:when>
			
			<c:otherwise>
			<script>
				alert('로그인 상태가 아니거나 동아리 멤버가 아닙니다.');
				location.assign('${DocRootUrl}');
			</script>
			</c:otherwise>
			
			</c:choose> <!-- end of choose tag -->
		</div>	<!-- main_content End -->
	
	</div>	<!-- allcontents end -->
	
	<div>
		<%@ include file="components/footer.jsp" %>
	</div>
</body>
</html>