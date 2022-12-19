package kata.academy.eurekadirectionservice.service.impl;

import kata.academy.eurekadirectionservice.model.entity.Chat;
import kata.academy.eurekadirectionservice.model.entity.Message;
import kata.academy.eurekadirectionservice.repository.MessageRepository;
import kata.academy.eurekadirectionservice.service.MessageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@RequiredArgsConstructor
@Transactional
@Service
public class MessageServiceImpl implements MessageService {
    private final MessageRepository messageRepository;

    @Override
    public void addMessage(Chat chat, Long userId, String text ) {
        messageRepository.save(
            Message
                .builder()
                .chat(chat)
                .userId(userId)
                .text(text)
                .createdDate(LocalDateTime.now())
                .build()
        );
    }
}
