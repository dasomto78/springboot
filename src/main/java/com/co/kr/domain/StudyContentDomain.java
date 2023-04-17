package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class StudyContentDomain {
	
	private Integer stSeq;
	private String mbId;

	private String stTitle;
	private String stContent;

}
