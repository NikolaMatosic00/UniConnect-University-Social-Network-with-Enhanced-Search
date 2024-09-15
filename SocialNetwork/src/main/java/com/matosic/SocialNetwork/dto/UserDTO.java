package com.matosic.SocialNetwork.dto;

import java.time.LocalDateTime;
import java.util.ArrayList;

import jakarta.persistence.Column;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UserDTO {
	
	private Long id;

	private String username;

	private String password;

	private String email;

	private LocalDateTime lastLogin;

	private String firstName;

	private String lastName;

	private String displayName;

	private String description;
	
	private ArrayList<String> groups = new ArrayList<>();
}
