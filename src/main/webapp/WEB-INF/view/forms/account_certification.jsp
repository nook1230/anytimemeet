<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ page trimDirectiveWhitespaces="true" %>
<c:url var="resourceRootUrl" value="/res" />
<c:url var="DocRootUrl" value="/" />
<c:url var="clubRootUrl" value="/club" />
<c:url var="userRootUrl" value="/user" />

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>계정 인증</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<style>
	</style>
	
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.0/jquery.min.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/custom_js.js"></script>
	<script>
		function certifyAccount(ajaxUrl, userName, certificationKey) {
			// Ajax 요청 데이터(댓글 입력 정보) - content는 한글 입력을 위해 utf-8로 인코딩
			var requestData = {
				"userName" : userName,
				"certiKey" : certificationKey
			};
			
			$.post(ajaxUrl, requestData, function(result) {
				if (result) {
					alert('인증 성공!');
					location.assign('${DocRootUrl}');
				} else {
					// 댓글 입력 실패
					alert('오류: 인증 실패!');
					location.assign('${DocRootUrl}');
				}
			});
		}
	</script>
</head>
<body>
	<div id="allcontents">
		<div id="main_contents">
			<c:choose>
			
			<c:when test="${certificationSuccess == false}">
			<script>alert('잘못된 인증 정보! 이미 인증되었거나 인증키가 잘못 되었습니다.'); location.assign('${DocRootUrl}');</script>
			</c:when>
			
			<c:otherwise>
			<div class="text-center" style="margin-top: 100px;">
				<img src="${resourceRootUrl}/static_img/wait.png" />
			</div>
			
			<c:set var="ajaxUrl" value="${userRootUrl}/certify" />
			<script>certifyAccount('${ajaxUrl}', '${user.userName}', '${user.certificationKey}');</script>
			</c:otherwise>
			
			</c:choose>
		</div>	<!-- main_contents end -->
	</div>	<!-- allcontents end -->
	
	<div>
		<%@ include file="../components/footer.jsp" %>
	</div>
</body>
</html>