package com.apiframework.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

//Ignore any extra JSON fields not mapped here --prevents errors
@JsonIgnoreProperties(ignoreUnknown = true)

public class Post {
	
	@JsonProperty("id")
	private int id;
	
	@JsonProperty("userId")
	private  int userId;
	
	@JsonProperty("title")
	private String title;
	
	@JsonProperty("body")
	private String body;
	
	//Default Constructor - required by Jackson
	public Post() {}
	
	//Constructor for building request body
	public Post(int userId, String title, String body) {
		this.userId = userId;
		this.title = title;
		this.body = body;
	}
	
	//Getters and Setters
	public int getId() { return id; }
	public void setId(int id) { this.id=id; }
	
	public int getUserId() { return userId; }
	public void setUserId(int userId) { this.userId = userId; }
	
	public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getBody() { return body; }
    public void setBody(String body) { this.body = body; }

    
    @Override
    public String toString() {
    	return "Post{id=" + id + ", userId='" + userId + "', title='" + title + "'}";
    }

}
