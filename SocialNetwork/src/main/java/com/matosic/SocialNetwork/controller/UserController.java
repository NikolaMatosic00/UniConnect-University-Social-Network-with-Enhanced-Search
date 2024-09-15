package com.matosic.SocialNetwork.controller;


import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matosic.SocialNetwork.dto.ChangePasswordRequest;
import com.matosic.SocialNetwork.dto.FriendDto;
import com.matosic.SocialNetwork.dto.LoginRequest;
import com.matosic.SocialNetwork.dto.PostDto;
import com.matosic.SocialNetwork.dto.RegistrationRequest;
import com.matosic.SocialNetwork.dto.UpdateDescriptionRequest;
import com.matosic.SocialNetwork.dto.UserDTO;
import com.matosic.SocialNetwork.model.Post;
import com.matosic.SocialNetwork.model.User;
import com.matosic.SocialNetwork.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/users")
@Slf4j
@CrossOrigin("http://localhost:3000/")
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@RequestBody RegistrationRequest registrationRequest) {
        log.info("Registering user with email: {}", registrationRequest.getEmail());
        User user = userService.registerUser(registrationRequest);
        log.info("User registered successfully: {}", user.getUsername());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<UserDTO> loginUser(@RequestBody LoginRequest credentials) {
        log.info("Attempting to log in user: {}", credentials.getUsername());
        UserDTO user = userService.loginUser(credentials.getUsername(), credentials.getPassword());
        if (user == null) {
            log.warn("Login failed for user: {}", credentials.getUsername());
            return ResponseEntity.status(401).build();
        }
        log.info("User logged in successfully: {}", credentials.getUsername());
        return ResponseEntity.ok(user);
    }
    @PostMapping("/change-password")
    public ResponseEntity<String> changePassword(@RequestBody ChangePasswordRequest changePasswordRequest) {
        boolean success = userService.changePassword(
                changePasswordRequest.getUsername(),
                changePasswordRequest.getOldPassword(),
                changePasswordRequest.getNewPassword());

        if (success) {
            return ResponseEntity.ok("Password changed successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to change password. Please check your credentials.");
        }
    }
    
    @GetMapping("/get-friends/{userId}")
    public List<UserDTO> getUserFriends(@PathVariable Long userId) {

        return userService.getFriends(userId);
    }
    
     
    @GetMapping("/get-user/{userId}")
    public UserDTO getUser(@PathVariable Long userId) {

        return userService.getUser(userId);
    }
    
    @PostMapping("/update-description")
    public ResponseEntity<String> updateDescription(@RequestBody UpdateDescriptionRequest updateDescriptionRequest) {
        boolean success = userService.updateDescription(
                updateDescriptionRequest.getUsername(),
                updateDescriptionRequest.getDescription());

        if (success) {
            return ResponseEntity.ok("Description updated successfully.");
        } else {
            return ResponseEntity.badRequest().body("Failed to update description.");
        }
    }

    
    
    @GetMapping("/get-all")
    public List<User> getAll() {

        return userService.findAll();
    }
    
    @GetMapping("/{userId}/friends-and-groups-posts")
    public List<PostDto> getFriendsAndGroupsPosts(@PathVariable Long userId) {
        List<Post> posts = userService.getFriendsAndGroupsPosts(userId);
        return posts.stream().map(PostDto::new).collect(Collectors.toList());
    }
    
    @GetMapping("/search-for-new-friends")
    public ResponseEntity<List<FriendDto>> searchForNewFriends(@RequestParam String keyword) {
        List<FriendDto> users = userService.searchForNewFriends(keyword);
        return ResponseEntity.ok(users);
    }
}
