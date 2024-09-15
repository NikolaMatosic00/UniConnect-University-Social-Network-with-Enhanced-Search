package com.matosic.SocialNetwork.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matosic.SocialNetwork.model.GroupAdmin;

@Repository
public interface GroupAdminRepository extends JpaRepository<GroupAdmin, Long> {

}
