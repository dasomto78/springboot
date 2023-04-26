package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.domain.QuestionFileDomain;
import com.co.kr.domain.QuestionListDomain;
import com.co.kr.vo.FileListVO;

public interface QuestionService {

	// 인서트
	public int fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq, String category);
	
	// 전체 리스트 조회   // 지난시간 작성
	public List<QuestionListDomain> questionList();

	// 하나 삭제
	public void qContentRemove(HashMap<String, Object> map);

	// 하나 삭제
	public void qFileRemove(QuestionFileDomain questionFileDomain);
	// 하나 리스트 조회
		public QuestionListDomain questionSelectOne(HashMap<String, Object> map);
		// 하나 파일 리스트 조회
		public List<QuestionFileDomain> questionSelectOneFile(HashMap<String, Object> map);
		
		//검색창
		public List<QuestionListDomain> questionSelectSelect(HashMap<String, Object> map);
		
		//전체 개수
		public int qGetAll();
		
		public List<QuestionListDomain> questionAllList(HashMap<String, Object> map);
}
