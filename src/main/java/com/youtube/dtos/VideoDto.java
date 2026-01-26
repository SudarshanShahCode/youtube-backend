package com.youtube.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.time.Instant;

@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class VideoDto {

    private Long videoId;

    @NotBlank(message = "S3 key cannot be blank")
    private String s3Key;

    private String playbackUrl;

    private String status;

    @NotBlank(message = "Title cannot be blank")
    private String title;

    @NotBlank(message = "Description cannot be blank")
    private String description;

    private String tags;

    @NotBlank(message = "S3 key cannot be blank")
    private String s3KeyThumbnail;

    private String thumbnail;

    private UserDto user;

    private Instant createdAt;

    private Instant updatedAt;
}
