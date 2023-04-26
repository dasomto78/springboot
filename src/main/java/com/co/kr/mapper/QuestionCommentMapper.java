package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.QuestionCommentContentDomain;
import com.co.kr.domain.QuestionCommentListDomain;

@Mapper
public interface QuestionCommentMapper {

	//list
	public List<QuestionCommentListDomain> questioncommentList(HashMap<String, Object> map);
	
	//select one
	public QuestionCommentListDomain questioncommentSelectOne(HashMap<String, Object> map);
	
	//content upload
	public void questioncommentContentUpload(QuestionCommentContentDomain questionCommentContentDomain);

	//content update
	public void questioncommentContentUpdate(QuestionCommentContentDomain questionCommentContentDomain);

	//content delete 
	public void questioncommentContentRemove(HashMap<String, Object> map);
	
	//every content delete
	public void questioncommentAllContentRemove(HashMap<String, Object> map);
	
}