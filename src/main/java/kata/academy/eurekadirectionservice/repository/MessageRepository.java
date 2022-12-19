package kata.academy.eurekadirectionservice.repository;

import kata.academy.eurekadirectionservice.model.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MessageRepository extends JpaRepository<Message, Long> {

    Page<Message> findMessagesByChatIdOrderByCreatedDateDesc(Long chatId, Pageable pageable);
}
