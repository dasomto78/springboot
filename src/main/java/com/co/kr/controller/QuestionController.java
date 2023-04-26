package com.co.kr.controller;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.code.Code;
import com.co.kr.domain.LoginDomain;
import com.co.kr.domain.QuestionCommentContentDomain;
import com.co.kr.domain.QuestionCommentListDomain;
import com.co.kr.domain.QuestionFileDomain;
import com.co.kr.domain.QuestionListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.service.QuestionCommentService;
import com.co.kr.service.QuestionService;
import com.co.kr.util.Pagination;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class QuestionController {

	@Autowired
	private QuestionService questionService;

	@Autowired
	private QuestionCommentService questionCommentService;
	
	@PostMapping(value = "/qdetail")
	//리스트 하나 가져오기 따로 함수뺌
		public ModelAndView qSelectOneCall(@ModelAttribute("fileListVO") FileListVO fileListVO, String qSeq, HttpServletRequest request) {
			ModelAndView mav = new ModelAndView();
			HashMap<String, Object> map = new HashMap<String, Object>();
			HttpSession session = request.getSession();
			
			map.put("qSeq", Integer.parseInt(qSeq));
			QuestionListDomain questionListDomain = questionService.questionSelectOne(map);
			System.out.println("questionListDomain"+questionListDomain);
			List<QuestionFileDomain> fileList =  questionService.questionSelectOneFile(map);
			List<QuestionCommentListDomain> questionCommentListDomains = questionCommentService.questioncommentList(map);
			System.out.println(questionCommentListDomains);
			for (QuestionFileDomain list : fileList) {
				String path = list.getQupFilePath().replaceAll("\\\\", "/");
				list.setQupFilePath(path);
			}
			
			mav.addObject("qcitems", questionCommentListDomains);
			mav.addObject("qdetail", questionListDomain);
			mav.addObject("files", fileList);
			mav.setViewName("/question/questionList.html");
			//삭제시 사용할 용도
			session.setAttribute("files", fileList);

			return mav;
		}
	
/*	public ModelAndView scSelect(FileListVO fileListVO) {
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("stSeq", fileListVO.getStscseq());
		List<StudyCommentListDomain> studyCommentListDomains = studyCommentService.studycommentList(map);
		mav.addObject("scitems", studyCommentListDomains);
		return mav;
	} */
	
	
	@PostMapping(value = "qupload")
	public ModelAndView qUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq, @RequestParam("category") String category) throws IOException, ParseException {
		
		ModelAndView mav = new ModelAndView();
		int qSeq = questionService.fileProcess(fileListVO, request, httpReq, category);
		fileListVO.setContent(""); //초기화
		fileListVO.setTitle(""); //초기화
		
		// 화면에서 넘어올때는 bdSeq String이라 string으로 변환해서 넣어즘
		mav = qSelectOneCall(fileListVO, String.valueOf(qSeq),request);
		mav.setViewName("question/questionList.html");
		return mav;
		
	}
	
	@PostMapping(value = "qcupload")
	public ModelAndView qcUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException, ParseException {
		
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<>();
		
		int qSeq = questionCommentService.fileProcess(fileListVO, httpReq);
		fileListVO.setContent(""); //초기화
		map.put("qSeq", qSeq);
		List<QuestionCommentListDomain> questionCommentListDomains = questionCommentService.questioncommentList(map);
		
		mav = qSelectOneCall(fileListVO, String.valueOf(qSeq), request);
		mav.addObject("qcitems", questionCommentListDomains);
		mav.setViewName("question/questionList.html");
		return mav;
		
	}
	
	//detail
		@GetMapping("qdetail")
	    public ModelAndView qDetail(@ModelAttribute("fileListVO") FileListVO fileListVO, @RequestParam("qSeq") String qSeq, HttpServletRequest request) throws IOException {
			ModelAndView mav = new ModelAndView();
			//하나파일 가져오기
			mav = qSelectOneCall(fileListVO, qSeq,request);
			mav.setViewName("question/questionList.html");
			return mav;
		}
		@GetMapping("qedit")
			public ModelAndView qedit(FileListVO fileListVO, @RequestParam("qSeq") String qSeq, HttpServletRequest request) throws IOException {
				ModelAndView mav = new ModelAndView();

				HashMap<String, Object> map = new HashMap<String, Object>();
				HttpSession session = request.getSession();
				
				map.put("qSeq", Integer.parseInt(qSeq));
				QuestionListDomain questionListDomain =questionService.questionSelectOne(map);
				List<QuestionFileDomain> fileList =  questionService.questionSelectOneFile(map);
				
				for (QuestionFileDomain list : fileList) {
					String path = list.getQupFilePath().replaceAll("\\\\", "/");
					list.setQupFilePath(path);
				}

				fileListVO.setSeq(questionListDomain.getQSeq());
				fileListVO.setContent(questionListDomain.getQContent());
				fileListVO.setTitle(questionListDomain.getQTitle());
				fileListVO.setIsEdit("edit");  // upload 재활용하기위해서
				
				mav.addObject("qdetail", questionListDomain);
				mav.addObject("files", fileList);
				mav.addObject("fileLen",fileList.size());
				
				mav.setViewName("question/questionEditList.html");
				return mav;
			}
		
		@GetMapping("qcedit")
		public ModelAndView qcedit(FileListVO fileListVO, @RequestParam("qcSeq") String qcSeq, HttpServletRequest request) throws IOException {
			
			ModelAndView mav = new ModelAndView();
			HashMap<String, Object> map = new HashMap<String, Object>();
			HttpSession session = request.getSession();
			
			map.put("qcSeq", qcSeq);
			QuestionCommentListDomain questionCommentListDomain = questionCommentService.questioncommentSelectOne(map);

			fileListVO.setSeq(questionCommentListDomain.getQcSeq());
			fileListVO.setContent(questionCommentListDomain.getQcContent());
			fileListVO.setIsEdit("edit");  // upload 재활용하기위해서
			
			mav.addObject("qcitems", questionCommentListDomain);
			mav.setViewName("question/questionEditList.html");
			return mav;
		}
		
		@PostMapping("qeditSave")
			public ModelAndView qeditSave(@ModelAttribute("fileListVO") FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq, @RequestParam("category") String category) throws IOException {
				ModelAndView mav = new ModelAndView();
				//저장
				questionService.fileProcess(fileListVO, request, httpReq, category);
				
				
				mav = qSelectOneCall(fileListVO, fileListVO.getSeq(),request);
				fileListVO.setContent(""); //초기화
				fileListVO.setTitle(""); //초기화
				mav.setViewName("question/questionList.html");
				return mav;
			}
		
		@PostMapping("qceditSave")
		public ModelAndView qceditSave(@ModelAttribute("fileListVO") FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
			ModelAndView mav = new ModelAndView();
			//저장
			int qSeq = questionCommentService.fileProcess(fileListVO, httpReq);
			HashMap<String, Object> map = new HashMap<>();
			
			map.put("qSeq", qSeq);
			List<QuestionCommentListDomain> questionCommentListDomains = questionCommentService.questioncommentList(map);
			
			mav = qSelectOneCall(fileListVO, String.valueOf(qSeq),request);
			mav.addObject("qcitems", questionCommentListDomains);
			fileListVO.setContent(""); //초기화
			fileListVO.setTitle(""); //초기화
			mav.setViewName("question/questionList.html");
			return mav;
		}
		
		@GetMapping("qremove")
		public ModelAndView qRemove(@RequestParam("qSeq") String qSeq, HttpServletRequest request) throws IOException {
			ModelAndView mav = new ModelAndView();
			
			HttpSession session = request.getSession();
			HashMap<String, Object> map = new HashMap<String, Object>();
			List<QuestionFileDomain> fileList = null;
			if(session.getAttribute("files") != null) {						
				fileList = (List<QuestionFileDomain>) session.getAttribute("files");
			}
			
			map.put("qSeq", Integer.parseInt(qSeq));
			
			//내용삭제
			questionService.qContentRemove(map);

			for (QuestionFileDomain list : fileList) {
				list.getQupFilePath();
				Path filePath = Paths.get(list.getQupFilePath());
		 
		        try {
		        	
		            // 파일 물리삭제
		            Files.deleteIfExists(filePath); // notfound시 exception 발생안하고 false 처리
		            // db 삭제 
								questionService.qFileRemove(list);
					
		        } catch (DirectoryNotEmptyException e) {
								throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
			qcAllRemove(qSeq);
			//세션해제
			session.removeAttribute("files"); // 삭제
			
			mav = Paging(request);
			return mav; 
		}

		public void qcAllRemove(String qSeq) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("qSeq", qSeq);
			questionCommentService.questioncommentAllContentRemove(map);
		}
		
		@GetMapping("qcremove")
		public ModelAndView qcRemove(@RequestParam("qcSeq") String qcSeq, @RequestParam("qSeq") String qSeq, FileListVO fileListVO ,HttpServletRequest request) throws IOException {
			ModelAndView mav = new ModelAndView();
			
			HttpSession session = request.getSession();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("qcSeq", Integer.parseInt(qcSeq));
			
			//내용삭제
			questionCommentService.questioncommentContentRemove(map);
			
			map.put("qSeq", Integer.parseInt(qSeq));
			List<QuestionCommentListDomain> questionCommentListDomains = questionCommentService.questioncommentList(map);
			mav = qSelectOneCall(fileListVO, qSeq, request);
			mav.addObject("qcitems", questionCommentListDomains);
			mav.setViewName("question/questionList.html");
			
			return mav;
		}
		
		@RequestMapping("questionselect")
		public ModelAndView questionSelect(FileListVO fileListVO, @RequestParam("category") String category) {
			ModelAndView mav = new ModelAndView();
			String keyword = fileListVO.getKeyword();
			HashMap<String, Object> map = new HashMap<>();
			map.put("qTitle", keyword);
			map.put("qCategory", category);
			List<QuestionListDomain> questionListDomains = questionService.questionSelectSelect(map);
			mav.addObject("items", questionListDomains);
			mav.setViewName("question/questionList.html");
			return mav;
		}


	//리스트 가져오기 따로 함수뺌
	public ModelAndView qListCall(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		//페이지네이션 추가  SELECT * FROM jsp.member order by mb_update_at limit 1, 5; {offset}{limit}

		//전체 갯수
		int totalcount = questionService.qGetAll();
		int contentnum = 10;
		
		
		//데이터 유무 분기
		boolean itemsNotEmpty;
		
		if(totalcount > 0) { // 데이터 있을때
			
			// itemsNotEmpty true일때만, 리스트 & 페이징 보여주기
			itemsNotEmpty = true;
			//페이지 표현 데이터 가져오기
			Map<String,Object> pagination = Pagination.pagination(totalcount, request);
			
			HashMap<String, Object> map = new HashMap<>();
	        map.put("offset",pagination.get("offset"));
	        map.put("contentnum",contentnum);
			
	        //페이지별 데이터 가져오기
			List<QuestionListDomain> questionListDomains = questionService.questionAllList(map);
			
			//모델객체 넣어주기
			mav.addObject("itemsNotEmpty", itemsNotEmpty);
			mav.addObject("items", questionListDomains);
			mav.addObject("rowNUM", pagination.get("rowNUM"));
			mav.addObject("pageNum", pagination.get("pageNum"));
			mav.addObject("startpage", pagination.get("startpage"));
			mav.addObject("endpage", pagination.get("endpage"));
			
		}else {
			itemsNotEmpty = false;
		}
		
		return mav;
	}
	
	public ModelAndView Paging(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		HttpSession session = request.getSession();
		String page = (String) session.getAttribute("page");
		if(page == null)page = "1";
		session.setAttribute("page", page);
		mav = qListCall(request);
		mav.setViewName("question/questionList.html");
		return mav;
	}
	}