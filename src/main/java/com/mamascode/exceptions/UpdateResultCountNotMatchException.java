/****************************************************
 * UpdateResultCountNotMatchException
 * exception class: DataAccessException 상속
 * 
 * DB 처리 결과 레코드 수가 기대했던 것과 다를 때 이 예외를 던진다.
 * DB 트랜잭션 롤백 처리를 위해 사용
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

package com.mamascode.exceptions;

import org.springframework.dao.DataAccessException;

@SuppressWarnings("serial")
public class UpdateResultCountNotMatchException extends DataAccessException {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constructors
	
	public UpdateResultCountNotMatchException(String msg) {
		super(msg);
	}

	public UpdateResultCountNotMatchException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
