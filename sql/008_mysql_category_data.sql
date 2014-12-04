use anytimemeet;

/* temporary data(for test) */
INSERT INTO club_categories (category_id, category_title) VALUES (1, "스포츠");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (101, 1, "야구");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (102, 1, "축구");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (103, 1, "농구");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (104, 1, "배구");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (105, 1, "골프");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (106, 1, "당구");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (107, 1, "족구");
	
INSERT INTO club_categories (category_id, category_title) VALUES (2, "문화/예술");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (201, 2, "음악");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (202, 2, "영화");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (203, 2, "미술");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (204, 2, "문학");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (205, 2, "밴드/그룹");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (206, 2, "창작");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (207, 2, "시");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (208, 2, "소설");
	
INSERT INTO club_categories (category_id, category_title) VALUES (3, "여행");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (301, 3, "국내여행");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (302, 3, "해외여행");
	
INSERT INTO club_categories (category_id, category_title) VALUES (4, "학술");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (401, 4, "수학");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (402, 4, "과학");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (403, 4, "경제학");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (404, 4, "역사");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (405, 4, "외국어");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (406, 4, "문학");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (407, 4, "철학");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (408, 4, "음악");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (409, 4, "미술");
	
INSERT INTO club_categories (category_id, category_title) VALUES (5, "패션");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (501, 5, "노홍철 패션 따라잡기");
	
INSERT INTO club_categories (category_id, category_title) VALUES (6, "정치/사회");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (601, 6, "정치사회 일반");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (602, 6, "토론");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (603, 6, "정치인");

INSERT INTO club_categories (category_id, category_title) VALUES (7, "경제");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (701, 7, "경제일반");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (702, 7, "투자");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (703, 7, "토론");
	
INSERT INTO club_categories (category_id, category_title) VALUES (8, "친목");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (801, 8, "소셜다이닝");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (802, 8, "음주가무");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (803, 8, "나이/띠/지역");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (804, 8, "친목 기타");
	
INSERT INTO club_categories (category_id, category_title) VALUES (9, "게임");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (901, 9, "롤플레잉");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (902, 9, "전략");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (903, 9, "총쏴");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (904, 9, "에뮬레이션");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (905, 9, "악션");
	
INSERT INTO club_categories (category_id, category_title) VALUES (10, "취미");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (1001, 10, "십자수");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (1002, 10, "수집");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (1003, 10, "야동");

INSERT INTO club_categories (category_id, category_title) VALUES (11, "방송");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (1101, 11, "드라마");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (1102, 11, "다큐/시사");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (1103, 11, "예능");
	INSERT INTO club_categories (category_id, parent_cat_id, category_title) VALUES (1104, 11, "해외방송");

INSERT INTO club_categories (category_id, category_title) VALUES (12, "기타");