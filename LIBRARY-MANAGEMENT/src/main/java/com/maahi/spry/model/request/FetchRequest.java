package com.maahi.spry.model.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.Map;
@Data
public class FetchRequest {
    @JsonProperty("sIsbn")
    String isbn;

    @JsonProperty("sAuther")
    String auther;

    @JsonProperty("sTitle")
    String title;

    @JsonProperty("aFilterParams")
    public Map<String, String> filterParams;

}
