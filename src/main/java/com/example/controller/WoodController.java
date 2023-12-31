package com.example.controller;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.servlet.http.HttpServletRequest;

import org.springframework.http.HttpEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.example.dto.PredictWood;
import com.example.dto.RequestPredictWood;
import com.example.model.Feature;
import com.example.model.FeatureWood;
import com.example.model.GeographicalArea;
import com.example.model.Image;
import com.example.model.Wood;
import com.example.repository.FeatureRepository;
import com.example.repository.ImageRepository;
import com.example.repository.WoodRepository;
import com.example.service.FireBaseFileService;
import com.google.gson.Gson;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.Result;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.qrcode.QRCodeReader;
import com.google.zxing.qrcode.QRCodeWriter;

import org.springframework.http.HttpHeaders;

@RestController
@RequestMapping("/wood")
@CrossOrigin(origins = "*")
public class WoodController {
	
	@Autowired
	private WoodRepository woodRepo;
	@Autowired
	private FireBaseFileService service;
	@Autowired
	private ImageRepository imgRepo;
	@Autowired
	private FeatureRepository feaRepo;
	@GetMapping("/get")
	private Page<Wood> getWood2(
			@RequestParam(name="category")int  category,
			@RequestParam(name = "pageNum") int pageNum,
			@RequestParam(name = "key", defaultValue = "", required = false) String key,
			@RequestParam(name = "family", required = false) List<String> listFamily,
			@RequestParam(name = "area", required = false) List<String> listArea,
			@RequestParam(name = "color", required = false) List<String> listColor,
			@RequestParam(name = "cites", required = false) List<String> listCistes,
			@RequestParam(name = "preservation", required = false) List<String> listIucns,
			@RequestParam(name = "sort", required = false) String sort
//			@RequestParam(name = "from", required = false) String from,
//			@RequestParam(name = "to", required = false) String to
			, HttpServletRequest request) {
		int size = 8;
		// sort
		String sortField = "vietnameName";
		if (sort != null) {
			if (sort.equalsIgnoreCase("Tên Việt Nam thường gọi"))
				sortField = "vietnameName";
			else if (sort.equalsIgnoreCase("Tên khoa học"))
				sortField = "scientificName";
			else if (sort.equalsIgnoreCase("Tên thương mại"))
				sortField = "commercialName";
			else if (sort.equalsIgnoreCase("Trọng lượng riêng"))
				sortField = "specificGravity";
		}

		// tim kiem theo tu khoa
		Specification<Wood> nameLike = (root, query, criteriaBuilder) -> {
			String likeKey = "%" + key + "%";
			return criteriaBuilder.or(criteriaBuilder.like(root.get("vietnameName"), likeKey),
					criteriaBuilder.like(root.get("scientificName"), likeKey));
		};
		
		// lọc theo tên tiếng việt không null
		
		// tim kiem theo mau

		Specification<Wood> filter = Specification.where(nameLike);
		// tìm kiếm theo category
		
		Specification<Wood> categoryWood = (root, query, criteriaBuilder) -> {
			return criteriaBuilder.equal(root.get("categoryWood"),category);
		};
		filter=filter.and(categoryWood);
		// tim kiem theo ho
		if (listFamily != null) {
			Specification<Wood> belongToFamily = (root, query, criteriaBuilder) -> {
				query.distinct(true);
				return criteriaBuilder.in(root.get("family").get("english")).value(listFamily);
			};
			filter = filter.and(belongToFamily);
		}
		// tim kiem theo cites
		if (listCistes != null) {
			Specification<Wood> belongToAppendixCites = (root, query, criteriaBuilder) -> criteriaBuilder
					.in(root.get("appendixCites").get("name")).value(listCistes);
			filter = filter.and(belongToAppendixCites);
		}
		// tim kiem theo iucns
		if (listIucns != null) {
			Specification<Wood> belongToPreservation = (root, query, criteriaBuilder) -> criteriaBuilder
					.in(root.get("preservation").get("acronym")).value(listIucns);
			filter = filter.and(belongToPreservation);
		}

		// tim kiem theo phan vung
		if (listArea != null) {
			Specification<Wood> belongToArea = (root, query, criteriaBuilder) -> criteriaBuilder
					.in(root.join("listAreas").get("english")).value(listArea);
			filter = filter.and(belongToArea);
		}

		// tim kiem dua tren mau
		if (listColor != null) {
			Specification<Wood> belong = (root, query, criteriaBuilder) -> criteriaBuilder
					.like(root.get("color"), "%" + listColor.get(0) + "%");
			Specification<Wood> belongToColor = Specification.where(belong);
			for (int i = 1; i < listColor.size(); i++) {
				String like = "%" + listColor.get(i) + "%";
				belong = (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("color"), like);
				belongToColor = belongToColor.or(belong);
			}
			filter = filter.and(belongToColor);
		}

		Pageable pageable = PageRequest.of(pageNum - 1, size, Sort.by(sortField).ascending());
		return woodRepo.findAll(filter, pageable);
	}
	@GetMapping("/get-400-species")
	private Page<Wood> getWood400Species(@RequestParam(name = "pageNum") int pageNum,
			@RequestParam(name = "key", defaultValue = "", required = false) String key,
			@RequestParam(name = "family", required = false) List<String> listFamily,
			@RequestParam(name = "area", required = false) List<String> listArea,
			@RequestParam(name = "color", required = false) List<String> listColor,
			@RequestParam(name = "cites", required = false) List<String> listCistes,
			@RequestParam(name = "sort", required = false) String sort) {

		int size = 9;
		// sort
		String sortField = "vietnameName";
		if (sort != null) {
			if (sort.equalsIgnoreCase("Tên khoa học"))
				sortField = "scientificName";
			else if (sort.equalsIgnoreCase("Tên thương mại"))
				sortField = "commercialName";
		}

		// tim kiem theo tu khoa
		Specification<Wood> nameLike = (root, query, criteriaBuilder) -> {
			String likeKey = "%" + key + "%";
			return criteriaBuilder.like(root.get("scientificName"), likeKey);
		};
		
		
		// tim kiem theo mau

		Specification<Wood> filter = Specification.where(nameLike);
		// lọc theo tên tiếng việt không null
		Specification<Wood> VietNameseNull = (root, query, criteriaBuilder) -> {
			return criteriaBuilder.isNull(root.get("vietnameName"));
		};
		filter=filter.and(VietNameseNull);
		// tim kiem theo ho
		if (listFamily != null) {
			Specification<Wood> belongToFamily = (root, query, criteriaBuilder) -> {
				query.distinct(true);
				return criteriaBuilder.in(root.get("family").get("english")).value(listFamily);
			};
			filter = filter.and(belongToFamily);
		}
		// tim kiem theo cites
		if (listCistes != null) {
			Specification<Wood> belongToAppendixCites = (root, query, criteriaBuilder) -> criteriaBuilder
					.in(root.get("appendixCites").get("name")).value(listCistes);
			filter = filter.and(belongToAppendixCites);
		}
		

		// tim kiem theo phan vung
		if (listArea != null) {
			Specification<Wood> belongToArea = (root, query, criteriaBuilder) -> criteriaBuilder
					.in(root.join("listAreas").get("english")).value(listArea);
			filter = filter.and(belongToArea);
		}

		// tim kiem dua tren mau
		if (listColor != null) {
			Specification<Wood> belong = (root, query, criteriaBuilder) -> criteriaBuilder
					.like(root.get("color"), "%" + listColor.get(0) + "%");
			Specification<Wood> belongToColor = Specification.where(belong);
			for (int i = 1; i < listColor.size(); i++) {
				String like = "%" + listColor.get(i) + "%";
				belong = (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("color"), like);
				belongToColor = belongToColor.or(belong);
			}
			filter = filter.and(belongToColor);
		}
		
		Pageable pageable = PageRequest.of(pageNum - 1, size, Sort.by(sortField).ascending());
		return woodRepo.findAll(filter, pageable);
	}

	@GetMapping("/getAll")
	private List<Wood>getAll(@RequestParam(name="category", defaultValue = "0", required = false)int category){
		if(category==0)
			return woodRepo.findAll();
		else
			return woodRepo.findByCategoryWoodId(category);
	}
	@PostMapping("/save")
	private Wood getWood(@RequestPart("file")MultipartFile[] files, @RequestPart("wood")Wood wood) throws IOException {
		List<Image>listImg=wood.getListImage();
		for(int i=0;i<files.length;i++) {
			Image img=listImg.get(i);
			MultipartFile file=files[i];
			String url=service.saveFile(file);
			img.setPath(url);
			img.setWood(wood);
			listImg.set(i, img);
		}
		List<FeatureWood>listFeatureWood=wood.getListFeatureWood();
		for(FeatureWood i:listFeatureWood)
		{
			i.setWood(wood);
			System.out.println(i.getFeature());
		}
		wood.setListFeatureWood(listFeatureWood);
		wood.setListImage(listImg);
		
		return woodRepo.save(wood);
	}
	@PostMapping("/saveImg")
	private Wood saveImg(@RequestPart("file")MultipartFile file, @RequestPart("wood")Wood wood) throws IOException {
		List<Image> listImg = wood.getListImage();
		Image img = listImg.get(listImg.size()-1);
		String url = service.saveFile(file);
		img.setPath(url);
		img.setWood(wood);
		listImg.set(listImg.size()-1, img);
		wood.setListImage(listImg);
		return woodRepo.save(wood);
	}
	@PostMapping("/saveEdit")
	private Wood saveEditWood(@RequestBody Wood wood) {
		for(FeatureWood i:wood.getListFeatureWood())
		{
			i.setWood(wood);
			for(Feature j:i.getFeature().getListSubFeature())
				j.setParentFeature(i.getFeature());
		}
		return woodRepo.save(wood);
	}
	@PostMapping("/delete")
	private void deleteWood(@RequestBody Wood wood) {
		for(Image i:wood.getListImage()) {
			String pathfile = i.getPath();
			// Tìm index của ký tự "/" cuối cùng trong URL
			int lastIndex = pathfile.lastIndexOf("/");
			// Tìm index của ký tự "?" đầu tiên sau ký tự "/"
			int indexOfQuestionMark = pathfile.indexOf("?", lastIndex);
			// Lấy ra chuỗi con giữa các index tìm được
			String fileName = pathfile.substring(lastIndex + 1, indexOfQuestionMark);
			service.deleteFile(fileName);
		}
		woodRepo.delete(wood);
	}
	@PostMapping("/deleteImg")
	private void deleteImg(@RequestBody Image i) {
		if(i.getPath().contains("https://www.delta-intkey.com")==false) {
			String pathfile = i.getPath();
			// Tìm index của ký tự "/" cuối cùng trong URL
			int lastIndex = pathfile.lastIndexOf("/");
			// Tìm index của ký tự "?" đầu tiên sau ký tự "/"
			int indexOfQuestionMark = pathfile.indexOf("?", lastIndex);
			// Lấy ra chuỗi con giữa các index tìm được
			String fileName = pathfile.substring(lastIndex + 1, indexOfQuestionMark);
			service.deleteFile(fileName);
		}
		imgRepo.delete(i);
	}
	@GetMapping("/getById")
	private Wood getById(int id) {
		return woodRepo.findById(id);
	}
	
	@GetMapping("/getAmountWood")
	private int getAmountWood() {
		return woodRepo.getAmountWood();
	}
	@PostMapping("/predictNhan")
	private Map<String, String> predictWood(@RequestBody RequestPredictWood image){
		Map<String, String>map=new LinkedHashMap<>();
		System.out.println(image);
		RestTemplate restTemplate = new RestTemplate();
		PredictWood predictWood=restTemplate.postForObject("http://ptitsure.tk:5001/predict", image, PredictWood.class);
		String name=predictWood.getWoodID();
		if(predictWood.getDes().equalsIgnoreCase("OK") && !name.equalsIgnoreCase("-1")) {
			map.put("des", "OK");
			name=name.replace("(50x)", "").trim();
			name=name.replace("\\s+", " ");
			String normalizedChuoi1 = Normalizer.normalize(name, Normalizer.Form.NFC);
			Wood wood=woodRepo.findByVietnameName("%"+normalizedChuoi1+"%");
			map.put("id", wood.getId()+"");
			map.put("vietnamName", wood.getVietnameName());
			map.put("scientificName", wood.getScientificName());
			map.put("commercialName", wood.getCommercialName());
			map.put("categoryWood", wood.getCategoryWood().getName());
			map.put("preservation", wood.getPreservation().getEnglish()+"("+wood.getPreservation().getAcronym()+")-"+wood.getPreservation().getVietnamese());
			map.put("family", wood.getVietnameName());
			map.put("specificGravity", wood.getSpecificGravity()+"kg/m³");
			map.put("characteristic", wood.getCharacteristic());
			map.put("appendixCites", wood.getAppendixCites().getName()+"-"+wood.getAppendixCites().getContent());
			map.put("note", wood.getNote());
			map.put("color", wood.getColor());
			String areas="";
			for(GeographicalArea area:wood.getListAreas()) {
				areas=areas+area.getEnglish()+", ";
			}
			areas=areas.substring(0,areas.length()-2);
			map.put("area", areas);
			map.put("prob", predictWood.getProb()+"");
		}else {
			map.put("des", "NOk");
		}
		return map;
	}
	@GetMapping("/createQRCode")
	private String generatedQRCode(@RequestParam("id")int id) throws WriterException, IOException{
		int width=300;
		int height=300;
		String data=id+"";
		StringBuilder result=new StringBuilder();
		if(!data.isEmpty()) {
			ByteArrayOutputStream os=new ByteArrayOutputStream();
			QRCodeWriter writer=new QRCodeWriter();
			BitMatrix bitMatrix=writer.encode(data, BarcodeFormat.QR_CODE,width , height);
			BufferedImage bufferedImage=MatrixToImageWriter.toBufferedImage(bitMatrix);
			ImageIO.write(bufferedImage, "png",os);
			result.append("data:image/png;base64,");
			result.append(new String(Base64.getEncoder().encode(os.toByteArray())));
		}
		return result.toString();
	}
	@PostMapping("/readQRCode")
	private Wood readQRCode(@RequestBody RequestPredictWood qrcode) {
		try {
			String image=qrcode.getImage();
			if(image.contains("data:image/png;base64,")) {
				image=qrcode.getImage().split(",")[1];
			}
			byte[] bytes=Base64.getDecoder().decode(image);
			ByteArrayInputStream bis=new ByteArrayInputStream(bytes);
			BufferedImage bufferedImage=ImageIO.read(bis);
			BufferedImageLuminanceSource bufferedImageLuminanceSource=new BufferedImageLuminanceSource(bufferedImage);
			HybridBinarizer hybridBinarizer=new HybridBinarizer(bufferedImageLuminanceSource);
			BinaryBitmap binaryBitmap=new BinaryBitmap(hybridBinarizer);
			MultiFormatReader multiFormatReader=new MultiFormatReader();
			Result result=multiFormatReader.decode(binaryBitmap);
			String qrCodeText=result.getText();
			Wood wood=woodRepo.findById(Integer.parseInt(qrCodeText));
			return wood;
		}catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return null;
	}

	@GetMapping("/searchByFeature")
	private Page<Wood> searchByFeatureWood(@RequestParam(name="idfeature", required = false)List<String>listIdFeatures,
			@RequestParam(name="selectfeature", required = false)List<String>listSelectFeature,
			@RequestParam(name="pageNum", required = false, defaultValue = "1")int page) {
		
		Specification<Wood> specification = Specification.where(null);
		for (int i = 0; i < listIdFeatures.size(); i++) {
			String idFeature=listIdFeatures.get(i);
			String selectFeature=listSelectFeature.get(i);
			specification = specification.and((root, query, criteriaBuilder) -> {
		        // Join FeatureWood entity
		        Join<Wood, FeatureWood> featureWoodJoin = root.join("listFeatureWood", JoinType.INNER);
		        
		        // Create predicates for filtering
		        List<String>list=new ArrayList<>();
		        if(selectFeature.equalsIgnoreCase("p"))
		        	list=Arrays.asList("p","v","?");
		        else if(selectFeature.equalsIgnoreCase("a"))
		        	list=Arrays.asList("a","v","?");
		        else if(selectFeature.equalsIgnoreCase("r"))
		        	list=Arrays.asList("p");
		        else if(selectFeature.equalsIgnoreCase("e"))
		        	list=Arrays.asList("a");
		        Predicate valuePredicate = criteriaBuilder.in(featureWoodJoin.get("value")).value(list);
		        Predicate featureIdPredicate = criteriaBuilder.equal(featureWoodJoin.get("feature").get("id"), idFeature);
		        
		        return criteriaBuilder.and(valuePredicate, featureIdPredicate);
		    });
		    
		    
		}
		int size=9;
		Pageable pageable = PageRequest.of(page - 1, size);
		return  woodRepo.findAll(specification, pageable);
	}
//	@GetMapping("/save")
//	private Wood saveWood() {
//		Wood wood=new Wood();
//		wood.setVietnameName("z");
//		Optional<Feature> f=feaRepo.findById(9);
//		FeatureWood featureWood=new FeatureWood();
//		featureWood.setFeature(f.get());
//		featureWood.setValue("p");
//		featureWood.setWood(wood);
//		wood.getListFeatureWood().add(featureWood);
//		return woodRepo.save(wood);
//	}
}


// lay dua tren from and to
//if (from != null && to != null) {
//	Specification<Wood> belong = (root, query, criteriaBuilder) -> {
//			return criteriaBuilder.and(criteriaBuilder.greaterThanOrEqualTo(root.get("specificGravity"), from),
//					criteriaBuilder.lessThanOrEqualTo(root.get("specificGravity"), to));
//	};
//	filter = filter.and(belong);
//}

//@GetMapping("/getMaxSpecificGravity")
//private int getMaxSpecificGravity() {
//	List[]list=woodRepo.findMaxSpecificGravity();
//	return Integer.valueOf(list[0].get(0).toString());
//}
//
//@GetMapping("/getMinSpecificGravity")
//private int getMinSpecificGravity() {
//	List[]list=woodRepo.findMinSpecificGravity();
//	return Integer.valueOf(list[0].get(0).toString());
//}

