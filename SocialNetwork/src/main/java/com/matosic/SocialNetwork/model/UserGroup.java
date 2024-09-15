package com.matosic.SocialNetwork.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Data
@NoArgsConstructor
@Slf4j
public class UserGroup {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true)
	private String name;

	private String description;

	@Column(nullable = false)
	private LocalDateTime creationDate;

	@Column(nullable = false)
	private boolean isSuspended;

	private String suspendedReason;

	@ManyToMany(mappedBy = "groups")
	private Set<User> users;

	@OneToMany(mappedBy = "group")
	private Set<GroupRequest> groupRequests;

	@OneToMany(mappedBy = "group")
	private Set<Banned> banneds;

	@ManyToOne
	@JoinColumn(name = "admin_id")
	private User admin;

	@OneToMany(mappedBy = "userGroup")
	private Set<Post> posts;

	private String pdfUrl;


	public void logGroupSuspension() {
		log.info("Group suspended: {} - Reason: {}", name, suspendedReason);
	}
}
