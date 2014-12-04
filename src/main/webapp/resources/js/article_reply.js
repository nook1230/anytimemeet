/******************************************
 * article_reply.js
 * 게시물에 대한 댓글 처리
 * 
 * 작성: 황인호
 * 최종 업데이트: 2014. 11. 17
******************************************/

/* writeReply: ajax 요청을 이용해서 게시물에 댓글을 쓴다 */
function writeReply(ajaxUrl, writerName, readAjaxUrl, deleteUrl) {
	// 댓글 내용
	var content = $('#txtReplyContent').attr('value');

	content = $.trim(content); // 앞뒤 공백 제거

	// 파라미터 필터링
	if (content.length < 1) {
		// 입력이 없는 경우
		alert('댓글을 입력해주세요.');
		$('#txtReplyContent').attr('value', '');
		return false;
	} else if (content > 500) {
		// 입력이 500자 이상인 경우
		alert('댓글은 500글자 이내로 작성하셔야 합니다');
		return false;
	}

	// Ajax 요청 데이터(댓글 입력 정보) - content는 한글 입력을 위해 utf-8로 인코딩
	var requestData = {
		"writerName" : writerName,
		"content" : encodeURIComponent(content)
	};

	$.post(ajaxUrl, requestData, function(result) {
		if (result) {
			// 댓글 입력 성공!

			// 댓글 입력 폼을 비운다
			$('#txtReplyContent').attr('value', '');

			// 댓글 목록 리프레시
			getReply(readAjaxUrl, deleteUrl);

			// 화면을 가장 하단으로 스크롤시킴(방금 작성한 댓글을 볼 수 있게끔)
			window.scrollTo(0, document.body.scrollHeight);
		} else {
			// 댓글 입력 실패
			alert('오류: 댓글 쓰기 실패!');
		}
	});
}

/* deleteReply: ajax 요청을 이용해서 게시물의 댓글을 삭제 */
function deleteReply(ajaxUrl, replyId, readAjaxUrl) {
	// Ajax 요청 데이터
	var requestData = {	};

	$.post(ajaxUrl + replyId, requestData, function(result) {
		if (result) {
			// 댓글 목록 리프레시
			getReply(readAjaxUrl, ajaxUrl);
			
			// 화면을 가장 하단으로 스크롤시킴
			window.scrollTo(0, document.body.scrollHeight);
		} else {
			// 댓글 입력 실패
			alert('내 댓글만 삭제 가능합니다!');
		}
	});
}

/* getReply: ajax 요청을 이용해서 게시물의 댓글을 가져온다 */
function getReply(ajaxUrl, deleteUrl) {
	// 댓글을 담을 테이블
	var $table = $('#replies_table');
	$table.children().remove(); // 테이블을 비운다

	// ajax 요청: get 방식, 동기 실행(댓글 목록을 가져온 후 스크롤 조정을 위해) - 댓글이 많은 경우 성능 상 문제 가능성
	$.ajax({
		url : ajaxUrl,
		method : 'get',
		async : false,
		success : function(result) {
			// ajax 오류가 없다면, 댓글 테이블에 새 목록을 가져와 넣어준다
			for (var i = 0; i < result.length; i++) {
				var $tr1 = $('<tr></tr>'); // 댓글 헤더(작성자, 작성시간, 삭제 버튼)
				$tr1.addClass('font-small');
				$tr1.addClass('reply-header');

				// 작성자 이름
				var writerName = '';
				if(result[i].writerNickname != null && result[i].writerNickname != '')
					writerName = result[i].writerNickname;
				else
					writerName = result[i].writerName;
				
				var $td1 = $('<td width="15%">' + writerName
						+ '</td>');

				var writeDate = new Date(result[i].writeDate);
				var writeDateStr = getDatetimeFormat(writeDate);

				// 작성 시간
				var $td2 = $('<td width="20%">' + writeDateStr + '</td>');

				// 삭제 버튼
				var $td3 = $('<td width="5%" onclick="deleteReply(\'' + deleteUrl + '\', ' + 
						result[i].replyId + ', \'' + ajaxUrl + '\');">[삭제]</td>');
				var $td4 = $('<td></td>');

				// CSS 클래스 추가
				$td1.addClass('font-bold');
				$td2.addClass('font-very-small');
				$td3.addClass('font-very-small');
				$td3.addClass('btn');

				// 댓글 본문
				var $tr2 = $('<tr></tr>');
				$tr2.addClass('reply-body');

				// 내용
				var $td5 = $('<td colspan="4">' + result[i].content + '</td>');

				// 테이블에 추가
				$td1.appendTo($tr1);
				$td2.appendTo($tr1);
				$td3.appendTo($tr1);
				$td4.appendTo($tr1);
				$td5.appendTo($tr2);

				$tr1.appendTo($table);
				$tr2.appendTo($table);
			}
		},
		error : function() {
			// ajax error
			alert("ajax error: 댓글 가져오기 실패");
		}
	});
}