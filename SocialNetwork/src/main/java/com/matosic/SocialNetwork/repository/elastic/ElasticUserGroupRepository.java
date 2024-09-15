package com.matosic.SocialNetwork.repository.elastic;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

import com.matosic.SocialNetwork.model.UserGroupIndex;

@Repository
public interface ElasticUserGroupRepository extends ElasticsearchRepository<UserGroupIndex, Long> {
	List<UserGroupIndex> findAllByName(String name);
}
