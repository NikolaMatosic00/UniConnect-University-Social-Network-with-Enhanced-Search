package com.matosic.SocialNetwork.repository.elastic;

import com.matosic.SocialNetwork.model.PostIndex;

import java.util.List;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ElasticPostRepository extends ElasticsearchRepository<PostIndex, Long> {
	List<PostIndex> findAllByTitle(String title);
}
