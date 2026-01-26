package com.youtube.controllers;

import com.youtube.dtos.PresignedUploadResponse;
import com.youtube.dtos.VideoDto;
import com.youtube.services.VideoService;
import com.youtube.utils.S3Utils;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/videos")
public class VideoController {

    private final S3Utils s3Utils;
    private final VideoService videoService;

    public VideoController(S3Utils s3Utils, VideoService videoService) {
        this.s3Utils = s3Utils;
        this.videoService = videoService;
    }

    @PostMapping("/upload-url")
    public ResponseEntity<PresignedUploadResponse> getUploadUrl(@RequestParam String fileName,
                                                                @RequestParam String folder,
                                                                @RequestParam(required = false) String contentType) throws URISyntaxException {
        String key = folder + "/" + UUID.randomUUID() + "-" + fileName;
        String url = s3Utils.presignUploadUrl(key, contentType);

        return ResponseEntity.ok(new PresignedUploadResponse(key, url));
    }

    @PostMapping("/upload-video")
    public ResponseEntity<VideoDto> uploadVideo(@Valid @RequestBody VideoDto videoDto) {
        return ResponseEntity.ok(videoService.uploadVideo(videoDto));
    }

    @GetMapping("/play/{videoId}")
    public ResponseEntity<String> playVideo(@PathVariable Long videoId) {
        return ResponseEntity.ok(videoService.playbackVideoUrl(videoId));
    }

    @GetMapping("/all")
    public ResponseEntity<List<VideoDto>> getAllVideos() {
        return ResponseEntity.ok(videoService.getAllVideos());
    }
}
