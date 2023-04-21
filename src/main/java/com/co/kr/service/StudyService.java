package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.domain.StudyFileDomain;
import com.co.kr.domain.StudyListDomain;
import com.co.kr.vo.FileListVO;

public interface StudyService {

	// 인서트
	public int fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq);
	
	// 전체 리스트 조회   // 지난시간 작성
	public List<StudyListDomain> studyList();

	// 하나 삭제
	public void stContentRemove(HashMap<String, Object> map);

	// 하나 삭제
	public void stFileRemove(StudyFileDomain studyFileDomain);
	// 하나 리스트 조회
		public StudyListDomain studySelectOne(HashMap<String, Object> map);
		// 하나 파일 리스트 조회
		public List<StudyFileDomain> studySelectOneFile(HashMap<String, Object> map);
		
		//검색창
		public List<StudyListDomain> studySelectSelect(HashMap<String, Object> map);
		
		//전체 개수
		public int stGetAll();
		
		public List<StudyListDomain> studyAllList(HashMap<String, Object> map);
}
