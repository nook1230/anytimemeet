<%@page import="com.mamascode.utils.DateFormatUtil"%>
<%@page import="com.mamascode.utils.ListPagingHelper"%>
<%@page import="java.util.List"%>
<%@page import="com.mamascode.utils.ListHelper"%>
<%@page import="com.mamascode.model.ClubArticle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<%
	ListHelper<ClubArticle> articleListHelper = null;
	
	if(request.getAttribute("articleListHelper") != null &&
			request.getAttribute("articleListHelper") instanceof ListHelper) {
		articleListHelper = (ListHelper) request.getAttribute("articleListHelper");
	}
%>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>게시물 읽기</title>
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
			
			<c:when test="${checkLogin ==true && checkMember == true}">
			<div>
				<h3 class="text-center" style="margin-top: 30px;">
					동아리 [<a href="${clubRootUrl}/${club.clubName}/clubMain">${club.clubTitle}</a>]의 게시물 목록:&nbsp;
					페이지 ${articleListHelper.curPageNumber}
				</h3>
				
				<% if(articleListHelper != null && articleListHelper.getList() != null &&
						articleListHelper.getList().size() != 0) {
					List<ClubArticle> articleList = articleListHelper.getList();
				%>
				<table class="basic_table lined_table w-80">
					<tr>
						<th width="10%">#</th>
						<th width="35%">제목</th>
						<th width="20%">작성자</th>
						<th width="25%">작성일시</th>
						<th width="10%">조회수</th>
					</tr>
					
					<% for(int i = 0;  i < articleList.size(); i++) {
						ClubArticle article = articleList.get(i);
					%>
					<c:set var="articleId" value="<%=article.getArticleId()%>" />
					<tr class="text-center">
						<td class="font-small">${articleId}</td>
						<td class="text-left">
							<a href="${clubRootUrl}/article/${club.clubName}/readArticle/${articleId}?redirect_page=${articleListHelper.curPageNumber}"><%=article.getTitle()%></a>
							<span class="font-small color-orange">[<%=article.getRepliesCount()%>]</span>
						</td>
						<td class="font-small">
							<%
							String writeName = 
								(article.getWriterNickname() != null && !article.getWriterNickname().equals("")) ?
								 article.getWriterNickname() : article.getWriterName();
							%>
							<%=writeName%>
						</td>
						<td class="font-very-small"><%=DateFormatUtil.getDatetimeFormat(article.getWriteDate())%></td>
						<td><%=article.getViewCount()%></td>
					</tr>
					<% } %>
				</table>
				
				<% ListPagingHelper pagingHelper = new ListPagingHelper(
						articleListHelper.getTotalPageCount(), 
						articleListHelper.getCurPageNumber(), 10); %>
				<c:set var="pagingHelper" value="<%=pagingHelper%>" />
				<div id="article_list_page">
					<c:if test="${pagingHelper.startPage != 1}">
					<a href="${clubRootUrl}/article/${club.clubName}/articleList?page=${pagingHelper.startPage-pagingHelper.pagePerList}">[prev]</a>
					</c:if>
					
					<c:forEach var="i" begin="${pagingHelper.startPage}" end="${pagingHelper.endPage}">
					
					<c:if test="${i == pagingHelper.curPage}">
					<span class="font-bold">${i}</span>
					</c:if>
					
					<c:if test="${i != pagingHelper.curPage}">
					<a href="${clubRootUrl}/article/${club.clubName}/articleList?page=${i}">${i}</a>
					</c:if>
					
					</c:forEach>
					<c:if test="${pagingHelper.endPage != pagingHelper.totalPage}">
					<a href="${clubRootUrl}/article/${club.clubName}/articleList?page=${pagingHelper.endPage+pagingHelper.pagePerList}">[next]</a>
					</c:if>
				</div>
				
				<% } else { %>
				작성된 동아리 게시물이 없습니다
				<% } %>
			</div>
			
			<p class="text-center" style="margin-top: 25px;">
				<a href="${clubRootUrl}/${club.clubName}/clubMain">
					<img src="${resourceRootUrl}/icon/go-icon-32.png" alt="동아리 메인 이동 버튼" title="동아리 메인으로 이동" />
				</a>
				&nbsp;&nbsp;
				<a href="${clubRootUrl}/article/${club.clubName}/writeNewArticle">
					<img src="${resourcRootUrl}/icon/pencil-icon-32.png" title="게시글 작성" alt="게시글 작성 버튼" />
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