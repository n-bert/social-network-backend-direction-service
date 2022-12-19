package kata.academy.eurekadirectionservice.rest.outer;

import kata.academy.eurekadirectionservice.model.dto.ChatMessageResponseDto;
import kata.academy.eurekadirectionservice.model.entity.Chat;
import kata.academy.eurekadirectionservice.service.ChatMessageResponseDtoService;
import kata.academy.eurekadirectionservice.service.ChatService;
import kata.academy.eurekadirectionservice.service.MessageService;
import kata.academy.eurekadirectionservice.util.ApiValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.Optional;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/directions/chats")
public class ChatMessageRestController {
    private final ChatService chatService;
    private final MessageService messageService;
    private final ChatMessageResponseDtoService chatMessageResponseDtoService;

    @PostMapping("/{chatId}/messages")
    public ResponseEntity<Void> addMessage(@RequestHeader @Positive Long userId,
                                           @PathVariable @Positive Long chatId,
                                           @RequestParam String text) {
        Optional<Chat> optionalChat = chatService.findChatById(chatId);
        ApiValidationUtil.requireTrue(optionalChat.isPresent(),
            String.format("Чат с chatId=%d не найден в базе данных", chatId));

        Chat chat = optionalChat.get();

        ApiValidationUtil.requireTrue(chat.getChatUsers().contains(userId),
            String.format("Пользователь с userId=%d не является участником чата с chatId=%d", userId, chatId));

        messageService.addMessage(chat, userId, text);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{chatId}/messages")
    public ResponseEntity<Page<ChatMessageResponseDto>> getChatMessagesPage(@RequestHeader @Positive Long userId,
                                                                            @PathVariable @Positive Long chatId,
                                                                            Pageable pageable) {
        Optional<Chat> optionalChat = chatService.findChatById(chatId);
        ApiValidationUtil.requireTrue(optionalChat.isPresent(),
            String.format("Чат с chatId=%d не найден в базе данных", chatId));

        Chat chat = optionalChat.get();

        ApiValidationUtil.requireTrue(chat.getChatUsers().contains(userId),
            String.format("Пользователь с userId=%d не является участником чата с chatId=%d", userId, chatId));

        return ResponseEntity.ok(chatMessageResponseDtoService.findMessagesByChatId(chat, pageable));
    }
}
