package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class StudyFileDomain {
	
	private Integer stSeq;
	private String mbId;
	
	private String stupOriginalFileName;
	private String stupNewFileName; //동일 이름 업로드 될 경우
	private String stupFilePath;
	private Integer stupFileSize;
}
