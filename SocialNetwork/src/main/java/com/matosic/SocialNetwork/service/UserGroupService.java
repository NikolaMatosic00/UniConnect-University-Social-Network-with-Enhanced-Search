package com.matosic.SocialNetwork.service;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.matosic.SocialNetwork.dto.CreateUserGroupDto;
import com.matosic.SocialNetwork.dto.GroupRequestDTO;
import com.matosic.SocialNetwork.dto.GroupsOfUserDto;
import com.matosic.SocialNetwork.dto.UserGroupDTO;
import com.matosic.SocialNetwork.model.GroupRequest;
import com.matosic.SocialNetwork.model.Post;
import com.matosic.SocialNetwork.model.User;
import com.matosic.SocialNetwork.model.UserGroup;
import com.matosic.SocialNetwork.model.UserGroupIndex;
import com.matosic.SocialNetwork.repository.elastic.ElasticUserGroupRepository;
import com.matosic.SocialNetwork.repository.jpa.GroupRequestRepository;
import com.matosic.SocialNetwork.repository.jpa.UserGroupRepository;
import com.matosic.SocialNetwork.repository.jpa.UserRepository;

import co.elastic.clients.elasticsearch.ElasticsearchClient;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.RangeQuery;
import co.elastic.clients.elasticsearch.core.SearchRequest;
import co.elastic.clients.elasticsearch.core.SearchResponse;
import co.elastic.clients.json.JsonData;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserGroupService {

	@Autowired
	private UserGroupRepository jpaUserGroupRepository;

	@Autowired
	private ElasticUserGroupRepository elasticUserGroupRepository;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private GroupRequestRepository groupRequestRepository;

	@Autowired
	private MinioService minioService;

	@Autowired
	private ElasticsearchClient elasticsearchClient;

	public List<UserGroup> getAllGroups() {
		return (List<UserGroup>) jpaUserGroupRepository.findAll();
	}

	public UserGroupDTO entityToDtoById(Long id) {
		Optional<UserGroup> optionalGroup = jpaUserGroupRepository.findById(id);
		if (optionalGroup.isPresent()) {
			UserGroup group = optionalGroup.get();
			UserGroupDTO uDTO = new UserGroupDTO();
			uDTO.setId(group.getId());
			uDTO.setName(group.getName());
			uDTO.setDescription(group.getDescription());
			uDTO.setPostsCount(group.getPosts().size());
			int averageLikes = 0;
			for (Post p : group.getPosts()) {
				averageLikes += p.getReactions().size();
			}
			if (group.getPosts().size() > 0) {

				uDTO.setAverageLikes(averageLikes / group.getPosts().size());
			}
			return uDTO;
		}
		return null;
	}

	public List<GroupsOfUserDto> getAllUsersGroups(Long userId) {
		Optional<User> userOpt = userRepository.findById(userId);
		User user = userOpt.get();
		List<GroupsOfUserDto> list = new ArrayList<>();
		for (UserGroup userGroup : user.getGroups()) {
			GroupsOfUserDto goud = new GroupsOfUserDto();
			goud.setGroupId(userGroup.getId());
			goud.setGroupName(userGroup.getName());
			if (userGroup.getAdmin() != null && userGroup.getAdmin().getId() == user.getId()) {
				goud.setAdmin(true);
			} else {
				goud.setAdmin(false);
			}
			if (userGroup.isSuspended() != true) {
				list.add(goud);
			}
		}
		return list;
	}

	@Transactional
	public String createUserGroup(CreateUserGroupDto createUserGroupDTO) throws IOException {
		User admin = userRepository.findById(createUserGroupDTO.getUserId())
				.orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

		String fileName = "group-" + System.currentTimeMillis() + ".pdf";
		String descriptionUrl = minioService.uploadFile(createUserGroupDTO.getPdfFile(), fileName);

		String pdfContent = "";
		try (InputStream inputStream = createUserGroupDTO.getPdfFile().getInputStream();
				PDDocument document = PDDocument.load(inputStream)) {
			PDFTextStripper pdfStripper = new PDFTextStripper();
			pdfContent = pdfStripper.getText(document);
		} catch (IOException e) {
			log.error("Failed to extract content from PDF file for group", e);
			throw new RuntimeException("Failed to extract content from PDF file", e);
		}

		String nameOfGroup = createUserGroupDTO.getName();

		UserGroup group = new UserGroup();
		group.setName(nameOfGroup);
		group.setDescription(pdfContent);
		group.setPdfUrl(descriptionUrl);
		group.setCreationDate(LocalDateTime.now());
		group.setAdmin(admin);
		group.setSuspended(false); // Nova grupa nije suspendovana po defaultu
		group.setUsers(new HashSet<>()); // Inicijalizujemo prazan skup korisnika

		// Dodajemo kreatora grupe kao člana
		group.getUsers().add(admin);

		// Save to MySQL
		UserGroup savedUserGroup = jpaUserGroupRepository.save(group);

		admin.getGroups().add(group);
		userRepository.save(admin);

		log.info("Created group: {}", group.getName());

		UserGroupIndex usergroup_index = new UserGroupIndex();

		usergroup_index.setId(savedUserGroup.getId());
		usergroup_index.setName(nameOfGroup);
		usergroup_index.setDescription(pdfContent);
		usergroup_index.setIdOfRealUserGroup(savedUserGroup.getId());
		usergroup_index.setPostCount(0);

		// Save to Elasticsearch
		elasticUserGroupRepository.save(usergroup_index);
		log.info("Created group at elastic: {}", usergroup_index.getName());

		return "created";
	}

	@Transactional
	public boolean suspendGroup(Long groupId, String reason) {
		Optional<UserGroup> optionalGroup = jpaUserGroupRepository.findById(groupId);
		if (optionalGroup.isPresent()) {
			UserGroup group = optionalGroup.get();

			// Suspendovanje grupe
			group.setSuspended(true);
			group.setSuspendedReason(reason);

			// Uklanjanje administratora
			group.setAdmin(null);

			// Uklanjanje svih članova grupe
			group.getUsers().clear();

			// Snimanje izmena
			jpaUserGroupRepository.save(group);

			// Logovanje
			log.info("Suspended group: {} - Reason: {}", group.getName(), reason);

			return true;
		}

		log.warn("Group with ID {} not found", groupId);
		return false;
	}

	@Transactional
	public boolean sendGroupJoinRequest(Long groupId, Long userId) {
		Optional<UserGroup> optionalGroup = jpaUserGroupRepository.findById(groupId);
		if (optionalGroup.isPresent()) {
			UserGroup group = optionalGroup.get();
			User user = userRepository.findById(userId)
					.orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

			// Provera da li korisnik već ima zahtev za pridruživanje ovoj grupi
			List<GroupRequest> existingRequests = groupRequestRepository.findByGroupAndUser(group, user);
			if (!existingRequests.isEmpty()) {
				log.warn("User {} already sent a request to join group {}", user.getUsername(), group.getName());
				return false;
			}

			// Kreiranje i čuvanje zahteva za pridruživanje
			GroupRequest request = new GroupRequest(false, LocalDateTime.now(), group, user);
			groupRequestRepository.save(request);

			log.info("User {} sent a request to join group {}", user.getUsername(), group.getName());
			return true;
		}
		log.warn("Group with ID {} not found", groupId);
		return false;
	}

	public List<GroupRequestDTO> getGroupRequests(String groupName) {
		List<GroupRequestDTO> dtos = new ArrayList<>();
		for (GroupRequest groupReq : groupRequestRepository.findAll()) {
			if (groupReq.getGroup().getName().equals(groupName) && groupReq.isApproved() == false) {
				log.info("zzzzzzzzzzz" + groupName);
				dtos.add(
						new GroupRequestDTO(groupReq.getId(), groupReq.isApproved(), groupReq.getUser().getUsername()));
			}
		}
		return dtos;
	}

	@Transactional
	public boolean approveJoinRequestAndAddUserToGroup(Long requestId) {
		Optional<GroupRequest> optionalRequest = groupRequestRepository.findById(requestId);
		if (optionalRequest.isPresent()) {
			GroupRequest request = optionalRequest.get();
			request.setApproved(true);
			groupRequestRepository.save(request);

			UserGroup group = request.getGroup();
			User user = request.getUser();

			// Add user to the group
			group.getUsers().add(user);
			user.getGroups().add(group);
			userRepository.save(user);
			jpaUserGroupRepository.save(group);
//            elasticUserGroupRepository.save(group); // Ovde se dodaje u elasticsearch

			log.info("Join request with ID {} approved and user {} added to group {}", requestId, user.getUsername(),
					group.getName());
			return true;
		}
		log.warn("Join request with ID {} not found", requestId);
		return false;
	}

	@Transactional
	public boolean rejectJoinRequest(Long requestId) {
		Optional<GroupRequest> optionalRequest = groupRequestRepository.findById(requestId);
		if (optionalRequest.isPresent()) {
			groupRequestRepository.delete(optionalRequest.get());

			log.info("Join request with ID {} rejected", requestId);
			return true;
		}
		log.warn("Join request with ID {} not found", requestId);
		return false;
	}

	// ELASTIC
	public List<UserGroupIndex> combinedUserGroupSearch(String name, String description, Integer minPostCount,
			Integer maxPostCount) {
		List<Query> mustQueries = new ArrayList<>();

		// Pretraga po nazivu grupe
		if (name != null) {
			mustQueries.add(Query.of(q -> q.match(mq -> mq.field("name").query(name))));
		}

		// Pretraga po opisu grupe
		if (description != null) {
			mustQueries.add(Query.of(q -> q.match(mq -> mq.field("description").query(description))));
		}

		// Pretraga po broju objava (postCount)
		if (minPostCount != null || maxPostCount != null) {
			// Kreiramo range upit koji će sadržati minimum i/ili maksimum
			RangeQuery.Builder rangeQueryBuilder = new RangeQuery.Builder().field("postCount");

			if (minPostCount != null) {
				rangeQueryBuilder.gte(JsonData.of(minPostCount)); // Greater Than or Equal
			}

			if (maxPostCount != null) {
				rangeQueryBuilder.lte(JsonData.of(maxPostCount)); // Less Than or Equal
			}

			// Dodajemo u mustQueries da bi oba uslova morala biti zadovoljena
			mustQueries.add(Query.of(q -> q.range(rangeQueryBuilder.build())));
		}

		// Kreiranje bool query-a koji koristi sve must upite
		Query query = Query.of(q -> q.bool(b -> b.must(mustQueries)));

		return executeUserGroupSearch(query);
	}

	// Metoda za izvršenje pretrage na Elasticsearchu
	private List<UserGroupIndex> executeUserGroupSearch(Query query) {
		try {
			SearchRequest request = new SearchRequest.Builder().index("usergroup") // Pretpostavljamo da je naziv
																					// indeksa "usergroup"
					.query(query) // Dodavanje upita
					.build();

			// Izvršenje pretrage
			SearchResponse<UserGroupIndex> response = elasticsearchClient.search(request, UserGroupIndex.class);

			return response.hits().hits().stream().map(hit -> hit.source()).collect(Collectors.toList());

		} catch (IOException e) {
			throw new RuntimeException("Failed to execute search query", e);
		}
	}

}
