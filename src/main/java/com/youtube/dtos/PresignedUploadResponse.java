package com.youtube.dtos;

public record PresignedUploadResponse(String s3Key, String uploadUrl) {
}
