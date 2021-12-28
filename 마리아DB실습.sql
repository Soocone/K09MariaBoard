/*
블럭단위 주석
*/
#라인 단위 주석

/*******
데이터타입(자료형)의 종류
1. 숫자형
*******/

CREATE TABLE tb_int(
	idx INT PRIMARY KEY AUTO_INCREMENT,
	
	num1 TINYINT UNSIGNED NOT NULL,
	num2 SMALLINT NOT NULL,
	num3 MEDIUMINT DEFAULT '100',
	num4 BIGINT,
	
	fnum1 FLOAT(10, 5) NOT NULL,
	fnum2 DOUBLE(20,10)
);
DESC tb_int;
/*
AUTO_INCREMENT: 테이블 생성시 자동 증가 컬럼으로 지정할 때 사용하는 제약조건
	오라클은 테이블과 별도로 시퀀스를 생성하지만, MySQL의 경우 테이블의 특정
	컬럼에 지정한다.
UNSIGNED: 숫자 컬럼인 경우 주로 -100~100과 같이 음수와 양수의 범위를
	사용하게 되는데, 양수만 사용하고 싶다면 unsigned를 지정한다. 이 경우
	음수의 범위만큼 양수의 범위가 2배로 늘어나게 된다.
*/
SELECT * FROM tb_int;
# 자동증가 컬럼으로 지정한 idx는 쿼리에서 제외한다.
INSERT INTO tb_int(num1, num2, num3, num4, fnum1, fnum2)
	VALUES(101, 12346, 1234568, 1234567891,
		12346.12346, 1234567891.12345678792); #2번 실행해서 레코드 삽입
		
SELECT * FROM tb_int;
/*
자동증가 컬럼에 임의의 값을 삽입할 수는 있으나, 사용하지 않는 것을 권장한다.
*/
INSERT INTO tb_int(idx, num1, num2, num3, num4, fnum1, fnum2)
	VALUES(3, 101, 12346, 1234568, 1234567891,
		12346.12346, 1234567891.12345678792); 
		
SELECT * FROM tb_int;

#idx컬럼은 PK로 지정했으므로 빈 값은 삽입할 수 없다. 에러 발생됨
INSERT INTO tb_int(idx, num1, num2, num3, num4, fnum1, fnum2)
	VALUES('', 101, 12346, 1234568, 1234567891,
		12346.12346, 1234567891.12345678792); 

/****
2. 날짜형
****/
CREATE table tb_date(
	idx INT PRIMARY KEY AUTO_INCREMENT,
	
	DATE1 DATE NOT NULL, #날짜만 표현
	DATE2 DATETIME DEFAULT CURRENT_TIMESTAMP #날짜와 시간까지 표현
);
DESC tb_date;
DROP TABLE tb_date;
/*
CURRENT_TIMESTAMP: 날짜 타입의 컬럼에 디폴트값으로 현재 시간을 입력한다.
NOW(): insert쿼리에서 날짜타입의 컬럼에 현재 시간을 입력할 때 사용하는 함수
*/
INSERT INTO tb_date(DATE1, DATE2) VALUES('2021-12-27', NOW());
INSERT INTO tb_date(DATE1) VALUES ('2021-12-25');

SELECT * FROM tb_date;

/*
날짜 및 시간 변환함수
	: date_format(컬럼명, '서식')
*/
SELECT * FROM tb_date;
SELECT DATE_FORMAT(DATE2, '%Y-%m-%d') FROM tb_date; #날짜 서식
SELECT DATE_FORMAT(DATE2, '%H:%i:%s') FROM tb_date; #시간 서식

/****
3. 문자형
****/
CREATE TABLE tb_string(
	idx INT PRIMARY KEY AUTO_INCREMENT,
	
	str1 VARCHAR(30) NOT NULL,
	str2 text
);
DESC tb_string;
/*
오라클에서는 varchar2(크기)로 사용하지만 
MySQL에서는 짧은 글인 경우 varchar(크기), 긴 글인 경우 text를 사용한다.
*/
INSERT INTO tb_string(str1, str2) VALUES('나는 짧은글', '나는 짱 긴글');
SELECT * FROM tb_string;

/****
4. 특수형
****/
/*
	enum: 정해진 항목중에서 하나만 선택 가능함.
	set: 정해진 항목중 여러개를 선택할 수 있음. 콤마로 구분해서 선택한다.
*/
CREATE TABLE tb_spec(
	idx INT AUTO_INCREMENT,
	
	spec1 ENUM('M','W','T'),
	spec2 SET('A','B','C','D'),
	
	PRIMARY KEY(idx)
);
DESC tb_spec;
#설정된 값 중 선택해서 입력하므로 정상입력됨
INSERT INTO tb_spec(spec1, spec2) VALUES('W', 'A,B,C');
# spec1은 null을 허용하는 컬럼이므로 정상입력됨
INSERT INTO tb_spec(spec2) VALUES('A,D');
# 설정된 값이 아니므로 입력시 오류 발생됨
INSERT INTO tb_spec(spec1, spec2) VALUES('W', 'A,B,E');

SELECT * FROM tb_spec;


#DML문은 오라클과 완전히 동일함
INSERT INTO tb_string (str1, str2) VALUES('오라클이랑', '동일하네유');
SELECT * FROM tb_string;
UPDATE tb_string SET str1='내용수정됨' WHERE idx=1;
SELECT * FROM tb_string;
DELETE FROM tb_string WHERE idx=1;
SELECT * FROM tb_string;
INSERT INTO tb_string (str1, str2) VALUES('자동증가컬럼', '테스트해봅시다');
SELECT * FROM tb_string;


/******
모델1 방식의 게시판을 MariaDB로 컨버팅
회원테이블과 게시판 테이블 생성 및 외래키 설정
******/
#회원테이블(부모)
CREATE TABLE member
(
	id VARCHAR(30) NOT NULL,
	pass VARCHAR(30) NOT NULL,
	NAME varchar(30)NOT NULL,
	regidate DATETIME DEFAULT CURRENT_TIMESTAMP,
	PRIMARY KEY(id)
);
DESC member;
#게시판테이블(자식)
CREATE TABLE board
(
	num INT NOT NULL AUTO_INCREMENT,
	title VARCHAR(100) NOT NULL,
	content TEXT NOT NULL,
	id VARCHAR(30) NOT NULL,
	postdate DATETIME DEFAULT CURRENT_TIMESTAMP,
	visitcount MEDIUMINT NOT NULL DEFAULT 0,
	PRIMARY KEY(num)
);
DESC board;
#외래키 설정
ALTER TABLE board ADD CONSTRAINT fk_board_member
	FOREIGN KEY (id) REFERENCES member (id);

#더미데이터 삽입
INSERT INTO member (id, pass, NAME) VALUES ('kosmo', '1234', '코스모인');

#부모테이블인 member에 먼저 삽입한 후 자식테이블인 board에 삽입한다.
INSERT INTO board (title, content, id)
	VALUES ('제목입니다1', '내용입니다1', 'kosmo');
INSERT INTO board (title, content, id)
	VALUES ('제목입니다2', '내용입니다2', 'kosmo');
INSERT INTO board (title, content, id)
	VALUES ('제목입니다3', '내용입니다3', 'kosmo');
INSERT INTO board (title, content, id)
	VALUES ('제목입니다4', '내용입니다4', 'kosmo');
INSERT INTO board (title, content, id)
	VALUES ('제목입니다5', '내용입니다5', 'kosmo');

#전체 데이터 확인하기
SELECT * FROM member;
SELECT * FROM board;

#join문 테스트
SELECT * FROM board B INNER JOIN member M
	ON B.id=M.id;

#MariaDB에서는 commit이 필요 없다. 삽입한 데이터는 자동 커밋 된다.

#회원 로그인을 위한 쿼리문 테스트
SELECT * FROM member;
SELECT * FROM member WHERE id='kosmo' AND pass='1234';
SELECT * FROM member WHERE id='kosmo' AND pass='9999'; #일치하는 레코드 없음


#게시판 목록 구현을 위한 쿼리문 테스트


#게시물 수 카운트하기
SELECT COUNT(*) FROM board;
#검색어가 있는 경우 카운트하기
SELECT COUNT(*) FROM board WHERE title LIKE '%입력-9%';
#회원테이블과 join해서 카운트하기
SELECT COUNT(*) FROM board INNER JOIN member
	ON board.id=member.id;

#페이징 처리를 위한 쿼리문 테스트
SELECT * FROM board ORDER BY num DESC;

/*
	MySQL에서는 게시물의 구간을 정해 가져오기 위해 limit를 사용한다.
	형식]
		.. 쿼리문 limit 시작 위치, 레코드 갯수;
*/
#한 페이지에 5개의 게시물을 출력한다고 가정하면
SELECT * FROM board ORDER BY num DESC LIMIT 0, 5; #1페이지 출력 내용
SELECT * FROM board ORDER BY num DESC LIMIT 5, 5; #2페이지 출력 내용
SELECT * FROM board ORDER BY num DESC LIMIT 10, 5; #3페이지 출력 내용


/*****
모델2 방식의 파일첨부형 게시판 컨버팅
*****/
CREATE TABLE mvcboard (
    idx INT PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR(50) not NULL,
    title VARCHAR(200) not null, 
    content TEXT not null,
    postdate DATETIME DEFAULT CURRENT_TIMESTAMP,
    ofile VARCHAR(200),
    sfile VARCHAR(30),
    downcount SMALLINT default 0 not NULL,
    pass VARCHAR(50) not NULL,
    visitcount SMALLINT default 0 not NULL
);
drop table mvcboard;
DESC mvcboard;

#더미데이터 입력
insert into mvcboard(name, title, content, pass)
    values('김유신', '자료실 제목1 입니다.', '내용', '1234');
insert into mvcboard(name, title, content, pass)
    values('장보고', '자료실 제목2 입니다.', '내용', '1234');
insert into mvcboard(name, title, content, pass)
    values('이순신', '자료실 제목3 입니다.', '내용', '1234');
insert into mvcboard(name, title, content, pass)
    values('강감찬', '자료실 제목4 입니다.', '내용', '1234');
insert into mvcboard(name, title, content, pass)
    values('대조영', '자료실 제목5 입니다.', '내용', '1234');


