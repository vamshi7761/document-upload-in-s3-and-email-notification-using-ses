package com.awss3.s3.Controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.awss3.s3.Service.S3Service;

import java.util.List;

@RestController
@RequestMapping("/api/s3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @PostMapping("/bucket")
    public void createBucket(@RequestParam String bucketName) {
        s3Service.createBucket(bucketName);
    }

    @DeleteMapping("/bucket")
    public void deleteBucket(@RequestParam String bucketName) {
        s3Service.deleteBucket(bucketName);
    }

    @GetMapping("/buckets")
    public List<String> findAllBuckets() {
        return s3Service.findAllBuckets();
    }

    @PostMapping("/upload")
    public void uploadFile(@RequestParam String bucketName, 
                           @RequestParam String key, 
                           @RequestParam String filePath) {
        s3Service.uploadFile(bucketName, key, filePath);
    }

    @GetMapping("/download")
    public void downloadFile(@RequestParam String bucketName, 
                             @RequestParam String key, 
                             @RequestParam String downloadPath) {
        s3Service.downloadFile(bucketName, key, downloadPath);
    }

    @DeleteMapping("/file")
    public void deleteFile(@RequestParam String bucketName, 
                           @RequestParam String key) {
        s3Service.deleteFile(bucketName, key);
    }
}
