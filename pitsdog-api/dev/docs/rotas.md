# PitsDog API - Rotas (Desenvolvimento)

Documentacao de rotas existentes (Categoria e Produto), separando acessos publicos e administrativos.

---

## Categoria

### Rotas publicas

#### Listar categorias (publico)

```http
GET /categorias/{id}
```

Descricao:
- Retorna uma lista de categorias.

Observacao:
- Atualmente o controller esta mapeado como `GET /categorias/{id}` mas o metodo retorna uma lista e nao utiliza o `id`.

### Rotas administrativas

Base:

```http
/admin/categorias
```

#### Listar categorias (admin)

```http
GET /admin/categorias
```

Descricao:
- Lista todas as categorias (visao administrativa).

#### Criar categoria (admin)

```http
POST /admin/categorias
```

Body (exemplo):

```json
{
  "nome": "Hamburgers",
  "descricao": "Hamburgers artesanais e combos",
  "imagem": "https://exemplo.com/imagens/categorias/hamburgers.png",
  "ordem": 1,
  "ativo": true
}
```

#### Atualizar categoria (admin)

```http
PUT /admin/categorias/{id}
```

Body:
- Mesmo formato do `POST /admin/categorias`.

#### Alterar status da categoria (admin)

```http
PATCH /admin/categorias/{id}/status
```

Body (exemplo):

```json
{
  "ativo": true
}
```

#### Deletar categoria (admin)

```http
DELETE /admin/categorias/{id}
```

---

## Produto

### Rotas publicas

#### Listar produtos (publico)

```http
GET /produtos
```

Descricao:
- Lista produtos (para exibicao no cardapio).

#### Buscar produto por ID (publico)

```http
GET /produtos/{id}
```

Descricao:
- Retorna um produto especifico pelo `id`.

### Rotas administrativas

Base:

```http
/produtos/admin
```

#### Criar produto (admin)

```http
POST /produtos/admin
```

Body (exemplo):

```json
{
  "nome": "X-Burger",
  "descricao": "Pao, hamburger, queijo e molho da casa",
  "preco": 24.9,
  "imagemUrl": "https://exemplo.com/imagens/produtos/x-burger.png",
  "ativo": true,
  "categoriaId": 1
}
```

#### Atualizar produto (admin)

```http
PUT /produtos/admin/{id}
```

Body:
- Mesmo formato do `POST /produtos/admin`.

#### Alterar status do produto (admin)

```http
PATCH /produtos/admin/{id}/status
```

Body (exemplo):

```json
{
  "ativo": false
}
```

#### Deletar produto (admin)

```http
DELETE /produtos/admin/{id}
```

