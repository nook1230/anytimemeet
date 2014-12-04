package com.mamascode.utils;

/**************************************
 * ListPagingHelper
 * 
 * 리스트의 페이지 목록을 만들 때 사용
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 *   
 * 최종 업데이트: 2014. 11. 17
***************************************/

public class ListPagingHelper {
	///////////// fields
	private int startPage;
	private int endPage;
	private int totalPage;
	private int curPage;
	private int pagePerList;
	
	///////////// constructor
	public ListPagingHelper(int totalPage, int curPage, int pagePerList) {
		this.totalPage = totalPage;
		this.curPage = curPage;
		this.pagePerList = pagePerList;
		
		// 시작 페이지, 끝 페이지 계산
		calculateStartAndEndPage();
	}
	
	///////////// getters and setters
	public int getStartPage() {
		return startPage;
	}
	
	public int getEndPage() {
		return endPage;
	}
	
	public int getPagePerList() {
		return pagePerList;
	}
	
	public void setPagePerList(int pagePerList) {
		this.pagePerList = pagePerList;
	}
	
	public int getCurPage() {
		return curPage;
	}

	public void setCurPage(int curPage) {
		this.curPage = curPage;
	}
	
	public int getTotalPage() {
		return totalPage;
	}

	public void setTotalPage(int totalPage) {
		this.totalPage = totalPage;
	}

	///////////// 내부 계산 메소드
	/* calculateStartAndEndPage: 시작 페이지, 마지막 페이지 계산 */
	private void calculateStartAndEndPage() {
		int startBlock = (curPage - 1) / pagePerList + 1;
		startPage = (startBlock - 1) * pagePerList + 1;
		endPage = startPage + pagePerList - 1;
		
		if(endPage > totalPage)
			endPage = totalPage;
		
		if(curPage > totalPage)
			curPage = totalPage;
	}
}
