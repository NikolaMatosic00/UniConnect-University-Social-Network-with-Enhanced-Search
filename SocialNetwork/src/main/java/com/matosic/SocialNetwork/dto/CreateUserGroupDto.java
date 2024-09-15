package com.matosic.SocialNetwork.dto;

import org.springframework.web.multipart.MultipartFile;

import lombok.Data;

@Data
public class CreateUserGroupDto {
	private String name;
	private Long userId;
	private MultipartFile pdfFile;
} 
