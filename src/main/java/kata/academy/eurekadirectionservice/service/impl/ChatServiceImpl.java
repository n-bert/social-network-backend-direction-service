package kata.academy.eurekadirectionservice.service.impl;

import kata.academy.eurekadirectionservice.model.entity.Chat;
import kata.academy.eurekadirectionservice.model.enums.ChatType;
import kata.academy.eurekadirectionservice.repository.ChatRepository;
import kata.academy.eurekadirectionservice.service.ChatService;
import kata.academy.eurekadirectionservice.util.ApiValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Transactional
@Service
public class ChatServiceImpl implements ChatService {
    private final ChatRepository chatRepository;

    @Transactional(readOnly = true)
    @Override
    public boolean existsByChatUsers(ChatType type, List<Long> chatUsers) {
        return chatRepository.existsByTypeAndAllChatUsersIn(type.toString(), chatUsers, (long) chatUsers.size());
    }

    @Override
    public Chat addChat(Long userId, String name, List<Long> chatUsers) {
        chatUsers.add(userId);
        ChatType type = chatUsers.size() > 2 ? ChatType.GROUP : ChatType.DIRECTION;

        if (type == ChatType.DIRECTION) {
            ApiValidationUtil.requireNull(name,
                "В DIRECTION чате параметр name должен быть null");
            ApiValidationUtil.requireFalse(existsByChatUsers(type, chatUsers),
                String.format("Чат между пользователями userId=%d и userId=%d уже существует в базе данных", chatUsers.get(0), chatUsers.get(1)));
        }
        if (type == ChatType.GROUP) {
            ApiValidationUtil.requireTrue(!name.isEmpty(),
                "Для группового чата необходим непустой параметр name");
        }

        Chat chat = Chat
            .builder()
            .type(type)
            .name(name)
            .chatUsers(chatUsers)
            .build();
        return chatRepository.save(chat);
    }

    @Transactional(readOnly = true)
    public Optional<Chat> findChatById(Long chatId) {
        return chatRepository.findChatById(chatId);
    }

    @Override
    public void updateChat(Chat chat) {
        chatRepository.save(chat);
    }
}
