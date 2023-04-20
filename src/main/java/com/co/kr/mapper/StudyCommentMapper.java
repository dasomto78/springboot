package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.StudyCommentContentDomain;
import com.co.kr.domain.StudyCommentListDomain;

@Mapper
public interface StudyCommentMapper {

	//list
	public List<StudyCommentListDomain> studycommentList(HashMap<String, Object> map);
	
	//select one
	public StudyCommentListDomain studycommentSelectOne(HashMap<String, Object> map);
	
	//content upload
	public void studycommentContentUpload(StudyCommentContentDomain studyCommentContentDomain);

	//content update
	public void studycommentContentUpdate(StudyCommentContentDomain studyCommentContentDomain);

  //content delete 
	public void studycommentContentRemove(HashMap<String, Object> map);
	
}