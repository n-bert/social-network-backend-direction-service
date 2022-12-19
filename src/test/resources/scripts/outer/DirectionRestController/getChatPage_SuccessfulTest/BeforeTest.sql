INSERT INTO chats(id, type, name, description)
VALUES (1, 'GROUP', 'chat name', 'chat description'),
    (2, 'DIRECTION', null, null);


INSERT INTO chats_users(chat_id, user_id)
VALUES (1, 1),
    (1, 2),
    (1, 3),
    (2, 1),
    (2, 3);

INSERT INTO messages(id, created_date, text, user_id, chat_id)
VALUES (1, '2022-01-01T01:00:00.000001', 'message_1', 1, 1),
    (2, '2022-01-02T01:00:00.000001', 'message_2', 2, 1),
    (3, '2022-01-03T01:00:00.000001', 'message_3', 3, 1),
    (4, '2022-01-05T01:00:00.000001', 'message_1', 1, 2),
    (5, '2022-01-06T01:00:00.000001', 'message_2', 1, 2);

