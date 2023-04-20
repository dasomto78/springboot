package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.domain.StudyCommentListDomain;
import com.co.kr.vo.FileListVO;

public interface StudyCommentService {
	
	// 전체 리스트 조회   // 지난시간 작성
	public List<StudyCommentListDomain> studycommentList(HashMap<String, Object> map);
	
	// 하나 리스트 조회
	public StudyCommentListDomain studycommentSelectOne(HashMap<String, Object> map);
	
	// 인서트
	public int fileProcess(FileListVO fileListVO, HttpServletRequest httpReq);

	// 하나 삭제
	public void studycommentContentRemove(HashMap<String, Object> map);
}
