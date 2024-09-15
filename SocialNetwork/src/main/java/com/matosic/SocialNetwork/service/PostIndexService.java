package com.matosic.SocialNetwork.service;

import com.matosic.SocialNetwork.model.PostIndex;
import com.matosic.SocialNetwork.repository.elastic.ElasticPostRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch.core.DeleteByQueryRequest;
import co.elastic.clients.elasticsearch.core.DeleteByQueryResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class PostIndexService {

	@Autowired
    private  ElasticPostRepository postRepository;
   
    // Create or Update a Post
    public PostIndex savePost(PostIndex post) {
        return postRepository.save(post);
    }

    // Get a Post by ID
    public Optional<PostIndex> getPostById(Long id) {
        return postRepository.findById(id);
    }

    // Delete a Post by ID
    public void deletePost(Long id) {
        postRepository.deleteAll();
    }

    // Get all Posts
    public Iterable<PostIndex> getAllPosts() {
        return postRepository.findAll();
    }
    
    // Get all Posts
    public Iterable<PostIndex> getAllPostsByTitle(String title) {
        return postRepository.findAllByTitle(title);
    }
}
