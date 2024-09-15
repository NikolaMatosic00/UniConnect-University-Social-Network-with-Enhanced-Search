package com.matosic.SocialNetwork.dto;

import lombok.Data;

import org.springframework.web.multipart.MultipartFile;

@Data
public class CreatePostRequest {
	private String title;
	private Long userId;
	private Long userGroupId;
	private MultipartFile pdfFile;
}
