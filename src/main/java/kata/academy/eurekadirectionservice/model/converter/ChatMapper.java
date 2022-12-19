package kata.academy.eurekadirectionservice.model.converter;

import kata.academy.eurekadirectionservice.model.dto.ChatRequestDto;
import kata.academy.eurekadirectionservice.model.entity.Chat;

public final class ChatMapper {

    public static Chat toEntity(ChatRequestDto dto, Chat chat) {
        chat.setName(dto.name());
        chat.setDescription(dto.description());
        return chat;
    }
}
