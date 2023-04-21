package com.co.kr.mapper;

import java.util.HashMap;
import java.util.List;

import org.apache.ibatis.annotations.Mapper;

import com.co.kr.domain.StudyContentDomain;
import com.co.kr.domain.StudyFileDomain;
import com.co.kr.domain.StudyListDomain;

@Mapper
public interface StudyMapper {

	//list
	public List<StudyListDomain> studyList();
	
	//content upload
	public void stcontentUpload(StudyContentDomain studyContentDomain);
	//file upload
	public void stfileUpload(StudyFileDomain studyFileDomain);

	//content update
	public void stContentUpdate(StudyContentDomain studyContentDomain);
	//file update
	public void stFileUpdate(StudyFileDomain studyFileDomain);

  //content delete 
	public void stContentRemove(HashMap<String, Object> map);
	//file delete 
	public void stFileRemove(StudyFileDomain studyFileDomain);
	//select one
	public StudyListDomain studySelectOne(HashMap<String, Object> map);

	//select one file
	public List<StudyFileDomain> studySelectOneFile(HashMap<String, Object> map);
	
	//검색창
	public List<StudyListDomain> studySelectSelect(HashMap<String, Object> map);
	
	//전체 개수
	public int stGetAll();
}
