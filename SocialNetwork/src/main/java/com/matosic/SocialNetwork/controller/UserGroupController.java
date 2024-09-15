package com.matosic.SocialNetwork.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matosic.SocialNetwork.dto.CreateUserGroupDto;
import com.matosic.SocialNetwork.dto.GroupRequestDTO;
import com.matosic.SocialNetwork.dto.GroupsOfUserDto;
import com.matosic.SocialNetwork.dto.PostDto;
import com.matosic.SocialNetwork.dto.UserGroupDTO;
import com.matosic.SocialNetwork.model.GroupRequest;
import com.matosic.SocialNetwork.model.User;
import com.matosic.SocialNetwork.model.UserGroupIndex;
import com.matosic.SocialNetwork.service.UserGroupIndexService;
import com.matosic.SocialNetwork.service.UserGroupService;
import com.matosic.SocialNetwork.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/groups")
@Slf4j
@CrossOrigin("http://localhost:3000/")
public class UserGroupController {

	@Autowired
	private UserGroupService userGroupService;

	@Autowired
	private UserService userService;

	@PostMapping("/create")
	public ResponseEntity<String> createGroup(@ModelAttribute CreateUserGroupDto userGroupDTO) {
		try {
			userGroupService.createUserGroup(userGroupDTO);
			return ResponseEntity.ok("created");
		} catch (IOException e) {
			log.error("Failed to create group", e);
			return ResponseEntity.status(500).body(null);
		}
	}

	@GetMapping("/get-all-groups-of-user/{userId}")
	public ResponseEntity<List<GroupsOfUserDto>> getUsersGroups(@PathVariable Long userId) {
		List<GroupsOfUserDto> postDtos = userGroupService.getAllUsersGroups(userId);
		return ResponseEntity.ok(postDtos);
	}

	@PostMapping("/suspend/{groupId}")
	public ResponseEntity<String> suspendGroup(@PathVariable Long groupId, @RequestParam String reason) {
		userGroupService.suspendGroup(groupId, reason);
		return ResponseEntity.ok("Group suspended successfully.");
	}

	@PostMapping("/send-join-request")
	public ResponseEntity<String> sendGroupJoinRequest(@RequestParam Long groupId, @RequestParam Long userId) {
		boolean requestSent = userGroupService.sendGroupJoinRequest(groupId, userId);

		if (requestSent) {
			return ResponseEntity.ok("Join request sent successfully.");
		} else {
			return ResponseEntity.badRequest().body("Failed to send join request.");
		}
	}

	@GetMapping("/group-join-requests/{groupName}")
	public ResponseEntity<List<GroupRequestDTO>> getGroupRequests(@PathVariable String groupName) {
		return ResponseEntity.ok(userGroupService.getGroupRequests(groupName));
	}

	@PostMapping("/requests/approve/{requestId}")
	public ResponseEntity<String> addUserToGroup(@PathVariable Long requestId) {
		userGroupService.approveJoinRequestAndAddUserToGroup(requestId);
		return ResponseEntity.ok("User added to group successfully.");
	}

	@PostMapping("/requests/reject/{requestId}")
	public ResponseEntity<String> rejectJoinRequest(@PathVariable Long requestId) {
		boolean rejected = userGroupService.rejectJoinRequest(requestId);
		if (rejected) {
			return ResponseEntity.ok("Join request rejected.");
		} else {
			return ResponseEntity.badRequest().body("Invalid join request ID.");
		}
	}

	@GetMapping("/search")
	public ResponseEntity<List<UserGroupDTO>> searchUserGroups(@RequestParam(required = false) String name,
			@RequestParam(required = false) String description, @RequestParam(required = false) Integer minPostCount,
			@RequestParam(required = false) Integer maxPostCount) {

		List<UserGroupIndex> result = userGroupService.combinedUserGroupSearch(name, description, minPostCount,
				maxPostCount);

		List<UserGroupDTO> userGroupDtoList = new ArrayList<>();
		for (UserGroupIndex indeks : result) {
			UserGroupDTO uDTO = userGroupService.entityToDtoById(indeks.getId());
			userGroupDtoList.add(uDTO);
		}
		return ResponseEntity.ok(userGroupDtoList);
	}
}
