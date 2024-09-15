package com.matosic.SocialNetwork.model;

import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(indexName = "usergroup")
//@Setting(settingPath = "/analyzers/serbianAnalyzer.json")
@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserGroupIndex {

	@Id
	private Long id;

	private Long idOfRealUserGroup;

	@Field(type = FieldType.Text, analyzer = "serbian_analyzer")
	private String name;

	@Field(type = FieldType.Text, analyzer = "serbian_analyzer")
	private String description;

	@Field(type = FieldType.Integer)
	private Integer postCount;

}
