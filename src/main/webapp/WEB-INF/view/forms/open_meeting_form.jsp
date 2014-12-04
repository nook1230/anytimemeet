<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="clubRootUrl" value="/club" />
<c:url var="docRootUrl" value="/" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>새 모임 열기</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/custom_js.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/meeting_date.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/calendar.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/webeditor.js"></script>
		
	<script>
	$(document).ready(function() {
		var $calDiv = $("<div id='calendar' class='text-center'></div>");
		$calDiv.appendTo($('body'));
		$calDiv.toggleClass('hidden');
		
		initializeCalendar($calDiv);
		
		initDoc();
		
		$('#textBox').get(0).innerHTML = $('#contentText').attr('value');
		
		$('#submit_btn').click(function(event) {
			event.preventDefault();
			event.stopPropagation();
			
			// 페이지 로드
			// 모임 날짜 정보를 JSON 문자열로 만들어서 서버로 전송한다 
			var JsonMeetingDates = makeJsonFormatString();
				
			// 히든 필드
			var JsonMeetingDatesObj = $('#JsonMeetingDates')[0];
			JsonMeetingDatesObj.value = JsonMeetingDates;
			
			// 본문 설정
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
		
		$('#calendar_btn').click(function(event) {
			var left = event.clientX;
			var top = event.clientY;
			
			left -= 100; top += 20;
			
			toggleCalendar(left, top);
		});
	});
	
	function checkParamters() {
		// 검증할 파라미터들
		var title = $('#title').attr('value');
		var location = $('#location').attr('value');
		var introduction = $('#contentText').attr('value');
		var $meetingDates = $('#meetingDates');
		
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
		
		if($meetingDates.children().size() < 1) {
			alert('모임 날짜을 하나 이상 지정해주세요.');
			return false;
		}
		
		return true;
	}
	</script>
	
	<style>
		#calendar { 
			width: 250px; height: 200px; 
			font-size: 0.9em; padding: 5px;
			border: 1px solid black;
			background-color: white;
		}
		#calController { margin-top: 20px; margin-bottom: 15px; }
		#calendar td { text-align: center; width: 2em; }
		#register_form_area { width: 900px; padding: 10px; margin-top: 15px;
			margin-left: auto; margin-right: auto; border: 1px solid silver; }
		.dateInput { }
		a.a_btn { text-decoration: none; }
		.hidden { display: none; }
		
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
				<form id="reg_form" action="${clubRootUrl}/meeting/${club.clubName}/openMeeting" method="post">
					<input type="hidden" name="JsonMeetingDates" id="JsonMeetingDates" />
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
							<input type="hidden" name="administratorName" value="${meeting.administratorName}" />
						</td>
					</tr>
					
					<tr>
						<td>모임 날짜</td>
						<td>
							<p><input id="year" type="text" class="smallInput" readonly />년&nbsp;
							<input id="month" type="text" class="smallInput" readonly />월&nbsp;
							<input id="date" type="text" class="smallInput" readonly />일&nbsp;
							<img id="calendar_btn" class="btn" src="${resourcRootUrl}/icon/calendar-icon-24.png" 
								style="vertical-align: bottom;" alt="달력 보기 버튼" title="달력 보기" />
							&nbsp;
							<select id="hour">
								<c:forEach var="i" begin="1" end="12">
								<option><c:if test="${i < 10}">0</c:if>${i}</option>
								</c:forEach>
							</select>:
							<select id="minute">
								<c:forEach var="i" begin="0" end="59">
								<option><c:if test="${i < 10}">0</c:if>${i}</option>
								</c:forEach>
							</select>
							<select id="ampm"><option>AM</option><option>PM</option></select>
							&nbsp;
							<span id="addDatetime"
									onclick="addMeetingDate('#year', '#month', '#date', '#hour', '#minute', '#ampm');">
								<img src="${resourceRootUrl}/icon/plus-icon-16.png"
									title="새 날짜 추가" alt="날짜 추가 버튼" />
							</span>
							</p>
							
							<p id="meetingDates"></p>
						</td>
					</tr>
					
					<tr>
						<td>장소</td>
						<td>
							<input type="text" id="location" name="location" class="bigInput" 
							   placeholder="차후 맵을 이용해 구현할 것" value="${meeting.location}" />
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
						<img src="${resourcRootUrl}/static_img/hul.jpg" width="300px" />
						<p class="error">모임 개설 권한이 없습니다.</p>
						<p><a href="${docRootUrl}">홈으로 가기</a></p>
					</div>
				</c:if>
			</c:when>
			
			<c:otherwise>
			<div class="error_msg_box">
				<img src="${resourcRootUrl}/static_img/hul.jpg" width="300px" />
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