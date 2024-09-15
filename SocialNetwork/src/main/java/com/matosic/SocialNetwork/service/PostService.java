package com.matosic.SocialNetwork.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.matosic.SocialNetwork.dto.CreatePostRequest;
import com.matosic.SocialNetwork.dto.PostDto;
import com.matosic.SocialNetwork.model.Post;
import com.matosic.SocialNetwork.model.PostIndex;
import com.matosic.SocialNetwork.model.User;
import com.matosic.SocialNetwork.model.UserGroup;
import com.matosic.SocialNetwork.model.UserGroupIndex;
import com.matosic.SocialNetwork.repository.elastic.ElasticPostRepository;
import com.matosic.SocialNetwork.repository.elastic.ElasticUserGroupRepository;
import com.matosic.SocialNetwork.repository.jpa.PostRepository;
import com.matosic.SocialNetwork.repository.jpa.UserGroupRepository;
import com.matosic.SocialNetwork.repository.jpa.UserRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.BoolQuery;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class PostService {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserGroupRepository userGroupRepository;

	@Autowired
	private PostRepository postRepository;

	@Autowired
	private ElasticPostRepository elasticPostRepository;

	@Autowired
	private ElasticUserGroupRepository elasticUserGroupRepository;

	@Autowired
	private MinioService minioService;

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	@Transactional
	public void createPost(CreatePostRequest createPostRequest) {
		User user = userRepository.findById(createPostRequest.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

		UserGroup userGroup = null;
		if (createPostRequest.getUserGroupId() != null) {
			userGroup = userGroupRepository.findById(createPostRequest.getUserGroupId()).orElse(null);
		}

		MultipartFile pdfFile = createPostRequest.getPdfFile();
		String fileName = "post-" + System.currentTimeMillis() + ".pdf";
		String pdfUrl;
		String pdfContent = "";
		String title = createPostRequest.getTitle();
		try {
			pdfUrl = minioService.uploadFile(pdfFile, fileName);

			// Extract text content from PDF
			try (InputStream inputStream = pdfFile.getInputStream();
					PDDocument document = PDDocument.load(inputStream)) {
				PDFTextStripper pdfStripper = new PDFTextStripper();
				pdfContent = pdfStripper.getText(document);
			}
		} catch (IOException e) {
			log.error("Failed to upload PDF file or extract content for post", e);
			throw new RuntimeException("Failed to upload PDF file or extract content", e);
		}

		Post post = new Post();
		post.setTitle(title);
		post.setPdfUrl(pdfUrl);
		post.setContent(pdfContent);
		post.setCreationDate(LocalDateTime.now());
		post.setUser(user);
		post.setUserGroup(userGroup);

		// Save to MySQL
		Post savedPost = postRepository.save(post);
		log.info("New post created in MySQL with PDF content URL: {} , user: {}", pdfUrl, user.getUsername());

		PostIndex post_index = new PostIndex();
		post_index.setId(savedPost.getId());
		post_index.setIdOfRealPost(savedPost.getId());
		post_index.setTitle(title);
		post_index.setContent(pdfContent);
		post_index.setLikesCount(0);

		// Save to Elasticsearch
		elasticPostRepository.save(post_index);
		log.info("New post indexed in Elasticsearch with ID: {}", post_index.getId());

		// Increment numberOfPosts in UserGroupIndex
		if (userGroup != null) {
			Optional<UserGroupIndex> userGroupIndexOptional = elasticUserGroupRepository.findById(userGroup.getId());
			if (userGroupIndexOptional.isPresent()) {
				UserGroupIndex userGroupIndex = userGroupIndexOptional.get();
				userGroupIndex.setPostCount(userGroupIndex.getPostCount() + 1);
				elasticUserGroupRepository.save(userGroupIndex);
				log.info("UserGroupIndex updated with new post count: {}", userGroupIndex.getPostCount());
			} else {
				log.warn("UserGroupIndex not found for group ID: {}", userGroup.getId());
			}
		}

		return;
	}

	public List<PostDto> getAllPosts() {
		List<PostDto> allPostsDtos = new ArrayList<>();
		for (Post post : postRepository.findAll()) {
			allPostsDtos.add(new PostDto(post));
		}
		return allPostsDtos;
	}
	
	public List<PostDto> getAllPostsFromGroup(String groupName) {
		List<PostDto> allPostsDtos = new ArrayList<>();
		for (Post post : postRepository.findAll()) {
			if(post.getUserGroup() != null && post.getUserGroup().getName().equals(groupName)) {
				allPostsDtos.add(new PostDto(post));				
			}
		}
		return allPostsDtos;
	}

	public List<PostDto> getFeedForUser(Long userId) {
		// Fetch the user
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

		// Fetch posts from groups the user is a member of
		Set<UserGroup> userGroups = user.getGroups();
		List<Post> groupPosts = new ArrayList<>();
		for (UserGroup group : userGroups) {
			groupPosts.addAll(postRepository.findByUserGroup(group));
		}

		// Fetch posts from the user's friends
		Set<User> friends = user.getFriends();
		List<Post> friendPosts = new ArrayList<>();
		for (User friend : friends) {
			friendPosts.addAll(postRepository.findByUser(friend));
		}

		// Merge the two lists and remove duplicates
		Set<Post> allPostsSet = new HashSet<>();
		allPostsSet.addAll(groupPosts);
		allPostsSet.addAll(friendPosts);

		// Convert Set back to List for sorting
		List<Post> allPosts = new ArrayList<>(allPostsSet);

		// Sort the posts by creation date (most recent first)
		allPosts.sort((post1, post2) -> post2.getCreationDate().compareTo(post1.getCreationDate()));

		List<PostDto> allPostsDtos = new ArrayList<>();
		for (Post post : allPosts) {
			allPostsDtos.add(new PostDto(post));
		}

		return allPostsDtos;
	}

	// ELASTIC PRETRAGA
	public List<PostIndex> combinedSearch(String title, String content, Integer minLikes, Integer maxLikes) {
		List<Query> mustQueries = new ArrayList<>();

		if (title != null) {
			mustQueries.add(Query.of(q -> q.match(mq -> mq.field("title").query(title))));
		}
		if (content != null) {
			mustQueries.add(Query.of(q -> q.match(mq -> mq.field("content").query(content))));
		}
		if (minLikes != null || maxLikes != null) {
		    // Kreiramo range upit koji će sadržati minimum i/ili maksimum
		    RangeQuery.Builder rangeQueryBuilder = new RangeQuery.Builder()
		        .field("likesCount");

		    if (minLikes != null) {
		        rangeQueryBuilder.gte(JsonData.of(minLikes)); // Greater Than or Equal za minLikes
		    }

		    if (maxLikes != null) {
		        rangeQueryBuilder.lte(JsonData.of(maxLikes)); // Less Than or Equal za maxLikes
		    }

		    // Dodajemo u mustQueries da bi oba uslova morala biti zadovoljena
		    mustQueries.add(Query.of(q -> q.range(rangeQueryBuilder.build())));
		}
		Query query = Query.of(q -> q.bool(b -> b.must(mustQueries)));

		return executeSearch(query);
	}

	private List<PostIndex> executeSearch(Query query) {
		try {

			SearchRequest request = new SearchRequest.Builder().index("post") // Pretpostavljamo da je naziv indeksa
																				// "post"
					.query(query) // Dodavanje upita
					.build();

			// Izvršenje pretrage
			SearchResponse<PostIndex> response = elasticsearchClient.search(request, PostIndex.class);

			return response.hits().hits().stream().map(hit -> hit.source()).collect(Collectors.toList());

		} catch (IOException e) {
			throw new RuntimeException("Failed to execute search query", e);
		}
	}

//    public List<PostIndex> searchByTitle(String title) {
//        Query query = Query.of(q -> q
//            .match(m -> m
//                .field("title")
//                .query(title)
//            )
//        );
//
//        return executeSearch(query);
//    }
//    
//    public List<PostIndex> searchByContent(String content) {
//        Query query = Query.of(q -> q
//            .match(m -> m
//                .field("content")
//                .query(content)
//            )
//        );
//
//        return executeSearch(query);
//    }
//
//    
//    public List<PostIndex> searchByLikesRange(Integer minLikes, Integer maxLikes) {
//        BoolQuery.Builder boolQuery = new BoolQuery.Builder();
//
//        if (minLikes != null) {
//            boolQuery.must(m -> m
//                .range(r -> r
//                    .field("likesCount")
//                    .gte(JsonData.of(minLikes))
//                )
//            );
//        }
//        if (maxLikes != null) {
//            boolQuery.must(m -> m
//                .range(r -> r
//                    .field("likesCount")
//                    .lte(JsonData.of(maxLikes))
//                )
//            );
//        }
//
//        Query query = Query.of(q -> q
//            .bool(boolQuery.build())
//        );
//
//        return executeSearch(query);
//    }

}
