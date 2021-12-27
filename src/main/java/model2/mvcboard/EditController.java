package model2.mvcboard;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import fileupload.FileUtil;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.oreilly.servlet.MultipartRequest;


import utils.JSFunction;

@WebServlet("/mvcboard/edit.do")
public class EditController extends HttpServlet
{
	//수정페이지 진입부분
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		//일련번호 파라미터 받기
		String idx = req.getParameter("idx");
		MVCBoardDAO dao = new MVCBoardDAO();
		//게시물 가져오기
		MVCBoardDTO dto = dao.selectView(idx);
		//DTO객체를 리퀘스트 영역에 저장
		req.setAttribute("dto", dto);
		
		req.getRequestDispatcher("/14MVCBoard/Edit.jsp").forward(req, resp);
	}
	
	
	//수정처리
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		//물리적 경로 얻어오기
		String saveDirectory = req.getServletContext().getRealPath("/Uploads");
		
		//application 내장객체를 통해 web.xml에 등록된 초기화 파라미터 얻어옴
		ServletContext application = this.getServletContext();
		
		//업로드할 파일의 최대용량 설정(만약 파일이 2개라면 둘을 합친 용량으로 설정)
		int maxPostSize = Integer.parseInt(
				application.getInitParameter("maxPostSize"));

		//파일 업로드 처리
		MultipartRequest mr = FileUtil.uploadFile(req, saveDirectory, maxPostSize);
		//업로드에 실패한 경우( 파일을 첨부하지 않더라도 객체는 생성된다)
		if(mr == null) {
			//경고창을 띄우고 쓰기 페이지로 이동
			JSFunction.alertBack(resp, "첨부 파일이 제한 용량을 초과합니다.");
			return;
		}
		
		//폼값 저장
		String idx = mr.getParameter("idx");//일련번호
		//새롭게 등록된 파일이 없는 경우 Query문에 사용함.
		String prevOfile = mr.getParameter("prevOfile");//기존 DB에 등록된 원본 파일명
		String prevSfile = mr.getParameter("prevSfile");//기존 DB에 등록된 저장된 파일명
		
		//수정페이지에서 새롭게 입력한 폼값
		String name = mr.getParameter("name");
		String title = mr.getParameter("title");
		String content = mr.getParameter("content");
		
		//패스워드 검증시 session영역에 저장된 속성값을 가져와서 DTO에 저장
		HttpSession session = req.getSession();
		String pass = (String)session.getAttribute("pass");
		
		//DTO에 데이터 세팅
		MVCBoardDTO dto = new MVCBoardDTO();
		dto.setIdx(idx);
		dto.setName(name);
		dto.setTitle(title);
		dto.setContent(content);
		dto.setPass(pass);
		
		
		//새롭게 저장된 파일이 있는지 확인하기 위해 파일명을 얻어옴
		String fileName = mr.getFilesystemName("ofile");
		//새롭게 서버에 저장된 파일이 있는 경우 아래 처리를 한다.
		if(fileName != null) {
			//현재 날짜와  시간 및 밀리세컨즈까지 이용해서 파일명으로 사용할 문자열을 만든다.
			String now = new SimpleDateFormat("yyyyMMdd_HmsS").format(new Date());
			String ext = fileName.substring(fileName.lastIndexOf("."));
			//확장자와 파일명을 합쳐서 저장할 파일명을 만들어준다.
			String newFileName = now + ext;
			
			//기존의 파일명을 새로운 파일명으로 변경한다.  
			//파일객체 생성
			//File.separator = / 또는 \ 임. 윈도우에 맞게 설정해줌
			File oldFile = new File(saveDirectory + File.separator + fileName); //사과.png
			File newFile = new File(saveDirectory + File.separator + newFileName); //20211213_123456.png
			oldFile.renameTo(newFile); //old파일이 new 파일로 이름이 바뀜
			
			//DTO객체에 원본파일명과 저장된파일명을 저장한다.
			dto.setOfile(fileName);
			dto.setSfile(newFileName);
			
			//새로운 파일이 등록되었으므로 기존에 등록한 파일은 삭제처리
			FileUtil.deleteFile(req, "/Uploads", prevSfile);
		}
		//새롭게 등록한 파일이 없는 경우
		else {
			//기존에 등록한 파일명을 유지한다.
			dto.setOfile(prevOfile);
			dto.setSfile(prevSfile);
		}
		
		//DB업데이트 처리
		MVCBoardDAO dao = new MVCBoardDAO();
		int result =dao.updatePost(dto);
		//여기서는 자원 반납. pool을 쓰고 있으므로
		dao.close();
		if(result ==1) {//수정이 완료되었다면
			//세션영역에 저장된 패스워드는 삭제
			session.removeAttribute("pass");
			//쓰기에 성공한 경우 상세보기 페이지로 이동
			resp.sendRedirect("../mvcboard/view.do?idx="+idx);
		}
		else {
			//실패하면 쓰기페이지로 이동한다.
			JSFunction.alertLocation(resp, "비밀번호 검증을 다시 진행해주세요",
					"../mvcboard/view.do?idx="+idx);
		}
	}
}
