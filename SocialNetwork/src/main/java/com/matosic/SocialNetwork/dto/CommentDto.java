package com.matosic.SocialNetwork.dto;

import java.time.LocalDate;
import java.time.LocalDateTime;

import com.matosic.SocialNetwork.model.Comment;
import com.matosic.SocialNetwork.model.Post;
import com.matosic.SocialNetwork.model.ReactionType;

import lombok.Data;

@Data
public class CommentDto {
    private Long id;
    private String text;
    private LocalDateTime timestamp;
    private boolean isDeleted;
    private String username; // ID korisnika koji je napisao komentar
    private Long parentId; // ID roditeljskog komentara (ako postoji)
    private long likeCount;
    private long dislikeCount;
    private long heartCount;

    public CommentDto(Comment comment) {
        this.id = comment.getId();
        this.text = comment.getText();
        this.timestamp = comment.getTimestamp();
        this.isDeleted = comment.isDeleted();
        this.username = comment.getUser().getUsername();
        this.likeCount = countReactions(comment, ReactionType.LIKE);
        this.dislikeCount = countReactions(comment, ReactionType.DISLIKE);
        this.heartCount = countReactions(comment, ReactionType.HEART);
        
        // Postavljanje roditeljskog komentara ako postoji
        if (comment.getParentComment() != null) {
            this.parentId = comment.getParentComment().getId();
        }
    }
    private long countReactions(Comment comment, ReactionType type) {
        return comment.getReactions().stream()
            .filter(r -> r.getType() == type)
            .count();
    }
}
