-- Добавление прав доступа
INSERT INTO authorities (name) VALUES
                                   ('FILE_READ'),
                                   ('FILE_WRITE'),
                                   ('FILE_DELETE'),
                                   ('FILE_SHARE'),
                                   ('FOLDER_CREATE'),
                                   ('FOLDER_DELETE'),
                                   ('USER_MANAGE'),
                                   ('SYSTEM_ADMIN');

-- Добавление ролей
INSERT INTO roles (name) VALUES
                             ('MODERATOR'),
                             ('ADMIN');

-- Связывание (предполагая, что ID сгенерировались по порядку)
-- authorities: 1-8, roles: 1-3

-- ROLE_USER
INSERT INTO role_authorities (role_id, authority_id) VALUES
                                                         (1, 1), (1, 2), (1, 3), (1, 5);

-- ROLE_PREMIUM
INSERT INTO role_authorities (role_id, authority_id) VALUES
                                                         (2, 1), (2, 2), (2, 3), (2, 4), (2, 5), (2, 6);

-- ROLE_ADMIN
INSERT INTO role_authorities (role_id, authority_id) VALUES
                                                         (3, 1), (3, 2), (3, 3), (3, 4), (3, 5), (3, 6), (3, 7), (3, 8);