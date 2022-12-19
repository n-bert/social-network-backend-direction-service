package kata.academy.eurekadirectionservice.service.impl;

import kata.academy.eurekadirectionservice.model.dto.ChatResponseDto;
import kata.academy.eurekadirectionservice.model.entity.Chat;
import kata.academy.eurekadirectionservice.repository.ChatRepository;
import kata.academy.eurekadirectionservice.service.ChatResponseDtoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Transactional
@Service
public class ChatResponseDtoServiceImpl implements ChatResponseDtoService {
    private final ChatRepository chatRepository;

    @Transactional(readOnly = true)
    @Override
    public Page<ChatResponseDto> findChatsByUserIdOrderedByLastMessageCreatedDate(Long userId, Pageable pageable) {
        Page<Chat> chatPage = chatRepository.findChatsByUserIdOrderedByMessageCreatedDate(userId, pageable);

        return chatPage.map(chat -> new ChatResponseDto(
            chat.getId(),
            null,
            chat.getType(),
            chat.getName(),
            chat.getDescription())
        );
    }
}
