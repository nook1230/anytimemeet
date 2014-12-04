<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ page trimDirectiveWhitespaces="true" %>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />
<c:url var="userRootUrl" value="/user" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>동아리 가입 신청서 작성</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<style>
		#appl_form_area { width: 450px; min-height: 450px; font-size: 1em; padding: 10px; 
				margin-top: 30px; margin-left: auto; margin-right: auto; border: 1px solid silver; }
		#appl_form_area textarea { width: 280px; height: 150px; resize: none; }
		#appl_form_area table { padding: 15px; margin-left: auto; margin-right: auto; }
		#appl_form_area td { padding: 15px; }
	</style>
	
	<script>
		$(document).ready(function() {
			$('input[type="submit"]').click(function(event) {
				event.preventDefault();
				event.stopPropagation();
				
				var txt = $('textarea').attr('value');
				var userName = $('#userName').attr('value');
				
				txt = $.trim(txt); // 앞뒤 공백 제거
				userName = $.trim(userName);
				
				// 파라미터 필터링
				if (txt.length < 1) {
					// 입력이 없는 경우
					alert('초대 말을 입력해주세요.');
					return false;
				} else if (txt > 500) {
					// 입력이 500자 이상인 경우
					alert('초대 말은 500글자 이내로 작성하셔야 합니다');
					return false;
				}
				
				if (userName.length < 1) {
					// 입력이 없는 경우
					alert('초대할 회원이 없습니다.');
					return false;
				}
				
				$('form').get(0).submit();
			});
			
			$('#btn_get_user_name').click(function(event) {
				event.preventDefault();
				event.stopPropagation();
				
				var keyword = $('input[type="text"]').attr('value');
				var userName = '';
				
				if(keyword.length > 0) {
					userName = getUserName(keyword);
					if(userName != null && userName != '') 
						alert(userName + "을 초대!");
					else 
						alert('초대할 회원이 없습니다!');
					
					$('#userName').attr('value', userName);
				} else {
					alert('초대할 회원의 이름이나 이메일을 입력해주세요');
				}
			});
		});
		
		function getUserName(keyword) {
			var ajaxUrl = '${userRootUrl}' + '/search/getUserName?keyword=' + keyword;
			var userName = '';
			
			$.ajax({url : ajaxUrl, method : 'get', async : false,
				success : function(result) {
					if(result != null && result != '')
						userName = result;
				}, error : function() {
					// ajax error
					alert("ajax error: 사용자 이름 가져오기 실패");
				}
			});
			
			return userName;
		}
	</script>
</head>
<body>
	<%@ include file="../components/header.jsp" %>
	
	<div id="allcontents">
		<div id="main_contents">
			<div id="appl_form_area">
			<c:choose>
				<c:when test="${checkMaster == true}">
				<form method="post">
					<input type="hidden" name="userName" id="userName" />				
					<table>
						<tr>
							<td colspan="2" class="text-center" 
									style="font-size: 1.2em; font-weight: bold;">
								동아리 가입 초대
							</td>
						</tr>
						
						<tr>
							<td>회원</td>
							<td>
								<input type="text" name="keyword" placeholder="아이디 혹은 이메일" />
								&nbsp;<button id="btn_get_user_name">확인</button><br />
								<span></span>
							</td>
						</tr>
						
						<tr>
							<td>코멘트</td>
							<td>
								<textarea name="comment"></textarea>
							</td>
						</tr>
						
						<tr>
							<td colspan="2" class="text-center">
								<input type="submit" value="초대" />
								<input type="button" value="취소" onclick="history.go(-1);" />
							</td>
						</tr>
					</table>
				</form>
				</c:when>
				
				<c:otherwise>
				<script>alert('부적절한 접근입니다.'); history.go(-1);</script>
				</c:otherwise>
				
			</c:choose>
			
			
			</div>
		</div>	<!-- main_content End -->
	</div>	<!-- allcontents end -->

	<%@ include file="../components/footer.jsp" %>	
</body>
</html>