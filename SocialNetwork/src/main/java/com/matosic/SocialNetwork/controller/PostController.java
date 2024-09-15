package com.matosic.SocialNetwork.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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

import com.matosic.SocialNetwork.dto.CreatePostRequest;
import com.matosic.SocialNetwork.dto.PostDto;
import com.matosic.SocialNetwork.model.Post;
import com.matosic.SocialNetwork.model.PostIndex;
import com.matosic.SocialNetwork.model.User;
import com.matosic.SocialNetwork.service.PostService;
import com.matosic.SocialNetwork.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/posts")
@CrossOrigin("http://localhost:3000/")
@Slf4j
public class PostController {

	@Autowired
	private PostService postService;

	@PostMapping("/create")
	public ResponseEntity<String> createPost(@ModelAttribute CreatePostRequest createPostRequest) {
		postService.createPost(createPostRequest);
		return ResponseEntity.ok("created");
	}

	@GetMapping("/{userId}")
	public ResponseEntity<List<PostDto>> getUserFeed(@PathVariable Long userId) {
		List<PostDto> postDtos = postService.getFeedForUser(userId);
		return ResponseEntity.ok(postDtos);
	}

	@GetMapping("/getall")
	public ResponseEntity<List<PostDto>> getAllPosts() {
		List<PostDto> postDtos = postService.getAllPosts();
		return ResponseEntity.ok(postDtos);
	}
	
	@GetMapping("/getall/{groupName}")
	public ResponseEntity<List<PostDto>> getAllPostsFromGroup(@PathVariable String groupName) {
		List<PostDto> postDtos = postService.getAllPostsFromGroup(groupName);
		return ResponseEntity.ok(postDtos);
	}
	
	@GetMapping("/search")
    public List<PostIndex> searchPosts(
        @RequestParam(required = false) String title,
        @RequestParam(required = false) String content,
        @RequestParam(required = false) Integer minLikes,
        @RequestParam(required = false) Integer maxLikes 
    ) {
        return postService.combinedSearch(title, content, minLikes, maxLikes);
    }
}
