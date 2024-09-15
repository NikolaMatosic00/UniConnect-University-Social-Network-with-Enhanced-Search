package com.matosic.SocialNetwork.dto;

import lombok.Data;

@Data
public class FriendRequestDTO {
	
	private Long id;
	private String username;
	private String firstName;
	private String lastName;
}
