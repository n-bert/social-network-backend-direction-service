package kata.academy.eurekadirectionservice.model.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ChatRequestDto(
    @JsonProperty
    String name,
    @JsonProperty
    String description) {
}
