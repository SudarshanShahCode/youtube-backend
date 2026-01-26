package com.youtube.services.impl;

import com.youtube.dtos.UserDto;
import com.youtube.dtos.VideoDto;
import com.youtube.entities.User;
import com.youtube.entities.Video;
import com.youtube.repositories.VideoRepository;
import com.youtube.services.UserService;
import com.youtube.services.VideoService;
import com.youtube.utils.S3Utils;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
public class VideoServiceImpl implements VideoService {

    private final S3Utils s3Utils;
    private final VideoRepository videoRepository;
    private final UserService userService;

    public VideoServiceImpl(S3Utils s3Utils, VideoRepository videoRepository, UserService userService) {
        this.s3Utils = s3Utils;
        this.videoRepository = videoRepository;
        this.userService = userService;
    }

    @Override
    public VideoDto uploadVideo(VideoDto videoDto) {
        if (!s3Utils.objectExists(videoDto.getS3Key())) {
            throw new IllegalStateException("Video upload not found in S3");
        }
        if (!s3Utils.objectExists(videoDto.getS3KeyThumbnail())) {
            throw new IllegalStateException("Thumbnail upload not found in S3");
        }

        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null) {
            throw new RuntimeException("User not authenticated");
        }

        String email = auth.getName();
        UserDto userDto = userService.getUserByEmail(email);
        var user = User.builder()
                .userId(userDto.getUserId())
                .build();

        var video = Video.builder()
                .s3Key(videoDto.getS3Key())
                .title(videoDto.getTitle())
                .status("READY")
                .description(videoDto.getDescription())
                .s3KeyThumbnail(videoDto.getS3KeyThumbnail())
                .tags(videoDto.getTags())
                .user(user)
                .build();

        Video savedVideo = videoRepository.save(video);

        return  VideoDto.builder()
                .videoId(savedVideo.getVideoId())
                .title(savedVideo.getTitle())
                .description(savedVideo.getDescription())
                .tags(savedVideo.getTags())
                .status(savedVideo.getStatus())
                .user(userDto)
                .build();
    }

    @Override
    public String playbackVideoUrl(Long videoId) {
        Video video = videoRepository.findById(videoId)
                .orElseThrow(() -> new RuntimeException("Video not found"));

        if (!Objects.equals(video.getStatus(), "READY")) {
            throw new RuntimeException("Video not ready for playback");
        }

        return s3Utils.presignGetUrl(video.getS3Key());
    }

    @Override
    public List<VideoDto> getAllVideos() {
        List<Video> videos = videoRepository.findAll();
        return videos.stream()
                .map(video -> VideoDto.builder()
                        .videoId(video.getVideoId())
                        .title(video.getTitle())
                        .description(video.getDescription())
                        .thumbnail(s3Utils.presignGetUrl(video.getS3KeyThumbnail()))
                        .tags(video.getTags())
                        .status(video.getStatus())
                        .playbackUrl(s3Utils.presignGetUrl(video.getS3Key()))
                        .user(userService.getUserById(video.getUser().getUserId()))
                        .build())
                .toList();
    }

    @Override
    public List<VideoDto> getVideosByUser(Long userId) {
        return List.of();
    }
}
