package com.matosic.SocialNetwork.controller;

import com.matosic.SocialNetwork.model.PostIndex;
import com.matosic.SocialNetwork.model.UserGroupIndex;
import com.matosic.SocialNetwork.service.UserGroupIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/usergroups")
public class ElasticUserGroupController {

	@Autowired
    private UserGroupIndexService userGroupService;

    // Create or Update a User Group
    @PostMapping
    public ResponseEntity<UserGroupIndex> createOrUpdateUserGroup(@RequestBody UserGroupIndex userGroup) {
        UserGroupIndex savedUserGroup = userGroupService.saveUserGroup(userGroup);
        return ResponseEntity.ok(savedUserGroup);
    }

    // Get a User Group by ID
    @GetMapping("/{id}")
    public ResponseEntity<UserGroupIndex> getUserGroupById(@PathVariable Long id) {
        Optional<UserGroupIndex> userGroup = userGroupService.getUserGroupById(id);
        return userGroup.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a User Group by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteUserGroup(@PathVariable Long id) {
        userGroupService.deleteUserGroup(id);
        return ResponseEntity.noContent().build();
    }

    // Get all User Groups
    @GetMapping
    public ResponseEntity<Iterable<UserGroupIndex>> getAllUserGroups() {
        Iterable<UserGroupIndex> userGroups = userGroupService.getAllUserGroups();
        return ResponseEntity.ok(userGroups);
    }
    
    // Get all Posts
    @GetMapping("/naziv/{name}")
    public ResponseEntity<Iterable<UserGroupIndex>> getAllPosts(@PathVariable String name) {
        Iterable<UserGroupIndex> usergroups = userGroupService.getAllUserGroupsByName(name);
        return ResponseEntity.ok(usergroups);
    }
}
