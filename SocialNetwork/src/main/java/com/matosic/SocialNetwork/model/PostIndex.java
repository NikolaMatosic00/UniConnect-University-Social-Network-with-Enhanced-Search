package com.matosic.SocialNetwork.model;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(indexName = "post")
//@Setting(settingPath = "/analyzers/serbianAnalyzer.json")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class PostIndex {
	
	@Id
	private Long id;
	
	private Long idOfRealPost;

    @Field(type = FieldType.Text, analyzer = "serbian_analyzer")
    private String title;
    
    @Field(type = FieldType.Text, analyzer = "serbian_analyzer")
    private String content;
    
    @Field(type = FieldType.Integer)
    private int likesCount;
}
