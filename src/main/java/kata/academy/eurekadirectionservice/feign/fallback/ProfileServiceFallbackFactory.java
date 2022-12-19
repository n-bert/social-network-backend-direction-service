package kata.academy.eurekadirectionservice.feign.fallback;

import kata.academy.eurekadirectionservice.feign.ProfileServiceFeignClient;
import org.springframework.cloud.openfeign.FallbackFactory;
import org.springframework.stereotype.Component;

@Component
public class ProfileServiceFallbackFactory implements FallbackFactory<ProfileServiceFeignClient> {

    @Override
    public ProfileServiceFeignClient create(Throwable cause) {
        return new ProfileServiceFallback(cause);
    }
}
