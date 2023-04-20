package com.co.kr.controller;

import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;

import com.co.kr.code.Code;
import com.co.kr.domain.StudyCommentContentDomain;
import com.co.kr.domain.StudyCommentListDomain;
import com.co.kr.domain.StudyFileDomain;
import com.co.kr.domain.StudyListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.service.StudyCommentService;
import com.co.kr.service.StudyService;
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

			//세션해제
			session.removeAttribute("files"); // 삭제
			mav = stListCall();
			mav.setViewName("study/studyList.html");
			
			return mav;
		}


	//리스트 가져오기 따로 함수뺌
	public ModelAndView stListCall() {
		ModelAndView mav = new ModelAndView();
		List<StudyListDomain> items = studyService.studyList();
		mav.addObject("items", items);
		return mav;
	}
}