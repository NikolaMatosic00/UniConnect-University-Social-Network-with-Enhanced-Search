package com.matosic.SocialNetwork.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@Configuration
@EnableJpaRepositories(basePackages = "com.matosic.SocialNetwork.repository.jpa")
public class JpaConfig {
}
