<%@page import="com.mamascode.model.Notice"%>
<%@page import="com.mamascode.utils.ListHelper"%>
<%@page import="com.mamascode.utils.ListPagingHelper"%>
<%@page import="com.mamascode.utils.DateFormatUtil"%>
<%@page import="com.mamascode.model.ClubJoinInfo"%>
<%@page import="java.util.List"%>
<%@page import="com.mamascode.model.User"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	ListHelper<Notice> noticeListHelper = null;

	if(request.getAttribute("noticeListHelper") != null &&
			request.getAttribute("noticeListHelper") instanceof ListHelper) {
		noticeListHelper = (ListHelper) request.getAttribute("noticeListHelper");
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
		#notice_list_page { text-align: center; margin-top: 15px; }
	</style>
	
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/custom_js.js"></script>
	<script>
		/* readNotice: 알림 읽기 표시 */
		function readNotice(noticeId, ajaxUrlRoot) {
			// ajax 요청 url
			var ajaxUrl = ajaxUrlRoot + '/notice/readNotice';
			
			// 요청 파라미터(알림 id)
			var requestData = {
				"noticeId" : noticeId,
			};
			
			$.ajaxSetup({'async': false}); // 비동기 방식: 알림 목록을 받아오기 위해
			
			// ajax 요청(post)
			$.post(ajaxUrl, requestData, function(result) {
				// do nothing
			});
		}

		/* deleteNotice: 알림 삭제 */
		function deleteNotice(noticeId, ajaxUrlRoot) {
			// ajax 요청 url
			var ajaxUrl = ajaxUrlRoot + '/notice/deleteNotice';
			
			// 요청 파라미터(알림 id)
			var requestData = {
				"noticeId" : noticeId,
			};
			
			$.ajaxSetup({'async': false}); // 비동기 방식: 알림 목록을 받아오기 위해
			
			// ajax 요청(post)
			$.post(ajaxUrl, requestData, function(result) {
				if(result > 0) {
					location.reload();
				}
			});
		}
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
						<th width="5%">#</th>
						<th width="45%">알림 내용</th>
						<th width="25%">일시</th>
						<th width="10%">종류</th>
						<th width="15%"></th>
					</tr>
					
					<% if(noticeListHelper != null && noticeListHelper.getList() != null
							&& noticeListHelper.getList().size() != 0) {
						List<Notice> notices = noticeListHelper.getList(); %>
					<% for(int i = 0; i < notices.size(); i++) { 
						Notice notice = notices.get(i); %>
					
					<c:set var="notice" value="<%=notice%>" />
					
					<c:if test="${notice.noticeUrl != null && notice.noticeUrl != ''}">
					<c:set var="noticeUrl" value="${DocRootUrl}${notice.noticeUrl}" />
					</c:if>
					<c:if test="${notice.noticeUrl == null || (notice.noticeUrl != null && notice.noticeUrl == '')}">
					<c:set var="noticeUrl" value="#" />
					</c:if>
					
					<c:choose>
						<c:when test="${notice.noticeRead == false && notice.noticeType == 2}">
							<c:set var="color" value="teal" />
						</c:when>
						<c:when test="${notice.noticeRead == true}">
							<c:set var="color" value="gray" />
						</c:when>
						<c:otherwise><c:set var="color" value="black" /></c:otherwise>
					</c:choose>
					
					<c:choose>
						<c:when test="${notice.noticeType == 2}">
							<c:set var="type" value="마스터" />
						</c:when>
						<c:otherwise><c:set var="type" value="일반" /></c:otherwise>
					</c:choose>
							
					<tr class="text-center font-very-small">
						<td><%=i%></td>
						<td class="text-left">
							<a href="${noticeUrl}" style="color: ${color}" 
									onclick="readNotice(${notice.noticeId}, '${userRootUrl}');">
								${notice.noticeMsg}
							</a>
						</td>
						<td><%=DateFormatUtil.getDateFormat(notice.getNoticeDate())%></td>
						<td>${type}</td>
						<td><button onclick="deleteNotice(${notice.noticeId}, '${userRootUrl}');">삭제</button></td>
					</tr>
					<% } %>
					
					<% } else { %>
					<tr class="text-center">
						<td colspan="5">알림이 없습니다</td>
					</tr>
					<% } %>
				</table>
				
				<% ListPagingHelper pagingHelper = new ListPagingHelper(
						noticeListHelper.getTotalPageCount(), 
						noticeListHelper.getCurPageNumber(), 10); %>
				<c:set var="pagingHelper" value="<%=pagingHelper%>" />
				<div id="notice_list_page">
					<c:if test="${pagingHelper.startPage != 1}">
					<a href="${userRootUrl}/notice/getNoticesAll/${loginUserName}?page=${pagingHelper.startPage-pagingHelper.pagePerList}">[prev]</a>
					</c:if>
					
					<c:forEach var="i" begin="${pagingHelper.startPage}" end="${pagingHelper.endPage}">
					
					<c:if test="${i == pagingHelper.curPage}">
					<span class="font-bold">${i}</span>
					</c:if>
					
					<c:if test="${i != pagingHelper.curPage}">
					<a href="${userRootUrl}/notice/getNoticesAll/${loginUserName}?page=${i}">${i}</a>
					</c:if>
					
					</c:forEach>
					<c:if test="${pagingHelper.endPage != pagingHelper.totalPage}">
					<a href="${userRootUrl}/notice/getNoticesAll/${loginUserName}?page=${pagingHelper.endPage+pagingHelper.pagePerList}">[next]</a>
					</c:if>
				</div>
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