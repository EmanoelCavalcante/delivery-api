-- Exemplos (comentados) de comandos SQL uteis no Supabase/PostgreSQL.
-- Observacao: este arquivo NAO deve conter dados sensiveis, senhas, URLs privadas, tokens ou chaves.

-- ============================================================
-- Conferencia de estrutura
-- ============================================================

-- Listar colunas de uma tabela (PostgreSQL)
-- SELECT column_name, data_type, is_nullable
-- FROM information_schema.columns
-- WHERE table_schema = 'public' AND table_name = 'categorias'
-- ORDER BY ordinal_position;

-- Ver constraints e chaves estrangeiras
-- SELECT conname, pg_get_constraintdef(oid)
-- FROM pg_constraint
-- WHERE conrelid = 'public.produtos'::regclass;

-- ============================================================
-- Alteracoes comuns (exemplos)
-- ============================================================

-- Adicionar coluna (exemplo)
-- ALTER TABLE public.produtos
-- ADD COLUMN IF NOT EXISTS destaque boolean DEFAULT false;

-- Alterar tipo de coluna (exemplo)
-- ALTER TABLE public.produtos
-- ALTER COLUMN preco TYPE numeric(10,2);

-- Tornar coluna obrigatoria (exemplo)
-- ALTER TABLE public.categorias
-- ALTER COLUMN nome SET NOT NULL;

-- Criar indice para performance (exemplo)
-- CREATE INDEX IF NOT EXISTS idx_produtos_categoria_id
-- ON public.produtos (categoria_id);

-- ============================================================
-- Checagens rapidas
-- ============================================================

-- Ver contagem por status (exemplo)
-- SELECT ativo, COUNT(*)
-- FROM public.produtos
-- GROUP BY ativo
-- ORDER BY ativo DESC;

