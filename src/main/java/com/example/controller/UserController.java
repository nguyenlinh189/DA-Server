package com.example.controller;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.Normalizer;
import java.util.Calendar;

import java.util.Date;
import java.util.Locale;
import java.util.Random;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringEscapeUtils;
import org.apache.tomcat.util.buf.Utf8Encoder;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.servlet.server.Encoding;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.unbescape.json.JsonEscape;

import com.example.dto.PredictWood;
import com.example.dto.RequestPredictWood;
import com.example.dto.UserDTO;
import com.example.exception.ResourceNotFoundException;
import com.example.model.EProvider;
import com.example.model.ERole;
import com.example.model.IdentifyWoodHistory;
import com.example.model.User;
import com.example.model.VerificationToken;
import com.example.model.Wood;
import com.example.repository.IdentifyHistoryRepository;
import com.example.repository.UserRepository;
import com.example.repository.VerificationTokenRepository;
import com.example.repository.WoodRepository;
import com.example.security.TokenProvider;
import com.example.security.UserPrincipal;
import com.example.service.FireBaseFileService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
	@Autowired
	private UserRepository userRepo;
	@Autowired
	private PasswordEncoder passwordEncoder;
	@Autowired
	private JavaMailSender mailSender;
	@Autowired
	private MessageSource messages;
	@Autowired
	private VerificationTokenRepository veriTokenRepo;
	@Autowired
    private AuthenticationManager authenticationManager;
	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	private FireBaseFileService service;
	@Autowired
	private WoodRepository woodRepo;
	@Autowired
	private IdentifyHistoryRepository idenRepo;

	// đăng kí tài khoản
	@PostMapping("/register")
	public ResponseEntity<String> registerUser(@RequestBody UserDTO user) {
		if (userRepo.existsByEmail(user.getEmail()))
			return ResponseEntity.status(HttpStatus.IM_USED).body("fail");
		User u = new User();
		u.setEmail(user.getEmail());
		u.setName(user.getName());
		u.setPassword(passwordEncoder.encode(user.getPassword()));
		if (user.getRole() == null)
			u.setRole(ERole.ROLE_USER);
		else
			u.setRole(ERole.ROLE_ADMIN);
		u.setProvider(EProvider.local);
		u.setEnabled(true);
		Date date=new Date();
		u.setCreateAt(date);
		u = userRepo.save(u);
		return ResponseEntity.ok().body("success");
	}
	//reset password
	@PostMapping("/resetPassword")
	public ResponseEntity<String> resetPassword(@RequestBody User user) {
		try {
			System.out.println(user.getEmail());
			int min = 10000000; // Số nhỏ nhất có 8 chữ số là 10,000,000
	        int max = 99999999; // Số lớn nhất có 8 chữ số là 99,999,999

	        Random random = new Random();
	        String token =(random.nextInt(max - min + 1) + min) +"";
			// luu ma token
			VerificationToken verificationToken = new VerificationToken(token, user);
			veriTokenRepo.save(verificationToken);
			String recipientAddress = user.getEmail();
			String subject = "Đặt lại mật khẩu";
			String text = "Mã xác nhận đặt lại mật khẩu: "+token;
			SimpleMailMessage email1 = new SimpleMailMessage();
			email1.setTo(recipientAddress);
			email1.setSubject(subject);
			email1.setText(text);
			mailSender.send(email1);
		} catch (MailException ex) {
			return ResponseEntity.ok("fail");
		}
		return ResponseEntity.ok("success");
	}

	// luu mat khau khi reset lai
	@GetMapping("/resetPassword")
//	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public ResponseEntity<String> savePassword(@RequestParam(name="token")String token, @RequestParam("password")String password) {
		VerificationToken veri=veriTokenRepo.findByToken(token);
		if (veri == null)
			return ResponseEntity.ok("fail");
		Calendar cal = Calendar.getInstance();
		if (veri.getExpiryDate().getTime() - cal.getTime().getTime() <= 0)
			return ResponseEntity.ok("fail");
		User user=veri.getUser();
		user.setPassword(passwordEncoder.encode(password));
		// cap nhat lai mat khau
		userRepo.save(user);
		// xoa token
		veriTokenRepo.delete(veri);
		return ResponseEntity.ok("success");
	}
	
	@PostMapping("/login")
	public ResponseEntity<String> user(@RequestBody UserDTO user) {
		Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        user.getEmail(),
                        user.getPassword()
                )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        String token = tokenProvider.createToken(authentication);
        User user1=new User();
        UserPrincipal userPrincipal=(UserPrincipal)authentication.getPrincipal();
        user1.setId(userPrincipal.getId());
        
        URI location = ServletUriComponentsBuilder
                .fromCurrentContextPath().path("/user/getUserInfo")
                .buildAndExpand(user1.getId()).toUri();
        return ResponseEntity.created(location)
                .body(token);
	}
	@GetMapping("/getUserInfo")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public User getUserInfo(@com.example.security.CurrentUser UserPrincipal userPrincipal) {
		return userRepo.findById(userPrincipal.getId())
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", userPrincipal.getId()));
	}	
	@PostMapping("/updateInfor")
	public int updateInfor(@RequestBody User user) {
		return userRepo.updateUser(user.getAddress(), user.getName(), user.getPhone(), user.getId());
	}
	@PostMapping("/updateListFavourite")
	public ResponseEntity<User> listFavourite(@RequestBody User user) {
		user = userRepo.save(user);
		return ResponseEntity.ok(user);
	}
	@PostMapping("/addIdentify")
	public ResponseEntity<User> addIdentify(@RequestPart(name="file", required = false) MultipartFile[] files,
			@RequestPart(name="user") User user) throws IOException {
		if(files!=null) {
			for(MultipartFile file: files) {
				String base64=Base64.encodeBase64String(file.getBytes());
				RequestPredictWood image=new RequestPredictWood(base64);
				RestTemplate restTemplate = new RestTemplate();
				PredictWood predictWood=restTemplate.postForObject("http://ptitsure.tk:5002/predict", image, PredictWood.class);
				IdentifyWoodHistory identifyWoodHistory=new IdentifyWoodHistory();
				String url = service.saveFile(file);
				identifyWoodHistory.setPath(url);
				identifyWoodHistory.setProb(predictWood.getProb());
				//identifyWoodHistory.setUser(user);
				String name=predictWood.getWoodID();
				
				if(name.equalsIgnoreCase("-1")) {
					identifyWoodHistory.setResult("Không nhận diện được");
				}else {
					name=name.replace("(50x)", "").trim();
					name=name.replace("\\s+", " ");
					String normalizedChuoi1 = Normalizer.normalize(name, Normalizer.Form.NFC);
					identifyWoodHistory.setResult("Nhận diện được thành công");
					Wood wood=woodRepo.findByVietnameName("%"+normalizedChuoi1+"%");
					System.out.println(name);
					identifyWoodHistory.setWood(wood);
				}
				user.getListIdentify().add(identifyWoodHistory);
			}
		}
		for(IdentifyWoodHistory i:user.getListIdentify())
			i.setUser(user);
		user = userRepo.save(user);
		return ResponseEntity.ok(user);
	}
	@PostMapping("/deleteIdentifyHistory")
	public ResponseEntity<String> deleteIdentifyHistory(@RequestBody IdentifyWoodHistory identifyWoodHistory) {
		idenRepo.delete(identifyWoodHistory);
		if (identifyWoodHistory.getPath() != null) {
			String url = identifyWoodHistory.getPath();
			// Tìm index của ký tự "/" cuối cùng trong URL
			int lastIndex = url.lastIndexOf("/");
			// Tìm index của ký tự "?" đầu tiên sau ký tự "/"
			int indexOfQuestionMark = url.indexOf("?", lastIndex);
			// Lấy ra chuỗi con giữa các index tìm được
			String fileName = url.substring(lastIndex + 1, indexOfQuestionMark);
			service.deleteFile(fileName);
		}
		return ResponseEntity.ok("success");
	}
}