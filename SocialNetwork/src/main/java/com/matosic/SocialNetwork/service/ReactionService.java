package com.matosic.SocialNetwork.service;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matosic.SocialNetwork.model.Comment;
import com.matosic.SocialNetwork.model.Post;
import com.matosic.SocialNetwork.model.PostIndex;
import com.matosic.SocialNetwork.model.Reaction;
import com.matosic.SocialNetwork.model.ReactionType;
import com.matosic.SocialNetwork.model.User;
import com.matosic.SocialNetwork.repository.elastic.ElasticPostRepository;
import com.matosic.SocialNetwork.repository.jpa.CommentRepository;
import com.matosic.SocialNetwork.repository.jpa.PostRepository;
import com.matosic.SocialNetwork.repository.jpa.ReactionRepository;
import com.matosic.SocialNetwork.repository.jpa.UserRepository;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ReactionService {

	@Autowired
	private ReactionRepository reactionRepository;

	@Autowired
	private ElasticPostRepository elasticPostRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private CommentRepository commentRepository;

	@Autowired
	private UserRepository userRepository;

	@Transactional
	public Reaction addReaction(Long postId, Long userId, ReactionType reactionType) {
		Post post = postRepository.findById(postId).orElseThrow(() -> new IllegalArgumentException("Invalid post ID"));
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

		Reaction reaction = new Reaction();
		reaction.setType(reactionType);
		reaction.setTimestamp(LocalDateTime.now());
		reaction.setPost(post);
		reaction.setUser(user);

		// Save the reaction
		reaction = reactionRepository.save(reaction);

		// Add the reaction to the post's set of reactions
		post.getReactions().add(reaction);
		postRepository.save(post);

		// Update the likesCount in PostIndex (Elasticsearch)
		Optional<PostIndex> postIndexOptional = elasticPostRepository.findById(postId);
		if (postIndexOptional.isPresent()) {
			PostIndex postIndex = postIndexOptional.get();
			postIndex.setLikesCount(postIndex.getLikesCount() + 1); // Increment likes count
			elasticPostRepository.save(postIndex);
			log.info("PostIndex updated with new likes count: {}", postIndex.getLikesCount());
		} else {
			log.warn("PostIndex not found for post ID: {}", postId);
		}

		return reaction;
	}

	public Reaction addReactionToComment(Long commentId, Long userId, ReactionType type) {
		Comment comment = commentRepository.findById(commentId)
				.orElseThrow(() -> new IllegalArgumentException("Invalid comment ID"));
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

		Reaction reaction = new Reaction();
		reaction.setType(type);
		reaction.setTimestamp(LocalDateTime.now());
		reaction.setUser(user);
		reaction.setComment(comment);

		// Save the reaction
	    reaction = reactionRepository.save(reaction);

	    // Update the likesCount in PostIndex (Elasticsearch)
	    Post post = comment.getPost(); // Assuming Comment has a reference to its Post
	    Optional<PostIndex> postIndexOptional = elasticPostRepository.findById(post.getId());
	    if (postIndexOptional.isPresent()) {
	        PostIndex postIndex = postIndexOptional.get();
	        postIndex.setLikesCount(postIndex.getLikesCount() + 1); // Increment likes count
	        elasticPostRepository.save(postIndex);
	        log.info("PostIndex updated with new likes count (after comment reaction): {}", postIndex.getLikesCount());
	    } else {
	        log.warn("PostIndex not found for post ID: {}", post.getId());
	    }

	    log.info("User '{}' reacted with '{}' to comment '{}'", user.getUsername(), type, comment.getId());

		
		
		return reaction;
	}
}
