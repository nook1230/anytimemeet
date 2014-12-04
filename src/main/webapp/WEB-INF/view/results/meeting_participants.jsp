<%@page import="com.mamascode.utils.ListPagingHelper"%>
<%@page import="java.util.List"%>
<%@page import="com.mamascode.model.User"%>
<%@page import="com.mamascode.utils.ListHelper"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	ListHelper<User> listHelper = (ListHelper) request.getAttribute("participantListHelper");
%>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>동아리 모임 참가자 명단</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
</head>
<body>
	<c:choose>
	
	<c:when test="${checkMember == true}">
	<div class="pull-center text-center" style="width: 350px; margin-top: 20px;">
		<h3>${theDate.recommendedDate}&nbsp;${theDate.recommendedTime} 모임 참가자</h3>
		<h5>모임 제목 - ${meeting.title}</h5>
		<% if(listHelper != null && listHelper.getList() != null &&
				listHelper.getList().size() != 0) {
			List<User> participants = listHelper.getList();
		%>
		<table class="basic_table lined_table w-80">
			<tr>
				<td>아이디</td>
				<td>별명</td>
			</tr>
			
			<% for(int i = 0; i < participants.size(); i++) {
				User participant = participants.get(i);
				String nickname = (participant.getNickname() != null 
									&& !participant.getNickname().equals("null")) ? 
						participant.getNickname() : "없음";
			%>
			<tr>
				<td><%=participant.getUserName()%></td>
				<td><%=nickname%></td>
			</tr>
			<% } %>
		</table>
		<br /><br />
		<%
			ListPagingHelper pagingHelper = new ListPagingHelper(
					listHelper.getTotalPageCount(), listHelper.getCurPageNumber(), 10);
		%>
		<div class="pull-center font-small">
		<c:set var="totalPage" value="<%=listHelper.getTotalPageCount()%>" />
		<c:set var="startPage" value="<%=pagingHelper.getStartPage()%>" />
		<c:set var="endPage" value="<%=pagingHelper.getEndPage()%>" />
		<c:set var="curPage" value="<%=listHelper.getCurPageNumber()%>" />
		<c:set var="perPage" value="<%=listHelper.getObjectPerPage()%>" />
		<c:set var="pagePerList" value="<%=pagingHelper.getPagePerList()%>" />
		<c:forEach var="i" begin="${startPage}" end="${endPage}">
			<c:if test="${startPage != 1}"><a href="${clubRootUrl}/meeting/${clubName}/participants?dateId${theDate.dateId}=&page=${startPage-pagePerList}">[이전]</a></c:if>
			<c:if test="${i == curPage}"><span class="font-normal font-bold">${i}</span></c:if>
			<c:if test="${i != curPage}"><a href="${clubRootUrl}/meeting/${clubName}/participants?dateId${theDate.dateId}=&page=${i}">${i}</a></c:if>
			<c:if test="${endPage != totalPage}"><a href="${clubRootUrl}/meeting/${clubName}/participants?dateId${theDate.dateId}=&page=${startPage+pagePerList}">[다음]</a></c:if>
		</c:forEach>
		</div>
		
		<% } else { %>
		참가자가 없습니다.
		<% } %>
		<div style="margin-top: 20px;">
			<input type="button" value="닫기" onclick="window.close('window.self');"/>
		</div>
	</div>
	</c:when>
		
	<c:otherwise>
	<script>
		alert('접근권한이 없습니다.');
		window.close('window.self');
	</script>
	</c:otherwise>
	
	</c:choose>		<!-- c:choose end -->
</body>
</html>