package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class StudyCommentContentDomain {

	private Integer scSeq;
	private String stSeq;
	private String mbId;
	private String scContent;
	
}
