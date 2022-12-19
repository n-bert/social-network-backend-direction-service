INSERT INTO chats(id, type, name, description)
VALUES (1, 'GROUP', 'chat name', 'chat description');

INSERT INTO chats_users(chat_id, user_id)
VALUES (1, 1),
    (1, 2),
    (1, 3);

