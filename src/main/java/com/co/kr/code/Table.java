package com.co.kr.code;

import lombok.Getter;

@Getter
public enum Table {

	MEMBER("member"),
	FILES("files"),
	BOARD("board"),
	STUDY("study"),
	STFILES("stfiles"),
	STCOMMENT("stcomment"),
	QUESTION("question"),
	QFILES("qfiles"),
	QCOMMENT("qcomment");
	
	
	private String table;

	Table(String table){
		this.table = table;
	}
	
}