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
	<title>모임 내용 수정</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/custom_js.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/webeditor.js"></script>
	<script>
		$(document).ready(function() {
			initDoc();
			
			$('#textBox').get(0).innerHTML = $('#contentText').attr('value');
			
			$('#submit_btn').click(function(event) {
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
				
				if(checkParamters()) {
					$('#reg_form').submit();	// 파라미터 검증이 완료되면 폼 전송
				}
			});
		});
		
		function checkParamters() {
			// 검증할 파라미터들
			var title = $('#title').attr('value');
			var location = $('#location').attr('value');
			var introduction = $('#contentText').attr('value');
			
			// 앞뒤 공백 제거
			title = $.trim(title);
			location = $.trim(location);
			introduction = $.trim(introduction);
					
			if(title.length < 1) {
				alert('제목을 입력해주세요.');
				return false;
			}
			
			if(location.length < 1) {
				alert('장소를 입력해주세요.');
				return false;
			}
			
			if(introduction.length < 1) {
				alert('모임 소개를 입력해주세요.');
				return false;
			}
			
			return true;
		}
	</script>
	
	<style>
		#register_form_area { width: 900px; padding: 10px; margin-top: 15px;
			margin-left: auto; margin-right: auto; border: 1px solid silver; }
		.dateInput { }
		a.a_btn { text-decoration: none; }
		#textBox { overflow: scroll; width: 550px; height: 500px; border: 1px solid gray; }
		#contentText { display: none; }
	</style>
</head>
<body>
	<%@ include file="../components/header.jsp" %>
	
	<div id="allcontents">
		
		<div id="main_contents">
		
		<c:choose>
			
			<c:when test="${isLoginUser == true}">
				<c:if test="${checkAuth == true}">
				<div id="register_form_area">
				<form id="reg_form" action="${clubRootUrl}/meeting/${club.clubName}/modifyMeeting/${meeting.meetingId}" method="post">
				<table class="basic_table w-80">
					<tr>
						<td>모임 타이틀</td>
						<td>
							<input type="text" id="title" name="title" class="bigInput" value="${meeting.title}" />
						</td>
					</tr>
					
					<tr>
						<td>개최인</td>
						<td>
							${meeting.administratorName}
						</td>
					</tr>
					
					<tr>
						<td>장소</td>
						<td>
							<input type="text" id="location" name="location" class="bigInput" value="${meeting.location}" />
						</td>
					</tr>
					
					<tr>
						<td>설명</td>
						<td>
							<div contenteditable="true" id="textBox"></div>
							<textarea name="introduction" id="contentText">${meeting.introduction}</textarea><br />
							<span class="font-small color-red">html 태그를 넣을 때는 요소의 크기에 주의해주십시오</span><br />
						</td>
					</tr>
					
					<tr>
						<td></td>
						<td style="text-align: center;">
							<input type="button" value="등록" id="submit_btn" />
							<input type="button" value="취소" onclick="history.go(-1);" />
							<span class="pull-right">
								<input type="checkbox" name="switchMode" id="switchBox"
									onchange="setDocMode(this.checked);" />
								<label for="switchBox">html 편집</label>
							</span>
						</td>
					</tr>
				</table>	
				</form>
				</div>
				</c:if>
				
				<c:if test="${checkAuth == false}">
					<div class="error_msg_box">
						<img src="${resourceRootUrl}/static_img/hul.jpg" width="300px" />
						<p class="error">모임 개설 권한이 없습니다.</p>
						<p><a href="${docRootUrl}">홈으로 가기</a></p>
					</div>
				</c:if>
			</c:when>
			
			<c:otherwise>
			<div class="error_msg_box">
				<img src="${resourceRootUrl}/static_img/hul.jpg" width="300px" />
				<p class="error">로그인이 필요한 기능입니다.</p>
				<p><a href="${docRootUrl}">홈으로 가기</a></p>
			</div>
			</c:otherwise>
		</c:choose>
		</div>	<!-- main_content End -->
	</div>	<!-- allcontents end -->
	
	<div>
		<%@ include file="../components/footer.jsp" %>
	</div>
</body>
</html>