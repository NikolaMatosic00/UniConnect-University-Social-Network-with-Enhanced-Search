package com.matosic.SocialNetwork.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matosic.SocialNetwork.model.Banned;

@Repository
public interface BannedRepository extends JpaRepository<Banned, Long>{

}
