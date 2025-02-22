package pl.woelke.gus_api.rest.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class LoginRequest {
    @JsonProperty("apiKey")
    private String api_key;
}
