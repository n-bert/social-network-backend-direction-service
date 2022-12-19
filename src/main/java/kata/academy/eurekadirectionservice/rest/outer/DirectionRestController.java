package kata.academy.eurekadirectionservice.rest.outer;

import kata.academy.eurekadirectionservice.api.Data;
import kata.academy.eurekadirectionservice.feign.ProfileServiceFeignClient;
import kata.academy.eurekadirectionservice.model.converter.ChatMapper;
import kata.academy.eurekadirectionservice.model.dto.ChatRequestDto;
import kata.academy.eurekadirectionservice.model.dto.ChatResponseDto;
import kata.academy.eurekadirectionservice.model.dto.ChatUserResponseDto;
import kata.academy.eurekadirectionservice.model.entity.Chat;
import kata.academy.eurekadirectionservice.model.enums.ChatType;
import kata.academy.eurekadirectionservice.service.ChatResponseDtoService;
import kata.academy.eurekadirectionservice.service.ChatService;
import kata.academy.eurekadirectionservice.util.ApiValidationUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Positive;
import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/directions/chats")
public class DirectionRestController {
    private final ChatService chatService;
    private final ChatResponseDtoService chatResponseDtoService;
    private final ProfileServiceFeignClient profileServiceFeignClient;

    @PostMapping
    public ResponseEntity<Data<Long>> addChat(@RequestHeader @Positive Long userId,
                                              @RequestParam(required = false) String name,
                                              @RequestParam List<Long> chatUsers) {
        ApiValidationUtil.requireFalse(chatUsers.isEmpty(),
            "Количество участников чата должно быть больше или равно двум");
        Chat chat = chatService.addChat(userId, name, chatUsers);
        return ResponseEntity.ok(Data.of(chat.getId()));
    }

    @PutMapping("/{chatId}")
    public ResponseEntity<Void> editChat(@RequestHeader @Positive Long userId,
                                         @RequestBody ChatRequestDto dto,
                                         @PathVariable @Positive Long chatId) {

        Optional<Chat> optionalChat = chatService.findChatById(chatId);
        ApiValidationUtil.requireTrue(optionalChat.isPresent(),
            String.format("Чат с chatId=%d не найден в базе данных", chatId));

        Chat chat = optionalChat.get();

        ApiValidationUtil.requireTrue(chat.getType().equals(ChatType.GROUP),
            String.format("Чат с chatId=%d не является групповым", chatId));
        ApiValidationUtil.requireTrue(chat.getChatUsers().contains(userId),
            String.format("Пользователь с userId=%d не является участником чата с chatId=%d", userId, chatId));

        chatService.updateChat(ChatMapper.toEntity(dto, chat));
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{chatId}")
    public ResponseEntity<ChatResponseDto> getChatById(@RequestHeader @Positive Long userId,
                                                       @PathVariable @Positive Long chatId) {
        Optional<Chat> optionalChat = chatService.findChatById(chatId);
        ApiValidationUtil.requireTrue(optionalChat.isPresent(),
            String.format("Чат с chatId=%d не найден в базе данных", chatId));

        Chat chat = optionalChat.get();

        ApiValidationUtil.requireTrue(chat.getChatUsers().contains(userId),
            String.format("Пользователь с userId=%d не является участником чата с chatId=%d", userId, chatId));

        List<ChatUserResponseDto> chatUserResponseDtoList = profileServiceFeignClient.getChatUserResponseDtoByUserId(chat.getChatUsers()).getBody();

        return ResponseEntity.ok(new ChatResponseDto(
            chat.getId(),
            chatUserResponseDtoList,
            chat.getType(),
            chat.getName(),
            chat.getDescription()
        ));
    }

    @GetMapping
    public ResponseEntity<Page<ChatResponseDto>> getChatPage(@RequestHeader @Positive Long userId,
                                                             Pageable pageable) {
        return ResponseEntity.ok(chatResponseDtoService.findChatsByUserIdOrderedByLastMessageCreatedDate(userId, pageable));
    }
}
