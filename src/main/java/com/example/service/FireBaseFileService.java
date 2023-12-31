package com.example.service;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.storage.BlobId;
import com.google.cloud.storage.BlobInfo;
import com.google.cloud.storage.Storage;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.StorageClient;

@Service
public class FireBaseFileService {
	private Storage storage;
	private String your_bucket_name="da-demo-f3220.appspot.com";
	public FireBaseFileService() {
		super();
		try {
			FileInputStream serviceAccount=new FileInputStream("./serviceAccountKey.json");
			FirebaseOptions options = new FirebaseOptions.Builder()
					.setCredentials(GoogleCredentials.fromStream(serviceAccount))
					.setStorageBucket(your_bucket_name).build();
			boolean hasBeenInitialized=false;
			List<FirebaseApp>firebaseApps=FirebaseApp.getApps();
			for(FirebaseApp app:firebaseApps) {
				if(app.getName().equals(FirebaseApp.DEFAULT_APP_NAME))
					hasBeenInitialized=true;
			}
			if(!hasBeenInitialized)
				FirebaseApp.initializeApp(options);
			storage=StorageClient.getInstance().bucket().getStorage();
		}catch(FileNotFoundException e) {
			e.printStackTrace();
		}catch(IOException e) {
			e.printStackTrace();
		}
		
	}
	public String saveFile(MultipartFile file) throws IOException {
		String imageName=generateFileName(file.getOriginalFilename());
        BlobId blobId = BlobId.of(your_bucket_name, imageName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();
        String fileUrl="https://firebasestorage.googleapis.com/v0/b/da-demo-f3220.appspot.com/o/"+imageName+"?alt=media";
        storage.create(blobInfo, file.getInputStream());
        return fileUrl;
	}
	// cap nhat mot anh da ton tai
	public void updateFile(MultipartFile file, String imageName) throws IOException{
		BlobId blobId = BlobId.of(your_bucket_name, imageName);
        BlobInfo blobInfo = BlobInfo.newBuilder(blobId)
                .setContentType(file.getContentType())
                .build();
        storage.create(blobInfo, file.getInputStream());
	}
	// xoa mot anh da ton tai
	public boolean deleteFile(String name) {
		BlobId blobId=BlobId.of(your_bucket_name, name);
		return storage.delete(blobId);
	}
	private String generateFileName(String originalFileName) {
        return UUID.randomUUID().toString() + "." + getExtension(originalFileName);
    }

    private String getExtension(String originalFileName) {
        return StringUtils.getFilenameExtension(originalFileName);
    }
}
