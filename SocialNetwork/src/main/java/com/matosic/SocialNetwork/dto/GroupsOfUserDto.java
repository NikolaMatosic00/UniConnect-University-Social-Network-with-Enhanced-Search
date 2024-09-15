package com.matosic.SocialNetwork.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupsOfUserDto {
	
	private Long groupId;
	
	private String groupName;
	
	private boolean admin;
	
}
