package com.mamascode.utils;

/**************************************
 * DateFormatUtil
 * 
 * Date와 Timestamp 형식의 자료형을
 * 일관된 포맷으로 변환해주는 클래스
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 *   
 * 최종 업데이트: 2014. 11. 17
***************************************/

import java.sql.Date;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DateFormatUtil {
	//////////////////////////////////////////////////////////////////////////////////////////
	// getDateFormat: "YYYY/MM/DD" 형태로 변환
	public static String getDateFormat(Date date) {
		return getDate(DateToGregorianCalendar(date));
	}
	
	public static String getDateFormat(Timestamp timestamp) {
		return getDate(TimeStampToGregorianCalendar(timestamp));
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////	
	// "YYYY/MM/DD AM|PM HH:MM" 형태로 변환
	public static String getDatetimeFormat(Date date) {
		return getDatetime(DateToGregorianCalendar(date));
	}
	
	public static String getDatetimeFormat(Timestamp timestamp) {
		return getDatetime(TimeStampToGregorianCalendar(timestamp));
	}
	
	//////////////////////////////////////////////////////////////////////////////////////////
	//////////////////////////////////////////////////////////////////////////////////////////
	// 내부 메소드
	
	// DateToGregorianCalendar: Date 형 자료를 GregorianCalendar 형으로
	private static GregorianCalendar DateToGregorianCalendar(Date date) {
		GregorianCalendar calendar = null;
		
		if(date != null) {
			calendar = new GregorianCalendar();
			
			if(calendar != null)
				calendar.setTimeInMillis(date.getTime());
		}
		
		return calendar;
	}
	
	// TimeStampToGregorianCalendar: Timestamp 형 자료를 GregorianCalendar 형으로
	private static GregorianCalendar TimeStampToGregorianCalendar(Timestamp timestamp) {
		GregorianCalendar calendar = null;
		
		if(timestamp != null) { 
			calendar = new GregorianCalendar();
			
			if(calendar != null)
				calendar.setTimeInMillis(timestamp.getTime());
		}
		
		return calendar;
	}
	
	// getDate: GregorianCalendar 형 자료를 "YYYY/MM/DD" 형태로 변환
	private static String getDate(Calendar calendar) {
		 StringBuilder builder = new StringBuilder();
		 
		 if(calendar != null) {
			 builder.append(calendar.get(Calendar.YEAR))
			 .append("/").append(String.format("%02d", (calendar.get(Calendar.MONTH) + 1)))
			.append("/").append(String.format("%02d",calendar.get(Calendar.DATE)));
		 } // 파라미터가 null이면 빈 문자열이 반환된다
		 
		 return builder.toString();
	}
	
	// getDatetime: GregorianCalendar 형 자료를 "YYYY/MM/DD AM|PM HH:MM" 형태로 반환
	private static String getDatetime(Calendar calendar) {
		 StringBuilder builder = new StringBuilder();
		 
		 if(calendar != null) {
			 // Calendar.HOUR는 12시가 0으로 표시되기 때문에 수정해줌
			 int hour = calendar.get(Calendar.HOUR);
			 if(hour == 0) hour = 12;
			 
			 builder.append(calendar.get(Calendar.YEAR))
			 .append("/").append(String.format("%02d", (calendar.get(Calendar.MONTH) + 1)))
			 .append("/").append(String.format("%02d",calendar.get(Calendar.DATE)))
			 .append(" ").append((calendar.get(Calendar.AM_PM) == Calendar.AM) ? "오전 " : "오후 ")
			 .append(String.format("%2d", hour))
			 .append(":").append(String.format("%02d", calendar.get(Calendar.MINUTE)));
		 } // 파라미터가 null이면 빈 문자열이 반환된다
		 
		 return builder.toString();
	}
}
