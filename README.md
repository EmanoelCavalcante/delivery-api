# 🍔 Delivery API

![Java](https://img.shields.io/badge/Java-21-red?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-API-green?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?style=for-the-badge&logo=postgresql)
![Supabase](https://img.shields.io/badge/Supabase-Storage%20%2B%20DB-3ECF8E?style=for-the-badge&logo=supabase&logoColor=white)
![Railway](https://img.shields.io/badge/Railway-Deploy-purple?style=for-the-badge&logo=railway)
![JWT](https://img.shields.io/badge/JWT-Auth-black?style=for-the-badge&logo=jsonwebtokens)
![Swagger](https://img.shields.io/badge/Swagger-OpenAPI-85EA2D?style=for-the-badge&logo=swagger&logoColor=black)
![Status](https://img.shields.io/badge/Status-Em%20desenvolvimento-yellow?style=for-the-badge)

API RESTful real desenvolvida em **Java 21 com Spring Boot** para gerenciamento do sistema de delivery da hamburgueria **PitsDog**.

O projeto foi criado com foco em **organização, escalabilidade, boas práticas de backend e uso real em produção**, integrando site público, painel administrativo, banco PostgreSQL no Supabase, upload de imagens com Supabase Storage e deploy na Railway.

---

## 📚 Sumário

- [Sobre o Projeto](#-sobre-o-projeto)
- [Objetivos](#-objetivos)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Arquitetura](#-arquitetura)
- [Funcionalidades](#-funcionalidades)
- [Regras de Negócio](#-regras-de-negócio)
- [Status dos Pedidos](#-status-dos-pedidos)
- [Pagamentos](#-pagamentos)
- [Upload de Imagens](#-upload-de-imagens)
- [Endpoints](#-endpoints)
- [Exemplos de Requisições](#-exemplos-de-requisições)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Segurança](#-segurança)
- [CORS](#-cors)
- [Como Rodar](#-como-rodar)
- [Swagger](#-swagger)
- [Deploy](#-deploy)
- [Testes](#-testes)
- [Integração com Frontend](#-integração-com-frontend)
- [Status do Projeto](#-status-do-projeto)
- [Roadmap](#-roadmap)
- [Atenção Antes de Usar](#-atenção-antes-de-usar)
- [Desenvolvedor](#-desenvolvedor)

---

## 📌 Sobre o Projeto

A **PitsDog API** é o backend principal de um sistema de delivery desenvolvido para uma hamburgueria.

A API centraliza o gerenciamento de:

| Módulo | Descrição |
|---|---|
| Categorias | Organização dos produtos exibidos no cardápio |
| Produtos | Cadastro e controle dos itens vendidos |
| Adicionais | Gerenciamento de extras adicionados aos pedidos |
| Combos | Cadastro e venda de combos promocionais |
| Pedidos | Criação, edição, acompanhamento e finalização de pedidos |
| Pagamentos | Controle manual de status de pagamento |
| Loja | Controle de funcionamento e tipos de pedido aceitos |
| Upload | Armazenamento de imagens no Supabase Storage |
| Admin | Rotas protegidas para o painel administrativo |
| Cardápio | Rotas públicas para consumo do frontend do cliente |

O backend atende dois ambientes principais:

| Ambiente | Finalidade |
|---|---|
| Site público | Clientes visualizam o cardápio e realizam pedidos |
| Painel administrativo | Equipe da hamburgueria gerencia pedidos, produtos, categorias, adicionais, combos e operação da loja |

---

## 🎯 Objetivos

Os principais objetivos da API são:

- Centralizar a lógica de pedidos da hamburgueria.
- Permitir criação de pedidos online.
- Permitir pedidos dos tipos `ENTREGA`, `RETIRADA` e `MESA`.
- Gerenciar categorias, produtos, adicionais e combos.
- Calcular valores automaticamente.
- Controlar status dos pedidos.
- Controlar pagamentos manuais.
- Permitir upload e armazenamento de imagens.
- Separar rotas públicas e administrativas.
- Proteger rotas administrativas com JWT.
- Facilitar integração com frontend web.
- Preparar o sistema para deploy real.
- Servir como projeto comercial e portfólio profissional em Java/Spring Boot.

---

## 🚀 Tecnologias Utilizadas

### Backend

| Tecnologia | Uso |
|---|---|
| Java 21 | Linguagem principal |
| Spring Boot | Framework backend |
| Spring Web | Criação da API REST |
| Spring Security | Segurança e proteção de rotas |
| Spring Data JPA | Persistência de dados |
| Hibernate | ORM |
| Bean Validation | Validação de dados |
| JWT | Autenticação administrativa |
| Maven | Gerenciamento de dependências e build |

### Banco de Dados e Storage

| Tecnologia | Uso |
|---|---|
| PostgreSQL | Banco relacional |
| Supabase PostgreSQL | Banco em produção |
| Supabase Pooler | Conexão otimizada com o banco |
| HikariCP | Pool de conexões |
| Supabase Storage | Armazenamento de imagens |

### Documentação, Deploy e Ferramentas

| Tecnologia | Uso |
|---|---|
| Swagger / OpenAPI | Documentação e testes da API |
| Railway | Deploy do backend |
| Netlify | Deploy do site público |
| Render | Deploy do painel administrativo |
| Git e GitHub | Versionamento |
| IntelliJ IDEA | Desenvolvimento |
| Postman / Insomnia | Testes manuais |
| Pytest | Testes externos |

---

## 🧱 Arquitetura

O projeto segue uma arquitetura em camadas, com separação por domínio.

Cada módulo possui responsabilidades bem definidas:

| Camada | Responsabilidade |
|---|---|
| `controller` | Recebe requisições HTTP e retorna respostas |
| `service` | Contém regras de negócio |
| `repository` | Acessa o banco de dados |
| `entity` | Representa as tabelas do banco |
| `dto` | Define objetos de entrada e saída da API |
| `enums` | Define valores fixos usados pelo sistema |
| `config` | Centraliza configurações da aplicação |
| `exception` | Trata erros específicos e globais |

### Estrutura base

```text
src/main/java/com/pitsdog/api
│
├── adicional
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
├── auth
│   ├── controller
│   ├── dto
│   └── service
│
├── categoria
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
├── combo
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
├── common
│   └── exception
│
├── config
│
├── loja
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
├── pedido
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── enums
│   ├── repository
│   └── service
│
├── produto
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
└── upload
    ├── controller
    ├── dto
    ├── exception
    └── service
```

---

## ⚙️ Funcionalidades

### 📁 Categorias

Permite gerenciar as categorias exibidas no cardápio público.

| Funcionalidade | Status |
|---|---|
| Criar categoria | ✅ |
| Listar categorias | ✅ |
| Buscar categoria por ID | ✅ |
| Atualizar categoria | ✅ |
| Deletar categoria | ✅ |
| Ativar/desativar categoria | ✅ |
| Definir ordem de exibição | ✅ |
| Definir descrição | ✅ |
| Definir URL de imagem | ✅ |
| Exibir no cardápio público | ✅ |

Exemplo de payload:

```json
{
  "nome": "Hambúrgueres",
  "descricao": "Categoria de hambúrgueres artesanais",
  "ativo": true,
  "ordem": 1,
  "imagemUrl": "https://exemplo.com/imagem.png"
}
```

---

### 🍔 Produtos

Permite cadastrar e gerenciar os produtos vendidos pela hamburgueria.

| Funcionalidade | Status |
|---|---|
| Criar produto | ✅ |
| Listar produtos | ✅ |
| Buscar produto por ID | ✅ |
| Atualizar produto | ✅ |
| Deletar produto | ✅ |
| Relacionar produto com categoria | ✅ |
| Controlar disponibilidade | ✅ |
| Definir preço | ✅ |
| Definir imagem | ✅ |
| Exibir no cardápio público | ✅ |

Exemplo de payload:

```json
{
  "nome": "X-Bacon",
  "descricao": "Hambúrguer com bacon, queijo e molho especial",
  "preco": 22.90,
  "disponivel": true,
  "imagemUrl": "https://exemplo.com/x-bacon.png",
  "categoriaId": 1
}
```

---

### ➕ Adicionais

Permite gerenciar itens extras que podem ser adicionados aos produtos.

Exemplos de adicionais:

- Bacon extra
- Cheddar
- Ovo
- Carne extra
- Molho especial
- Queijo
- Milho
- Cebola

| Funcionalidade | Status |
|---|---|
| Criar adicional | ✅ |
| Listar adicionais | ✅ |
| Buscar adicional por ID | ✅ |
| Atualizar adicional | ✅ |
| Deletar adicional | ✅ |
| Ativar/desativar adicional | ✅ |
| Definir preço | ✅ |
| Definir imagem | ✅ |
| Associar adicionais aos itens do pedido | ✅ |
| Somar adicionais no total do pedido | ✅ |

Exemplo de payload:

```json
{
  "nome": "Bacon extra",
  "descricao": "Porção extra de bacon",
  "preco": 4.00,
  "ativo": true,
  "imagemUrl": "https://exemplo.com/bacon.png"
}
```

---

### 🍱 Combos

Permite cadastrar e gerenciar combos vendidos pela hamburgueria.

| Funcionalidade | Status |
|---|---|
| Criar combo | ✅ |
| Listar combos | ✅ |
| Buscar combo por ID | ✅ |
| Atualizar combo | ✅ |
| Deletar combo | ✅ |
| Controlar disponibilidade | ✅ |
| Definir preço | ✅ |
| Definir descrição | ✅ |
| Definir imagem | ✅ |
| Exibir no cardápio público | ✅ |
| Permitir pedido com item do tipo combo | ✅ |

Exemplo de payload:

```json
{
  "nome": "Combo Casal",
  "descricao": "Combo com dois hambúrgueres, batata e refrigerante",
  "preco": 59.90,
  "disponivel": true,
  "imagemUrl": "https://exemplo.com/combo-casal.png"
}
```

---

### 🧾 Pedidos

A API permite criar e controlar pedidos dos tipos:

- `ENTREGA`
- `RETIRADA`
- `MESA`

| Funcionalidade | Status |
|---|---|
| Criar pedido | ✅ |
| Criar pedido com produtos | ✅ |
| Criar pedido com combos | ✅ |
| Adicionar adicionais aos itens | ✅ |
| Calcular subtotal | ✅ |
| Calcular taxa de entrega | ✅ |
| Calcular total | ✅ |
| Controlar status do pedido | ✅ |
| Editar pedido pelo painel administrativo | ✅ |
| Cancelar pedido | ✅ |
| Restaurar pedido cancelado | ✅ |
| Listar pedidos com paginação | ✅ |
| Filtrar pedidos por status, tipo e data | ✅ |
| Separar pedidos em andamento e finalizados | ✅ |

---

### 🏪 Loja

Permite controlar o funcionamento da loja e os tipos de pedidos aceitos.

| Funcionalidade | Status |
|---|---|
| Verificar se a loja está aberta | ✅ |
| Controlar se aceita entrega | ✅ |
| Controlar se aceita retirada | ✅ |
| Controlar se aceita pedidos de mesa | ✅ |
| Retornar mensagem operacional ao frontend | ✅ |
| Bloquear pedidos quando a loja estiver fechada | ✅ |

Rota principal:

```http
GET /loja/status
```

---

## 🧮 Regras de Negócio

A API possui regras internas para manter pedidos, pagamentos e itens consistentes.

| Regra | Descrição |
|---|---|
| Pedido com múltiplos itens | Um pedido pode possuir vários itens |
| Produto ou combo | Um item pode ser do tipo `PRODUTO` ou `COMBO` |
| Adicionais | Um item pode possuir vários adicionais |
| Cálculo de adicionais | O valor dos adicionais entra no cálculo do item |
| Subtotal | Considera produtos, combos, quantidades e adicionais |
| Taxa de entrega | Somada apenas em pedidos do tipo `ENTREGA` |
| Total | Calculado automaticamente |
| Validação de existência | Produtos, combos e adicionais precisam existir antes de serem associados |
| Disponibilidade | Produtos e combos indisponíveis não devem ser vendidos |
| Adicionais ativos | Adicionais inativos não devem ser utilizados |
| Pedido de mesa | Não exige telefone obrigatório |
| Pedido cancelado | Não pode ser editado normalmente |
| Restauração | Apenas admin pode restaurar pedido cancelado |
| Pagamento | Pode ser confirmado ou ter confirmação cancelada manualmente |
| Painel admin | Pedidos finalizados são separados dos pedidos em andamento |

### Exemplo de cálculo

```text
Valor do item = preço do produto ou combo + soma dos adicionais

Subtotal = soma de todos os itens considerando quantidade

Total = subtotal + taxa de entrega
```

---

## 🔄 Status dos Pedidos

Status disponíveis:

| Status | Descrição |
|---|---|
| `AGUARDANDO_APROVACAO` | Pedido criado e aguardando aprovação |
| `EM_PREPARO` | Pedido aprovado e em preparo |
| `PRONTO_PARA_RETIRADA` | Pedido pronto para retirada |
| `SAIU_PARA_ENTREGA` | Pedido saiu para entrega |
| `CONCLUIDO` | Pedido concluído |
| `FINALIZADO` | Pedido encerrado no painel |
| `CANCELADO` | Pedido cancelado |

Regras importantes:

- Todo pedido novo inicia como `AGUARDANDO_APROVACAO`.
- O admin pode aprovar e movimentar o pedido.
- Pedido de retirada pode ir para `PRONTO_PARA_RETIRADA`.
- Pedido de entrega pode ir para `SAIU_PARA_ENTREGA`.
- Pedido concluído pode ser marcado como `CONCLUIDO`.
- Pedido encerrado no painel pode ser marcado como `FINALIZADO`.
- Pedido cancelado fica bloqueado para edição comum.
- Pedido cancelado pode ser restaurado pelo admin.

---

## 💰 Pagamentos

O sistema possui controle manual de pagamento.

### Formas de pagamento suportadas

| Forma de pagamento | Descrição |
|---|---|
| `DINHEIRO` | Pagamento em dinheiro |
| `PIX` | Pagamento via Pix |
| `CARTAO_CREDITO` | Pagamento no cartão de crédito |
| `CARTAO_DEBITO` | Pagamento no cartão de débito |

### Status de pagamento

| Status | Descrição |
|---|---|
| `PENDENTE` | Pagamento ainda não confirmado |
| `CONFIRMADO` | Pagamento confirmado |
| `CANCELADO` | Confirmação de pagamento cancelada |

Rotas principais:

```http
PATCH /admin/pedidos/{id}/pagamento/confirmar
PATCH /admin/pedidos/{id}/pagamento/cancelar-confirmacao
```

---

## 🖼️ Upload de Imagens

A API possui integração com o **Supabase Storage** para upload de imagens.

| Recurso | Descrição |
|---|---|
| Upload via API | Envio de imagens por rota HTTP |
| Validação de tipo | Aceita apenas formatos permitidos |
| Validação de tamanho | Limite máximo de upload |
| Storage externo | Imagens armazenadas no Supabase Storage |
| URL pública | A API retorna a URL pública da imagem |
| Campo de uso | A URL é salva no campo `imagemUrl` |

Formatos aceitos:

```text
image/jpeg
image/png
```

Tamanho máximo:

```text
5MB
```

Rota principal:

```http
POST /upload/imagem
```

---

## 🧠 Conceitos Aplicados

Durante o desenvolvimento foram aplicados conceitos importantes de backend:

- REST API
- Arquitetura em camadas
- DTO Pattern
- Repository Pattern
- Service Layer
- Injeção de Dependência
- Separação de responsabilidades
- Validação de dados
- Relacionamentos com JPA
- Paginação
- Filtros dinâmicos
- HTTP Status Codes
- Tratamento global de exceções
- Autenticação com JWT
- Proteção de rotas administrativas
- Configuração de CORS
- Upload de arquivos
- Integração com serviço externo de storage
- Uso de variáveis de ambiente
- Configuração para produção
- Documentação com Swagger
- Deploy em ambiente cloud

---

## 📡 Endpoints

Os endpoints são divididos entre rotas públicas e rotas administrativas.

### 🌐 Rotas Públicas

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/loja/status` | Consulta o status operacional da loja |
| `GET` | `/categorias` | Lista categorias públicas |
| `GET` | `/produtos` | Lista produtos públicos |
| `GET` | `/adicionais` | Lista adicionais públicos |
| `GET` | `/combos` | Lista combos públicos |
| `GET` | `/cardapio` | Retorna o cardápio completo |
| `POST` | `/pedidos` | Cria um novo pedido |
| `GET` | `/pedidos/{id}` | Consulta um pedido por ID |

### 🔐 Autenticação

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/auth/login` | Realiza login administrativo e retorna JWT |

### 🧾 Admin Pedidos

| Método | Rota | Descrição |
|---|---|---|
| `GET` | `/admin/pedidos` | Lista pedidos com paginação e filtros |
| `GET` | `/admin/pedidos/{id}` | Busca pedido por ID |
| `PATCH` | `/admin/pedidos/{id}/status` | Atualiza status do pedido |
| `PUT` | `/admin/pedidos/{id}` | Edita dados do pedido |
| `PATCH` | `/admin/pedidos/{id}/pagamento/confirmar` | Confirma pagamento |
| `PATCH` | `/admin/pedidos/{id}/pagamento/cancelar-confirmacao` | Cancela confirmação de pagamento |
| `PATCH` | `/admin/pedidos/{id}/restaurar` | Restaura pedido cancelado |

### 📁 Admin Categorias

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/admin/categorias` | Cria uma nova categoria |
| `GET` | `/admin/categorias` | Lista categorias no painel admin |
| `PUT` | `/admin/categorias/{id}` | Atualiza uma categoria |
| `DELETE` | `/admin/categorias/{id}` | Remove uma categoria |
| `PATCH` | `/admin/categorias/{id}/status` | Ativa ou desativa uma categoria |

### 🍔 Admin Produtos

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/admin/produtos` | Cria um novo produto |
| `GET` | `/admin/produtos` | Lista produtos no painel admin |
| `GET` | `/admin/produtos/{id}` | Busca produto por ID |
| `PUT` | `/admin/produtos/{id}` | Atualiza um produto |
| `DELETE` | `/admin/produtos/{id}` | Remove um produto |

### ➕ Admin Adicionais

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/admin/adicionais` | Cria um novo adicional |
| `GET` | `/admin/adicionais` | Lista adicionais no painel admin |
| `GET` | `/admin/adicionais/{id}` | Busca adicional por ID |
| `PUT` | `/admin/adicionais/{id}` | Atualiza um adicional |
| `DELETE` | `/admin/adicionais/{id}` | Remove um adicional |

### 🍱 Admin Combos

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/admin/combos` | Cria um novo combo |
| `GET` | `/admin/combos` | Lista combos no painel admin |
| `GET` | `/admin/combos/{id}` | Busca combo por ID |
| `PUT` | `/admin/combos/{id}` | Atualiza um combo |
| `DELETE` | `/admin/combos/{id}` | Remove um combo |

### 🖼️ Upload

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/upload/imagem` | Faz upload de imagem para o Supabase Storage |

---

## 📦 Exemplos de Requisições

### Login administrativo

```http
POST /auth/login
Content-Type: application/json
```

```json
{
  "email": "admin@email.com",
  "senha": "sua_senha"
}
```

Resposta esperada:

```json
{
  "token": "jwt_gerado_pela_api",
  "tipo": "Bearer"
}
```

---

### Criar categoria

```http
POST /admin/categorias
Authorization: Bearer SEU_TOKEN_AQUI
Content-Type: application/json
```

```json
{
  "nome": "Hambúrgueres",
  "descricao": "Lanches artesanais da casa",
  "ativo": true,
  "ordem": 1,
  "imagemUrl": "https://exemplo.com/hamburgueres.png"
}
```

---

### Criar produto

```http
POST /admin/produtos
Authorization: Bearer SEU_TOKEN_AQUI
Content-Type: application/json
```

```json
{
  "nome": "X-Bacon",
  "descricao": "Pão, carne, queijo, bacon e molho especial",
  "preco": 22.90,
  "disponivel": true,
  "imagemUrl": "https://exemplo.com/x-bacon.png",
  "categoriaId": 1
}
```

---

### Criar adicional

```http
POST /admin/adicionais
Authorization: Bearer SEU_TOKEN_AQUI
Content-Type: application/json
```

```json
{
  "nome": "Cheddar extra",
  "descricao": "Porção extra de cheddar cremoso",
  "preco": 3.50,
  "ativo": true,
  "imagemUrl": "https://exemplo.com/cheddar.png"
}
```

---

### Criar pedido para entrega

```http
POST /pedidos
Content-Type: application/json
```

```json
{
  "tipoPedido": "ENTREGA",
  "nomeCliente": "Emanoel",
  "telefoneCliente": "(91) 99999-9999",
  "bairroEntrega": "Centro",
  "ruaEntrega": "Rua Principal",
  "numeroCasa": "123",
  "complemento": "Casa azul",
  "taxaEntrega": 5.00,
  "formaPagamento": "PIX",
  "itens": [
    {
      "tipoItem": "PRODUTO",
      "produtoId": 1,
      "quantidade": 2,
      "observacao": "Sem cebola",
      "adicionaisIds": [1, 2]
    }
  ]
}
```

---

### Criar pedido com combo

```http
POST /pedidos
Content-Type: application/json
```

```json
{
  "tipoPedido": "RETIRADA",
  "nomeCliente": "Emanoel",
  "telefoneCliente": "(91) 99999-9999",
  "formaPagamento": "DINHEIRO",
  "trocoPara": 100.00,
  "itens": [
    {
      "tipoItem": "COMBO",
      "comboId": 1,
      "quantidade": 1,
      "observacao": "Retirada no balcão",
      "adicionaisIds": []
    }
  ]
}
```

---

### Criar pedido de mesa

```http
POST /pedidos
Content-Type: application/json
```

```json
{
  "tipoPedido": "MESA",
  "numeroMesa": 5,
  "formaPagamento": "CARTAO_DEBITO",
  "itens": [
    {
      "tipoItem": "PRODUTO",
      "produtoId": 2,
      "quantidade": 1,
      "observacao": "Bem passado",
      "adicionaisIds": []
    }
  ]
}
```

---

### Atualizar status do pedido

```http
PATCH /admin/pedidos/{id}/status
Authorization: Bearer SEU_TOKEN_AQUI
Content-Type: application/json
```

```json
{
  "status": "EM_PREPARO"
}
```

---

### Confirmar pagamento

```http
PATCH /admin/pedidos/{id}/pagamento/confirmar
Authorization: Bearer SEU_TOKEN_AQUI
Content-Type: application/json
```

```json
{
  "formaPagamento": "DINHEIRO",
  "trocoPara": 100.00
}
```

---

## 🔐 Variáveis de Ambiente

Para evitar exposição de dados sensíveis, o projeto utiliza variáveis de ambiente.

### Banco de dados

```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
```

### Variáveis necessárias

| Variável | Descrição |
|---|---|
| `DATABASE_URL` | URL de conexão com o banco PostgreSQL |
| `DATABASE_USERNAME` | Usuário do banco de dados |
| `DATABASE_PASSWORD` | Senha do banco de dados |
| `PORT` | Porta usada no ambiente de deploy |
| `ADMIN_EMAIL` | Email do administrador |
| `ADMIN_PASSWORD_HASH` | Hash BCrypt da senha do administrador |
| `JWT_EXPIRATION_SECONDS` | Tempo de expiração do token JWT |
| `APP_CORS_ALLOWED_ORIGINS` | Lista de origens permitidas no CORS |
| `SUPABASE_URL` | URL do projeto Supabase |
| `SUPABASE_SERVICE_ROLE_KEY` | Chave service role do Supabase |
| `SUPABASE_BUCKET` | Bucket usado para armazenar imagens |

### Exemplo local

```env
DATABASE_URL=jdbc:postgresql://localhost:5432/pitsdog
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=sua_senha
ADMIN_EMAIL=admin@email.com
ADMIN_PASSWORD_HASH=$2a$10$hash_bcrypt_aqui
JWT_EXPIRATION_SECONDS=10800
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:5174,http://localhost:3000
SUPABASE_URL=https://seu-projeto.supabase.co
SUPABASE_SERVICE_ROLE_KEY=sua_chave_service_role
SUPABASE_BUCKET=imagens
```

---

## ⚠️ Segurança

> **Nunca envie senhas, tokens, hashes, chaves privadas, URLs sensíveis do banco ou `SUPABASE_SERVICE_ROLE_KEY` para o GitHub.**

Arquivos que devem ficar fora do versionamento:

```text
application.properties
application.yml
application-dev.properties
application-prod.properties
.env
```

Exemplo de `.gitignore`:

```gitignore
# Configurações da aplicação
src/main/resources/application.properties
src/main/resources/application.yml
src/main/resources/application-dev.properties
src/main/resources/application-prod.properties

# Variáveis locais
.env
```

Caso alguma senha, token ou URL sensível seja enviada para o GitHub por engano:

1. Revogue ou troque imediatamente a credencial exposta.
2. Atualize as variáveis de ambiente no Railway/Supabase.
3. Remova a credencial do código.
4. Garanta que o arquivo sensível esteja no `.gitignore`.
5. Faça um novo commit sem a credencial exposta.

---

## 🌐 CORS

A API possui configuração de CORS para permitir acesso do frontend local e dos ambientes publicados.

Exemplos de origens usadas no projeto:

```text
http://localhost:5173
http://localhost:5174
http://localhost:3000
```

As origens permitidas devem ser configuradas na variável:

```env
APP_CORS_ALLOWED_ORIGINS=
```

Exemplo:

```env
APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:5174,http://localhost:3000,https
```

---

## 🛠️ Como Rodar

### 1. Clone o repositório

```bash
git clone https://github.com/EmanoelCavalcante/pitsdog-api.git
```

### 2. Entre na pasta do projeto

```bash
cd pitsdog-api
```

### 3. Configure as variáveis de ambiente

Configure as variáveis necessárias para banco de dados, autenticação, CORS e Supabase Storage.

Exemplo com PostgreSQL local:

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pitsdog
spring.datasource.username=postgres
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true
```

### 4. Execute a aplicação

No Windows:

```bash
./mvnw.cmd spring-boot:run
```

No Linux/Mac:

```bash
./mvnw spring-boot:run
```

Também é possível executar diretamente pela IDE.

---

## 📖 Swagger

A API possui documentação via Swagger.

Com o projeto rodando, acesse:

```text
http://localhost:8080/swagger-ui/index.html
```

ou:

```text
http://localhost:8080/swagger-ui.html
```

Documentação OpenAPI:

```text
http://localhost:8080/v3/api-docs
```

O Swagger permite:

- Visualizar endpoints.
- Testar requisições.
- Ver parâmetros esperados.
- Ver modelos de request e response.
- Facilitar integração com frontend.

---

## ☁️ Deploy

O projeto está preparado para deploy na Railway.

Principais pontos configurados:

- Uso de PostgreSQL em produção.
- Banco hospedado no Supabase.
- Uso de Supabase Pooler.
- Upload de imagens com Supabase Storage.
- Variáveis de ambiente.
- Build com Maven.
- Deploy a partir do GitHub.
- Configuração externa do banco.
- Configuração de CORS para frontend publicado.
- Execução usando a variável `PORT`.

Configuração de porta:

```properties
server.port=${PORT:8080}
```

Configuração recomendada do HikariCP para Railway/Supabase:

```properties
spring.datasource.hikari.minimum-idle=0
spring.datasource.hikari.maximum-pool-size=3
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.max-lifetime=240000
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.keepalive-time=0
```

Fluxo geral do deploy:

1. Subir o projeto no GitHub.
2. Conectar o repositório na Railway.
3. Configurar as variáveis de ambiente.
4. Configurar banco PostgreSQL do Supabase.
5. Configurar Supabase Storage.
6. Realizar build com Maven.
7. Executar a aplicação em produção.
8. Acessar a API pela URL gerada pela Railway.

---

## 🧪 Testes

A API pode ser testada usando:

- Swagger UI
- Postman
- Insomnia
- Scripts automatizados com Python
- Pytest

### Testes com Maven

```bash
./mvnw test
```

### Testes externos com Python

```bash
pytest
```

---

## 🧾 Fluxo de Teste Manual

Uma ordem recomendada para testar a API:

1. Verificar status da loja em `/loja/status`.
2. Criar ou listar categorias.
3. Criar ou listar produtos.
4. Criar ou listar adicionais.
5. Criar ou listar combos.
6. Consultar o cardápio em `/cardapio`.
7. Criar pedido do tipo `RETIRADA`.
8. Criar pedido do tipo `ENTREGA`.
9. Criar pedido do tipo `MESA`.
10. Fazer login administrativo.
11. Listar pedidos no painel admin.
12. Atualizar status do pedido.
13. Confirmar pagamento.
14. Cancelar pedido.
15. Restaurar pedido cancelado.
16. Finalizar pedido.
17. Testar upload de imagem.
18. Conferir se a URL da imagem foi salva corretamente.

---

## 🤝 Integração com Frontend

A API foi pensada para ser consumida por um frontend moderno.

Ambientes usados no projeto:

| Ambiente | Plataforma |
|---|---|
| Site público | Netlify |
| Painel administrativo | Render |
| Frontend local | Vite |

### Rotas principais consumidas pelo site público

```http
GET /loja/status
GET /cardapio
POST /pedidos
GET /pedidos/{id}
```

### Rotas principais consumidas pelo painel administrativo

```http
POST /auth/login
GET /admin/pedidos
PATCH /admin/pedidos/{id}/status
PATCH /admin/pedidos/{id}/pagamento/confirmar
GET /admin/produtos
GET /admin/categorias
GET /admin/adicionais
GET /admin/combos
POST /upload/imagem
```

Na listagem de pedidos administrativos, o retorno é paginado.

O frontend deve consumir os pedidos dentro do campo:

```json
{
  "content": []
}
```

---

## 📦 Status do Projeto

O backend principal está funcional e preparado para integração final com o frontend.

| Item | Status |
|---|---|
| Backend principal funcional | ✅ |
| Módulos principais criados | ✅ |
| JWT implementado | ✅ |
| Rotas admin protegidas | ✅ |
| Deploy configurado na Railway | ✅ |
| Banco PostgreSQL conectado no Supabase | ✅ |
| Supabase Storage integrado | ✅ |
| Upload de imagens implementado | ✅ |
| Swagger configurado | ✅ |
| CORS configurado | ✅ |
| Paginação de pedidos implementada | ✅ |
| Filtros administrativos implementados | ✅ |
| Controle manual de pagamento implementado | ✅ |
| API preparada para integração final com frontend | ✅ |

---

## 🗺️ Roadmap

### ✅ Concluído

- [x] Criar estrutura base do projeto.
- [x] Configurar Spring Boot.
- [x] Configurar banco PostgreSQL.
- [x] Criar módulo de categorias.
- [x] Criar módulo de produtos.
- [x] Criar módulo de adicionais.
- [x] Criar módulo de combos.
- [x] Criar módulo de pedidos.
- [x] Implementar criação de pedidos.
- [x] Implementar pedidos por entrega, retirada e mesa.
- [x] Implementar cálculo de subtotal.
- [x] Implementar cálculo de adicionais.
- [x] Implementar taxa de entrega.
- [x] Implementar status dos pedidos.
- [x] Implementar controle manual de pagamento.
- [x] Implementar autenticação JWT.
- [x] Proteger rotas administrativas.
- [x] Separar rotas públicas e administrativas.
- [x] Criar documentação Swagger.
- [x] Preparar deploy no Railway.
- [x] Usar variáveis de ambiente.
- [x] Configurar CORS.
- [x] Implementar upload de imagens.
- [x] Integrar Supabase Storage.
- [x] Implementar paginação no painel admin.
- [x] Implementar filtros de pedidos.
- [x] Implementar tratamento global de erros.

### 🔧 Em melhoria

- [ ] Ajustes finais de integração com frontend.
- [ ] Melhorias finas no painel administrativo.
- [ ] Melhorias de experiência no fluxo de pedido.
- [ ] Testes finais em produção.
- [ ] Ajustes de performance conforme uso real.

### 💡 Futuro

- [ ] Dashboard financeiro.
- [ ] Relatórios de vendas.
- [ ] Produtos mais vendidos.
- [ ] Ticket médio.
- [ ] Controle de caixa.
- [ ] Integração com gateway de pagamento.
- [ ] Integração com Mercado Pago.
- [ ] Notificações em tempo real.
- [ ] WebSocket ou polling otimizado.
- [ ] Integração com bot externo.
- [ ] CI/CD com GitHub Actions.
- [ ] Docker Compose.

---

## ⚠️ Atenção Antes de Usar

Antes de executar ou contribuir com o projeto, verifique os seguintes pontos:

### Pacote base da aplicação

Confirme se o pacote principal do projeto está como:

```text
com.pitsdog.api
```

### Rotas administrativas

As rotas administrativas usam o prefixo:

```text
/admin
```

Exemplos:

```http
/admin/categorias
/admin/produtos
/admin/adicionais
/admin/combos
/admin/pedidos
```

### Token JWT

As rotas administrativas exigem o header:

```http
Authorization: Bearer SEU_TOKEN_AQUI
```

### Retorno paginado

A listagem de pedidos administrativos retorna uma página.

Os dados ficam dentro de:

```json
{
  "content": []
}
```

### Campo de imagem

O campo usado para armazenar imagens é:

```json
"imagemUrl"
```

### Campo de adicionais no pedido

O DTO do pedido utiliza o campo:

```json
"adicionaisIds"
```

---

## 👨‍💻 Desenvolvedor

Desenvolvido por **Emanoel Cavalcante**

GitHub: [https://github.com/EmanoelCavalcante](https://github.com/EmanoelCavalcante)

---

## ⭐ Considerações Finais

A **PitsDog API** é um projeto real em evolução, desenvolvido com foco em aprendizado prático, aplicação comercial e construção de portfólio profissional.

O sistema já possui os principais recursos de um backend de delivery moderno, incluindo cardápio público, painel administrativo, autenticação, controle de pedidos, pagamentos manuais, upload de imagens, deploy em cloud e integração com banco PostgreSQL.

O projeto continuará recebendo melhorias conforme novas necessidades surgirem no uso real da hamburgueria.
