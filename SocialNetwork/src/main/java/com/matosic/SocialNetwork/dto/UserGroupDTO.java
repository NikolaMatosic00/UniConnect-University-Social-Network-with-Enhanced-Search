package com.matosic.SocialNetwork.dto;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class UserGroupDTO {
	private Long id;
    private String name;
    private int postsCount;
    private float averageLikes;
    private String description;
//    private MultipartFile descriptionPdf;
//    private Long adminId;
}
