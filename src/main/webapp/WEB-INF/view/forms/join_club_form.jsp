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
				
				txt = $.trim(txt); // 앞뒤 공백 제거

				// 파라미터 필터링
				if (txt.length < 1) {
					// 입력이 없는 경우
					alert('가입 인사를 입력해주세요.');
					$('textarea').attr('value', '');
					return false;
				} else if (txt > 500) {
					// 입력이 500자 이상인 경우
					alert('가입 인사은 500글자 이내로 작성하셔야 합니다');
					return false;
				}
				
				$('form').get(0).submit();
			});
		});
	</script>
</head>
<body>
	<%@ include file="../components/header.jsp" %>
	
	<div id="allcontents">
		<div id="main_contents">
			<div id="appl_form_area">
			<c:choose>
				
				<c:when test="${checkLogin == true}">
				<form action="${clubRootUrl}/joinClub" method="post">
					<input type="hidden" name="clubName" value="${joinInfo.clubName}" />
					<input type="hidden" name="userName" value="${joinInfo.userName}" />
					
					<table>
						<tr>
							<td colspan="2" class="text-center" 
									style="font-size: 1.2em; font-weight: bold;">
								동아리 가입 신청
							</td>
						</tr>
						
						<tr>
							<td>코멘트</td>
							<td>
								<textarea name="comment">${joinInfo.comment}</textarea>
							</td>
						</tr>
						
						<tr>
							<td colspan="2" class="text-center">
								<input type="submit" value="신청" />
								<input type="button" value="취소" onclick="history.go(-1);" />
							</td>
						</tr>
					</table>
				</form>
				</c:when>
				
				<c:otherwise>
				<script>alert('로그인이 필요한 기능입니다.'); history.go(-1);</script>
				</c:otherwise>
				
			</c:choose>
			
			
			</div>
		</div>	<!-- main_content End -->
	</div>	<!-- allcontents end -->

	<%@ include file="../components/footer.jsp" %>	
</body>
</html>