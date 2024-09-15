package com.matosic.SocialNetwork.controller;

import com.matosic.SocialNetwork.model.PostIndex;
import com.matosic.SocialNetwork.service.PostIndexService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/api/postselastic")
public class ElasticPostController {

	@Autowired
    private PostIndexService postService;

    // Create or Update a Post
    @PostMapping
    public ResponseEntity<PostIndex> createOrUpdatePost(@RequestBody PostIndex post) {
        PostIndex savedPost = postService.savePost(post);
        return ResponseEntity.ok(savedPost);
    }

    // Get a Post by ID
    @GetMapping("/{id}")
    public ResponseEntity<PostIndex> getPostById(@PathVariable Long id) {
        Optional<PostIndex> post = postService.getPostById(id);
        return post.map(ResponseEntity::ok).orElseGet(() -> ResponseEntity.notFound().build());
    }

    // Delete a Post by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePost(@PathVariable Long id) {
        postService.deletePost(id);
        return ResponseEntity.noContent().build();
    }

    // Get all Posts
    @GetMapping("/naziv/{title}")
    public ResponseEntity<Iterable<PostIndex>> getAllPosts(@PathVariable String title) {
        Iterable<PostIndex> posts = postService.getAllPostsByTitle(title);
        return ResponseEntity.ok(posts);
    }
    
    // Get all Posts
    @GetMapping
    public ResponseEntity<Iterable<PostIndex>> getAllPosts() {
        Iterable<PostIndex> posts = postService.getAllPosts();
        return ResponseEntity.ok(posts);
    }
}
