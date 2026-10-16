-- ============================================================
-- V5 — Refresh Tokens
-- ============================================================
-- Permite renovar o access token sem novo login.
-- O refresh token é opaco (não é JWT) e armazenado como hash.
-- ============================================================

CREATE TABLE refresh_tokens (
                                id BIGSERIAL PRIMARY KEY,

                                token_hash VARCHAR(64) NOT NULL UNIQUE,
                                usuario_id BIGINT NOT NULL,

                                expiracao TIMESTAMP NOT NULL,
                                revogado BOOLEAN NOT NULL DEFAULT false,
                                criado_em TIMESTAMP NOT NULL,
                                revogado_em TIMESTAMP,

    -- Contexto para auditoria (opcional)
                                ip VARCHAR(45),
                                user_agent VARCHAR(255),

                                CONSTRAINT fk_refresh_token_usuario
                                    FOREIGN KEY (usuario_id) REFERENCES usuarios(id)
                                        ON DELETE CASCADE
);

CREATE INDEX idx_refresh_token_usuario
    ON refresh_tokens (usuario_id);

CREATE INDEX idx_refresh_token_expiracao
    ON refresh_tokens (expiracao);

-- Limpeza automática: tokens expirados ou revogados há mais de 30 dias
CREATE INDEX idx_refresh_token_limpeza
    ON refresh_tokens (revogado, expiracao);