package com.matosic.SocialNetwork.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matosic.SocialNetwork.model.FriendRequest;

@Repository
public interface FriendRequestRepository extends JpaRepository<FriendRequest, Long> {

	List<FriendRequest> findByFromUserId(Long userId);

	List<FriendRequest> findByToUserId(Long userId);

}
