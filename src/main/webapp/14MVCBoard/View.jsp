<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>파일 첨부형 게시판</title>
</head>
<body>
<h2>파일 첨부형 게시판 - 상세 보기(View)</h2>

<!-- 
	상세보기 페이지는 작성폼은 아니나 삭제시 게시물의 일련번호를 post방식으로
	전송해야하므로 <form>태그를 사용하였다.
 -->
    <table border="1" width="90%">
    	<colgroup>
    		<col width="15%"><col width="35%"/>
    		<col width="15%"><col width="*"/>
    	</colgroup>
        <tr>
            <td>번호</td><td>${dto.idx }</td>
            <td>작성자</td><td>${dto.name }</td>
        </tr>
        <tr>
            <td>작성일</td><td>${dto.postdate }</td>
            <td>조회수</td><td>${dto.visitcount }</td>
        </tr>
        <tr>
            <td>제목</td>
            <td colspan="3">${dto.title }</td>
        </tr>
        <tr>
            <td>내용</td>
            <td colspan="3" height="100">${dto.content }</td> 
        </tr>
        <tr>
            <td>첨부파일</td>
            <td>
            <!-- 
            첨부된 파일이 있는 경우에는 파일명과 다운로드 링크를 출력한다.
            다운로드가 완료되면 카운트 하기 위해 idx(일련번호)를 파라미터로 받는다.
             -->
            	<c:if test="${not empty dto.ofile }">
            	${dto.ofile }
            	<a href="../mvcboard/download.do?ofile=${dto.ofile }&sfile=${dto.sfile}&idx=${dto.idx}">
            	[다운로드]
            	</a></c:if>
            </td>
            <td>다운로드수</td>
            <td>${dto.downcount }</td>
        </tr>
        
        <tr>
            <td colspan="4" align="center">    
                <button type="button"
                		onclick="location.href='../mvcboard/pass.do?mode=edit&idx=${param.idx }';">
                	수정하기</button>
                <button type="button" 
                		onclick="location.href='../mvcboard/pass.do?mode=delete&idx=${param.idx }';">
                	삭제하기</button>  
                <button type="button" 
                	onclick="location.href='../mvcboard/list.do';">
                	목록 바로가기</button>
            </td>
        </tr>
    </table>
</form>
</body>
</html>