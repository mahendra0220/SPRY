package com.maahi.spry.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "book")
public class Book {

    @JsonProperty("sBookName")
    String  bookName;

    @JsonProperty("sAuthor")
    String author;

    @Id
    @JsonProperty("sIsbn")
    String isbn;

    @JsonProperty("sPublishedYear")
    String publishedYear;

    @JsonProperty("sAvailabilityStatus")
    AvailabiltyStatus availabilityStatus;

    @JsonProperty("aWishListUsers")
    List<String> wishListUsers;
}
