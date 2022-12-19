package kata.academy.eurekadirectionservice.service;

import kata.academy.eurekadirectionservice.model.dto.ChatResponseDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatResponseDtoService {

    Page<ChatResponseDto> findChatsByUserIdOrderedByLastMessageCreatedDate(Long userId, Pageable pageable);
}
