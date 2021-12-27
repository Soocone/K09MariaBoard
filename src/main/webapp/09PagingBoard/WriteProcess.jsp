<%@page import="model1.board.BoardDAO"%>
<%@page import="model1.board.BoardDTO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!-- 글쓰기 처리(insert) 전 로그인 확인 -->
<%@ include file="./IsLoggedIn.jsp" %>
<%
//폼값 받기
String title = request.getParameter("title");
String content = request.getParameter("content");

//사용자가 입력한 폼값을 저장하기 위해 DTO 객체 생성
BoardDTO dto = new BoardDTO();
dto.setTitle(title);
dto.setContent(content);
//세션영역에 저장된 회원 인증정보(아이디)를 가져와서 DTO에 저장한다.
dto.setId(session.getAttribute("UserId").toString());

//DAO객체 생성 및 DB연결
BoardDAO dao = new BoardDAO(application);



/* 기존에 1개씩 입력하는 방식  */
//dto 객체를 매개변수로 전달하여 레코드 insert 처리
int iResult = dao.insertWrite(dto);

 
/* 더미데이터 100개를 한꺼번에 입력하기 */
/*
int iResult = 0;
for(int i=1; i<=100; i++){
	dto.setTitle(title +"-"+ i);
	iResult = dao.insertWrite(dto);
} 
*/





//자원 해제
dao.close();

if(iResult ==1){
	//글쓰기에 성공했다면 리스트(목록) 페이지로 이동한다.
	response.sendRedirect("List.jsp");
}
else{
	//실패한 경우에는 글쓰기 페이지로 이동한다. 즉 뒤로 이동한다.
	JSFunction.alertBack("글쓰기에 실패하였습니다", out);
}
%>
