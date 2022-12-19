package kata.academy.eurekadirectionservice.service;

import kata.academy.eurekadirectionservice.model.entity.Chat;

public interface MessageService {

    void addMessage(Chat chat, Long userId, String text);
}
