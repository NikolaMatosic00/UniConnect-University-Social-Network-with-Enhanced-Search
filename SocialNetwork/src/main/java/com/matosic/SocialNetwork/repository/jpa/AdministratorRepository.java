package com.matosic.SocialNetwork.repository.jpa;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.matosic.SocialNetwork.model.Administrator;

@Repository
public interface AdministratorRepository extends JpaRepository<Administrator, Long>{

}
