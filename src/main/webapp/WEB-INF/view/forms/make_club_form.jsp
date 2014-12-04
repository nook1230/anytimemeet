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
	<title>동아리 만들기</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	
	<style>
		#register_form_area { width: 900px; padding: 10px; margin-top: 30px;
			margin-left: auto; margin-right: auto; border: 1px solid silver; }
	</style>
		
	<script>
		$(document).ready(function() {
			// 대분류 디폴트 선택 = 0
			$('#grandCategoryId').get(0).selectedIndex = 0;
			
			// 이벤트 바인딩: change - 대분류 선택 변경
			$('#grandCategoryId').change(function() {
				var $grandCategoryId = $('#grandCategoryId');
				var $catId = $('#categoryId');
				
				var sel = $grandCategoryId.get(0);
				var opt = sel.options[sel.selectedIndex];
				var selectedValue = opt.value;
				
				if(selectedValue != '-1') {
					// 대분류 카테고리 id가 결정되었을 때
					removeCategories($catId);	// 소분류 카테고리 목록을 정리
					$.getJSON('${clubRootUrl}/getCategories/' + selectedValue, function(result) {
						// Ajax(JSON)를 이용해서 DB로부터 소분류 카테고리 목록을 가져온다
						for(var i = 0; i < result.length; i++) {
							
							$('<option value="' + result[i].categoryId + '">' 
									+ result[i].categoryTitle + '</option>')
							.appendTo($catId);
						}
					});
				} else {
					// 선택된 대분류가 없으면 소분류의 카테고리를 초기화(비운다)
					removeCategories($catId);
				}
			});
		});
		
		// 소분류 카테고리 정리
		function removeCategories($catId) {
			$opts = $catId.children();
			$opts.each(function() {
				if($(this).get(0).value != '-1')	// 비선택 옵션은 제거하지 않음
					$(this).remove();
			});
		}
		
		function checkParams() {
			// 검증할 파라미터들
			var maxMemberNum = $('#maxMemberNum').attr('value');
			
			// 앞뒤 공백 제거
			maxMemberNum = $.trim(maxMemberNum);
				
			if(maxMemberNum.length < 1) {
				alert('동아리 최대 회원 수를 입력해주세요.');
				return false;
			} else if(isNaN(maxMemberNum)) {
				alert('동아리 최대 회원 수에는 숫자만 입력해주세요.');
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
		
		<c:choose>
			
			<c:when test="${checkLogin}">
			<div id="register_form_area">
				<sf:form method="post" modelAttribute="club">
				<table class="basic_table w-80">
					<tr>
						<td>동아리 아이디</td>
						<td>
							<sf:input path="clubName" cssClass="bigInput" /><br />
							<span><sf:errors path="clubName" cssClass="error" /></span>
						</td>
					</tr>
					
					<tr>
						<td>동아리 이름</td>
						<td>
							<sf:input path="clubTitle" cssClass="bigInput" /><br />
							<span><sf:errors path="clubTitle" cssClass="error" /></span>
						</td>
					</tr>
					
					<tr>
						<td>카테고리</td>
						<td>
							<sf:select path="grandCategoryId">
								<option value="-1">대분류 선택</option>
								<sf:options items="${grandCategories}" 
										itemLabel="categoryTitle" itemValue="categoryId" />
							</sf:select>
							&nbsp;
							<sf:select path="categoryId">
								<option value="-1">소분류 선택</option>
							</sf:select>
							
							<span><sf:errors path="grandCategoryId" cssClass="error" /></span>
							<span><sf:errors path="categoryId" cssClass="error" /></span>
						</td>
					</tr>
					
					<tr>
						<td>타입</td>
						<td>
							<sf:radiobutton path="type" label="승인형" value="1" />
							<sf:radiobutton path="type" label="초대형" value="2" />
						</td>
					</tr>
					
					<tr>
						<td>최대 회원 수<br />(max: 100명)</td>
						<td>
							<sf:input path="maxMemberNum" cssClass="smallInput" />명
							<span><sf:errors path="maxMemberNum" cssClass="error" /></span>
						</td>
					</tr>
					
					<tr>
						<td>동아리 설명</td>
						<td>
							<sf:textarea path="clubIntroduction" cssClass="bigInput" style="resize: none; "></sf:textarea>
							<span><sf:errors path="clubIntroduction" cssClass="error" /></span>
						</td>
					</tr>
					
					<tr>
						<td colspan="2" style="text-align: center;">
							<input type="submit" value="동아리 생성" onclick="return checkParams();" />
							<input type="button" value="취소" onclick="location.assign('${DocRootUrl}');" />
						</td>
					</tr>
				</table>
				</sf:form>
			</div>
			</c:when>
			
			<c:otherwise>
			<script>alert('로그인이 필요한 기능입니다.'); location.assign('${DocRootUrl}');</script>
			</c:otherwise>
		</c:choose>
		</div>	<!-- main_content End -->
	</div>	<!-- allcontents end -->
	
	<div>
		<%@ include file="../components/footer.jsp" %>
	</div>
</body>
</html>