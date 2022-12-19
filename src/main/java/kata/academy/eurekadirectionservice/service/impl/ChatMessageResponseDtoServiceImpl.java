package kata.academy.eurekadirectionservice.service.impl;

import kata.academy.eurekadirectionservice.feign.ProfileServiceFeignClient;
import kata.academy.eurekadirectionservice.model.dto.ChatMessageResponseDto;
import kata.academy.eurekadirectionservice.model.dto.ChatUserResponseDto;
import kata.academy.eurekadirectionservice.model.entity.Chat;
import kata.academy.eurekadirectionservice.model.entity.Message;
import kata.academy.eurekadirectionservice.repository.MessageRepository;
import kata.academy.eurekadirectionservice.service.ChatMessageResponseDtoService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@RequiredArgsConstructor
@Transactional
@Service
public class ChatMessageResponseDtoServiceImpl implements ChatMessageResponseDtoService {

    private final MessageRepository messageRepository;
    private final ProfileServiceFeignClient profileServiceFeignClient;

    @Transactional(readOnly = true)
    @Override
    public Page<ChatMessageResponseDto> findMessagesByChatId(Chat chat, Pageable pageable) {
        Page<Message> messagePage = messageRepository.findMessagesByChatIdOrderByCreatedDateDesc(chat.getId(), pageable);
        List<ChatUserResponseDto> chatUserResponseDtoList = profileServiceFeignClient
            .getChatUserResponseDtoByUserId(chat.getChatUsers()).getBody();

        return messagePage.map((Message message) ->  {
            return new ChatMessageResponseDto(
                message.getId(),
                message.getText(),
                chatUserResponseDtoList.stream().filter(dto -> dto.userId().equals(message.getUserId())).findFirst().get(),
                message.getCreatedDate(),
                message.getChat().getId()
                );
        });
    }
}

