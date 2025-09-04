package com.maahi.spry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document(collation = "user")
public class User {

    @Id
    @JsonProperty("sUserId")
    String userId;

    @JsonProperty("sName")
    String name;

    @JsonProperty("aWishListBooks")
    List<String> wishListBooks;
}
