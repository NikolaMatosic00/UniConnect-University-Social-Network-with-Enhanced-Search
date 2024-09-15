package com.matosic.SocialNetwork.repository.jpa;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matosic.SocialNetwork.model.GroupRequest;
import com.matosic.SocialNetwork.model.User;
import com.matosic.SocialNetwork.model.UserGroup;

@Repository
public interface GroupRequestRepository extends JpaRepository<GroupRequest, Long>{

	List<GroupRequest> findByGroupAndUser(UserGroup group, User user);

}
