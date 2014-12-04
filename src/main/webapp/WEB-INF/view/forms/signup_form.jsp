<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ page trimDirectiveWhitespaces="true" %>
<c:url var="docRootUrl" value="/" />
<c:url var="userRootUrl" value="/user" />
<c:url var="resourceRootUrl" value="/res" />
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>회원가입</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<style>
		#signup_form_area { width: 450px; min-height: 450px; font-size: 1em; padding: 10px; 
			margin-left: auto; margin-right: auto; margin-top: 20px; border: 1px solid silver; }
		#signup_form_area h2 { text-align: center; }
		#signup_form_area input[type='text'], #signup_form_area input[type='password'] { width: 280px; height: 30px; }
		#signup_form_area input[type='checkbox'] { text-align: left; }
		#signup_form_area textarea { width: 280px; height: 50px; resize: none; }
		#signup_form_area table { padding: 15px; margin-left: auto; margin-right: auto; }
		#signup_form_area td { padding: 15px; }
		.btn_duplicate_check { font-size: 0.8em; padding: 0;}
	</style>
	
	<script>
		var bUserName = false;
		var bEmail = false;
		var $userNameCheckMsg;
		var $emailCheckMsg;
		
		$(document).ready(function() {
			$userNameCheckMsg = $('#userNameCheckMsg');
			$emailCheckMsg = $('#emailCheckMsg');
			
			// 아이디 중복 체크 버튼 이벤트 바인딩
			$('#btn_check_user_name').click(function(event) {
				event.preventDefault();
				event.stopPropagation();
				
				// 검증할 사용자 계정 아이디가 입력되었는지 체크
				var userName = $('#userName').attr('value');
				
				userName = $.trim(userName); // 앞뒤 공백 제거
				
				if(userName.length == 0) {
					alert('검사할 아이디를 입력해주세요.');
					return;
				} else if(userName.length < 4 || userName.length > 20) {
					alert('사용자 계정 아이디는 4글자 이상 20글자 이하로 입력해주세요.');
					return;
				}			
				
				bUserName = checkUserName(); // 중복 체크
			});
			
			// 이메일 중복 체크 버튼 이벤트 바인딩
			$('#btn_check_email').click(function(event) {
				event.preventDefault();
				event.stopPropagation();
				
				// 검증할 이메일 주소가 입력되었는지 체크
				var email = $('#email').attr('value');
				
				email = $.trim(email); // 앞뒤 공백 제거
				
				if(email.length == 0) {
					alert('검사할 이메일 주소를 입력해주세요.');
					return;
				} else if(email.length < 4 || email.length > 100) {
					alert('이메일 주소는 4글자 이상 100글자 이하로 입력해주세요.');
					return;
				}			
				
				bEmail = checkEmailAddress(); // 중복 체크
			});
			
			// 폼 제출 버튼 이벤트 바인딩
			$('#btn_submit').click(function(event) {
				event.preventDefault();
				event.stopPropagation();
				
				if(checkParamters())
					$('#signup_form').submit();
			});
		});
		
		function checkDuplicatedParam(paramId, ajaxUrl, successMsg, failMsg, $msgSpan) {
			// 결과값
			var retValue = false;
			
			// 파라미터
			var param = $('#' + paramId).attr('value');
			ajaxUrl += param; // 파라미터 설정
			
			// 동기 방식의 ajax 요청
			$.ajax({
				url : ajaxUrl, method : 'get', async: false,
				success : function(result) {
					if(!result) {
						// 성공!
						$msgSpan.text(successMsg);
						$msgSpan.addClass('color-green');
						$msgSpan.removeClass('color-red');
						retValue = true;
					} else {
						// 실패: result가 true이면 중복
						$msgSpan.text(failMsg);
						$msgSpan.addClass('color-red');
						$msgSpan.removeClass('color-green');
						retValue = false;
					}
				},
				error : function() {
					// ajax error
					alert("ajax 요청 실패!");
				}
			});
			
			return retValue;
		}
		
		// 아이디 중복 체크
		function checkUserName() {
			var paramId = "userName";
			var ajaxUrl = "${userRootUrl}/checkUserName?userName=";
			var successMsg = "사용할 수 있는 아이디입니다";
			var failMsg = "이미 존재하는 아이디입니다";
			
			return checkDuplicatedParam(paramId, ajaxUrl, successMsg, failMsg, $userNameCheckMsg);
		}
		
		// 이메일 중복 체크
		function checkEmailAddress() {
			var paramId = "email";
			var ajaxUrl = "${userRootUrl}/checkEmail?email=";
			var successMsg = "사용할 수 있는 이메일 주소입니다";
			var failMsg = "이미 존재하는 이메일 주소입니다";
			
			return checkDuplicatedParam(paramId, ajaxUrl, successMsg, failMsg, $emailCheckMsg);
		}
		
		// 파라미터 검증
		function checkParamters() {
			// 검증할 파라미터들
			var userName = $('#userName').attr('value');
			var passwd = $('#passwd').attr('value');
			var passwd2 = $('#passwd2').attr('value');
			var email = $('#email').attr('value');
			
			// 앞뒤 공백 제거
			userName = $.trim(userName);
			passwd = $.trim(passwd);
			passwd2 = $.trim(passwd2);
			email = $.trim(email);
					
			if(userName.length < 4 || userName.length > 20) {
				alert('사용자 계정 아이디는 4글자 이상 20글자 이하로 입력해주세요.');
				return false;
			}
			
			if(passwd.length < 4 || passwd.length > 20) {
				alert('비밀번호는 4글자 이상 20글자 이하로 입력해주세요.');
				return false;
			}
			
			if(passwd != passwd2) {
				alert('입력한 비밀번호 두 개가 서로 다릅니다.');
				return false;
			}
			
			if(email.length > 100) {
				alert('이메일은 100글자 이하로 작성해주세요.');
				return false;
			}
			
			if(!bUserName) {
				alert('사용자 계정 아이디 중복 체크를 해주세요.');
				return false;
			}
			
			if(!bEmail) {
				alert('이메일 계정 중복 체크를 해주세요.');
				return false;
			}
			
			return true;
		}
	</script>
</head>
<body>
	<%@ include file="../components/header.jsp" %>
	
	<div id="allcontents">
		
		<div id="main_contents">
			<c:if test="${sessionScope.login == 'loginSuccess'}">
				<script>alert('로그인 상태입니다'); location.assign('${docRootUrl}');</script>
			</c:if>
			
			<div id="signup_form_area">
				<h2>회원가입</h2>
				<sf:form id="signup_form" action="${userRootUrl}/signup" method="post" modelAttribute="user">
					<table>
						<tr>
							<td>
								<sf:input id="userName" path="userName" placeholder="아이디" />&nbsp;
								<button class="btn_duplicate_check" id="btn_check_user_name">아이디 체크</button><br />
								<span id="userNameCheckMsg"></span>
								<sf:errors path="userName" cssClass="error" />
							</td>
						</tr>
						
						<tr>
							<td>
								<sf:password id="passwd" path="passwd" placeholder="비밀번호" /><br />
								<sf:errors path="passwd" cssClass="error" />
							</td>
						</tr>
						
						<tr>
							<td>
								<sf:password id="passwd2" path="passwd2" placeholder="비밀번호 재입력" /><br />
								<sf:errors path="passwd2" cssClass="error" />
							</td>
						</tr>
						
						<tr>
							<td>
								<sf:input id="email" path="email" placeholder="이메일" />&nbsp;
								<button class="btn_duplicate_check" id="btn_check_email">이메일 체크</button><br />
								<span id="emailCheckMsg"></span>
								<sf:errors path="email" cssClass="error" />
							</td>
						</tr>
						
						<tr>
							<td style="text-align: center;">
								<input type="submit" value="회원가입" id="btn_submit" />
								<input type="button" value="취소" onclick="location.assign('${docRootUrl}');" />
							</td>
						</tr>
					</table>
				</sf:form>
			</div>	<!-- signup_form_area End -->
			
		</div>	<!-- main_content End -->
	</div>	<!-- allcontents end -->
	
	<div>
		<%@ include file="../components/footer.jsp" %>
	</div>
</body>
</html>