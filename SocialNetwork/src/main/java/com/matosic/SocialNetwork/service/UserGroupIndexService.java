package com.matosic.SocialNetwork.service;

import com.matosic.SocialNetwork.model.PostIndex;
import com.matosic.SocialNetwork.model.UserGroupIndex;
import com.matosic.SocialNetwork.repository.elastic.ElasticUserGroupRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserGroupIndexService {

	@Autowired
    private ElasticUserGroupRepository userGroupRepository;


    // Create or Update a User Group
    public UserGroupIndex saveUserGroup(UserGroupIndex userGroup) {
        return userGroupRepository.save(userGroup);
    }

    // Get a User Group by ID
    public Optional<UserGroupIndex> getUserGroupById(Long id) {
        return userGroupRepository.findById(id);
    }

    // Delete a User Group by ID
    public void deleteUserGroup(Long id) {
        userGroupRepository.deleteAll();
    }

    // Get all User Groups
    public Iterable<UserGroupIndex> getAllUserGroups() {
        return userGroupRepository.findAll();
    }
    
    
    // Get all Posts
    public Iterable<UserGroupIndex> getAllUserGroupsByName(String title) {
        return userGroupRepository.findAllByName(title);
    }
}
