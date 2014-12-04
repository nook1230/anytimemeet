<%@page import="com.mamascode.utils.DateFormatUtil"%>
<%@page import="java.util.List"%>
<%@page import="com.mamascode.utils.ListPagingHelper"%>
<%@page import="com.mamascode.model.Reply"%>
<%@page import="com.mamascode.utils.ListHelper"%>
<%@page import="com.mamascode.model.ClubArticle"%>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />
<% 
	ClubArticle clubArticle = (ClubArticle) request.getAttribute("article");
	List<Reply> articleReplyList = null;
	if(request.getAttribute("articleReplyList") != null)
		articleReplyList = (List<Reply>) request.getAttribute("articleReplyList");
%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>게시물 읽기</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/custom_js.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/article_reply.js"></script>
	<style>
		#article_table { }
		td.article_content { min-height: 100px; padding: 15px; max-width: 800px; }
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
			var writeAjaxUrl = '${clubRootUrl}/article/${club.clubName}/writeReply/${article.articleId}';
			// read Ajax url
			var readAjaxUrl = '${clubRootUrl}/article/${club.clubName}/readReplies/${article.articleId}';
			// delete url
			var deleteUrl = '${clubRootUrl}/article/${club.clubName}/deleteReply/';
			
			$('#btnReplyWrite').click(function(event) {
				event.preventDefault();
				event.stopPropagation();
				
				writeReply(writeAjaxUrl, '${sessionScope.loginUserName}', readAjaxUrl, deleteUrl);
			});
		});
		
	</script>
</head>
<body onload="resizeImagesWidth($('.article_content'), 800);">
	<%@ include file="components/header.jsp" %>
	
	<div id="allcontents">
		
		<div id="main_contents">
			
			<c:choose>
			
			<c:when test="${checkLogin == true && checkMember == true && article.articleId != 0 && club.active == true}">
			<div>				
				<h3 class="text-center" style="margin-top: 30px;">동아리 게시글 읽기</h3>
				<table id="article_table" class="basic_table lined_table w-80" style="margin-top: 20px;">
					<%
						String writeName = "";
						if(clubArticle.getWriterNickname() != null && !clubArticle.getWriterNickname().equals(""))
							writeName = String.format("%s(%s)", clubArticle.getWriterNickname(), clubArticle.getWriterName());
						else
							writeName = clubArticle.getWriterName();
					%>
					<tr>
						<td>동아리</td>
						<td><a href="${clubRootUrl}/${club.clubName}/clubMain">${club.clubTitle}</a></td>
						<td>조회수</td>
						<td>${article.viewCount}</td>
					</tr>
					
					<tr>
						<td>제목</td>
						<td colspan="3">${article.title}</td>
					</tr>
					
					<tr>
						<td width="20%">작성자</td>
						<td width="30%">
							
							<%=writeName%>
						</td>
						<td width="20%">작성일시</td>
						<td width="30%"><%=DateFormatUtil.getDatetimeFormat(clubArticle.getWriteDate())%></td>
					</tr>
					
					<tr>
						<td colspan="4" class="article_content">
							${article.content}
						</td>
					</tr>
					
					<tr>
						<c:set var="userProfilePicture" value="${writer.profilePicture}" />
						<c:if test="${userProfilePicture != null}">
						<c:set var="userProfilePictureSrcPath" value="${resourceRootUrl}/user_files/user_profile_pictures/${userProfilePicture.userName}_${userProfilePicture.fileName}" />
						</c:if>
						<c:if test="${userProfilePicture == null}">
						<c:set var="userProfilePictureSrcPath" value="${resourceRootUrl}/static_img/default_profile.jpg" />
						</c:if>
						<td colspan="4" class="text-right">
							<span><%=writeName%></span>&nbsp;
							<img src="${userProfilePictureSrcPath}" alt="user profile image" width="120px" height="160px" title="${writer.userIntroduction}" />
						</td>
					</tr>
					
					<c:if test="${sessionScope.loginUserName == article.writerName || sessionScope.loginUserName == club.masterName}">
					<tr>
						<td colspan="4" class="text-right">
							<c:set var="deletePopupUrl" value="${clubRootUrl}/article/${club.clubName}/deleteArticle/${article.articleId}?redirect_page=${redirectPage}" />
							<span class="btn" onclick="popup('${deletePopupUrl}', 400, 200, 600, 200, 'new_popup_delete_article')">
								<img src="${resourceRootUrl}/icon/trash-can-icon-16.png" alt="게시글 삭제 버튼" title="게시글 글 삭제" />
							</span>&nbsp;
							<c:if test="${sessionScope.loginUserName == article.writerName}">
							<span class="btn"><a href="${clubRootUrl}/article/${club.clubName}/modifyArticle/${article.articleId}?redirect_page=${redirectPage}">
								<img src="${resourceRootUrl}/icon/pencil-icon-16.png" alt="게시글 글 수정 버튼" title="게시글 글 수정" />
							</a></span>&nbsp;
							</c:if>
						</td>
					</tr>
					</c:if>
				</table>
			</div>
			
			<div id="reply_area" class="w-80 pull-center">
				<div id="write_reply_form">
					<form>
						<textarea id="txtReplyContent" name="replyContent"></textarea><br />
						<button id="btnReplyWrite" onclick="">댓글쓰기</button>
					</form>
				</div>
				
				<div id="replies_list">
					<table id="replies_table" class="w-100">
						<% for(Reply articleReply : articleReplyList) { %>
						<c:set var="articleReply" value="<%=articleReply%>" />
						<tr class="font-small reply-header">
							<td width="15%" class="font-bold">
								<c:set var="nicknameCheck" value="${articleReply.writerNickname != null && articleReply.writerNickname != ''}" />
								<c:if test="${nicknameCheck == true}">${articleReply.writerNickname}</c:if>
								<c:if test="${nicknameCheck == false}">${articleReply.writerName}</c:if>
							</td>
							<td width="20%" class="font-very-small">
								<%=DateFormatUtil.getDatetimeFormat(articleReply.getWriteDate())%>
							</td>
							<td width="5%" class="font-very-small btn" 
								onclick="deleteReply('${clubRootUrl}/article/${club.clubName}/deleteReply/', ${articleReply.replyId}, '${clubRootUrl}/article/${club.clubName}/readReplies/${article.articleId}');">[삭제]</td>
							<td></td>
						</tr>
						<tr class="reply-body">
							<td colspan="4">${articleReply.content}</td>
						</tr>
						<% } %>
					</table>
				</div>
			</div>
			
			<div class="text-center" style="margin-top: 30px;">
				<a href="${clubRootUrl}/${club.clubName}/clubMain">
					<img src="${resourceRootUrl}/icon/go-icon-32.png" alt="동아리 메인 이동 버튼" title="동아리 메인으로 이동" />
				</a>&nbsp;&nbsp;
				<a href="${clubRootUrl}/article/${club.clubName}/articleList?page=${redirectPage}">
					<img src="${resourceRootUrl}/icon/archive-icon-32.png" alt="게시글 목록 이동 버튼" title="게시글 목록으로 이동" />
				</a>
			</div>
			</c:when>
			
			<c:when test="${checkLogin == true && checkMember == true && article.articleId == 0}">
			<script>
				alert('게시물이 존재하지 않습니다.');
				location.assign('${DocRootUrl}');
			</script>
			</c:when>
			
			<c:when test="${checkLogin == true && checkMember == true && article.articleId != 0 && club.active == false}">
			<script>
				alert('비활성화된 동아리입니다. 게시물을 볼 수 없습니다.');
				location.assign('${DocRootUrl}');
			</script>
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