package com.matosic.SocialNetwork.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matosic.SocialNetwork.model.Post;
import com.matosic.SocialNetwork.model.User;
import com.matosic.SocialNetwork.model.UserGroup;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
    List<Post> findByUserGroup(UserGroup userGroup);
    List<Post> findByUser(User user);
}
