-- ============================================================
-- V4 — Adiciona suporte a cancelamento de vendas
-- ============================================================
-- A venda é marcada como CANCELADA em vez de ser eliminada.
-- O stock é devolvido via movimento de DEVOLUCAO.
-- ============================================================

ALTER TABLE vendas
    ADD COLUMN IF NOT EXISTS status VARCHAR(20) NOT NULL DEFAULT 'CONCLUIDA';

ALTER TABLE vendas
    ADD COLUMN IF NOT EXISTS cancelada_em TIMESTAMP NULL;

ALTER TABLE vendas
    ADD COLUMN IF NOT EXISTS cancelada_por_id BIGINT NULL;

ALTER TABLE vendas
    ADD COLUMN IF NOT EXISTS motivo_cancelamento VARCHAR(500) NULL;

-- Foreign key para o utilizador que cancelou
ALTER TABLE vendas
    ADD CONSTRAINT fk_venda_cancelada_por
        FOREIGN KEY (cancelada_por_id) REFERENCES usuarios(id);

-- Índice para filtrar por status
CREATE INDEX IF NOT EXISTS idx_venda_status
    ON vendas (status);

-- Índice composto para relatórios
CREATE INDEX IF NOT EXISTS idx_venda_empresa_status_data
    ON vendas (empresa_id, status, data_venda);