package com.youtube.services;

import com.youtube.dtos.VideoDto;

import java.util.List;

public interface VideoService {

    VideoDto uploadVideo(VideoDto videoDto);

    String playbackVideoUrl(Long videoId);

    List<VideoDto> getAllVideos();

    List<VideoDto> getVideosByUser(Long userId);
}
