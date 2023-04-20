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
import com.co.kr.domain.StudyCommentContentDomain;
import com.co.kr.domain.StudyCommentListDomain;
import com.co.kr.domain.StudyContentDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.mapper.StudyCommentMapper;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class StudyCommentServiceImpl implements StudyCommentService {

	@Autowired
	private StudyCommentMapper studyCommentMapper;
	
	@Override
	public List<StudyCommentListDomain> studycommentList(HashMap<String, Object> map) {
		return studyCommentMapper.studycommentList(map);
	}
	@Override
	public int fileProcess(FileListVO fileListVO, HttpServletRequest httpReq) {
		//session 생성
		HttpSession session = httpReq.getSession();
		
		//content domain 생성 
		StudyCommentContentDomain studyCommentContentDomain = StudyCommentContentDomain.builder()
				.stSeq(fileListVO.getStscseq())
				.mbId(session.getAttribute("id").toString())
				.scContent(fileListVO.getContent())
				.build();
		
				if(fileListVO.getIsEdit() != null) {
					studyCommentContentDomain.setScSeq(Integer.parseInt(fileListVO.getSeq()));
					System.out.println("수정업데이트");
					// db 업데이트
					studyCommentMapper.studycommentContentUpdate(studyCommentContentDomain);
				}else {	
					// db 인서트
					studyCommentMapper.studycommentContentUpload(studyCommentContentDomain);
					System.out.println(" db 인서트");
				}
				
				int stSeq = Integer.parseInt(studyCommentContentDomain.getStSeq());

				return stSeq; // 저장된 게시판 번호
	}

	@Override
	public void studycommentContentRemove(HashMap<String, Object> map) {
		studyCommentMapper.studycommentContentRemove(map);
	}

	// 하나만 가져오기
	@Override
	public StudyCommentListDomain studycommentSelectOne(HashMap<String, Object> map) {
		return studyCommentMapper.studycommentSelectOne(map);
	}
	
}