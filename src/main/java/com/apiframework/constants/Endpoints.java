package com.apiframework.constants;

public class Endpoints {
	//jsonplaceholder.typicode.in endpoints
	public static final String GET_ALL_POSTS   = "/posts";
    public static final String GET_POST_BY_ID  = "/posts/{id}";
    public static final String CREATE_POST     = "/posts";
    public static final String UPDATE_POST     = "/posts/{id}";
    public static final String DELETE_POST     = "/posts/{id}";
    public static final String GET_USER_POSTS  = "/users/{userId}/posts";
    
    // DummyJSON auth endpoints
    public static final String AUTH_LOGIN    = "/auth/login";
    public static final String AUTH_ME       = "/auth/me";
    public static final String GET_PRODUCTS  = "/products";
    public static final String GET_PRODUCT   = "/products/{id}";
    
    // Prevent instantiation — this is a constants class
    private Endpoints() {}
   
}
