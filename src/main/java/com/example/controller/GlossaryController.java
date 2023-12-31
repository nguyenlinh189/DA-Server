package com.example.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.Glossary;
import com.example.repository.GlossaryRepository;
import com.example.service.FireBaseFileService;

@RestController
@RequestMapping("/glossary")
public class GlossaryController {
	@Autowired
	private GlossaryRepository gloRepo;
	@Autowired
	private FireBaseFileService service;
	//index lay tu 0
	@GetMapping("/get")
	private Page<Glossary> getAll(@RequestParam("pageNum")int pageNum,
			@RequestParam("sortField")String sortField, @RequestParam("sortDir")String sortDir,
			@RequestParam(name="key")String key){
		int pageSize=5;
		Pageable pageable=PageRequest.of(pageNum-1, pageSize, sortDir.equals("asc")?Sort.by(sortField).ascending(): Sort.by(sortField).descending());
		if(key.isEmpty())
			return gloRepo.findAll(pageable);
		else
			return gloRepo.findByKey(pageable, key);
	}
	@GetMapping("/getAll")
	private List<Glossary> get(@RequestParam(name = "key", defaultValue = "", required = false) String key) {
		if (key.isEmpty())
			return gloRepo.findAll();
		else
			return gloRepo.findByVietnameseContaining(key);
	}

	@PostMapping(path = "/add")
	private Glossary saveGlossary(@RequestPart(name = "file", required = false) MultipartFile file,
			@RequestPart("glossary") Glossary glossary) throws IOException {
		if(file!=null) {
			String url = service.saveFile(file);
			glossary.setImage(url);
		}
		return gloRepo.save(glossary);
	}

	@PostMapping(path = "/update")
	private Glossary updateGlossary(@RequestPart(name = "file", required = false) MultipartFile file,
			@RequestPart("glossary") Glossary glossary, @RequestPart("status") int status) throws IOException {
		System.out.println(status);
		// chọn thêm file mới
		if (file != null) {
			String url = service.saveFile(file);
			if (glossary.getImage() != null) {
				String pathfile = glossary.getImage();
				// Tìm index của ký tự "/" cuối cùng trong URL
				int lastIndex = pathfile.lastIndexOf("/");
				// Tìm index của ký tự "?" đầu tiên sau ký tự "/"
				int indexOfQuestionMark = pathfile.indexOf("?", lastIndex);
				// Lấy ra chuỗi con giữa các index tìm được
				String fileName = pathfile.substring(lastIndex + 1, indexOfQuestionMark);
				service.deleteFile(fileName);
			}
			glossary.setImage(url);
		} // không chọn thêm file mới -- không làm gì đến file -- muốn xóa file
		else {
			if (status == 1) {
				if (glossary.getImage() != null) {
					String pathfile = glossary.getImage();
					// Tìm index của ký tự "/" cuối cùng trong URL
					int lastIndex = pathfile.lastIndexOf("/");
					// Tìm index của ký tự "?" đầu tiên sau ký tự "/"
					int indexOfQuestionMark = pathfile.indexOf("?", lastIndex);
					// Lấy ra chuỗi con giữa các index tìm được
					String fileName = pathfile.substring(lastIndex + 1, indexOfQuestionMark);
					service.deleteFile(fileName);
					glossary.setImage(null);
				}
			}
		}
		return gloRepo.save(glossary);
	}

	@PostMapping("/delete")
	private void deleteGlossary(@RequestBody Glossary glossary) {
		gloRepo.delete(glossary);
		if (glossary.getImage() != null) {
			String url = glossary.getImage();
			// Tìm index của ký tự "/" cuối cùng trong URL
			int lastIndex = url.lastIndexOf("/");
			// Tìm index của ký tự "?" đầu tiên sau ký tự "/"
			int indexOfQuestionMark = url.indexOf("?", lastIndex);
			// Lấy ra chuỗi con giữa các index tìm được
			String fileName = url.substring(lastIndex + 1, indexOfQuestionMark);
			service.deleteFile(fileName);
		}
	}
}