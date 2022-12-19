package kata.academy.eurekadirectionservice.outer;

import com.google.common.collect.Lists;
import kata.academy.eurekadirectionservice.SpringSimpleContextTest;
import kata.academy.eurekadirectionservice.feign.ProfileServiceFeignClient;
//import kata.academy.eurekadirectionservice.model.converter.ChatResponseDtoMapper;
import kata.academy.eurekadirectionservice.model.dto.ChatRequestDto;
import kata.academy.eurekadirectionservice.model.dto.ChatResponseDto;
import kata.academy.eurekadirectionservice.model.dto.ChatUserResponseDto;
import kata.academy.eurekadirectionservice.model.entity.Chat;
import kata.academy.eurekadirectionservice.model.enums.ChatType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.MockBeans;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.SerializationFeature;

import java.util.List;

import static org.hamcrest.Matchers.nullValue;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.doReturn;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@MockBeans({
    @MockBean(ProfileServiceFeignClient.class)
})
@DirtiesContext
public class DirectionRestControllerTestIT extends SpringSimpleContextTest {
    @Autowired
    private ProfileServiceFeignClient profileServiceFeignClient;

    @Test
    @DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/DirectionRestController/addChat_GroupSuccessfulTest/AfterTest.sql")
    public void addChat_GroupSuccessfulTest() throws Exception {
        Long userId = 1L;
        String name = "new chat";
        Long expectedChatId = 1L;
        ChatType expectedChatType = ChatType.GROUP;
        List<Long> chatUsers = List.of(2L, 3L);

        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("chatUsers", Lists.transform(chatUsers, Object::toString));

        mockMvc.perform(post("/api/v1/directions/chats")
            .header("userId", userId)
                .param("name", name)
            .params(params))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(expectedChatId));

        assertTrue(entityManager.createQuery("""
                                                        SELECT COUNT(ch.id) > 0
                                                        FROM Chat ch
                                                        JOIN ch.chatUsers
                                                        WHERE ch.id = :expectedChatId
                                                            AND ch.type = :expectedChatType
                                                            AND ch.name = :name
                                                      """, Boolean.class)
            .setParameter("expectedChatId", expectedChatId)
            .setParameter("expectedChatType", expectedChatType)
            .setParameter("name", name)
            .getSingleResult());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/DirectionRestController/addChat_DirectionSuccessfulTest/AfterTest.sql")
    public void addChat_DirectionSuccessfulTest() throws Exception {
        Long userId = 1L;
        Long expectedChatId = 1L;
        ChatType expectedChatType = ChatType.DIRECTION;
        List<Long> chatUsers = List.of(2L);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("chatUsers", Lists.transform(chatUsers, Object::toString));

        mockMvc.perform(post("/api/v1/directions/chats")
                .header("userId", userId)
                .params(params)
                .accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.data").value(expectedChatId));

        assertTrue(entityManager.createQuery("""
                                                        SELECT COUNT(ch.id) > 0
                                                        FROM Chat ch
                                                        WHERE ch.id = :expectedChatId
                                                            AND ch.type = :expectedChatType
                                                      """, Boolean.class)
            .setParameter("expectedChatId", expectedChatId)
            .setParameter("expectedChatType", expectedChatType)
            .getSingleResult());
    }

    @Test
    public void addChat_ChatUsersCountFailTest() throws Exception {
        Long userId = 1L;

        mockMvc.perform(post("/api/v1/directions/chats")
                .header("userId", userId)
                .param("chatUsers",""))
            .andExpect(status().is4xxClientError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.text")
                .value(("Количество участников чата должно быть больше или равно двум")));
    }

    @Test
    public void addChat_GroupChatEmptyNameFailTest() throws Exception {
        Long userId = 1L;
        List<Long> chatUsers = List.of(2L, 3L);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("chatUsers", Lists.transform(chatUsers, Object::toString));

        mockMvc.perform(post("/api/v1/directions/chats")
                .header("userId", userId)
                .params(params)
                .param("name", ""))
            .andExpect(status().is4xxClientError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.text")
                .value(("Для группового чата необходим непустой параметр name")));
    }

    @Test
    public void addChat_DirectionChatNotNullNameFailTest() throws Exception {
        Long userId = 1L;
        String name = "chat name";
        List<Long> chatUsers = List.of(2L);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("chatUsers", Lists.transform(chatUsers, Object::toString));

        mockMvc.perform(post("/api/v1/directions/chats")
                .header("userId", userId)
                .params(params)
                .param("name", name))
            .andExpect(status().is4xxClientError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.text")
                .value(("В DIRECTION чате параметр name должен быть null")));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/DirectionRestController/addChat_DirectionChatAlreadyExistsTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/DirectionRestController/addChat_DirectionChatAlreadyExistsTest/AfterTest.sql")
    public void addChat_DirectionChatAlreadyExistsTest() throws Exception {
        Long userId = 1L;
        List<Long> chatUsers = List.of(2L);
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
        params.addAll("chatUsers", Lists.transform(chatUsers, Object::toString));

        mockMvc.perform(post("/api/v1/directions/chats")
                .header("userId", userId)
                .params(params))
            .andExpect(status().is4xxClientError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.text")
                .value(String.format("Чат между пользователями userId=%d и userId=%d уже существует в базе данных", chatUsers.get(0), userId)));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/DirectionRestController/editChat_SuccessfulTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/DirectionRestController/editChat_SuccessfulTest/AfterTest.sql")
    public void editChat_SuccessfulTest() throws Exception {
        Long chatId = 1L;
        Long userId = 1L;
        ChatType chatType = ChatType.GROUP;
        ChatRequestDto requestDto = new ChatRequestDto("chat_new_name", "chat_new_description");
        ObjectMapper mapper = new ObjectMapper();
//        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String requestJson=mapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/api/v1/directions/chats/{chatId}", chatId)
            .header("userId", userId)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().isOk());

        assertTrue(entityManager.createQuery("""
                                            SELECT COUNT(ch.id) > 0
                                            FROM Chat ch
                                            JOIN ch.chatUsers ON :userId MEMBER OF ch.chatUsers
                                            WHERE ch.id = :chatId
                                                AND ch.type = :chatType
                                                AND ch.name = :chatName
                                                AND ch.description = :chatDescription
                                            """, Boolean.class)

            .setParameter("chatId", chatId)
            .setParameter("userId", userId)
            .setParameter("chatType", chatType)
            .setParameter("chatName", requestDto.name())
            .setParameter("chatDescription", requestDto.description())
            .getSingleResult());
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/DirectionRestController/editChat_ChatNotFoundTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/DirectionRestController/editChat_ChatNotFoundTest/AfterTest.sql")
    public void editChat_ChatNotFoundTest() throws Exception {
        Long chatId = 2L;
        Long userId = 1L;
        ChatRequestDto requestDto = new ChatRequestDto("chat new name", "chat new description");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String requestJson = mapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/api/v1/directions/chats/{chatId}", chatId)
                .header("userId", userId)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.text")
                .value(String.format("Чат с chatId=%d не найден в базе данных", chatId)));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/DirectionRestController/editChat_ChatUserNotFoundTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/DirectionRestController/editChat_ChatUserNotFoundTest/AfterTest.sql")
    public void editChat_UserIdFailTest() throws Exception {
        Long chatId = 1L;
        Long userId = 4L;
        ChatRequestDto requestDto = new ChatRequestDto("chat new name", "chat new description");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String requestJson = mapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/api/v1/directions/chats/{chatId}", chatId)
                .header("userId", userId)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.text")
                .value(String.format("Пользователь с userId=%d не является участником чата с chatId=%d", userId, chatId)));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/DirectionRestController/editChat_IncorrectChatTypeTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/DirectionRestController/editChat_IncorrectChatTypeTest/AfterTest.sql")
    public void editChat_IncorrectChatTypeTest() throws Exception {
        Long chatId = 1L;
        Long userId = 1L;
        ChatRequestDto requestDto = new ChatRequestDto("new chat name", "new chat description");
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        String requestJson = mapper.writeValueAsString(requestDto);

        mockMvc.perform(put("/api/v1/directions/chats/{chatId}", chatId)
                .header("userId", userId)
                .content(requestJson)
                .contentType(MediaType.APPLICATION_JSON))
            .andExpect(status().is4xxClientError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.text")
                .value(String.format("Чат с chatId=%d не является групповым", chatId)));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/DirectionRestController/getChatById_SuccessfulTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/DirectionRestController/getChatById_SuccessfulTest/AfterTest.sql")
    public void getChatById_SuccessfulTest() throws Exception {
        Long chatId = 1L;
        Long userId = 1L;
        List<Long> userIdList = List.of(1L, 2L, 3L);
        ResponseEntity<List<ChatUserResponseDto>> chatUserResponseDtoList = ResponseEntity.ok(List.of(
            new ChatUserResponseDto(1L, "name1"),
            new ChatUserResponseDto(2L, "name2"),
            new ChatUserResponseDto(3L, "name3")));
        Chat chat = entityManager.createQuery("""
                                                SELECT ch
                                                FROM Chat ch
                                                JOIN ch.chatUsers ON :userId MEMBER OF ch.chatUsers
                                                WHERE ch.id = :chatId
                                                """, Chat.class)
            .setParameter("userId", userId)
            .setParameter("chatId", chatId)
            .getSingleResult();
//        ChatResponseDto expectedChatResponseDto = ChatResponseDtoMapper.toDto(chat, chatUserResponseDtoList.getBody());
        ChatResponseDto expectedChatResponseDto = new ChatResponseDto(
            chat.getId(),
            chatUserResponseDtoList.getBody(),
            chat.getType(),
            chat.getName(),
            chat.getDescription()
        );

        doReturn(chatUserResponseDtoList).when(profileServiceFeignClient).getChatUserResponseDtoByUserId(userIdList);
        mockMvc.perform(get("/api/v1/directions/chats/{chatId}", chatId)
            .header("userId", userId))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(expectedChatResponseDto.id()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.chatUsers[0].userId").value(expectedChatResponseDto.chatUsers().get(0).userId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.chatUsers[0].name").value(expectedChatResponseDto.chatUsers().get(0).name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.chatUsers[1].userId").value(expectedChatResponseDto.chatUsers().get(1).userId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.chatUsers[1].name").value(expectedChatResponseDto.chatUsers().get(1).name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.chatUsers[2].userId").value(expectedChatResponseDto.chatUsers().get(2).userId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.chatUsers[2].name").value(expectedChatResponseDto.chatUsers().get(2).name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(expectedChatResponseDto.id()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.type").value(expectedChatResponseDto.type().toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expectedChatResponseDto.name()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(expectedChatResponseDto.description()));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/DirectionRestController/getChatById_ChatIdFailTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/DirectionRestController/getChatById_ChatIdFailTest/AfterTest.sql")
    public void getChatById_ChatIdFailTest() throws Exception {
        Long chatId = 2L;
        Long userId = 1L;
        mockMvc.perform(get("/api/v1/directions/chats/{chatId}", chatId)
                .header("userId", userId))
            .andExpect(status().is4xxClientError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.text")
                .value(String.format("Чат с chatId=%d не найден в базе данных", chatId)));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/DirectionRestController/getChatById_UserIdFailTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/DirectionRestController/getChatById_UserIdFailTest/AfterTest.sql")
    public void getChatById_UserIdFailTest() throws Exception {
        Long chatId = 1L;
        Long userId = 4L;
        mockMvc.perform(get("/api/v1/directions/chats/{chatId}", chatId)
                .header("userId", userId))
            .andExpect(status().is4xxClientError())
            .andExpect(MockMvcResultMatchers.jsonPath("$.text")
                .value(String.format("Пользователь с userId=%d не является участником чата с chatId=%d", userId, chatId)));
    }

    @Test
    @Sql(executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD, value = "/scripts/outer/DirectionRestController/getChatPage_SuccessfulTest/BeforeTest.sql")
    @Sql(executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD, value = "/scripts/outer/DirectionRestController/getChatPage_SuccessfulTest/AfterTest.sql")
    public void getChatPage_SuccessfulTest() throws Exception {
        Long userId = 1L;
        Pageable requestPage = PageRequest.of(0, 1);
        int totalPages = 2;

        List<Chat> expectedChatList = entityManager.createQuery("""
                                                            SELECT ch
                                                            FROM Chat ch
                                                            JOIN ch.chatMessages cm
                                                            JOIN ch.chatUsers ON :userId MEMBER OF ch.chatUsers
                                                            GROUP BY ch.id
                                                            ORDER BY MIN(cm.createdDate) DESC
                                                            """, Chat.class)
                    .setParameter("userId", userId)
                    .getResultList();

        mockMvc.perform(get("/api/v1/directions/chats")
                .header("userId", userId)
                .param("page", String.valueOf(requestPage.getPageNumber()))
                .param("size", String.valueOf(requestPage.getPageSize())))
            .andExpect(status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id").value(expectedChatList.get(0).getId()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].chatUsers").value(nullValue()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].type").value(expectedChatList.get(0).getType().toString()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value(expectedChatList.get(0).getName()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].description").value(expectedChatList.get(0).getDescription()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalPages").value(totalPages))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(expectedChatList.size()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.size").value(requestPage.getPageSize()))
            .andExpect(MockMvcResultMatchers.jsonPath("$.sort.sorted").value(Boolean.FALSE));
    }
}