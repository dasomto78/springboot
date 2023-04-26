package com.co.kr.service;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryNotEmptyException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import com.co.kr.code.Code;
import com.co.kr.domain.QuestionContentDomain;
import com.co.kr.domain.QuestionFileDomain;
import com.co.kr.domain.QuestionListDomain;
import com.co.kr.exception.RequestException;
import com.co.kr.mapper.QuestionMapper;
import com.co.kr.util.CommonUtils;
import com.co.kr.vo.FileListVO;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@Transactional
public class QuestionServiceImpl implements QuestionService{

	@Autowired
	private QuestionMapper questionMapper;
	
	@Override
	public List<QuestionListDomain> questionList() {
		// TODO Auto-generated method stub
		return questionMapper.questionList();
	}

	@Override
	public int fileProcess(FileListVO fileListVO, MultipartHttpServletRequest request, HttpServletRequest httpReq, String category) {
		//session 생성
		HttpSession session = httpReq.getSession();
		
		//content domain 생성 
		QuestionContentDomain questionContentDomain = QuestionContentDomain.builder()
				.mbId(session.getAttribute("id").toString())
				.qCategory(category)
				.qTitle(fileListVO.getTitle())
				.qContent(fileListVO.getContent())
				.build();
		
				if(fileListVO.getIsEdit() != null) {
					questionContentDomain.setQSeq(Integer.parseInt(fileListVO.getSeq()));
					System.out.println("수정업데이트");
					// db 업데이트
					questionMapper.qContentUpdate(questionContentDomain);
				}else {	
					// db 인서트
					questionMapper.qcontentUpload(questionContentDomain);
					System.out.println(" db 인서트");

				}
				
				// file 데이터 db 저장시 쓰일 값 추출
				int qSeq = questionContentDomain.getQSeq();
				String mbId = questionContentDomain.getMbId();
				
				//파일객체 담음
				List<MultipartFile> multipartFiles = request.getFiles("files");
				
				
				// 게시글 수정시 파일관련 물리저장 파일, db 데이터 삭제 
				if(fileListVO.getIsEdit() != null) { // 수정시 

	
					List<QuestionFileDomain> fileList = null;
					
					
					
					for (MultipartFile multipartFile : multipartFiles) {
						
						if(!multipartFile.isEmpty()) {   // 수정시 새로 파일 첨부될때 세션에 담긴 파일 지우기
							
							
							if(session.getAttribute("files") != null) {	

								fileList = (List<QuestionFileDomain>) session.getAttribute("files");
								
								for (QuestionFileDomain list : fileList) {
									list.getQupFilePath();
									Path filePath = Paths.get(list.getQupFilePath());
							 
							        try {
							        	
							            // 파일 삭제
							            Files.deleteIfExists(filePath); // notfound시 exception 발생안하고 false 처리
							            //삭제 
										qFileRemove(list); //데이터 삭제
										
							        } catch (DirectoryNotEmptyException e) {
										throw RequestException.fire(Code.E404, "디렉토리가 존재하지 않습니다", HttpStatus.NOT_FOUND);
							        } catch (IOException e) {
							            e.printStackTrace();
							        }
								}
								

							}
							
							
						}

					}
					
					
				}
				
				
				///////////////////////////// 새로운 파일 저장 ///////////////////////
				
				// 저장 root 경로만들기
				Path rootPath = Paths.get(new File("C://").toString(),"upload", File.separator).toAbsolutePath().normalize();			
				File pathCheck = new File(rootPath.toString());
				
				// folder chcek
				if(!pathCheck.exists()) pathCheck.mkdirs();
				
				
	
				for (MultipartFile multipartFile : multipartFiles) {
					
					if(!multipartFile.isEmpty()) {  // 파일 있을때 
						
						//확장자 추출
						String originalFileExtension;
						String contentType = multipartFile.getContentType();
						String origFilename = multipartFile.getOriginalFilename();
						
						//확장자 조재안을경우
						if(ObjectUtils.isEmpty(contentType)){
							break;
						}else { // 확장자가 jpeg, png인 파일들만 받아서 처리
							if(contentType.contains("image/jpeg")) {
								originalFileExtension = ".jpg";
							}else if(contentType.contains("image/png")) {
								originalFileExtension = ".png";
							}else {
								break;
							}
						}
						
						//파일명을 업로드한 날짜로 변환하여 저장
						String uuid = UUID.randomUUID().toString();
						String current = CommonUtils.currentTime();
						String newFileName = uuid + current + originalFileExtension;
						
						//최종경로까지 지정
						Path targetPath = rootPath.resolve(newFileName);
						
						File file = new File(targetPath.toString());
						
						try {
							//파일복사저장
							multipartFile.transferTo(file);
							// 파일 권한 설정(쓰기, 읽기)
							file.setWritable(true);
							file.setReadable(true);
							
							
							//파일 domain 생성 
							QuestionFileDomain questionFileDomain = QuestionFileDomain.builder()
									.qSeq(qSeq)
									.mbId(mbId)
									.qupOriginalFileName(origFilename)
									.qupNewFileName("resources/upload/"+newFileName) // WebConfig에 동적 이미지 폴더 생성 했기때문
									.qupFilePath(targetPath.toString())
									.qupFileSize((int)multipartFile.getSize())
									.build();
							
								// db 인서트
								questionMapper.qfileUpload(questionFileDomain);
								System.out.println("upload done");
							
						} catch (IOException e) {
							throw RequestException.fire(Code.E404, "잘못된 업로드 파일", HttpStatus.NOT_FOUND);
						}
					}

				}
				
		
				return qSeq; // 저장된 게시판 번호
	}

	@Override
	public void qContentRemove(HashMap<String, Object> map) {
		questionMapper.qContentRemove(map);
	}

	@Override
	public void qFileRemove(QuestionFileDomain questionFileDomain) {
		questionMapper.qFileRemove(questionFileDomain);
	}
	
	// 하나만 가져오기
	@Override
	public QuestionListDomain questionSelectOne(HashMap<String, Object> map) {
		return questionMapper.questionSelectOne(map);
	}

	// 하나 게시글 파일만 가져오기
	@Override
	public List<QuestionFileDomain> questionSelectOneFile(HashMap<String, Object> map) {
		return questionMapper.questionSelectOneFile(map);
	}
	
	//검색창
	public List<QuestionListDomain> questionSelectSelect(HashMap<String, Object> map) {
		return questionMapper.questionSelectSelect(map);
	};

	//전체 개수
		public int qGetAll() {
			return questionMapper.qGetAll();
		};
		
		public List<QuestionListDomain> questionAllList(HashMap<String, Object> map){
			return questionMapper.questionAllList(map);
		};
}