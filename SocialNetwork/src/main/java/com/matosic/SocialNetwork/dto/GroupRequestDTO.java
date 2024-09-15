package com.matosic.SocialNetwork.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class GroupRequestDTO {
	private Long groupRequestId;
	private boolean approved;
	private String username;

	public GroupRequestDTO(Long groupRequestId, boolean approved, String username) {
        this.groupRequestId = groupRequestId;
        this.approved = approved;
        this.username = username;
    }
}