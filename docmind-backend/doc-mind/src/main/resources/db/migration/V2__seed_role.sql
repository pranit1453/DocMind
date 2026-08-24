INSERT INTO auth.roles
(role_name, role_description, created_at, updated_at, created_by, updated_by, version)
VALUES ('ADMIN',
        'Manages users, roles, and permissions within the application.',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        gen_random_uuid(),
        NULL,
        0),
       ('USER',
        'Standard authenticated user with limited access.',
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        gen_random_uuid(),
        NULL,
        0);

INSERT INTO auth.users
(user_id, username, password, email, full_name, enabled, deleted,
 scheduled_deletion_at, version, created_at, updated_at, created_by, updated_by)
VALUES ('11111111-1111-1111-1111-111111111111',
        'admin',
        '$2a$10$6kwo3aK94GMy3iNrfVE4N.5XvF6DeOCGbzcCrUjamGtWZhUp/A8T.',
        'DocMind@gmail.com',
        'DocMind Admin',
        TRUE,
        FALSE,
        NULL,
        0,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        NULL,
        NULL);

INSERT INTO auth.user_roles
(user_id, role_id, status, version, created_at, updated_at, created_by, updated_by)
VALUES ('11111111-1111-1111-1111-111111111111',
        1,
        'ACTIVE',
        0,
        CURRENT_TIMESTAMP,
        CURRENT_TIMESTAMP,
        NULL,
        NULL);