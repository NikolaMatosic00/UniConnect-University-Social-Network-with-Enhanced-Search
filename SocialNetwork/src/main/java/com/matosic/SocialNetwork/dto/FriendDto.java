package com.matosic.SocialNetwork.dto;

import com.matosic.SocialNetwork.model.User;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FriendDto {
    private Long id;
    private String username;
    private String firstName;
    private String lastName;
    private String displayName;
    private String email;

    public FriendDto(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.firstName = user.getFirstName();
        this.lastName = user.getLastName();
        this.displayName = user.getDisplayName();
        this.email = user.getEmail();
    }
}
