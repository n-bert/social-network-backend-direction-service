package kata.academy.eurekadirectionservice.feign.fallback;

import kata.academy.eurekadirectionservice.exception.FeignRequestException;
import kata.academy.eurekadirectionservice.feign.ProfileServiceFeignClient;
import kata.academy.eurekadirectionservice.model.dto.ChatUserResponseDto;
import org.springframework.http.ResponseEntity;

import java.util.List;

record ProfileServiceFallback(Throwable cause) implements ProfileServiceFeignClient {
    @Override
    public ResponseEntity<List<ChatUserResponseDto>> getChatUserResponseDtoByUserId(List<Long> userIds) {
        throw new FeignRequestException("Сервис временно недоступен. Причина -> %s"
            .formatted(cause.getMessage()), cause);
    }
}
