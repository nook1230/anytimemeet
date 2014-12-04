/*************************************
 * 사용자 알림을 위한 자바스크립트&jQuery 함수들
 * 
 * 작성: 황인호
 * 최종 업데이트: 2014. 11. 17
 *************************************/

/* **********************************
 * readNotice: 알림에 읽음 표시 
 * noticeId를 제외한 파라미터는
 * ajax 요청 성공 시 getNotices에서 사용됨
 * **********************************/
function readNotice(noticeId, userName, ajaxUrlRoot, docRootUrl) {
	// ajax 요청 url
	var ajaxUrl = ajaxUrlRoot + '/notice/readNotice';
	
	// 요청 파라미터(알림 id)
	var requestData = {
		"noticeId" : noticeId,
	};
	
	$.ajaxSetup({'async': false}); // 비동기 방식: 알림 목록을 받아오기 위해
	
	// ajax 요청(post)
	$.post(ajaxUrl, requestData, function(result) {
		if(result > 0) {
			$('#noticeContents').empty(); // 알림 목록 div를 비운다
			getNotices(userName, ajaxUrlRoot, docRootUrl); // 새로운 알림 목록을 가져온다
		}
	});
}

/* **********************************
 * deleteNotice: 알림 삭제 
 * noticeId를 제외한 파라미터는
 * ajax 요청 성공 시 getNotices에서 사용됨
 * **********************************/
function deleteNotice(noticeId, userName, ajaxUrlRoot, docRootUrl) {
	// ajax 요청 url
	var ajaxUrl = ajaxUrlRoot + '/notice/deleteNotice';
	
	// 요청 파라미터(알림 id)
	var requestData = {
		"noticeId" : noticeId,
	};
	
	$.ajaxSetup({'async': false}); // 비동기 방식: 알림 목록을 받아오기 위해
	
	// ajax 요청(post)
	$.post(ajaxUrl, requestData, function(result) {
		if(result > 0) {
			$('#noticeContents').empty(); // 알림 목록 div를 비운다
			getNotices(userName, ajaxUrlRoot, docRootUrl);  // 새로운 알림 목록을 가져온다
		}
	});
}

/* **********************************
 * getNotices: 알림 목록 가져오기
 * **********************************/
function getNotices(userName, ajaxUrlRoot, docRootUrl) {
	// ajax 요청 url
	var ajaxUrl = ajaxUrlRoot + '/notice/getNotices/' + userName;
	var requestData = {}; // 파라미터 없음
	
	var $div = $('#noticeContents'); // 알림 목록을 담을 div 요소
	
	// ajax 요청(get)
	$.get(ajaxUrl, requestData, function(result) {
		var $ul = $('<ul></ul>'); // 상위 리스트 요소(unsorted list)
		
		for(var i = 0; i < result.length; i++) {
			var $li = $('<li></li>'); // list item
			
			// 알림을 클릭할 때 연결될 주소(주소 링크가 없다면 #)
			var noticeUrl = (result[i].noticeUrl != null && result[i].noticeUrl.length != 0) ?
					(docRootUrl + result[i].noticeUrl) : "#";
			
			// 알림 링크를 담을 span 요소
			var $span = $('<span></span>');			
			
			// 알림 링크를 위한 anchor 요소(a) - 클릭 이벤트 바인딩(onclick: readNotice)
			var $anchor = $('<a href="' + noticeUrl + '" onclick="readNotice(' + 
					result[i].noticeId + ', \'' + userName + '\', \'' + ajaxUrlRoot + '\', \'' + docRootUrl +
					'\');">' + result[i].noticeMsg + '</a>');
			
			$anchor.css('font-size', '0.8em'); // 폰트 사이즈 설정
			$anchor.appendTo($span); // span 요소 내부에 삽입한다
			
			// 알림 삭제를 위한 버튼(span 요소) - 클릭 이벤트 바인딩(onclick: deleteNotice)
			var $deleteBtn = $('<span class="btn" style="margin-left: 15px;" onclick="deleteNotice(' + 
					result[i].noticeId + ', \'' + userName + '\', \'' + ajaxUrlRoot + '\', \'' + docRootUrl +
					'\');">x</span>');
			
			// 알림의 종류가 마스터를 위한 것일 때
			if(result[i].noticeType == 2) {
				$anchor.css('color', 'teal'); // 앵커의 색을 teal로 설정
				$spanMaster = $('<span>&nbsp;M</span>'); // 앵커 뒤에 삽입될 span 요소(마스터 알림임을 표시)
				$spanMaster.addClass('font-very-small'); // 이하 css 설정
				$spanMaster.addClass('font-bold');
				$spanMaster.css('color', 'gray');
				
				$spanMaster.appendTo($span); // span 요소를 상위 요소의 내부에 삽입
			}
			
			// 알림을 읽은 경우 - 회색으로 표시
			if(result[i].noticeRead == true) {
				$anchor.css('color', 'gray'); // 앵커의 색을 gray로 설정
				if(result[i].noticeType == 2)
					$spanMaster.css('color', 'gray');
			}
			
			$span.appendTo($li); // 최상위 span을 list item에 삽입
			$deleteBtn.appendTo($li); // 삭제 버튼을 list item에 삽입
			
			$li.appendTo($ul); // list item을 list에 삽입
		}
		
		$ul.appendTo($div); // list를 div 블럭 요소에 삽입
	});
}