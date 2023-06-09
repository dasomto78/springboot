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
import com.co.kr.domain.StudyCommentContentDomain;
import com.co.kr.domain.StudyCommentListDomain;
import com.co.kr.domain.StudyFileDomain;
import com.co.kr.domain.StudyListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.service.StudyCommentService;
import com.co.kr.service.StudyService;
import com.co.kr.util.Pagination;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Controller
public class StudyListController {

	@Autowired
	private StudyService studyService;

	@Autowired
	private StudyCommentService studyCommentService;
	
	@PostMapping(value = "/stdetail")
	//리스트 하나 가져오기 따로 함수뺌
		public ModelAndView stSelectOneCall(@ModelAttribute("fileListVO") FileListVO fileListVO, String stSeq, HttpServletRequest request) {
			ModelAndView mav = new ModelAndView();
			HashMap<String, Object> map = new HashMap<String, Object>();
			HttpSession session = request.getSession();
			
			map.put("stSeq", Integer.parseInt(stSeq));
			StudyListDomain studyListDomain = studyService.studySelectOne(map);
			System.out.println("studyListDomain"+studyListDomain);
			List<StudyFileDomain> fileList =  studyService.studySelectOneFile(map);
			List<StudyCommentListDomain> studyCommentListDomains = studyCommentService.studycommentList(map);
			System.out.println(studyCommentListDomains);
			for (StudyFileDomain list : fileList) {
				String path = list.getStupFilePath().replaceAll("\\\\", "/");
				list.setStupFilePath(path);
			}
			
			mav.addObject("scitems", studyCommentListDomains);
			mav.addObject("stdetail", studyListDomain);
			mav.addObject("files", fileList);
			mav.setViewName("/study/studyList.html");
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
	
	
	@PostMapping(value = "stupload")
	public ModelAndView stUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException, ParseException {
		
		ModelAndView mav = new ModelAndView();
		int stSeq = studyService.fileProcess(fileListVO, request, httpReq);
		fileListVO.setContent(""); //초기화
		fileListVO.setTitle(""); //초기화
		
		// 화면에서 넘어올때는 bdSeq String이라 string으로 변환해서 넣어즘
		mav = stSelectOneCall(fileListVO, String.valueOf(stSeq),request);
		mav.setViewName("study/studyList.html");
		return mav;
		
	}
	
	@PostMapping(value = "scupload")
	public ModelAndView scUpload(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException, ParseException {
		
		ModelAndView mav = new ModelAndView();
		HashMap<String, Object> map = new HashMap<>();
		
		int stSeq = studyCommentService.fileProcess(fileListVO, httpReq);
		fileListVO.setContent(""); //초기화
		map.put("stSeq", stSeq);
		List<StudyCommentListDomain> studyCommentListDomains = studyCommentService.studycommentList(map);
		
		mav = stSelectOneCall(fileListVO, String.valueOf(stSeq), request);
		mav.addObject("scitems", studyCommentListDomains);
		mav.setViewName("study/studyList.html");
		return mav;
		
	}
	
	//detail
		@GetMapping("stdetail")
	    public ModelAndView stDetail(@ModelAttribute("fileListVO") FileListVO fileListVO, @RequestParam("stSeq") String stSeq, HttpServletRequest request) throws IOException {
			ModelAndView mav = new ModelAndView();
			//하나파일 가져오기
			mav = stSelectOneCall(fileListVO, stSeq,request);
			mav.setViewName("study/studyList.html");
			return mav;
		}
		@GetMapping("stedit")
			public ModelAndView stedit(FileListVO fileListVO, @RequestParam("stSeq") String stSeq, HttpServletRequest request) throws IOException {
				ModelAndView mav = new ModelAndView();

				HashMap<String, Object> map = new HashMap<String, Object>();
				HttpSession session = request.getSession();
				
				map.put("stSeq", Integer.parseInt(stSeq));
				StudyListDomain studyListDomain =studyService.studySelectOne(map);
				List<StudyFileDomain> fileList =  studyService.studySelectOneFile(map);
				
				for (StudyFileDomain list : fileList) {
					String path = list.getStupFilePath().replaceAll("\\\\", "/");
					list.setStupFilePath(path);
				}

				fileListVO.setSeq(studyListDomain.getStSeq());
				fileListVO.setContent(studyListDomain.getStContent());
				fileListVO.setTitle(studyListDomain.getStTitle());
				fileListVO.setIsEdit("edit");  // upload 재활용하기위해서
				
				mav.addObject("stdetail", studyListDomain);
				mav.addObject("files", fileList);
				mav.addObject("fileLen",fileList.size());
				
				mav.setViewName("study/studyEditList.html");
				return mav;
			}
		
		@GetMapping("scedit")
		public ModelAndView scedit(FileListVO fileListVO, @RequestParam("scSeq") String scSeq, HttpServletRequest request) throws IOException {
			
			ModelAndView mav = new ModelAndView();
			HashMap<String, Object> map = new HashMap<String, Object>();
			HttpSession session = request.getSession();
			
			map.put("scSeq", scSeq);
			StudyCommentListDomain studyCommentListDomain = studyCommentService.studycommentSelectOne(map);

			fileListVO.setSeq(studyCommentListDomain.getScSeq());
			fileListVO.setContent(studyCommentListDomain.getScContent());
			fileListVO.setIsEdit("edit");  // upload 재활용하기위해서
			
			mav.addObject("scitems", studyCommentListDomain);
			mav.setViewName("study/studyEditList.html");
			return mav;
		}
		
		@PostMapping("steditSave")
			public ModelAndView steditSave(@ModelAttribute("fileListVO") FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
				ModelAndView mav = new ModelAndView();
				//저장
				studyService.fileProcess(fileListVO, request, httpReq);
				
				
				mav = stSelectOneCall(fileListVO, fileListVO.getSeq(),request);
				fileListVO.setContent(""); //초기화
				fileListVO.setTitle(""); //초기화
				mav.setViewName("study/studyList.html");
				return mav;
			}
		
		@PostMapping("sceditSave")
		public ModelAndView sceditSave(@ModelAttribute("fileListVO") FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq) throws IOException {
			ModelAndView mav = new ModelAndView();
			//저장
			int stSeq = studyCommentService.fileProcess(fileListVO, httpReq);
			HashMap<String, Object> map = new HashMap<>();
			
			map.put("stSeq", stSeq);
			List<StudyCommentListDomain> studyCommentListDomains = studyCommentService.studycommentList(map);
			
			mav = stSelectOneCall(fileListVO, String.valueOf(stSeq),request);
			mav.addObject("scitems", studyCommentListDomains);
			fileListVO.setContent(""); //초기화
			fileListVO.setTitle(""); //초기화
			mav.setViewName("study/studyList.html");
			return mav;
		}
		
		@GetMapping("stremove")
		public ModelAndView stRemove(@RequestParam("stSeq") String stSeq, HttpServletRequest request) throws IOException {
			ModelAndView mav = new ModelAndView();
			
			HttpSession session = request.getSession();
			HashMap<String, Object> map = new HashMap<String, Object>();
			List<StudyFileDomain> fileList = null;
			if(session.getAttribute("files") != null) {						
				fileList = (List<StudyFileDomain>) session.getAttribute("files");
			}
			
			map.put("stSeq", Integer.parseInt(stSeq));
			
			//내용삭제
			studyService.stContentRemove(map);

			for (StudyFileDomain list : fileList) {
				list.getStupFilePath();
				Path filePath = Paths.get(list.getStupFilePath());
		 
		        try {
		        	
		            // 파일 물리삭제
		            Files.deleteIfExists(filePath); // notfound시 exception 발생안하고 false 처리
		            // db 삭제 
								studyService.stFileRemove(list);
					
		        } catch (DirectoryNotEmptyException e) {
								throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
		        } catch (IOException e) {
		            e.printStackTrace();
		        }
			}
			scAllRemove(stSeq);
			//세션해제
			session.removeAttribute("files"); // 삭제
			
			mav = Paging(request);
			return mav; 
		}

		public void scAllRemove(String stSeq) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("stSeq", stSeq);
			studyCommentService.studycommentAllContentRemove(map);
		}
		
		@GetMapping("scremove")
		public ModelAndView scRemove(@RequestParam("scSeq") String scSeq, @RequestParam("stSeq") String stSeq, FileListVO fileListVO ,HttpServletRequest request) throws IOException {
			ModelAndView mav = new ModelAndView();
			
			HttpSession session = request.getSession();
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put("scSeq", Integer.parseInt(scSeq));
			
			//내용삭제
			studyCommentService.studycommentContentRemove(map);
			
			map.put("stSeq", Integer.parseInt(stSeq));
			List<StudyCommentListDomain> studyCommentListDomains = studyCommentService.studycommentList(map);
			mav = stSelectOneCall(fileListVO, stSeq, request);
			mav.addObject("scitems", studyCommentListDomains);
			mav.setViewName("study/studyList.html");
			
			return mav;
		}
		
		@RequestMapping("studyselect")
		public ModelAndView studySelect(FileListVO fileListVO) {
			ModelAndView mav = new ModelAndView();
			String keyword = fileListVO.getKeyword();
			HashMap<String, Object> map = new HashMap<>();
			map.put("stTitle", keyword);
			List<StudyListDomain> studyListDomains = studyService.studySelectSelect(map);
			mav.addObject("items", studyListDomains);
			mav.setViewName("study/studyList.html");
			return mav;
		}


	//리스트 가져오기 따로 함수뺌
	public ModelAndView stListCall(HttpServletRequest request) {
		ModelAndView mav = new ModelAndView();
		//페이지네이션 추가  SELECT * FROM jsp.member order by mb_update_at limit 1, 5; {offset}{limit}

		//전체 갯수
		int totalcount = studyService.stGetAll();
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
			List<StudyListDomain> studyListDomains = studyService.studyAllList(map);
			
			//모델객체 넣어주기
			mav.addObject("itemsNotEmpty", itemsNotEmpty);
			mav.addObject("items", studyListDomains);
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
		mav = stListCall(request);
		mav.setViewName("study/studyList.html");
		return mav;
	}
	}