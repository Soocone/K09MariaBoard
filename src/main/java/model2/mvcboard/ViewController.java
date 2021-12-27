package model2.mvcboard;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/mvcboard/view.do")
public class ViewController extends HttpServlet
{
	/*
	 service()는 get방식과 post방식의 요청을 동시에 처리할 수 있다.
	 */
	@Override
	protected void service(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		//커넥션풀을 통해 DB연결
		MVCBoardDAO dao = new MVCBoardDAO();
		//일련번호를 파라미터로 받기
		String idx = req.getParameter("idx");
		//조회수 증가
		dao.updateVisitCount(idx);
		//게시물 조회
		MVCBoardDTO dto = dao.selectView(idx);
		//자원 반납
		dao.close();
		
		/*
		 내용에 대해 줄바꿈 처리를 위해 <br>태그로 변환한다.
		 db에는 \r\n으로 들어가는 거고.
		 */
		dto.setContent(dto.getContent().replaceAll("\r\n", "<br>"));
		
		//request영역에 DTO객체를 저장한다.
		req.setAttribute("dto", dto);
		//View로 포워드
		req.getRequestDispatcher("/14MVCBoard/View.jsp").forward(req, resp);
	}
}
