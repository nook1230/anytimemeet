package com.mamascode.model;

/****************************************************
 * ClubCategory: Model
 *  
 * source by Hwang Inho(mmuse1230@gmail.com)
 * 
 * Srping 프레임워크 사용(3.1.4.RELEASE)
 * 본 프로젝트는 아파치 라이선스 버전 2.0을 준수합니다
 *  
 * 최종 업데이트: 2014. 11. 17
 ****************************************************/

public class ClubCategory {
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// fields
	private int categoryId;			// 카테고리 id
	private int parentCategoryId;	// 상위 카테고리 id(0이면 최상위 카테고리임)
	private String categoryTitle;	// 카테고리 이름
	
	////////////////////////////////////////////////
	////////////////////////////////////////////////
	// getters and setters /////////////////////////
	
	public int getCategoryId() {
		return categoryId;
	}
	
	public void setCategoryId(int categoryId) {
		this.categoryId = categoryId;
	}
	
	public int getParentCategoryId() {
		return parentCategoryId;
	}
	
	public void setParentCategoryId(int parentCategoryId) {
		this.parentCategoryId = parentCategoryId;
	}
	
	public String getCategoryTitle() {
		return categoryTitle;
	}
	
	public void setCategoryTitle(String categoryTitle) {
		this.categoryTitle = categoryTitle;
	}
}
