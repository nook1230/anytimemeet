/******************************************
 * calendar.js
 * 달력 관련 자바스크립트
 * 
 * 작성: 황인호
 * 최종 업데이트: 2014. 8. 16
******************************************/
//////// global variables
//////// 날짜와 관련된 배열들
// 월별 날짜수
var endDatesArr = new Array(31, 28, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31);
// 요일 이름 문자열
var dayArr = new Array('Sun', 'Mon', 'Tue', 'Wed', 'Thu', 'Fri', 'Sat');
// 달 이름 문자열
var monthArr = new Array('Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun', 'Jul',
		'Aug', 'Sep', 'Oct', 'Nov', 'Dec');

// 날짜를 저장할 전역 변수들
var glbYear;
var glbMonth;
var glbDate;

// 날짜 입력 폼 아이디
var yearId = '#year';
var monthId = '#month';
var dateId = '#date';

// calendar div 요소 레퍼런스
var $calDiv;

/* initializeCalendar: calendar div 요소 초기화 */
function initializeCalendar(calDiv) {
	$calDiv = calDiv;
	$calDiv.empty();
	
	// date object for today
	var dateObjToday = new Date();
	year = dateObjToday.getYear();
	month = dateObjToday.getMonth();
	date = dateObjToday.getDate();
	
	// 달력 만들기
	var $calTable = makeCalendarTable(year, month, date);
	var $calController = makeCalendarController();
	
	// div 요소에 추가
	$calController.appendTo($calDiv);
	$calTable.appendTo($calDiv);
	
	// 전역 변수 할당
	glbYear = year;
	glbMonth = month;
	glbDate = date;
	
	bindCalendarEvents();	// 이벤트 바인딩
	refreshYearSpan(year, month);	// 달력 상단에 날짜 출력
}

/* makeCalendarController: 달력 컨트롤러 만들기 */
function makeCalendarController() {
	var $calController = $('<div><span id="closeCal" class="pull-right" onclick="toggleCalendar();">X</span></div>' + 
			'<div id="calController">' + 
			'<span id="prevBtn"><<</span>&nbsp;' +
			'<span id="yearSpan"></span>&nbsp;' + 
			'<span id="nextBtn">>></span></div>');
	
	return $calController;
}

/* refreshYearSpan: 날짜 출력(년월) */
function refreshYearSpan(year, month) {
	if (year > 100) {
		year = 2000 + (year - 100);
	} else {
		year = 1900 + year;
	}
	
	$('#yearSpan').text(year + " " + monthArr[month]);
}

/* makeCalendarTable: 달력 만들기(테이블) */
function makeCalendarTable(year, month, date) {
	// make a table and append to body
	var $table = $("<table></table>");
	$table.addClass('pull-center');
	
	// 요일 항목 생성
	var $weekTr = $("<tr></tr>");

	for (var i = 0; i < 7; i++) {
		var $weekTd;	// 요일 테이블 데이터
		if (i == 0) {
			// 일요일은 빨간색으로
			$weekTd = $("<td style='color: red;'>" + dayArr[i] + "</td>");
		} else {
			$weekTd = $("<td>" + dayArr[i] + "</td>");
		}

		$weekTd.appendTo($weekTr);	// 테이블 행에 추가
	}
	
	$weekTr.appendTo($table);	// 테이블에 추가
	
	// 자바스크립트의 Date 객체를 이용해 2000년대 날짜를 받으면 년도가 세자리로 리턴된다
	// 예를 들어 2014년은 114년으로 넘어옮. 아래는 이것을 수정해주는 구문.
	// 연도 정보를 이용해 새 Date 객체를 생성하려면 네자리 연도로 바꿔줘야 한다(예: 2014)
	if (year > 100) {
		year = 2000 + (year - 100);
	} else {
		year = 1900 + year;
	}

	// date object for the start day of the month
	var dateObjStart = new Date(year, month, 1);

	// get a start and end date of this month
	var startDate = dateObjStart.getDate();
	var endDate = endDatesArr[month];
	var dayOfWeek = dateObjStart.getDay();

	// 윤년 처리
	if (year % 4 == 0 && month == 1) {
		endDate++;
	}
	
	/******** 여기서부터 날짜 정보 생성 및 추가 ********/
	var $tr = $("<tr></tr>");	// 테이블 행(한 주가 된다)

	for (var i = startDate; i <= endDate; i++) {
		if (i == startDate && dayOfWeek != 0) {
			// 1일의 요일이 일요일이 아닌 경우 빈 테이블 열을 추가
			for (var j = 0; j < dayOfWeek; j++) {
				$("<td></td>").appendTo($tr);
			}
		}
		
		// 날짜 정보가 들어갈 td(테이블 데이터)
		var $dateTd = $(
				"<td class='dateNode' onclick='selectDate(" + year + ", "
						+ month + ", " + i + ")'>" + i + "</td>").appendTo($tr);
		
		// 일요일은 빨간색으로
		if (dayOfWeek == 0)
			$dateTd.css('color', 'red');
		
		// 오늘은 푸른색으로
		if (i == date)
			$dateTd.css('color', 'blue');
		
		// 마지막 날짜가 토요일이 아닌 경우 빈 td 삽입
		if (i == endDate && dayOfWeek != 6) {
			for (var j = dayOfWeek; j < 7; j++) {
				$("<td></td>").appendTo($tr);
			}
		}

		dayOfWeek++;	// 요일 증가

		if ((dayOfWeek %= 7) == 0) {
			// 요일이 한바퀴 돈 경우 테이블 로우를 테이블에 추가
			$tr.appendTo($table);
			$tr = $("<tr></tr>");	// 새 테이블 로우 생성
		}
	}
	
	// 다 채워지지 않은 테이블 로우가 있는 경우 테이블에 추가
	if ((dayOfWeek %= 7) != 0)
		$tr.appendTo($table);
	
	// 마우스 진입 이벤트 바인딩
	$('.dateNode').mouseenter(function() {
		$(this).css('cursor', 'pointer');
	});
	
	// 테이블 객체를 리턴
	return $table;
}

/* bindCalendarEvents: 이벤트 바인딩 */
function bindCalendarEvents() {
	$('#prevBtn').click(function() {
		changeCalendar(true);
	});

	$('#nextBtn').click(function() {
		changeCalendar(false);
	});

	$('#prevBtn').mouseenter(function() {
		$(this).css('cursor', 'pointer');
	});

	$('#nextBtn').mouseenter(function() {
		$(this).css('cursor', 'pointer');
	});
	
	$('#closeCal').mouseenter(function() {
		$(this).css('cursor', 'pointer');
	});
	
	$('.dateNode').mouseenter(function() {
		$(this).css('cursor', 'pointer');
	});
}

/* toggleCalendar: 달력 토글 */
function toggleCalendar(left, top) {
	$calDiv.css('position', 'absolute');
	$calDiv.css('left', left + 'px');
	$calDiv.css('top', top + 'px');
	$calDiv.toggleClass('hidden');
}

/* selectDate: 날짜 선택. 선택된 날짜가 입력 폼에 들어간다 */
function selectDate(year, month, date) {
	var $year = $(yearId);
	var $month = $(monthId);
	var $date = $(dateId);
	
	var dateObjSelected = new Date(year, month, date);
	var yearSelected = dateObjSelected.getYear();
	var monthSelected = dateObjSelected.getMonth() + 1;
	var dateSelected = dateObjSelected.getDate();
	//var dayOfWeekSelected = dateObjSelected.getDay();

	if (yearSelected > 100)
		yearSelected = 2000 + (yearSelected - 100);

	$year.attr('value', yearSelected);
	$month.attr('value', monthSelected);
	$date.attr('value', dateSelected);
	
	toggleCalendar();	// 달력을 숨긴다
	initializeCalendar($calDiv);	// 날짜가 추가되면 달력을 초기화한다
}

/* changeCalendar: 달력의 월을 증감시킨다 */
function changeCalendar(prev) {
	$calDiv.empty();	// 달력 요소를 비운다
	
	if (prev)	// 감소
		glbMonth--;
	else		// 증가
		glbMonth++;

	if (glbMonth < 0) {
		// 1월에서 감소되는 경우: 전년으로
		glbMonth = 11;
		glbYear--;
	}

	if (glbMonth > 11) {
		// 12월에서 증가되는 경우: 익년으로
		glbMonth = 0;
		glbYear++;
	}
	
	// 달력 새로 만들기
	var $calController = makeCalendarController();
	var $calTable = makeCalendarTable(glbYear, glbMonth, glbDate);
	
	// 달력 추가
	$calController.appendTo($calDiv);
	$calTable.appendTo($calDiv);
	
	// 날짜 표시 수정
	refreshYearSpan(glbYear, glbMonth);
	bindCalendarEvents();	// 이벤트 바인딩 새로
}

