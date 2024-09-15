package com.matosic.SocialNetwork.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.matosic.SocialNetwork.model.Reaction;
import com.matosic.SocialNetwork.model.ReactionType;
import com.matosic.SocialNetwork.service.ReactionService;

@RestController
@RequestMapping("/api/reactions")
@CrossOrigin("http://localhost:3000/")
public class ReactionController {

	@Autowired
	private ReactionService reactionService;

	@PostMapping("/like")
	public ResponseEntity<Reaction> likePost(@RequestParam Long postId, @RequestParam Long userId) {
		Reaction reaction = reactionService.addReaction(postId, userId, ReactionType.LIKE);
		return ResponseEntity.ok(reaction);
	}

	@PostMapping("/dislike")
	public ResponseEntity<Reaction> dislikePost(@RequestParam Long postId, @RequestParam Long userId) {
		Reaction reaction = reactionService.addReaction(postId, userId, ReactionType.DISLIKE);
		return ResponseEntity.ok(reaction);
	}

	@PostMapping("/heart")
	public ResponseEntity<Reaction> heartPost(@RequestParam Long postId, @RequestParam Long userId) {
		Reaction reaction = reactionService.addReaction(postId, userId, ReactionType.HEART);
		return ResponseEntity.ok(reaction);
	}

	@PostMapping("/like/comment")
	public ResponseEntity<Reaction> likeComment(@RequestParam Long commentId, @RequestParam Long userId) {
		Reaction reaction = reactionService.addReactionToComment(commentId, userId, ReactionType.LIKE);
		return ResponseEntity.ok(reaction);
	}

	@PostMapping("/dislike/comment")
	public ResponseEntity<Reaction> dislikeComment(@RequestParam Long commentId, @RequestParam Long userId) {
		Reaction reaction = reactionService.addReactionToComment(commentId, userId, ReactionType.DISLIKE);
		return ResponseEntity.ok(reaction);
	}

	@PostMapping("/heart/comment")
	public ResponseEntity<Reaction> heartComment(@RequestParam Long commentId, @RequestParam Long userId) {
		Reaction reaction = reactionService.addReactionToComment(commentId, userId, ReactionType.HEART);
		return ResponseEntity.ok(reaction);
	}
}
