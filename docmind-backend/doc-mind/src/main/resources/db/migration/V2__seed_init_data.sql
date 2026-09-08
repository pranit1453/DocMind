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