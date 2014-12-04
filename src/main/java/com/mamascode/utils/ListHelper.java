package com.mamascode.utils;

/**************************************
 * ListHelper
 * 
 * view에서 객체의 리스트를 쉽게 표현하도록 
 * 도와주는 헬퍼 클래스
 * 
 * source by Hwang Inho(mmuse1230@gmail.com)
 *   
 * 최종 업데이트: 2014. 11. 17
***************************************/

import java.util.ArrayList;
import java.util.List;

public class ListHelper<T> {
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	// 필드
	private int totalCount;			// 전체 객체 수
	private int curPageNumber;		// 현재 페이지
	private int totalPageCount;		// 전체 페이지 수
	private int objectPerPage;		// 페이지 당 객체 수
	private int offset;				// DB 조회시 시작 레코드 오프셋 
	
	// 객체를 담을 리스트
	private List<T> list = new ArrayList<T>();
	
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	// 생성자
	public ListHelper(int totalCount, 
			int curPageNumber, int objectPerPage) {
		this.totalCount = totalCount;
		this.curPageNumber = curPageNumber;
		this.objectPerPage = objectPerPage;
		calculateTotalPage();
		calculateOffset();
	}
	
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	// getters and setters

	public int getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(int totalCount) {
		this.totalCount = totalCount;
	}

	public int getCurPageNumber() {
		return curPageNumber;
	}

	public void setCurPageNumber(int curPageNumber) {
		this.curPageNumber = curPageNumber;
	}

	public int getTotalPageCount() {
		return totalPageCount;
	}

	public void setTotalPageCount(int totalPageCount) {
		this.totalPageCount = totalPageCount;
	}

	public int getObjectPerPage() {
		return objectPerPage;
	}

	public void setObjectPerPage(int objectPerPage) {
		this.objectPerPage = objectPerPage;
	}
	
	public boolean isEmpty() {
		return getTotalCount() == 0;
	}
	
	public int getOffset() {
		return offset;
	}

	public List<T> getList() {
		return list;
	}

	public void setList(List<T> list) {
		this.list = list;
	}	
	
	////////////////////////////////////////////////////////////
	////////////////////////////////////////////////////////////
	// 자동 설정 메소드
	
	// 전체 페이시 수 계산
	public void calculateTotalPage() {
		
		totalPageCount = 1;
		
		// 페이지 수 계산
		if(totalCount != 0) {
			totalPageCount = totalCount / objectPerPage;
			
			if(totalCount % objectPerPage != 0)
				totalPageCount++;
		}
		
		// 현재 페이지 수정(Validation 클래스 이용)
		// 범위를 벗어나면, 현재 페이지 수를 수정한다
		// 1보다 작으면 1로, totalPageCount보다 크면 totalPageCount로 수정
		curPageNumber = (int) Validation.ProcrustesBed(curPageNumber, 1, totalPageCount);
	}
	
	
	// offset 계산
	public void calculateOffset() {
		offset = (curPageNumber - 1) * objectPerPage;
	}
}
