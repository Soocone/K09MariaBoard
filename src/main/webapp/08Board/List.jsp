<%@page import="model1.board.BoardDTO"%>
<%@page import="java.util.List"%>
<%@page import="java.util.HashMap"%>
<%@page import="java.util.Map"%>
<%@page import="model1.board.BoardDAO"%>
<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%
//DAO 객체 생성 및 DB 연결
BoardDAO dao = new BoardDAO(application);

//검색어가 있는 경우 파라미터를 저장하기 위한 Map컬렉션 생성
Map<String, Object> param = new HashMap<String, Object>();
//검색 파라미터를 request 내장객체를 통해 얻어온다.
String searchField = request.getParameter("searchField");
String searchWord = request.getParameter("searchWord");

//검색어가 있는 경우에만
if(searchWord != null){
	//Map컬렉션에 파라미터 값을 추가한다.
	param.put("searchField", searchField); //검색필드명(title, content등)
	param.put("searchWord", searchWord); //검색어
}

//board테이블에 저장된 게시물의 갯수 카운트
int totalCount = dao.selectCount(param);
//출력할 레코드 추출
List<BoardDTO> boardLists = dao.selectList(param);
//자원 해제
dao.close();
%>


<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원제 게시판</title>
</head>
<body>
    <jsp:include page="../Common/Link.jsp" /> 

    <h2>목록 보기(List)</h2>
    
    <form method="get">  
    <table border="1" width="90%">
    <tr>
        <td align="center">
            <select name="searchField"> 
                <option value="title">제목</option> 
                <option value="content">내용</option>
            </select>
            <input type="text" name="searchWord" />
            <input type="submit" value="검색하기" />
        </td>
    </tr>   
    </table>
    </form>
  
    <table border="1" width="90%">
        <!-- 각 칼럼의 이름 --> 
        <tr>
            <th width="10%">번호</th>
            <th width="50%">제목</th>
            <th width="15%">작성자</th>
            <th width="10%">조회수</th>
            <th width="15%">작성일</th>
        </tr>
<%
if(boardLists.isEmpty()){
	//게시물이 하나도 없을때
%>
		<tr>
			<td colspan="5" align="center">
				 등록된 게시물이 없습니다
			</td>
		</tr>
<%
}
else{
	//게시물이 있을때
	int virtualNum =0; //게시물의 출력 번호
	//확장for문을 통해 List컬렉션에 저장된 레코드의 갯수만큼 반복한다.
	for(BoardDTO dto : boardLists)
	{
		//전체 레코드 수를 1씩 차감하면서 번호를 출력
		virtualNum = totalCount--;
%>
        <tr align="center">
            <td><%= virtualNum %></td> 
            <td align="left"> 
                <a href="View.jsp?num=<%= dto.getNum() %>"><%= dto.getTitle() %></a>
            </td>
            <td align="center"><%= dto.getId() %></td>        
            <td align="center"><%= dto.getVisitcount() %></td> 
            <td align="center"><%= dto.getPostdate() %></td> 
        </tr>
<%
	}
}
%>
    </table>
    <table border="1" width="90%">
        <tr align="right">
            <td><button type="button" onclick="location.href='Write.jsp';">글쓰기
                </button></td>
        </tr>
    </table>
</body>
</html>
