package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.QuestionContentDomain;
import com.co.kr.domain.QuestionFileDomain;
import com.co.kr.domain.QuestionListDomain;

@Mapper
public interface QuestionMapper {

	//list
	public List<QuestionListDomain> questionList();
	
	//content upload
	public void qcontentUpload(QuestionContentDomain questionContentDomain);
	//file upload
	public void qfileUpload(QuestionFileDomain questionFileDomain);

	//content update
	public void qContentUpdate(QuestionContentDomain questionContentDomain);
	//file update
	public void qFileUpdate(QuestionFileDomain questionFileDomain);

  //content delete 
	public void qContentRemove(HashMap<String, Object> map);
	//file delete 
	public void qFileRemove(QuestionFileDomain questionFileDomain);
	//select one
	public QuestionListDomain questionSelectOne(HashMap<String, Object> map);

	//select one file
	public List<QuestionFileDomain> questionSelectOneFile(HashMap<String, Object> map);
	
	//검색창
	public List<QuestionListDomain> questionSelectSelect(HashMap<String, Object> map);
	
	//전체 개수
	public int qGetAll();
	
	public List<QuestionListDomain> questionAllList(HashMap<String, Object> map);
}
