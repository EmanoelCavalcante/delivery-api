-- Inserts ficticios para testes locais/dev.
-- Ajuste nomes de colunas conforme a estrutura do seu banco (ex.: coluna de imagem do produto).
-- Nao utilize dados reais.

BEGIN;

-- ============================================================
-- CATEGORIAS
-- ============================================================

INSERT INTO public.categorias (nome, descricao, imagem, ordem, ativo)
VALUES
  ('Hamburgers', 'Hamburgers artesanais e combos', 'https://exemplo.com/imagens/categorias/hamburgers.png', 1, true),
  ('Bebidas', 'Refrigerantes, sucos e agua', 'https://exemplo.com/imagens/categorias/bebidas.png', 2, true),
  ('Sobremesas', 'Doces e sobremesas', 'https://exemplo.com/imagens/categorias/sobremesas.png', 3, true);

-- ============================================================
-- PRODUTOS
-- ============================================================

-- Observacao: a coluna de imagem pode variar conforme naming strategy.
-- Exemplo com coluna snake_case (mais comum):
-- INSERT INTO public.produtos (nome, preco, descricao, image_url, ativo, categoria_id)
-- VALUES ('X-Burger', 24.90, 'Pao, hamburger, queijo e molho da casa', 'https://exemplo.com/imagens/produtos/x-burger.png', true, 1);

-- Exemplo alternativo com coluna camelCase:
-- INSERT INTO public.produtos (nome, preco, descricao, imageUrl, ativo, categoria_id)
-- VALUES ('X-Burger', 24.90, 'Pao, hamburger, queijo e molho da casa', 'https://exemplo.com/imagens/produtos/x-burger.png', true, 1);

-- Produtos (sem imagem, para simplificar)
INSERT INTO public.produtos (nome, preco, descricao, ativo, categoria_id)
VALUES
  ('X-Burger', 24.90, 'Pao, hamburger, queijo e molho da casa', true, 1),
  ('X-Salada', 27.90, 'Hamburger, queijo, alface, tomate e maionese', true, 1),
  ('Refrigerante Lata', 6.50, '350ml', true, 2);

COMMIT;

