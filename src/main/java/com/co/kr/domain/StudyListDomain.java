package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "builder")
public class StudyListDomain {
	
	private String stSeq;
	private String mbId;
	private String stTitle;
	private String stContent;
	private String stCreateAt;
	private String stUpdateAt;
	
}
