CREATE
EXTENSION IF NOT EXISTS pgcrypto;

CREATE SCHEMA IF NOT EXISTS auth;
CREATE SCHEMA IF NOT EXISTS doc;
CREATE SCHEMA IF NOT EXISTS seed;

--------------------------------------------------------------------------------------------------------
CREATE TABLE auth.users
(
    user_id               UUID                     NOT NULL,

    username              VARCHAR(255)             NOT NULL,
    password              VARCHAR(255)             NOT NULL,
    email                 VARCHAR(255)             NOT NULL,
    full_name             VARCHAR(255)             NOT NULL,

    enabled               BOOLEAN                  NOT NULL DEFAULT TRUE,
    deleted               BOOLEAN                  NOT NULL DEFAULT FALSE,

    scheduled_deletion_at TIMESTAMP WITH TIME ZONE,

    version               BIGINT                   NOT NULL DEFAULT 0,

    created_at            TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at            TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by            UUID,
    updated_by            UUID,

    CONSTRAINT pk_users
        PRIMARY KEY (user_id),

    CONSTRAINT uk_username
        UNIQUE (username),

    CONSTRAINT uk_email
        UNIQUE (email)
);

CREATE INDEX idx_username
    ON auth.users (username);

CREATE INDEX idx_email
    ON auth.users (email);

CREATE INDEX idx_users_deleted
    ON auth.users (deleted);

CREATE INDEX idx_users_scheduled_deletion
    ON auth.users (scheduled_deletion_at);

--------------------------------------------------------------------------------------------------------
CREATE TABLE auth.refresh_tokens
(
    refresh_token_id  BIGSERIAL PRIMARY KEY,

    jti               VARCHAR(128)             NOT NULL,
    session_id        VARCHAR(128)             NOT NULL,
    user_id           UUID                     NOT NULL,

    expires_at        TIMESTAMP WITH TIME ZONE NOT NULL,

    revoked           BOOLEAN                  NOT NULL DEFAULT FALSE,

    replaced_by_token VARCHAR(128),

    created_at        TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at        TIMESTAMP WITH TIME ZONE NOT NULL,

    created_by        UUID,
    updated_by        UUID,

    version           BIGINT                   NOT NULL DEFAULT 0,

    CONSTRAINT uk_refresh_token_jti
        UNIQUE (jti),

    CONSTRAINT fk_refresh_token_user
        FOREIGN KEY (user_id)
            REFERENCES auth.users (user_id)
            ON DELETE CASCADE
);

CREATE INDEX idx_refresh_token_user
    ON auth.refresh_tokens (user_id);

CREATE INDEX idx_refresh_token_session
    ON auth.refresh_tokens (session_id);

CREATE INDEX idx_refresh_token_expires_at
    ON auth.refresh_tokens (expires_at);

CREATE INDEX idx_refresh_token_revoked_expires
    ON auth.refresh_tokens (revoked, expires_at);

--------------------------------------------------------------------------------------------------------
CREATE TABLE auth.roles
(
    role_id          BIGSERIAL PRIMARY KEY,
    role_name        VARCHAR(255)             NOT NULL,
    role_description VARCHAR(500),

    created_at       TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at       TIMESTAMP WITH TIME ZONE NOT NULL,

    created_by       UUID,
    updated_by       UUID,

    version          BIGINT                   NOT NULL DEFAULT 0,

    CONSTRAINT uk_role
        UNIQUE (role_name)
);

CREATE INDEX idx_role
    ON auth.roles (role_name);

--------------------------------------------------------------------------------------------------------
CREATE TABLE auth.user_roles
(
    user_role_id BIGSERIAL PRIMARY KEY,

    user_id      UUID                     NOT NULL,
    role_id      BIGINT                   NOT NULL,

    status       VARCHAR(20)              NOT NULL DEFAULT 'ACTIVE',

    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,

    created_by   UUID,
    updated_by   UUID,

    version      BIGINT                   NOT NULL DEFAULT 0,

    CONSTRAINT fk_user_role_user
        FOREIGN KEY (user_id)
            REFERENCES auth.users (user_id)
            ON DELETE CASCADE,

    CONSTRAINT fk_user_role_role
        FOREIGN KEY (role_id)
            REFERENCES auth.roles (role_id)
            ON DELETE CASCADE,

    CONSTRAINT uk_user_role
        UNIQUE (user_id, role_id),

    CONSTRAINT chk_user_role_status
        CHECK (status IN ('ACTIVE', 'INACTIVE'))
);

CREATE INDEX idx_user_role_user
    ON auth.user_roles (user_id);

CREATE INDEX idx_user_role_role
    ON auth.user_roles (role_id);

--------------------------------------------------------------------------------------------------------
CREATE TABLE doc.document
(
    document_id    UUID                     NOT NULL,
    user_id        UUID                     NOT NULL,
    file_name      VARCHAR(255)             NOT NULL,
    file_size      BIGINT                   NOT NULL,

    file_status    VARCHAR(20)              NOT NULL,

    chunks_created BIGINT                   NOT NULL DEFAULT 0,

    version        BIGINT                   NOT NULL DEFAULT 0,

    created_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at     TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by     UUID,
    updated_by     UUID,

    CONSTRAINT pk_document
        PRIMARY KEY (document_id),

    CONSTRAINT uk_document_user_file_name
        UNIQUE (user_id, file_name),

    CONSTRAINT fk_document_user
        FOREIGN KEY (user_id)
            REFERENCES auth.users (user_id)
            ON DELETE CASCADE,

    CONSTRAINT chk_document_file_size
        CHECK (file_size >= 0),

    CONSTRAINT chk_document_chunks_created
        CHECK (chunks_created >= 0),

    CONSTRAINT chk_document_file_status
        CHECK (
            file_status IN (
                            'UPLOADING',
                            'PROCESSING',
                            'INDEXED',
                            'FAILED'
                )
            )
);

CREATE INDEX idx_document_file_status
    ON doc.document (file_status);

CREATE INDEX idx_document_user_id
    ON doc.document (user_id);

CREATE INDEX idx_document_user_status
    ON doc.document (user_id, file_status);

CREATE INDEX idx_document_created_at
    ON doc.document (created_at);

--------------------------------------------------------------------------------------------------------
CREATE TABLE auth.otp
(
    otp_id       BIGSERIAL PRIMARY KEY,

    challenge_id VARCHAR(36)              NOT NULL,
    email        VARCHAR(320)             NOT NULL,
    otp_hash     VARCHAR(255)             NOT NULL,

    purpose      VARCHAR(30)              NOT NULL,
    status       VARCHAR(20)              NOT NULL,

    expires_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    verified_at  TIMESTAMP WITH TIME ZONE,

    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    created_by   UUID,
    updated_by   UUID,

    CONSTRAINT uk_otp_challenge_id
        UNIQUE (challenge_id),

    CONSTRAINT chk_otp_purpose
        CHECK (
            purpose IN (
                        'FORGOT_PASSWORD',
                        'PASSWORD_CHANGE',
                        'REGISTRATION'
                )
            ),

    CONSTRAINT chk_otp_status
        CHECK (
            status IN (
                       'PENDING',
                       'VERIFIED',
                       'EXPIRED',
                       'FAILED'
                )
            )
);

CREATE INDEX idx_otp_email
    ON auth.otp (email);

CREATE INDEX idx_otp_email_purpose
    ON auth.otp (email, purpose);

CREATE INDEX idx_otp_status
    ON auth.otp (status);

CREATE INDEX idx_otp_expires_at
    ON auth.otp (expires_at);


CREATE TABLE seed.seed_history
(
    seed_name VARCHAR(255)             NOT NULL,
    seeded_at TIMESTAMP WITH TIME ZONE NOT NULL,
    version   BIGINT                   NOT NULL DEFAULT 0,

    CONSTRAINT pk_seed_history
        PRIMARY KEY (seed_name)
);
