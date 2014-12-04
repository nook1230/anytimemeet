/******************************************
 * custom_js.js
 * 편의를 위한 자바스크립트 함수 모음
 * 
 * 작성: 황인호
 * 최종 업데이트: 2014. 11. 10
 * 
******************************************/

/* popup: 새 창을 열어 url 요청 페이지를 출력 */
function popup(url, width, height, left, top, wName) {
	window.open(url, wName,
			'width=' + width + 
			', height=' + height +
			', left=' + left +
			', top= ' + top +
			', menubar=no, status=yes, toolbar=no, resizable=no');
}

/* popupScroll: 새 창을 열어 url 요청 페이지를 출력(스크롤바 포함) */
function popupScroll(url, width, height, left, top, wName) {
	window.open(url, wName,
			'width=' + width + 
			', height=' + height +
			', left=' + left +
			', top= ' + top +
			', menubar=no, status=yes, toolbar=no, scrollbars=yes, resizable=no');
}

//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////

/* goUrl: url에 해당하는 페이지로 이동 */
function goUrl(url) {
	location.assign(url);
}

/* removeArr: 배열의 idx에 해당하는 요소를 삭제 */
function removeArr(arr, idx) {
	var tmp = arr[idx];
	var len = arr.length;
	
	for(var i = idx; i < len - 1; i++) {
		arr[i] = arr[i+1];
	}
	
	arr[len-1] = tmp;
	
	arr.pop();
}

//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////

/* getDateFormat: Date 객체를 "YYYY/MM/DD" 형태의 문자열로 변환 */
function getDateFormat(date) {
	var year = date.getFullYear(); // 연도를 Full Year(4자리) 형태로 가져온다 
	var month = date.getMonth() + 1;
	var dayOfMonth = date.getDate();
	
	// 포맷 조정
	if(month < 10) month = "0" + month; // 월이 10보다 작으면 앞에 0을 붙여준다
	if(dayOfMonth < 10) dayOfMonth = "0" + dayOfMonth; // 날짜가 10보다 작으면 앞에 0을 붙여준다
	
	return year + "/" + month + "/" + dayOfMonth;
}

/* getDatetimeFormat: Date 객체를 "YYYY/MM/DD AM|PM HH:MM" 형태의 문자열로 변환 */
function getDatetimeFormat(date) {
	var hour = date.getHours();
	var minutes = date.getMinutes();
	var ampm = "오전 ";
	
	// 시간 조정 및 오전 오후 설정
	if(hour == 0) {
		// 0이면 오전 12시(자정)
		hour = 12;
		ampm = "오전 ";
	} else if(hour == 12) {
		// 정오: ampm만 오후로 설정(오후 12시)
		ampm = "오후 ";
	} else if(hour > 12) {
		// 오후 시간대: 12진법 시간으로 바꿔준다
		hour %= 12;
		ampm = "오후 ";
	}
	
	// 포맷 조정
	if(hour < 10) hour = " " + hour; // 시간이 10보다 작으면 앞에 빈 문자를 붙여서 폭을 맞춘다
	if(minutes < 10) minutes = "0" + minutes; // 분이 10보다 작으면 앞에 0을 붙여준다
	
	return getDateFormat(date) + " " + ampm + hour + ":" + minutes;
}

//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////

/* resizeImagesWidth: $elmt에 포함된 img 태그의 요소들 중 
 * 넓이가 maxWidth를 초과하는 요소의 크기를 재조정해주는 함수  */
function resizeImagesWidth($elmt, maxWidth) {
	var $imgs = $elmt.find('img');
	
	for(var i = 0; i < $imgs.size(); i++) {
		var $img = $imgs.eq(i);
		
		resizeImageByWidth($img, maxWidth);
	}
}

/* resizeImageWidth: $img 요소의 넓이를 maxWidth 이하로 조정 */
function resizeImageWidth($img, maxWidth) {
	var imgWidth = $img.outerWidth();
	
	if(imgWidth > maxWidth || imgWidth == 0) {
		$img.attr('width', maxWidth + 'px');
	}		
}

/* resizeImageWidth: $img 요소의 높이를 maxHeight 이하로 조정 */
function resizeImageHeight($img, maxHeight) {
	var imgHeight = $img.outerHeight();

	if(imgHeight > maxHeight || imgHeight == 0) {
		$img.attr('height', maxHeight + 'px');
	}
}

//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////

function resizeImageByWidth($img, maxWidth) {
	var imgWidth = $img.innerWidth();
	var imgHeight = $img.innerHeight();
	var ratio = 1;
	
	if(imgWidth > maxWidth) {
		if(imgWidth != 0) {
			ratio = imgHeight / imgWidth;
			
			var resizeHeight = maxWidth * ratio;
			img.attr('width', maxWidth + 'px');
			$img.attr('height', resizeHeight + 'px');
		} else {
		$img.attr('width', maxWidth + 'px');
		}
	}
	
}

function resizeImageByHeight($img, maxHeight) {
	var imgWidth = $img.innerWidth();
	var imgHeight = $img.innerHeight();
	var ratio = imgHeight / imgWidth;
	
	if(imgHeight > maxHeight) {
		if(imgHeight != 0) {
			ratio = imgHeight / imgWidth;
			
			var resizeWidth = maxHeight * (1/ratio);
			$img.attr('width', resizeWidth + 'px');
			$img.attr('height', maxHeight + 'px');
		} else {
			$img.attr('height', maxHeight + 'px');
		}
	}
}

//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////

/* removeImages: $elmt에 포함된 img 태그의 요소들을 모두 삭제한다 */
function removeImages($elmt) {
	var $imgs = $elmt.find('img');
	
	for(var i = 0; i < $imgs.size(); i++) {
		var $img = $imgs.eq(i);
		
		$img.remove();
	}
}

/* hideImages: $elmt에 포함된 img 태그의 요소들을 모두 감춘다 */
function hideImages($elmt) {
	var $imgs = $elmt.find('img');
	
	for(var i = 0; i < $imgs.size(); i++) {
		var $img = $imgs.eq(i);
		
		$img.css('display', 'none');
	}
}

//////////////////////////////////////////////////////////////////////////////////////
//////////////////////////////////////////////////////////////////////////////////////

/* abbreviateText: $elmt에 포함된 텍스트를 축약한다 */
function abbreviateText($elmt, maxLength) {
	var txt = $elmt.text();
	
	abbrTxt = txt.substring(0, maxLength);
	
	if(abbrTxt.length > 200)
		abbrTxt	+= "... (하략)";
	
	$elmt.text(abbrTxt);
}


