package com.co.kr.service;

import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.domain.QuestionCommentListDomain;
import com.co.kr.vo.FileListVO;

public interface QuestionCommentService {
	
	// 전체 리스트 조회   // 지난시간 작성
	public List<QuestionCommentListDomain> questioncommentList(HashMap<String, Object> map);
	
	// 하나 리스트 조회
	public QuestionCommentListDomain questioncommentSelectOne(HashMap<String, Object> map);
	
	// 인서트
	public int fileProcess(FileListVO fileListVO, HttpServletRequest httpReq);

	// 하나 삭제
	public void questioncommentContentRemove(HashMap<String, Object> map);
	
	// 모두 삭제
	public void questioncommentAllContentRemove(HashMap<String, Object> map);
}
