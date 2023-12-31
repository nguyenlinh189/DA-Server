package com.example.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.example.model.Blog;
import com.example.model.Comment;
import com.example.repository.BlogRepository;
import com.example.repository.CommentRepository;
import com.example.repository.UserRepository;
import com.example.service.FireBaseFileService;



@RestController
@RequestMapping("/blog")
public class BlogController {
	@Autowired
	private BlogRepository blogRepo;
	@Autowired
	private FireBaseFileService service;
	@Autowired
	private CommentRepository commentRepo;

	@PostMapping("/save")
	private Blog saveBlog(@RequestPart(name="file", required = false)MultipartFile file, @RequestPart("blog")Blog blog) throws IOException {
		if(file!=null)
		{
			String url=service.saveFile(file);
			blog.setImage(url);
		}
		
		return blogRepo.save(blog);
		
	}
	@GetMapping("/getAll")
	private List<Blog>getListBlog(@RequestParam(name="status", defaultValue = "publish", required = false)String status){
		List<Blog>listBlog=blogRepo.findByStatus(status);
		for(Blog blog:listBlog) {
			blog.setListComment(commentRepo.findByParentIsNullAndBlogId(blog.getId()));
		}
		return listBlog;
	}
	@GetMapping("/get")
	private Page<Blog> getByPage(@RequestParam("pageNum")int pageNum,
			@RequestParam(name="key", required = false, defaultValue = "")String key,
			@RequestParam(name="idCate", required = false, defaultValue = "-1")int idCate){
		Page<Blog> page;
		int pageSize=3;
		Pageable pageable=PageRequest.of(pageNum-1, pageSize);
		if(idCate==-1)
			page= blogRepo.findByTitleContainingAndStatus(pageable, key, "publish");
		else
			page= blogRepo.findByTitleContainingAndCategoryBlogIdAndStatus(pageable, key, idCate,"publish");
		for(Blog b:page.getContent())
			b.setListComment(commentRepo.findByParentIsNullAndBlogId(b.getId()));
		return page;
	}
	@GetMapping("/getTop5")
	private List<Blog> getTop5(){
		return blogRepo.getLimit5();
	}
	@PostMapping("/delete")
	private void deleteWood(@RequestBody Blog blog) {
			String pathfile = blog.getImage();
			// Tìm index của ký tự "/" cuối cùng trong URL
			int lastIndex = pathfile.lastIndexOf("/");
			// Tìm index của ký tự "?" đầu tiên sau ký tự "/"
			int indexOfQuestionMark = pathfile.indexOf("?", lastIndex);
			// Lấy ra chuỗi con giữa các index tìm được
			String fileName = pathfile.substring(lastIndex + 1, indexOfQuestionMark);
			service.deleteFile(fileName);
		blogRepo.delete(blog);
	}
	@PostMapping(path = "/update")
	private Blog updateBlog(@RequestPart(name = "file", required = false) MultipartFile file,
			@RequestPart("blog") Blog blog, @RequestPart("status") int status) throws IOException {
		System.out.println(status);
		// chọn thêm file mới
		if (file != null) {
			String url = service.saveFile(file);
			if (blog.getImage() != null) {
				String pathfile = blog.getImage();
				// Tìm index của ký tự "/" cuối cùng trong URL
				int lastIndex = pathfile.lastIndexOf("/");
				// Tìm index của ký tự "?" đầu tiên sau ký tự "/"
				int indexOfQuestionMark = pathfile.indexOf("?", lastIndex);
				// Lấy ra chuỗi con giữa các index tìm được
				String fileName = pathfile.substring(lastIndex + 1, indexOfQuestionMark);
				service.deleteFile(fileName);
			}
			blog.setImage(url);
		} // không chọn thêm file mới -- không làm gì đến file -- muốn xóa file
		else {
			if (status == 1) {
				if (blog.getImage() != null) {
					String pathfile = blog.getImage();
					// Tìm index của ký tự "/" cuối cùng trong URL
					int lastIndex = pathfile.lastIndexOf("/");
					// Tìm index của ký tự "?" đầu tiên sau ký tự "/"
					int indexOfQuestionMark = pathfile.indexOf("?", lastIndex);
					// Lấy ra chuỗi con giữa các index tìm được
					String fileName = pathfile.substring(lastIndex + 1, indexOfQuestionMark);
					service.deleteFile(fileName);
					blog.setImage(null);
				}
			}
		}
		return blogRepo.save(blog);
	}

	@PostMapping("/addComment")
	private int addComment(@RequestBody Comment comment, @RequestParam(name="idBlog")int idblog, @RequestParam(name="idComment")int idcommentRepli) {
		int comment1;
		if(idcommentRepli!=0)
			comment1=commentRepo.addComment1(comment.getContent(), idcommentRepli, idblog, comment.getUser().getId(), comment.getCreateAt());
		else 
			comment1=commentRepo.addComment2(comment.getContent(),idblog, comment.getUser().getId(), comment.getCreateAt());
		return comment1;
	}
	@GetMapping("/detailBlog")
	private Blog getDetailBlog(@RequestParam(name="id")int id) {
		Blog blog=blogRepo.findById(id);
		blog.setListComment(commentRepo.findByParentIsNullAndBlogId(id));
		return blog;
	}
	@GetMapping("/getAmountBlog")
	private int getAmountBlog() {
		return blogRepo.getAmountBlog();
	}
}

//@GetMapping("/comment")
//private List<Comment>getListComment(){
//	Comment comment1=new Comment("demo1");
//	Comment comment11=new Comment("demo11",comment1);
//	Comment comment12=new Comment("demo12",comment1);
//	comment1.addChild(comment11);
//	comment1.addChild(comment12);
//	Comment comment111=new Comment("demo111", comment11);
//	comment11.addChild(comment111);
//	
//	//commentRepo.save(comment1);
//	return commentRepo.findByParentIsNullAndBlogId(2);
//}
