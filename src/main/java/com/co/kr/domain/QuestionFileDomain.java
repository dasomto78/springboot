package com.co.kr.domain;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(builderMethodName="builder")
public class QuestionFileDomain {
	
	private Integer qSeq;
	private String mbId;
	private String qupOriginalFileName;
	private String qupNewFileName; //동일 이름 업로드 될 경우
	private String qupFilePath;
	private Integer qupFileSize;
}
