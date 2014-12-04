/******************************************
 * meeting_date.js
 * 동아리 모임 날짜 추가 관련 변수 및 함수들
 * 
 * 작성: 황인호
 * 최종 업데이트: 2014. 8. 16
******************************************/

// 전역 변수들(배열)
var arrDates = new Array();
var arrTimes = new Array();
		
/* makeJsonFormatString: 배열에 저장된 날짜 정보를 이용해 JSON 문자열을 만든다 */
function makeJsonFormatString() {
	var JsonMeetingDates = '{\"MeetingDates\":[';
	
	var len = arrDates.length;
	for(var i = 0; i < len; i++) {
		JsonMeetingDates += '{\"date\":\"' + arrDates[i] + '\", \"time\":';
		JsonMeetingDates += '\"' + arrTimes[i] + '\"}';
		
		if(i != len - 1)
			JsonMeetingDates += ',';
	}
			
	JsonMeetingDates += ']}';
	
	return JsonMeetingDates;
}

/* addMeetingDate: 입력 창으로부터 정보를 가져와 전역 배열에 저장 */
function addMeetingDate(yearId, monthId, dateId, hourId, minuteId, ampmId) {
	var year = $(yearId).attr('value');
	var month = $(monthId).attr('value');
	var date = $(dateId).attr('value');
	var hour = $(hourId).attr('value');
	var minute = $(minuteId).attr('value');
	var ampm = $(ampmId).attr('value');
	
	var meetingDate = year + '/' + month + '/' + date;
	var meetingTime = hour +':' + minute + " " + ampm;
	
	if(!checkDateElems(year, month, date, hour, minute, ampm)) {
		alert('필수항목을 입력하지 않으셨습니다!');
		return;
	}
	
	arrDates.push(meetingDate);
	arrTimes.push(meetingTime);
	
	refreshSpan();	// 추가된 날짜 정보를 화면에 출력
	
	$(yearId).attr('value', '');
	$(monthId).attr('value', '');
	$(dateId).attr('value', '');
}

function checkDateElems(year, month, date, hour, minute, ampm) {
	if(year != '' && month != '' && date != '' &&
			hour != '' && minute != '' && ampm != '') {
		return true;
	}
	
	return false;
}

/* refreshSpan: 날짜 정보가 추가될 때마다 span 요소로 정보를 추가해서 화면에 보여줌 */
function refreshSpan() {
	var $pMeetingDates = $('#meetingDates');
	$pMeetingDates.empty();
	
	for(var i = 0; i < arrDates.length; i++) {
		var meetingDate = arrDates[i];
		var meetingtime = arrTimes[i];
		
		var splitedDate = meetingDate.split('/');
		var year = splitedDate[0];
		var month = splitedDate[1];
		var date = splitedDate[2];
		
		var $span = $("<span>[" + (i+1) + "] " + year + "년 " + month + "월 " 
				+ date + "일 " + meetingtime +"&nbsp;<a class='a_btn' title='삭제' onclick='deleteMeetingDate(" + i + ");'>[-]</a><br /></span>");
		
		$span.appendTo($pMeetingDates);
		
		$('.a_btn').mouseenter(function() {
			$(this).css('cursor', 'pointer');
		});
	}
}

/* deleteMeetingDate: 날짜 삭제 처리 */
function deleteMeetingDate(idx) {
	removeArr(arrDates, idx);
	removeArr(arrTimes, idx);
	refreshSpan();	// span 요소들 리프레쉬
}