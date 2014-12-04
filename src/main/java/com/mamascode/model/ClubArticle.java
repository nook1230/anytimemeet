package com.mamascode.model;

/****************************************************
 * ClubArticle: Model
 * 
 * JSR-303 빈 검증 적용
 *  
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

import java.sql.Timestamp;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class ClubArticle {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// fields
	private int articleId = 0;
	private String clubName;		// 동아리 식별 이름
	private String writerName;		// 작성자 식별 이름
	private String writerNickname;	// 작성자 별명
	
	///////////// required /////////////////
	// 입력 상에서 필수 항목(DB에는 다른 항목들도 필수)
	////////////////////////////////////////
	
	// 제목
	@NotNull
	@Size(min=1, max=250, message="제목은 250글자 이하만 가능합니다(필수 입력).")
	private String title;
	
	// 내용
	@NotNull
	@Size(min=1, message="내용을 입력해주세요")
	private String content;
	////////////////////////////////////////
	
	private Timestamp writeDate;	// 작성 날짜
	private boolean blind;			// 블라인드 처리 여부
	private int viewCount;			// 조회수
	private int repliesCount;		// 댓글 수
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// getters and setters /////////////////////////
	
	public int getArticleId() {
		return articleId;
	}
	
	public void setArticleId(int articleId) {
		this.articleId = articleId;
	}
	
	public String getClubName() {
		return clubName;
	}
	
	public void setClubName(String clubName) {
		this.clubName = clubName;
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

	public String getTitle() {
		return title;
	}
	
	public void setTitle(String title) {
		this.title = title;
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

	public int getViewCount() {
		return viewCount;
	}

	public void setViewCount(int viewCount) {
		this.viewCount = viewCount;
	}
	
	public int getRepliesCount() {
		return repliesCount;
	}

	public void setRepliesCount(int repliesCount) {
		this.repliesCount = repliesCount;
	}
}
