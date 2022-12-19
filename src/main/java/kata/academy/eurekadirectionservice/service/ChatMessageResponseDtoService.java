package kata.academy.eurekadirectionservice.service;

import kata.academy.eurekadirectionservice.model.dto.ChatMessageResponseDto;
import kata.academy.eurekadirectionservice.model.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatMessageResponseDtoService {

    Page<ChatMessageResponseDto> findMessagesByChatId(Chat chat, Pageable pageable);
}
