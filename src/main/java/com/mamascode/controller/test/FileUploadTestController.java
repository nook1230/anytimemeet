package com.mamascode.controller.test;

import java.io.File;
import java.io.IOException;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/test/fileUpload")
public class FileUploadTestController {
	@RequestMapping(value="upload", method=RequestMethod.GET)
	public String uploadForm() {
		return "test/file_upload";
	}
	
	@RequestMapping(value="upload", method=RequestMethod.POST)
	public String upload(@RequestParam("upload_file") MultipartFile uploadFile,
			Model model) throws IOException {
		/* pwd가 프로젝트 디렉토리가 아님
		 * 따라서 목적지를 상수로 저장해두어야 함
		 * 목적지는 시스템에 따라 달라진다. 
		 * 현재 targetPath는 윈도우즈에 맞게 설정되어 있음.
		 * 어디에 저장해두어야 할까? 
		 * 
		 * 테스트 성공! MultipartFile로도 어느 정도 기능 구현 가능
		 * 하지만 정밀한 제어를 위해서는 다른 수단들도 함께 사용해야 할 것 같다
		 * 예를 들면, 포맷 체크, 파일 크기 체크 등 
		*/
		
		boolean result = false;
		//String pwd = System.getProperty("user.dir");
		//System.out.println(pwd);
		
		String targetPath = "C:\\Users\\Hwang\\Documents\\workspace\\springweb\\project_meet_maker_iter1"
				+ "\\user_files\\test\\copy_copy_room_room_"
			+ uploadFile.getOriginalFilename();
		
		File targetFile = new File(targetPath);
		uploadFile.transferTo(targetFile);
		
		result = targetFile.exists() && targetFile.isFile()
				&& (targetFile.length() == uploadFile.getSize());
		
		model.addAttribute("success", result);
		
		return "test/file_upload_test_result";
	}
}
