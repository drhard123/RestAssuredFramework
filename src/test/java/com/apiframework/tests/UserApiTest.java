package com.apiframework.tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.Test;

import com.apiframework.base.BaseTest;
import com.apiframework.constants.Endpoints;
import com.apiframework.models.Post;

import io.qameta.allure.Description;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;

import static io.restassured.RestAssured.*; //Used to import static methods or variables inside a class
import static org.hamcrest.Matchers.*;

public class UserApiTest extends BaseTest {
	
	private static final Logger log = LogManager.getLogger(UserApiTest.class);
	
	//Shared across tests — stores the ID created in POST, used in PUT/PATCH/DELETE
	private static int createdPostId;
	
	//------------------------------
	//TEST 1 --GET all posts
	//------------------------------
	@Test(priority = 1)
	@Story("Post Management")
	@Severity(SeverityLevel.NORMAL)
	@Description("GET all posts and verify list is not empty")
	public void testGetAllPosts() {
		log.info(">>> TEST: GET all posts");
		
		log.info(">>> TEST: GET all posts");

        given()
        .when()
            .get(Endpoints.GET_ALL_POSTS)
        .then()
        	.statusCode(200)
        	.body("$", not(empty()))
            .body("[0].id", notNullValue())
            .body("[0].title", notNullValue());
        
        log.info("<<< PASSED: GET all posts");
	}
	
	//------------------------------
	//TEST 2 --GET single post
	//------------------------------
	@Test(priority = 2)
    @Story("Post Management")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET a single post by ID and verify all fields")
	public void testGetSingePost() {
		log.info(">>> TEST: GET Single post with id=1");
		
		Response response = given()
			.pathParam("id",1)
		.when()
			.get(Endpoints.GET_POST_BY_ID)
		.then()
			.statusCode(200)
			.body("id", equalTo(1))
			.body("userId", notNullValue())
            .body("title", notNullValue())
            .body("body", notNullValue())
            .extract().response();
		
		// Deserialise response into POJO and verify via assert
		Post post = response.as(Post.class);
		log.info("Retreived post: " + post.toString());
		Assert.assertEquals(post.getId(), 1, "Post ID should be 1");
		Assert.assertNotNull(post.getTitle(), "Title should not be null");
		
        log.info("<<< PASSED: GET single post");	
	}
	
	// ─────────────────────────────────────────────
    // TEST 3 — POST create post
    // ─────────────────────────────────────────────
    @Test(priority = 3)
    @Story("{POST Management")
    @Severity(SeverityLevel.CRITICAL)
    @Description("POST to create a new post and verify response fields")
    public void testCreatePost() {
        log.info(">>> TEST: POST create post");
        
        // Build request body using POJO — Jackson converts this to JSON
        Post newPost = new Post(1, "API Automation with RestAssured", "This post was created by our framework.");
        
        Response response = given()
                .body(newPost)
            .when()
                .post(Endpoints.CREATE_POST)
            .then()
                .statusCode(201)
                .body("title", equalTo("API Automation with RestAssured"))
                .body("body", notNullValue())
                .body("id", notNullValue())
                .extract().response();
        
        // Store the created ID — used in PUT, PATCH, DELETE tests
        createdPostId = response.jsonPath().getInt("id");
        log.info("Created post ID: " + createdPostId);

        log.info("<<< PASSED: POST create post");
    }
    
    // ─────────────────────────────────────────────
    // TEST 4 — PUT full update
    // ─────────────────────────────────────────────
    @Test(priority = 4, dependsOnMethods = "testCreatePost")
    @Story("Post Management")
    @Severity(SeverityLevel.CRITICAL)
    @Description("PUT to fully replace an existing post's data")
    public void testUpdatePostPut() {
        log.info(">>> TEST: PUT update post id= 1 (Using known existing ID");
        
        // PUT sends full object — replaces everything
        Post updatedPost = new Post(1, "Updated Title via PUT", "Fully replaced body content.");
        
        given()
        	.pathParam("id", 1)
        	.body(updatedPost)
        .when()
        	.put(Endpoints.UPDATE_POST)
        .then()
        	.statusCode(200)
        	.body("title", equalTo("Updated Title via PUT"))
        	.body("body", equalTo("Fully replaced body content."));
        
        log.info("<<< PASSED: PUT update post");
    }
    
    // ─────────────────────────────────────────────
    // TEST 5 — PATCH partial update
    // ─────────────────────────────────────────────
    @Test(priority = 5, dependsOnMethods = "testCreatePost")
    @Story("Post Management")
    @Severity(SeverityLevel.NORMAL)
    @Description("PATCH to update only the title field")
    public void testUpdatePostPatch() {
        log.info(">>> TEST: PATCH update post id=" + createdPostId);
        
        // PATCH sends only changed fields — name stays untouched
        String partialBody = "{ \"title\": \"Patched Title Only\" }";
        
        given()
        .pathParam("id", createdPostId)
        .body(partialBody)
    .when()
        .patch(Endpoints.UPDATE_POST)
    .then()
        .statusCode(200)
        .body("title", equalTo("Patched Title Only"));
       log.info("<<< PASSED: PATCH update post");
    }
    
    // ─────────────────────────────────────────────
    // TEST 6 — DELETE post
    // ─────────────────────────────────────────────
    @Test(priority = 6, dependsOnMethods = "testCreatePost")
    @Story("Post Management")
    @Severity(SeverityLevel.CRITICAL)
    @Description("DELETE post and verify 200 response")
    public void testDeletePost() {
        log.info(">>> TEST: DELETE post id=" + createdPostId);
        
        given()
        .pathParam("id", createdPostId)
    .when()
        .delete(Endpoints.DELETE_POST)
    .then()
        .statusCode(200);

    log.info("<<< PASSED: DELETE post — 200 received");
    }
    
    // ─────────────────────────────────────────────
    // TEST 7 — Negative test: post not found
    // ─────────────────────────────────────────────
    @Test(priority = 7)
    @Story("Negative Scenarios")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET a non-existent post and verify 404 response")
    public void testGetExistentPost() {
        log.info(">>> TEST: GET non-existent post id=99999");

        given()
        .pathParam("id", 99999)
    .when()
        .get(Endpoints.GET_POST_BY_ID)
    .then()
        .statusCode(404)
        .body(equalTo("{}"));

    log.info("<<< PASSED: 404 received for non-existent post");
    }
    
    // ─────────────────────────────────────────────
    // TEST 8 — GET posts by userId (nested resource)
    // ─────────────────────────────────────────────
    @Test(priority = 8)
    @Story("POST Management")
    @Severity(SeverityLevel.NORMAL)
    @Description("GET all post for a specific user using nested endpoint")
    public void testGetPostByUser() {
    	log.info(">>>TEST: GET Posts for userId = 1");
    	
    	given()
    		.pathParam("userId",1)
    	.when()
    		.get(Endpoints.GET_USER_POSTS)
    	.then()
    		.statusCode(200)
    		.body("$", not(empty()))
    		.body("[0].userId", equalTo(1));
    	
    	log.info("<<< PASSED: GET Post by UserId");		
    }
}
