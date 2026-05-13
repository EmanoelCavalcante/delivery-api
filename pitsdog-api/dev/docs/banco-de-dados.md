# Pit's Dog - Banco de Dados (Supabase/PostgreSQL)

Documentacao basica das tabelas principais usadas atualmente no projeto.

---

## Tabela: categorias

Tabela: `categorias`

Campos principais (conforme entity):
- `id` (PK): identificador da categoria.
- `nome`: nome exibido no cardapio.
- `descricao`: descricao da categoria.
- `ordem`: define a ordenacao no cardapio.
- `imagem`: URL/caminho de imagem associada a categoria (opcional).
- `ativo`: indica se a categoria esta ativa para exibicao/uso.

---

## Tabela: produtos

Tabela: `produtos`

Campos principais (conforme entity/DTO):
- `id` (PK): identificador do produto.
- `nome`: nome exibido no cardapio.
- `descricao`: descricao do produto (opcional).
- `preco`: valor do produto.
- `ativo`: indica se o produto esta ativo para exibicao/uso.
- `categoria_id` (FK): referencia para `categorias.id` (obrigatorio).
- Campo de imagem: o projeto usa `imagemUrl` nos DTOs e um campo de URL de imagem no produto (ajuste o nome da coluna conforme sua estrutura atual no banco).

---

## Relacionamento Produto -> Categoria

O relacionamento e ManyToOne:
- Um produto pertence a uma categoria.
- Uma categoria pode possuir varios produtos.

Vinculo:
- O produto usa `categoria_id` para se vincular a uma categoria existente.
- No request de produto, esse vinculo e enviado como `categoriaId`.

