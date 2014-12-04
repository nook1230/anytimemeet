<%@page import="com.mamascode.model.ClubCategory"%>
<%@page import="com.mamascode.model.Club"%>
<%@page import="com.mamascode.utils.ListPagingHelper"%>
<%@page import="java.util.List"%>
<%@page import="com.mamascode.utils.ListHelper"%>
<%@page import="com.mamascode.model.ClubArticle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	ListHelper<Club> clubListHelper = null;
	
	if(request.getAttribute("clubListHelper") != null &&
			request.getAttribute("clubListHelper") instanceof ListHelper) {
		clubListHelper = (ListHelper) request.getAttribute("clubListHelper");
	}
	
	List<ClubCategory> childCategories = null;
	
	if(request.getAttribute("childCategories") != null &&
			request.getAttribute("childCategories") instanceof List) {
		childCategories = (List) request.getAttribute("childCategories");
	}
%>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>동아리 목록<c:if test="${category == true}">(카테고리)</c:if></title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<style>
		#club_list { clrea: both; min-height: 200px; padding: 15px; }
		#club_list_page { text-align: center; margin-top: 15px; }
		#category_area { margin: 15px; }
	</style>
	
	<script>
	$(document).ready(function() {
		$('#childCategoryId').change(function() {
			var $childCategoryId = $('#childCategoryId');
			
			var sel = $childCategoryId.get(0);
			var opt = sel.options[sel.selectedIndex];
			var selectedValue = opt.value;
			
			if(selectedValue != '0')
				location.assign('${clubRootUrl}/clubListByCat/' + selectedValue);
		});
	});
	</script>
</head>
<body>
	<%@ include file="components/header.jsp" %>
	
	<div id="allcontents">
	
		<c:if test="">
		<script>alert('개설된 동아리가 없습니다!'); location.assign('${DocRootUrl}');</script>
		</c:if>
		
		<div id="main_contents">
			
			<h3 class="text-center" style="margin-top: 30px;">
				동아리 목록 보기<c:if test="${category == true}">(카테고리: ${clubCategory.categoryTitle})</c:if>
			</h3>
			
			<div id="category_area" class="pull-right">
				<span>카테고리:&nbsp;<a href="${clubRootUrl}/clubList">모두</a></span>
				
				<c:if test="${parentCategory != null}">
				<span class="font-very-small">&nbsp;&gt;&gt;&nbsp;</span>
				<a href="${clubRootUrl}/clubListByCat/${parentCategory.categoryId}">${parentCategory.categoryTitle}</a>
				
				</c:if>
				
				<c:if test="${clubCategory != null}">
				<span class="font-very-small">&nbsp;&gt;&gt;&nbsp;</span>
				<a href="${clubRootUrl}/clubListByCat/${clubCategory.categoryId}">${clubCategory.categoryTitle}</a>
				</c:if>
				
				<% if(childCategories != null && childCategories.size() != 0) { %>
				<span class="font-very-small">&nbsp;&gt;&gt;&nbsp;</span>
				<select id="childCategoryId">
					<option value="0">하위 카테고리</option>
					<c:forEach var="childCategory" items="${childCategories}">
					<option value="${childCategory.categoryId}">${childCategory.categoryTitle}</option>
					</c:forEach>
				</select>
				<% } %>
			</div>
			
			<% if(clubListHelper != null && clubListHelper.getList() != null &&
					clubListHelper.getList().size() != 0) {
				List<Club> clubs = clubListHelper.getList();
			%>
			<div id="club_list">
			
			<table class="basic_table lined_table font-small" style="width: 98%; margin: 5px;">
				<tr>
					<th width="15%">#</th>
					<th width="25%">동아리 이름</th>
					<th width="40%">소개</th>
					<th width="20%">회원수(최대인원)</th>
				</tr>
				<% for(Club club : clubs) { %>
				<c:set var="club" value="<%=club%>" />
				<tr>
					<td class="text-center">${club.clubNo}</td>
					<td class="text-center">
						<a href="${clubRootUrl}/${club.clubName}/clubMain">
							${club.clubTitle}
						</a>
					</td>
					<td class="text-center">${club.clubIntroduction }</td>
					<td class="text-center">${club.numberOfClubMember}(${club.maxMemberNum})</td>
				</tr>
				<% } %>
			</table>
			
			<% ListPagingHelper pagingHelper = new ListPagingHelper(
					clubListHelper.getTotalPageCount(), 
					clubListHelper.getCurPageNumber(), 10); %>
			<c:set var="pagingHelper" value="<%=pagingHelper%>" />
			<div id="club_list_page">
				<c:if test="${pagingHelper.startPage != 1}">
				<a href="${clubRootUrl}/clubList?page=${pagingHelper.startPage-pagingHelper.pagePerList}">[prev]</a>
				</c:if>
					
				<c:forEach var="i" begin="${pagingHelper.startPage}" end="${pagingHelper.endPage}">
				
				<c:if test="${i == pagingHelper.curPage}">
				<span class="font-bold">${i}</span>
				</c:if>
				
				<c:if test="${i != pagingHelper.curPage}">
				<a href="${clubRootUrl}/clubList?page=${i}">${i}</a>
				</c:if>
				
				</c:forEach>
				<c:if test="${pagingHelper.endPage != pagingHelper.totalPage}">
				<a href="${clubRootUrl}/clubList?page=${pagingHelper.endPage+pagingHelper.pagePerList}">[next]</a>
				</c:if>
			</div>
				
			<% } else { %>
				<div class="error_msg_box">
					<img src="${resourceRootUrl}/static_img/error2.jpg" width="400px" /><br /><br />
					개설된 동아리가 없습니다 ㅠㅠ 
					<a href="${clubRootUrl}/makeClub" style="color: blue;">동아리 만들기</a>
				</div>
			<% } %>
			</div>
			
			<p class="text-center" style="margin-top: 25px;">
				<a href="${DocRootUrl}">
					<img src="${resourceRootUrl}/icon/go-icon-32.png" alt="사이트 메인 이동 버튼" title="사이트 홈으로 이동" />
				</a>
			</p>
		</div>	<!-- main_content End -->
	
	</div>	<!-- allcontents end -->
	
	<div>
		<%@ include file="components/footer.jsp" %>
	</div>
</body>
</html>