package com.mamascode.model;

/****************************************************
 * Reply: Model
 * 
 * 게시글 댓글과 모임글 댓글 모두에 사용됨
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.sql.Timestamp;

public class Reply {
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// fields
	private int replyId;			// 식별 id
	private int targetId;			// 댓글이 달린 게시물 id
	private String writerName;		// 작성자 식별 이름
	private String writerNickname;	// 작성자 별명
	private String content;			// 내용
	private Timestamp writeDate;	// 작성 일시
	private boolean blind;			// 블라인드 여부
	
	////////////////////////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////////////////////////
	// getters and setters
	public int getReplyId() {
		return replyId;
	}
	
	public void setReplyId(int replyId) {
		this.replyId = replyId;
	}
	
	public int getTargetId() {
		return targetId;
	}
	
	public void setTargetId(int targetId) {
		this.targetId = targetId;
	}
	
	public String getWriterName() {
		return writerName;
	}
	
	public void setWriterName(String writerName) {
		this.writerName = writerName;
	}
	
	public String getWriterNickname() {
		return writerNickname;
	}

	public void setWriterNickname(String writerNickname) {
		this.writerNickname = writerNickname;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Timestamp getWriteDate() {
		return writeDate;
	}
	
	public void setWriteDate(Timestamp writeDate) {
		this.writeDate = writeDate;
	}
	
	public boolean isBlind() {
		return blind;
	}
	
	public void setBlind(boolean blind) {
		this.blind = blind;
	}
}
