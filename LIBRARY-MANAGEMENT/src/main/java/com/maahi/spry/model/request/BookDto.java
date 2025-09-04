package com.maahi.spry.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class BookDto {

    @JsonProperty("sTitle")
    String  title;

    @JsonProperty("sWriter")
    String writer;

    @JsonProperty("sIsbn")
    String isbn;

    @JsonProperty("sPublishedYear")
    String publishedYear;

    @JsonProperty("sAvailabilityStatus")
    String availabilityStatus;

}
