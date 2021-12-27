package model1.board;

import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;
import javax.servlet.ServletContext;
import common.JDBConnect; //DB 연결을 위한 클래스로 패키지가 다르므로 import해야함

public class BoardDAO extends JDBConnect
{
	//부모의 인자생성자를 호출한다. 이때 application 내장객체를 매개변수로 전달한다.
	public BoardDAO(ServletContext application) {
		//내장객체를 통해 web.xml에 작성된 컨텍스트초기화 파라미터를 얻어온다.
		super(application);
	}
	
	/*
	 board테이블에 저장된 게시물의 갯수를 카운트하기 위한 메서드
	 카운트한 결과값을 통해 목록에서 게시물의 순번은 출력한다.
	 */
	public int selectCount(Map<String, Object> map) {
		//카운트 변수
		int totalCount =0;
		
		//쿼리문 작성
		String query = "SELECT COUNT(*) FROM board";
		
		//검색어가 있는 경우 where절을 동적으로 추가한다.
		if(map.get("searchWord") != null) {
			query += " WHERE " + map.get("searchField") + " "
					+ " LIKE '%" + map.get("searchWord") + "%'";
		}
		
		try {
			//정적쿼리문(?가 없는 쿼리문) 실행을 위한 Statement 객체 생성
			stmt = con.createStatement();
			//select 쿼리문을 실행 후 ResultSet 객체를 반환받음
			rs = stmt.executeQuery(query);
			//커서를 이동시켜 결과데이터를 읽음
			rs.next();
			//결과값을 변수에 저장. 첫번째 결과값을 가져온다.
			totalCount = rs.getInt(1);
		}
		catch (Exception e) {
			System.out.println("게시물 수를 구하는 중 예외 발생");
			e.printStackTrace();
		}
		
		return totalCount;
	}
	
	
	
	/*
	 목록에 출력할 게시물을 오라클로부터 추출하기 위한 쿼리문 실행(페이지 처리 없음)
	 */
	public List<BoardDTO> selectList(Map<String, Object> map){
		/*
		 board 테이블에서 select한 결과데이터를 저장하기 위한 리스트 컬렉션
		 여러가지의 List컬렉션 중 동기화가 보장되는 Vector를 사용한다.
		 */
		List<BoardDTO> bbs = new Vector<BoardDTO>();
		
		/*
		 목록에 출력할 게시물을 추출하기 위한 쿼리문으로 항상 일련번호의
		 역순(내림차순)으로 정렬해야 한다. 게시판의 목록은 최근 게시물이
		 제일 앞에 노출되기 때문이다.
		 */
		String query = "SELECT * FROM board ";
		//검색어가 있는 경우 where절을 추가한다.
		if(map.get("searchWord") != null) {
			query += " WHERE " + map.get("searchField") + " "
					+ " LIKE '%" + map.get("searchWord") + "%'";
		}
		query += " ORDER BY num DESC ";
		
		try{
			stmt = con.createStatement();
			rs = stmt.executeQuery(query);
			
			//추출된 결과에 따라 반복한다.
			while(rs.next()) {
				//하나의 레코드를 읽어서 DTO객체에 저장한다.
				BoardDTO dto = new BoardDTO();
				
				dto.setNum(rs.getString("num"));
				dto.setTitle(rs.getString("title"));
				dto.setContent(rs.getString("content"));
				dto.setPostdate(rs.getDate("postdate"));
				dto.setId(rs.getString("id"));
				dto.setVisitcount(rs.getString("visitcount"));
				
				//리스트 컬렉션에 DTO객체를 추가한다.
				bbs.add(dto);
			}
		} 
		catch (Exception e){
			System.out.println("게시물 조회 중 예외 발생");
			e.printStackTrace();
		}
		
		return bbs;
	}
	
	
	//사용자가 입력한 내용을 board테이블에 insert 처리하는 메서드
	public int insertWrite(BoardDTO dto) {
		//입력결과 확인용 변수
		int result = 0;
		
		try{
			//인파라미터가 있는 쿼리문 작성(동적쿼리문)
			String query = "INSERT INTO board ( "
					+ " title,content,id,visitcount) "
					+ " VALUES ( "
					+ " ?, ?, ?, 0)";
			System.out.println(query);
			//동적쿼리문 실행을 위한 prepared객체 생성
			psmt = con.prepareStatement(query);
			//순서대로 인파라미터 설정
			psmt.setString(1, dto.getTitle());
			psmt.setString(2, dto.getContent());
			psmt.setString(3, dto.getId());
			
			//쿼리문 실행: 입력에 성공한다면 1이 반환된다. 실패시 0 반환.
			result = psmt.executeUpdate();
		} 
		catch (Exception e){
			System.out.println("게시물 입력 중 예외 발생");
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	//상세보기를 위해 특정 일련번호에 해당하는 게시물을 인출
	public BoardDTO selectView(String num) {
		BoardDTO dto = new BoardDTO();
		
		//join을 이용해서 member테이블의 name컬럼까지 가져온다.
		String query = "SELECT B.*, M.name "
				+ " FROM member M INNER JOIN board B "
				+ " ON M.id=B.id "
				+ " WHERE num=?";
		
		try{
			psmt = con.prepareStatement(query);
			psmt.setString(1, num);
			rs = psmt.executeQuery();
			
			//일련번호는 중복되지 않으므로 if문에서 처리하면 된다.
			if(rs.next()) { //ResultSet에서 커서를 이동시켜 레코드를 읽은 후
				//DTO객체에 레코드의 내용을 추가한다.
				dto.setNum(rs.getString(1));
				dto.setTitle(rs.getString(2));
				dto.setContent(rs.getString("content"));
				dto.setPostdate(rs.getDate("postdate")); //날짜타입이므로 getDate()사용함.
				dto.setId(rs.getString("id"));
				dto.setVisitcount(rs.getString(6));
				dto.setName(rs.getString("name"));
			}
		} 
		catch (Exception e){
			System.out.println("게시물 상세보기 중 예외 발생");
			e.printStackTrace();
		}
		
		return dto;
	}
	
	
	//게시물의 조회수를 1 증가시킨다.
	public void updateVisitCount(String num) {
		//visitcount 컬럼은 number타입이므로 덧셈이 가능하다.
		String query = "UPDATE board SET "
				+ " visitcount=visitcount+1 "
				+ " WHERE num=?";
		
		try{
			psmt = con.prepareStatement(query);
			psmt.setString(1, num);
			psmt.executeQuery();
		} 
		catch (Exception e){
			System.out.println("게시물 조회수 증가 중 예외 발생");
			e.printStackTrace();
		}
	}
	
	
	//게시물 수정: 수정할 내용을 DTO객체에 저장 후 매개변수로 전달
	public int updateEdit(BoardDTO dto) {
		int result = 0;
		
		try{
			//update를 위한 쿼리문
			String query = "UPDATE board SET "
					+ " title=?, content=? "
					+ " WHERE num=?";
			
			//prepared객체 생성
			psmt = con.prepareStatement(query);
			//인파라미터 설정
			psmt.setString(1, dto.getTitle());
			psmt.setString(2, dto.getContent());
			psmt.setString(3, dto.getNum());
			
			//쿼리 실행
			result = psmt.executeUpdate();
		} 
		catch (Exception e){
			System.out.println("게시물 수정 중 예외 발생");
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	//DTO객체를 받아 게시물 삭제처리
	public int deletePost(BoardDTO dto) {
		int result = 0;
		
		try{
			//쿼리문 작성
			String query = "DELETE FROM board WHERE num=?";
			
			//prepared객체 생성 및 인파라미터 설정
			psmt = con.prepareStatement(query);
			psmt.setString(1, dto.getNum());
			
			//쿼리 실행
			result = psmt.executeUpdate();
		} 
		catch (Exception e){
			System.out.println("게시물 삭제 중 예외 발생");
			e.printStackTrace();
		}
		
		return result;
	}
	
	
	
	//게시판의 페이징 처리를 위한 메서드
	public List<BoardDTO> selectListPage(Map<String, Object> map){
		List<BoardDTO> bbs = new ArrayList<BoardDTO>();
		
		//3개의 서브쿼리문을 통한 페이지 처리
		String query = "SELECT * FROM board ";
		
		//검색 조건 추가(검색어가 있는 경우에만 where절이 추가됨)
		if(map.get("searchWord") != null) {
			query += " WHERE " + map.get("searchField")
					+ " LIKE '%" + map.get("searchWord") + "%' ";
		}
		query += " ORDER BY num  desc LIMIT ?, ? ";
		/* JSP에서 계산된 게시물의 구간을 인파라미터로 처리함 */
		
		try{
			// 쿼리 실행을 위한 prepared객체 생성
			psmt = con.prepareStatement(query);
			//인파라미터설정: 
			/*
			 setString()으로 인파라미터를 설정하면 문자형이 되므로 값 양쪽에
			 싱글 쿼테이션이 자동으로 삽입된다. 여기서는 정수를 입력해야 하므로
			 setInt()를 사용하고, 인수로 전달되는 변수를 정수로 변경해야 한다.
			 */
			psmt.setInt(1, Integer.parseInt(map.get("start").toString()));
			psmt.setInt(2, Integer.parseInt(map.get("end").toString()));
			
			//쿼리문 실행
			rs = psmt.executeQuery();
			
			//select한 게시물의 갯수만큼 반복함
			while(rs.next()) {
				//한 행(게시물 하나)의 데이터를 DTO에 저장
				BoardDTO dto = new BoardDTO();
				
				dto.setNum(rs.getString("num"));
				dto.setTitle(rs.getString("title"));
				dto.setContent(rs.getString("content"));
				dto.setPostdate(rs.getDate("postdate"));
				dto.setId(rs.getString("id"));
				dto.setVisitcount(rs.getString("visitcount"));
				
				//반환할 결과 목록에 게시물 추가
				bbs.add(dto);
			}
		} 
		catch (Exception e){
			System.out.println("게시물 조회 중 예외 발생");
			e.printStackTrace();
		}
		
		//목록 반환
		return bbs;
	}
}
