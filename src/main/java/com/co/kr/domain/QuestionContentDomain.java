package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class QuestionContentDomain {
	
	private Integer qSeq;
	private String mbId;
	private String qCategory;
	private String qTitle;
	private String qContent;

}
