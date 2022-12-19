INSERT INTO chats(id, type, name, description)
VALUES (1, 'DIRECTION', null, null),
    (2, 'DIRECTION', null, null),
    (3, 'DIRECTION', null, null);

INSERT INTO chats_users(chat_id, user_id)
VALUES (1, 1),
    (1, 2),
    (2, 1),
    (2, 3),
    (3, 2),
    (3, 3);

