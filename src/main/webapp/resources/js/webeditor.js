/*************************************
 * 게시물 작성을 위한 웹 에디터 소스
 * 
 * source by 김천호 
 * in "No more Copy & Paste 자바스크립트"
 * p. 205 (2012 초판)
 * 
 * commented by Hwang Inho
 * 최종 업데이트: 2014. 11. 17
 *************************************/

// document object
var oDoc;

/* initDoc: oDoc 초기화 */
function initDoc() {
	oDoc = document.getElementById('textBox');
}

/* setDocMode: 에디터 편집 모두 설정(일반 텍스트 | html 편집) */
function setDocMode(bToSource) {
	var oContent;
	
	if(bToSource) {
		// html 모드
		oContent = document.createTextNode(oDoc.innerHTML); // oDoc의 내부 html 코드를 가져와 텍스트 노드로 생성
		oDoc.innerHTML = ""; // oDoc의 내부를 비운다
		
		var oPre = document.createElement("pre"); // pre 태그 생성(html 태그가 그대로 적용)
		oDoc.contentEditable = false; // oDoc은 편집 불가능한 div로 변경
		oPre.id = "sourceText";
		oPre.contentEditable = true; // 이제 편집은 oPre에서 이루어진다
		oPre.appendChild(oContent); // 컨텐츠를 oPre에 삽입
		oDoc.appendChild(oPre); // oDoc에 oPre를 삽입
	} else {
		// 일반 텍스트 모드
		if(document.all) {
			oDoc.innerHTML = oDoc.innerText;
		} else {
			oContent = document.createRange();
			oContent.selectNodeContents(oDoc.firstChild);
			oDoc.innerHTML = oContent.toString();
		}
		
		oDoc.contentEditable = true;
	}
	
	oDoc.focus();
}