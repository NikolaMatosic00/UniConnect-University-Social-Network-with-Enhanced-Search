package com.matosic.SocialNetwork.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class UpdateDescriptionRequest {
    private String username;
    private String description;
}
