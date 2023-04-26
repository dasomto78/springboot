package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class QuestionCommentContentDomain {

	private Integer qcSeq;
	private String qSeq;
	private String mbId;
	private String qcContent;
	
}
