package kata.academy.eurekadirectionservice.model.dto;

import kata.academy.eurekadirectionservice.model.enums.ChatType;

import java.util.List;

public record ChatResponseDto(
    Long id,
    List<ChatUserResponseDto> chatUsers,
    ChatType type,
    String name,
    String description) {
}
