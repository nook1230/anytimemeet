/****************************************************
 * FilePictureUploadFailedException
 * exception class: DataAccessException 상속
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
public class FilePictureUploadFailedException extends DataAccessException {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// constructors
	
	public FilePictureUploadFailedException(String msg) {
		super(msg);
	}
	
	public FilePictureUploadFailedException(String msg, Throwable cause) {
		super(msg, cause);
	}
}
