package com.mamascode.utils;

/**************************************
 * SecurityUtil
 * 
 * 보안 관련 유틸
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 *   
 * 최종 업데이트: 2014. 11. 17
***************************************/

public class SecurityUtil {
	/*******************************
	 * replaceScriptTag: 태그 제거
	 * 태그가 일정한 형태를 갖지 않는 경우가 있어서
	 * 정상적으로 작동이 되지 않음
	 * 예를 들면, img 태그는 <img src="" />
	 * 의 형태를 갖기 때문에 이 함수로 걸러낼 수 없다
	 * 
	 * 수정 요망
	 *******************************/
	public static String replaceScriptTag(String src, boolean useHtml, String[] allowedTags) {
		String retStr = "";
		if(useHtml) {
			retStr = src.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
			
			// 허용할 HTML 태그만 변경
			for(String tag : allowedTags) {
				// 허용할 태그의 표현식
				String regexLower = new StringBuilder().append("&lt;")
						.append(tag.toLowerCase()).append("&gt;").toString();
				String regexUpper = new StringBuilder().append("&lt;")
						.append(tag.toUpperCase()).append("&gt;").toString();
				// 변경할 태그
				String replacementLower = new StringBuilder().append("<")
						.append(tag.toLowerCase()).append(">").toString();
				String replacementUpper = new StringBuilder().append("<")
						.append(tag.toUpperCase()).append(">").toString();
				
				retStr = retStr.replaceAll(regexLower, replacementLower);
				retStr = retStr.replaceAll(regexUpper, replacementUpper);
			}	
		} else {
			retStr = src.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
		}
		
		return retStr;
	}
	
	/* replaceJavaScriptTag: script 태그만 제거해준다 */
	public static String replaceJavaScriptTag(String src) {
		return src.replaceAll("<script>", "&lt;x-script&gt;").
				replaceAll("</script>", "&lt;/xxx-script&gt;").
				replaceAll("<script", "&lt;x-script").
				replaceAll("</script", "&lt;/xxx-script");
	}
}
