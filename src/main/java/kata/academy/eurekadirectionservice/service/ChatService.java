package kata.academy.eurekadirectionservice.service;

import kata.academy.eurekadirectionservice.model.entity.Chat;
import kata.academy.eurekadirectionservice.model.enums.ChatType;

import java.util.List;
import java.util.Optional;

public interface ChatService {

    boolean existsByChatUsers(ChatType type, List<Long> chatUsers);

    Optional<Chat> findChatById(Long chatId);

    Chat addChat(Long userId, String name, List<Long> chatUsers);

    void updateChat(Chat chat);
}
