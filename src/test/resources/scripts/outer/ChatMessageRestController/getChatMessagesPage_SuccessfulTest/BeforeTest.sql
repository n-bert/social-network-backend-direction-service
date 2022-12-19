INSERT INTO chats(id, type, name, description)
VALUES (1, 'GROUP', 'chat name', 'chat description');


INSERT INTO chats_users(chat_id, user_id)
VALUES (1, 1),
    (1, 2),
    (1, 3);

INSERT INTO messages(id, created_date, text, user_id, chat_id)
VALUES (1, '2022-01-01T01:00:00.000001', 'message 1', 1, 1),
    (2, '2022-01-02T01:00:00.000001', 'message 2', 2, 1);

