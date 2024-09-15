package com.matosic.SocialNetwork.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.matosic.SocialNetwork.dto.FriendDto;
import com.matosic.SocialNetwork.dto.RegistrationRequest;
import com.matosic.SocialNetwork.dto.UserDTO;
import com.matosic.SocialNetwork.model.Post;
import com.matosic.SocialNetwork.model.User;
import com.matosic.SocialNetwork.model.UserGroup;
import com.matosic.SocialNetwork.repository.jpa.UserRepository;

import lombok.extern.slf4j.Slf4j; // Dodajemo import za log objekat

@Service
@Slf4j // Anotacija za Lombok log objekat
public class UserService {

	@Autowired
	private UserRepository userRepository;

	public User findById(Long id) {
		return userRepository.findById(id)
				.orElseThrow(() -> new IllegalArgumentException("User not found with id: " + id));
	}

	public List<User> findAll() {
		return userRepository.findAll();

	}

	public User registerUser(RegistrationRequest registrationRequest) {
		User user = new User();
		user.setUsername(registrationRequest.getUsername());
		user.setPassword(registrationRequest.getPassword()); // U stvarnom scenariju, hashuje se lozinka pre čuvanja
		user.setEmail(registrationRequest.getEmail());
		user.setFirstName(registrationRequest.getFirstName());
		user.setLastName(registrationRequest.getLastName());
		userRepository.save(user);
		log.info("User '{}' registered successfully.", user.getUsername());
		return user;
	}

	public UserDTO loginUser(String username, String password) {
		User user = userRepository.findByUsernameAndPassword(username, password);
		if (user != null) {
			user.setLastLogin(LocalDateTime.now());
			userRepository.save(user);
			log.info("User '{}' logged in.", user.getUsername());
		}

		UserDTO userDTO = new UserDTO();
		userDTO.setId(user.getId());
		userDTO.setUsername(user.getUsername());
		userDTO.setPassword(user.getPassword());
		userDTO.setEmail(user.getEmail());
		userDTO.setLastLogin(user.getLastLogin());
		userDTO.setFirstName(user.getFirstName());
		userDTO.setLastName(user.getLastName());
		userDTO.setDisplayName(user.getDisplayName());
		userDTO.setDescription(user.getDescription());

		return userDTO;
	}

	public boolean changePassword(String username, String oldPassword, String newPassword) {
		User user = userRepository.findByUsername(username);

		if (user != null && user.getPassword().equals(oldPassword)) {
			user.setPassword(newPassword);
			userRepository.save(user);
			log.info("User '{}' changed their password.", username);
			return true;
		} else {
			log.warn("Failed attempt to change password for user '{}'", username);
			return false;
		}
	}
	public List<UserDTO> getFriends(Long userId) {
	    List<UserDTO> dtos = new ArrayList<>();

	    Optional<User> userOpt = userRepository.findById(userId);
	    User user = userOpt.get();
	        
	        if (user.getFriends() != null && !user.getFriends().isEmpty()) {
	            for (User sourceUser : user.getFriends()) {
	                UserDTO dto = new UserDTO();
	                dto.setId(sourceUser.getId());
	                dto.setUsername(sourceUser.getUsername());
	                // Omit the password if not necessary
	                // dto.setPassword(sourceUser.getPassword());
	                dto.setEmail(sourceUser.getEmail());
	                dto.setLastLogin(sourceUser.getLastLogin());
	                dto.setFirstName(sourceUser.getFirstName());
	                dto.setLastName(sourceUser.getLastName());
	                dto.setDisplayName(sourceUser.getDisplayName());
	                dto.setDescription(sourceUser.getDescription());
	                dtos.add(dto);
	            }
	        } 
	        return dtos;
	    }
	
	public List<FriendDto> searchForNewFriends(String keyword) {
        keyword = keyword.toLowerCase();
        List<User> users = userRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(keyword, keyword);
        return users.stream().map(FriendDto::new).collect(Collectors.toList());
    }

	public UserDTO getUser(Long userId) {
		User sourceUser = userRepository.findById(userId).orElse(null);
		log.info("cccccccc" + sourceUser.getUsername());
		UserDTO dto = new UserDTO();
		dto.setId(sourceUser.getId());
		dto.setUsername(sourceUser.getUsername());
		dto.setPassword(sourceUser.getPassword());
		dto.setEmail(sourceUser.getEmail());
		dto.setLastLogin(sourceUser.getLastLogin());
		dto.setFirstName(sourceUser.getFirstName());
		dto.setLastName(sourceUser.getLastName());
		dto.setDisplayName(sourceUser.getDisplayName());
		dto.setDescription(sourceUser.getDescription());
		if (sourceUser.getGroups().size() > 0) {

			for (UserGroup ug : sourceUser.getGroups()) {
				dto.getGroups().add(ug.getName());
			}
		}
		return dto;
	}

	public boolean updateDescription(String username, String description) {
		User user = userRepository.findByUsername(username);
		if (user != null) {
			user.setDescription(description);
			userRepository.save(user);
			return true;
		}
		return false;
	}

	public List<Post> getFriendsAndGroupsPosts(Long userId) {
		User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("Invalid user ID"));

		List<Post> posts = user.getFriends().stream().flatMap(friend -> friend.getPosts().stream())
				.collect(Collectors.toList());

		user.getGroups().forEach(group -> posts.addAll(group.getPosts()));

		posts.sort((post1, post2) -> post2.getCreationDate().compareTo(post1.getCreationDate()));

		return posts;
	}

}
