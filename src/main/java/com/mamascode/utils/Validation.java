package com.mamascode.utils;

import java.sql.Date;
import java.util.GregorianCalendar;

/**************************************
 * Validation
 * 
 * 요소 검증을 위한 함수들 제공
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 *   
 * 최종 업데이트: 2014. 11. 17
***************************************/

public class Validation {
	// checkStringLength: 문자열 길이 검증(byte 길이 검증)
	public static boolean checkStringLength(String str, int minLen, int maxLen) {
		return str.getBytes().length <= maxLen && str.getBytes().length >= minLen;
	}
	
	// notNullStringLength: 문자열 길이 검증(byte 길이 검증), 빈 문자열의 경우 false
	public static boolean notNullStringLength(String str, int maxLen) {
		return str != null && 
				str.getBytes().length <= maxLen && 
				str.getBytes().length >= 1;
	}
	
	// checkIntegerSize: 정수형 변수 크기 검증
	public static boolean checkIntegerSize(long num, long minNum, int maxNum) {
		return num <= maxNum && num >= minNum;	// 범위 안에 있으면 true
	}
	
	// ProcrustesBed: 정수형 변수를 적당히 자른다
	public static long ProcrustesBed(long num,  long minNum, long maxNum) {
		if(num > maxNum)
			// maxNum보다 크면 maxNum로
			return maxNum;
		else if(num < minNum)
			// minNum보다 작으면 minNum으로
			return minNum;
		else 
			// 범위 안에 있으면 그대로
			return num;
	}
	
	// ProcrustesBed: 정수형 변수를 적당히 자른다(최저값으로)
	public static long ProcrustesBedHead(long num,  long minNum) {
		if(num < minNum)
			// minNum보다 작으면 minNum으로
			return minNum;
		else 
			// 범위 안에 있으면 그대로
			return num;
	}
	
	// ProcrustesBed: 정수형 변수를 적당히 자른다(최고값으로)
	public static long ProcrustesBedFoot(long num,  long maxNum) {
		if(num > maxNum)
			// maxNum보다 크면 maxNum로
			return maxNum;
		else
			// 범위 안에 있으면 그대로
			return num;
	}
	
	// notNullString: 문자열이 null이 아닌지
	public static boolean notNullString(String str) {
		return str != null;
	}
	
	// notEmptyString: 문자열이 빈 문자열이 아닌지
	public static boolean notEmptyString(String str) {
		return str != null && !str.isEmpty();
	}
	
	// notNullStrings: 해당 문자열들이 모두 null이 아닌지
	public static boolean notNullStrings(String[] strs) {
		for(String str : strs) {
			if(str == null)
				return false;
		}
		
		return true;
	}
	
	// notNullObject: 해당 Object가 null이 아닌지
	public static boolean notNullObject(Object obj) {
		return obj != null;
	}
	
	// isThisObjectType: obj의 타입이 type인지
	public static boolean isThisObjectType(Object obj, String type) {
		return obj.getClass().getName().equals(type);
	}
	
	// replaceHtmlCharacter: <와 > 문자를 html의 특수 문자로 변경
	public static String replaceHtmlCharacter(String str) {
		String retStr = str;
		retStr = retStr.replace("<", "&lt;");
		retStr = retStr.replace(">", "&gt;");
		
		return retStr;
	}
	
	// strToDate: 문자열을 날짜 형으로 변경
	public static Date strToDate(String str) {
		String[] hireDateStrs = str.split("/", 3);
		int year = Integer.parseInt(hireDateStrs[0]);
		int month = Integer.parseInt(hireDateStrs[1]) - 1;
		int day = Integer.parseInt(hireDateStrs[2]);
		
		return new Date(new GregorianCalendar(year, month, day).getTimeInMillis());
	}
}
