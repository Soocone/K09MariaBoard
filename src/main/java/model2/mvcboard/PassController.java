package model2.mvcboard;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import fileupload.FileUtil;
import utils.JSFunction;

@WebServlet("/mvcboard/pass.do")
public class PassController extends HttpServlet
{
	//패스워드 검증을 위한 페이지 진입
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		//수정 혹은 삭제를 위한 파라미터 받기
		req.setAttribute("mode", req.getParameter("mode"));
		//포워드
		req.getRequestDispatcher("/14MVCBoard/Pass.jsp").forward(req, resp);
	}
	
	
	//패스워드 확인 후 전송했을때 처리
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		//폼값 저장
		String idx = req.getParameter("idx");
		String mode = req.getParameter("mode");
		String pass = req.getParameter("pass");
		
		//DB연결 후 패스워드 검증
		MVCBoardDAO dao = new MVCBoardDAO();
		boolean confirmed = dao.confirmPassword(pass, idx);
		dao.close();
		
		if(confirmed) { //자체가 true이므로 패스워드가 일치한다면~
			if(mode.equals("edit")) {//수정일때
				//서블릿에서 session 내장객체를 얻어온다.
				HttpSession session = req.getSession();
				//인증이 완료된 패스워드를 세션영역에 저장한다.
				session.setAttribute("pass", pass);
				//수정 페이지로 이동한다. 이때 세션영역의 속성값은 사라지지 않는다.
				resp.sendRedirect("../mvcboard/edit.do?idx="+ idx);
			}
			else if(mode.equals("delete")) {//삭제일때
				dao = new MVCBoardDAO();
				//기존의 게시물 가져오기
				MVCBoardDTO dto = dao.selectView(idx);
				//게시물 삭제
				int result = dao.deletePost(idx);
				dao.close();
				if(result==1) { //게시물 삭제가 완료되었다면
					//서버에 저장된 파일명을 가져온 후 파일을 삭제한다.
					String saveFileName = dto.getSfile();
					FileUtil.deleteFile(req, "/Uploads", saveFileName);
				}
				JSFunction.alertLocation(resp, "삭제되었습니다.", "../mvcboard/list.do");
			}
		}
		else {
			JSFunction.alertBack(resp, "비밀번호 검증에 실패했습니다.");
		}
	}
}
