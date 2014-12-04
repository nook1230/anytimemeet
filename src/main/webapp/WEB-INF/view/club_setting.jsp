<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="sf" uri="http://www.springframework.org/tags/form"%>
<%@ page trimDirectiveWhitespaces="true" %>
<c:url var="docRootUrl" value="/" />
<c:url var="userRootUrl" value="/user" />
<c:url var="clubRootUrl" value="/club" />
<c:url var="resourceRootUrl" value="/res" />
	
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title>동아리 설정</title>
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic.css" media="all" />
	<link rel="stylesheet" type="text/css" href="${resourceRootUrl}/css/basic_style.css" media="all" />
	<script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.7.2/jquery.min.js"></script>
	<script type="text/javascript" src="${resourceRootUrl}/js/custom_js.js"></script>
	<style>
		#form_modify_club_info_area { width: 900px; padding: 10px; margin-top: 30px;
			margin-left: auto; margin-right: auto; border: 1px solid silver; }
			
		#form_inactivate_club_area { width: 900px; padding: 10px; margin-top: 30px;
			margin-left: auto; margin-right: auto; border: 1px solid silver; }
			
		#link_area { width: 900px; padding: 10px; margin-top: 30px;
			margin-left: auto; margin-right: auto; border: 1px solid silver; }
	</style>
	
	<script>	
	$(document).ready(function() {
		// (현재 선택된 최상위 카테고리의) 하위 카테고리 가져오기
		var $grandCategoryId = $('#grandCategoryId');
		var $catId = $('#categoryId');
		var sel = $grandCategoryId.get(0);
		
		getCategories($catId, sel.options[sel.selectedIndex].value);
		
		// 입력 요소들 초기 상태 설정
		var bindingResultError = '${bindingResultError}';
		if(bindingResultError)
			$('#modify').attr('checked', 'checked');
		
		toggleInputs($('#modify').attr('checked'));
		
		// 이벤트 바인딩: change - 대분류 선택 변경
		$('#grandCategoryId').change(function() {
			var opt = sel.options[sel.selectedIndex];
			var selectedValue = opt.value;
			
			if(selectedValue != '-1') {
				// 대분류 카테고리 id가 결정되었을 때
				removeCategories($catId);	// 소분류 카테고리 목록을 정리
				getCategories($catId, selectedValue);
			} else {
				// 선택된 대분류가 없으면 소분류의 카테고리를 초기화(비운다)
				removeCategories($catId);
			}
		});
	
		// 이벤트 바인딩: #btn_setting_submit 클릭
		$('#btn_setting_submit').click(function(event) {
			event.preventDefault();
			event.stopPropagation();
			
			if(checkParams())
				$('#form_modify_club_info').submit();
		});
		
		// 이벤트 바인딩: #modify 상태 변경
		$('#modify').change(function() {
			var checked = $('#modify').attr('checked');
			toggleInputs(checked);
		});
		
		// 이벤트 바인딩: #btn_inactivate_club 클릭
		$('#btn_inactivate_club').click(function(event) {
			event.preventDefault();
			event.stopPropagation();
			
			var url = "${clubRootUrl}/admin/${club.clubName}/setting/inactivateClub";
			popup(url, 400, 300, 600, 200, 'new_popup_inactivate_club');
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
	
	function getCategories($catId, selectedValue) {
		$.getJSON('${clubRootUrl}/getCategories/' + selectedValue, function(result) {
			// Ajax(JSON)를 이용해서 DB로부터 소분류 카테고리 목록을 가져온다
			for(var i = 0; i < result.length; i++) {
				var html = '<option value="' + result[i].categoryId + '">' 
					+ result[i].categoryTitle + '</option>';
				
				var $option = $(html);
				$option.appendTo($catId);
				
				if(result[i].categoryId == '${club.categoryId}')
					$option.get(0).selected = true;
			}
		});
	}
	
	function toggleInputs(checked) {
		if(checked) {
			$('.clubInfo').css('display', 'none');
			$('.clubSettingInput').css('display', 'table-row');
			$('.clubSettingInputInline').css('display', 'inline');
			
		} else {
			$('.clubInfo').css('display', 'table-row');
			$('.clubSettingInput').css('display', 'none');
			$('.clubSettingInputInline').css('display', 'none');
		}
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
	<%@ include file="components/header.jsp" %>
	
	<div id="allcontents">
	
		<div id="main_contents">
			<c:choose>
		
			<c:when test="${checkMaster == true}">
			<div id="form_modify_club_info_area">
				<sf:form method="post" modelAttribute="club" action="${clubRootUrl}/admin/${club.clubName}/setting/modifyClubInfo" id="form_modify_club_info">
				<table class="basic_table w-80">
					<tr>
						<th colspan="2">동아리 설정<c:if test="${bindingResultError == true}">
						<span class="font-narmal font-very-small color-red">&nbsp;작성 중</span></c:if></th>
					</tr>
					
					<tr class="clubInfo">
						<td width="30%">카테고리</td>
						<td width="70%">
							<span id="infoCategory">${club.grandCategoryTitle}
								<c:if test="${club.categoryTitle != null && club.categoryTitle != ''}">
								&gt;&gt;${club.categoryTitle}
								</c:if>
							</span>
						</td>
					</tr>
					
					<tr class="clubSettingInput">
						<td>카테고리</td>
						<td>
							<sf:select path="grandCategoryId">
								<option value="-1">대분류 선택</option>
								<sf:options items="${grandCategories}" itemLabel="categoryTitle" 
									itemValue="categoryId" />
							</sf:select>
							
							<sf:select path="categoryId" style="margin-left: 5px;">
								<option value="-1">소분류 선택</option>
							</sf:select>
							
							<span><sf:errors path="grandCategoryId" cssClass="error" /></span>
							<span><sf:errors path="categoryId" cssClass="error" /></span>
						</td>
					</tr>
					
					<tr class="clubInfo">
						<td>타입</td>
						<td>
							<span id="infoType">
								<c:if test="${club.type == 1}">
								승인형
								</c:if>
								<c:if test="${club.type == 2}">
								초대형
								</c:if>
							</span>
						</td>
					</tr>
					
					<tr class="clubSettingInput">
						<td>타입</td>
						<td>
							<sf:radiobutton path="type" label="승인형" value="1" />
							<sf:radiobutton path="type" label="초대형" value="2" />
						</td>
					</tr>
					
					<tr class="clubInfo">
						<td>최대 회원 수<br />(max: 100명)</td>
						<td>
							<span id="infoMaxMemberNum">
								${club.maxMemberNum}
							</span>
						</td>
					</tr>
					
					<tr class="clubSettingInput">
						<td>최대 회원 수<br />(max: 100명)</td>
						<td>
							<sf:input path="maxMemberNum" cssClass="smallInput" />명
							<span><sf:errors path="maxMemberNum" cssClass="error" /></span>
							<c:if test="${maxNumCheck == false}">
							<span class="error">현재 멤버 수보다 더 작게 설정할 수 없습니다</span>
							</c:if>
						</td>
					</tr>
					
					<tr class="clubInfo">
						<td>동아리 설명</td>
						<td>
							<span id="infoClubIntroduction">
								${club.clubIntroduction}
							</span>
						</td>
					</tr>
					
					<tr class="clubSettingInput">
						<td>동아리 설명</td>
						<td>
							<sf:textarea path="clubIntroduction" cssClass="bigInput" style="resize: none; "></sf:textarea>
							<span><sf:errors path="clubIntroduction" cssClass="error" /></span>
						</td>
					</tr>
					
					<tr>
						<td colspan="2" style="text-align: center;">
							<input type="button" value="변경" id="btn_setting_submit" class="clubSettingInputInline" />
							<input type="button" value="취소" onclick="location.assign('${clubRootUrl}/admin/${club.clubName}/main');" />
							<span class="pull-right"><input type="checkbox" id="modify" /><label for="modify">수정</label></span>
						</td>
					</tr>
				</table>
				</sf:form>
			</div>
			
			<div id="form_inactivate_club_area">
				<table class="basic_table w-80">
					<tr>
						<th colspan="2">동아리 비활성화</th>
					</tr>
					
					<tr>
						<td width="80%">
							이 동아리를 비활성화시키겠습니까?
						</td>
						
						<td width="20%">
							<input type="button" value="네" id="btn_inactivate_club" />
							<input type="button" value="아니요" onclick="location.assign('${clubRootUrl}/admin/${club.clubName}/main');" />
						</td>
					</tr>					
				</table>
			</div>
			
			<div id="link_area" class="text-right">
				<a href="${clubRootUrl}/admin/${club.clubName}/setting/transferMasterPre">[동아리 마스터 권한 양도]</a>
				<a href="${clubRootUrl}/admin/${club.clubName}/closeClub">[동아리 폐쇄 및 삭제]</a>
			</div>
			
			</c:when>
			
			<c:otherwise>
			<script>alert('접근 권한이 없는 사용자입니다.'); location.assign('${docRootUrl}');</script>
			</c:otherwise>
			
			</c:choose>	<!-- choose tag End -->
		
		</div>	<!-- main_content End -->
	</div>	<!-- allcontents end -->
	
	<div>
		<%@ include file="components/footer.jsp" %>
	</div>
</body>
</html>