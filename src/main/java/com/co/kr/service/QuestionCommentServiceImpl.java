package com.co.kr.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.code.Code;
import com.co.kr.domain.QuestionCommentContentDomain;
import com.co.kr.domain.QuestionCommentListDomain;
import com.co.kr.domain.QuestionContentDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.mapper.QuestionCommentMapper;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class QuestionCommentServiceImpl implements QuestionCommentService {

	@Autowired
	private QuestionCommentMapper questionCommentMapper;
	
	@Override
	public List<QuestionCommentListDomain> questioncommentList(HashMap<String, Object> map) {
		return questionCommentMapper.questioncommentList(map);
	}
	@Override
	public int fileProcess(FileListVO fileListVO, HttpServletRequest httpReq) {
		//session 생성
		HttpSession session = httpReq.getSession();
		
		//content domain 생성 
		QuestionCommentContentDomain questionCommentContentDomain = QuestionCommentContentDomain.builder()
				.qSeq(fileListVO.getStscseq())
				.mbId(session.getAttribute("id").toString())
				.qcContent(fileListVO.getContent())
				.build();
		
				if(fileListVO.getIsEdit() != null) {
					questionCommentContentDomain.setQcSeq(Integer.parseInt(fileListVO.getSeq()));
					System.out.println("수정업데이트");
					// db 업데이트
					questionCommentMapper.questioncommentContentUpdate(questionCommentContentDomain);
				}else {	
					// db 인서트
					questionCommentMapper.questioncommentContentUpload(questionCommentContentDomain);
					System.out.println(" db 인서트");
				}
				
				int qSeq = Integer.parseInt(questionCommentContentDomain.getQSeq());

				return qSeq; // 저장된 게시판 번호
	}

	@Override
	public void questioncommentContentRemove(HashMap<String, Object> map) {
		questionCommentMapper.questioncommentContentRemove(map);
	}

	// 하나만 가져오기
	@Override
	public QuestionCommentListDomain questioncommentSelectOne(HashMap<String, Object> map) {
		return questionCommentMapper.questioncommentSelectOne(map);
	}
	
	// 모두 삭제
	public void questioncommentAllContentRemove(HashMap<String, Object> map) {
		questionCommentMapper.questioncommentAllContentRemove(map);
	};
	
}