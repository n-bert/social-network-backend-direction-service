package kata.academy.eurekadirectionservice.repository;

import kata.academy.eurekadirectionservice.model.entity.Chat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

public interface ChatRepository extends JpaRepository<Chat, Long> {

    @Query(nativeQuery = true,
        value = """
                SELECT CASE WHEN (
                    SELECT count(ch.id)
                    FROM chats ch
                    JOIN chats_users cu on ch.id = cu.chat_id
                    WHERE type = :type
                        AND cu.user_id in :chatUsers
                    GROUP BY ch.id
                    HAVING count(ch.id) = :usersCount
                )
                IS NOT NULL THEN TRUE ELSE FALSE END
                """)
    boolean existsByTypeAndAllChatUsersIn(String type, List<Long> chatUsers, Long usersCount);

    Optional<Chat> findChatById(Long chatId);

    @Query("""
            SELECT ch
            FROM Chat ch
            JOIN ch.chatMessages cm
            JOIN ch.chatUsers ON :userId MEMBER OF ch.chatUsers
            GROUP BY ch.id
            ORDER BY MIN(cm.createdDate) DESC
           """)
    Page<Chat> findChatsByUserIdOrderedByMessageCreatedDate(Long userId, Pageable pageable);
}
