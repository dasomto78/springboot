package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "builder")
public class QuestionCommentListDomain {
	
	private String qcSeq;
	private String qSeq;
	private String mbId;
	private String qcContent;
	private String qcCreateAt;
	private String qcUpdateAt;
	
}
