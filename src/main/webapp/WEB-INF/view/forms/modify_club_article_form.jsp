<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ page trimDirectiveWhitespaces="true" %>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>게시물 수정</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/webeditor.js"></script>
	<style>
		#write_form_area { width: 600px; min-height: 650px; font-size: 1em; padding: 10px; 
			margin-top: 30px; margin-left: auto; margin-right: auto; border: 1px solid silver; }
		#write_form_area h2 { text-align: center; }
		#write_form_area input[type='text'], input[type='password'] { width: 400px; height: 30px; }
		#write_form_area textarea { width: 400px; height: 300px; resize: none; }
		#write_form_area table { padding: 15px; margin-left: auto; margin-right: auto; }
		#write_form_area td { padding: 15px; }
		#textBox { overflow: scroll; width: 550px; height: 500px; border: 1px solid gray; }
		#contentText { display: none; }
	</style>
	<script>
		$(document).ready(function() {
			initDoc();
			
			$('#textBox').get(0).innerHTML = $('#contentText').attr('value');
			
			$('#btnSubmit').click(function(event) {
				event.preventDefault();
				event.stopPropagation();
				
				var $swtichBox = $('#switchBox');
				var content = "";
				
				if($swtichBox.attr('checked') == undefined) {
					setDocMode(true);
					setDocMode(false);
					content = $('#textBox').get(0).innerHTML;
				} else if($swtichBox.attr('checked') == 'checked') {
					setDocMode(false);
					content = $('#textBox').get(0).innerHTML;
					setDocMode(true);
				}
				
				$('#contentText').attr('value', content);
				
				$('form').get(0).submit();
			});
		});
	</script>
</head>
<body>
	<%@ include file="../components/header.jsp" %>
	
	<div id="allcontents">
		
		<div id="main_contents">
		
		<c:choose>
			
			<c:when test="${checkValidUser == true && clubArticle.articleId != 0}">
			<div id="write_form_area">
			<sf:form method="post" modelAttribute="clubArticle">
				<table>
					<tr>
						<td>동아리 이름</td>
						<td>${clubArticle.clubName}</td>
					</tr>
					
					<tr>
						<td>작성자</td>
						<td>${clubArticle.writerName}</td>
					</tr>
					
					<tr>
						<td>제목</td>
						<td>
							<sf:input path="title" value="${clubArticle.title}" /><br />
							<sf:errors path="title" cssClass="error" />
						</td>
					</tr>
					
					<tr>
						<td colspan="2">
							<div contenteditable="true" id="textBox"></div>
							<sf:textarea path="content" id="contentText" value="${clubArticle.content}" /><br />
							<sf:errors path="content" cssClass="error" /><br />
							<span class="font-small color-red">html 태그를 넣을 때는 요소의 크기에 주의해주십시오</span><br />
						</td>
					</tr>
					
					<tr>
						<td></td>
						<td style="text-align: center;">
							<input type="button" value="등록" id="btnSubmit" />
							<input type="button" value="취소" onclick="history.go(-1);" />
							<span class="pull-right">
								<input type="checkbox" name="switchMode" id="switchBox"
									onchange="setDocMode(this.checked);" />
								<label for="switchBox">html 편집</label>
							</span>
						</td>
					</tr>
				</table>
			</sf:form>
			</div>	<!-- write_form_area End -->
			</c:when>
			
			<c:when test="${checkValidUser == true && article.articleId == 0}">
			<script>alert('게시물이 존재하지 않습니다.'); location.assign('${DocRootUrl}');</script>
			</c:when>
			
			<c:when test="${checkValidUser == false}">
			<script>alert('부적절한 접근입니다.'); location.assign('${DocRootUrl}');</script>
			</c:when>
		</c:choose>
		
		</div>	<!-- main_content End -->
	</div>	<!-- allcontents end -->
	
	<div>
		<%@ include file="../components/footer.jsp" %>
	</div>
</body>
</html>