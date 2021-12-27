package model2.mvcboard;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.oreilly.servlet.MultipartRequest;

import fileupload.FileUtil;
import utils.JSFunction;

/*
 	글쓰기 페이지로 진입시에는 단순히 페이지 이동으로 doGet()이 요청을 처리하고
 	내용을 채운후 작성할때는 Post방식으로 전송하므로 doPost()가 요청을 처리한다.
 */
public class WriteController extends HttpServlet
{
	//글쓰기 페이지 진입
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		req.getRequestDispatcher("/14MVCBoard/Write.jsp").forward(req, resp);
	}
	
	
	//글쓰기 처리
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		//파일이 저장될 디렉토리의 물리적 경로 얻어오기
		String saveDirectory = req.getServletContext().getRealPath("/Uploads");
		
		//application 내장객체를 통해 web.xml에 등록된 초기화 파라미터 얻어옴
		ServletContext application = getServletContext();
		
		//업로드할 파일의 최대용량 설정(만약 파일이 2개라면 둘을 합친 용량으로 설정)
		int maxPostSize = Integer.parseInt(
				application.getInitParameter("maxPostSize"));

		//파일 업로드 처리
		MultipartRequest mr = FileUtil.uploadFile(req, saveDirectory, maxPostSize);
		//업로드에 실패한 경우( 파일을 첨부하지 않더라도 객체는 생성된다)
		if(mr == null) {
			//경고창을 띄우고 쓰기 페이지로 이동
			JSFunction.alertLocation(resp, "첨부 파일이 제한 용량을 초과합니다.",
					"../mvcboard/write.do");
			return;
		}
		
		
		//업로드에 성공했다면 DTO 객체 생성 및 폼값 설정
		MVCBoardDTO dto = new MVCBoardDTO();
		dto.setName(mr.getParameter("name"));
		dto.setTitle(mr.getParameter("title"));
		dto.setContent(mr.getParameter("content"));
		dto.setPass(mr.getParameter("pass"));
		
		//업로드된 원본 파일명을 가져온다.
		String fileName = mr.getFilesystemName("ofile");
		//서버에 저장된 파일이 있는 경우 아래 처리를 한다.
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
		}
		
		//새로운 게시물을 테이블에 저장한다.
		MVCBoardDAO dao = new MVCBoardDAO();
		int result =dao.insertWrite(dto);
		//여기서는 자원 반납. pool을 쓰고 있으므로
		dao.close();
		if(result ==1) {
			//쓰기에 성공한 경우 리스트로 이동한다.
			resp.sendRedirect("../mvcboard/list.do");
		}
		else {
			//실패하면 쓰기페이지로 이동한다.
			resp.sendRedirect("../mvcboard/write.do");
		}
		
		
		
	}
}
