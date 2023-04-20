package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName = "builder")
public class StudyCommentListDomain {
	
	private String scSeq;
	private String stSeq;
	private String mbId;
	private String scContent;
	private String scCreateAt;
	private String scUpdateAt;
	
}
