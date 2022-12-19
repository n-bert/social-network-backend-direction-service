package kata.academy.eurekadirectionservice.feign;

import kata.academy.eurekadirectionservice.feign.fallback.ProfileServiceFallbackFactory;
import kata.academy.eurekadirectionservice.model.dto.ChatUserResponseDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@FeignClient(value = "eureka-profile-service", fallbackFactory = ProfileServiceFallbackFactory.class)
public interface ProfileServiceFeignClient {

    @GetMapping("/api/internal/v1/profiles")
    ResponseEntity<List<ChatUserResponseDto>> getChatUserResponseDtoByUserId(@RequestParam List<Long> userIds);
}
