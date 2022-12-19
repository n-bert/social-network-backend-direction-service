package kata.academy.eurekadirectionservice.outer;

import kata.academy.eurekadirectionservice.SpringSimpleContextTest;
import kata.academy.eurekadirectionservice.feign.ProfileServiceFeignClient;
import kata.academy.eurekadirectionservice.model.dto.ChatUserResponseDto;
import kata.academy.eurekadirectionservice.model.entity.Message;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBeans({
    @MockBean(ProfileServiceFeignClient.class)
})
@DirtiesContext
public class ChatMessageRestControllerIT extends SpringSimpleContextTest {
    @Autowired
    ProfileServiceFeignClient profileServiceFeignClient;

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/ChatMessageRestController/addMessage_SuccessfulTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/ChatMessageRestController/addMessage_SuccessfulTest/AfterTest.sql")
    public void addMessage_SuccessfulTest() throws Exception {
        Long chatId = 1L;
        Long userId = 1L;
        String text = "new message";

        mockMvc.perform(post("/api/v1/directions/chats/{chatId}/messages", chatId)
                .header("userId", userId)
                .param("text", text))
            .andExpect(status().isOk());
        assertTrue(entityManager.createQuery("""
                                              SELECT COUNT(m.id) > 0
                                              FROM Message m
                                              WHERE m.chat.id = :chatId
                                                AND m.text = :text
                                             """, Boolean.class)
            .setParameter("chatId", chatId)
            .setParameter("text", text)
            .getSingleResult());

    }

    @Test
    public void addMessage_ChatNotFoundTest() throws Exception {
        Long chatId = 1L;
        Long userId = 1L;
        String text = "new message";

        mockMvc.perform(post("/api/v1/directions/chats/{chatId}/messages", chatId)
                .header("userId", userId)
                .param("text", text))
            .andExpect(status().is4xxClientError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.text")
                .value(String.format("Чат с chatId=%d не найден в базе данных", chatId)));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/ChatMessageRestController/addMessage_UserNotFoundTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/ChatMessageRestController/addMessage_UserNotFoundTest/AfterTest.sql")
    public void addMessage_UserNotFoundTest() throws Exception {
        Long chatId = 1L;
        Long userId = 4L;
        String text = "new message";

        mockMvc.perform(post("/api/v1/directions/chats/{chatId}/messages", chatId)
                .header("userId", userId)
                .param("text", text))
            .andExpect(status().is4xxClientError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.text")
                .value(String.format("Пользователь с userId=%d не является участником чата с chatId=%d", userId, chatId)));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/ChatMessageRestController/getChatMessagesPage_SuccessfulTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/ChatMessageRestController/getChatMessagesPage_SuccessfulTest/AfterTest.sql")
    public void getChatMessagesPage_SuccessfulTest() throws Exception {
        Long chatId = 1L;
        Long userId = 1L;
        List<Long> userIdList = List.of(1L, 2L, 3L);
        Pageable requestPage = PageRequest.of(0, 1);
        int totalPages = 2;

        ResponseEntity<List<ChatUserResponseDto>> chatUserResponseDtoList = ResponseEntity.ok(List.of(
            new ChatUserResponseDto(1L, "name1"),
            new ChatUserResponseDto(2L, "name2"),
            new ChatUserResponseDto(3L, "name3")));

        List<Message> expectedMessageList = entityManager.createQuery("""
                                                                        SELECT m
                                                                        FROM Message m
                                                                        WHERE m.chat.id = :chatId
                                                                        GROUP BY m.id
                                                                        ORDER BY MIN(m.createdDate) DESC
                                                                      """, Message.class)
            .setParameter("chatId", chatId)
            .getResultList();

        doReturn(chatUserResponseDtoList).when(profileServiceFeignClient).getChatUserResponseDtoByUserId(userIdList);
        mockMvc.perform(get("/api/v1/directions/chats/{chatId}/messages", chatId)
                .header("userId", userId)
                .param("page", String.valueOf(requestPage.getPageNumber()))
                .param("size", String.valueOf(requestPage.getPageSize())))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(expectedMessageList.get(0).getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].text").value(expectedMessageList.get(0).getText()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].chatUser.userId").value(expectedMessageList.get(0).getUserId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].createdDate").value(expectedMessageList.get(0).getCreatedDate().toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].chatId").value(expectedMessageList.get(0).getChat().getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(totalPages))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(expectedMessageList.size()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(requestPage.getPageSize()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.sort.sorted").value(Boolean.FALSE));
    }
}
