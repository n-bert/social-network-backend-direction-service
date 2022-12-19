package kata.academy.eurekadirectionservice.model.dto;

import java.time.LocalDateTime;

public record ChatMessageResponseDto(
    Long id,
    String text,
    ChatUserResponseDto chatUser,
    LocalDateTime createdDate,
    Long chatId) {
}
