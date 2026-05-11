# 🍔 PitsDog API

API RESTful desenvolvida em Java com Spring Boot para gerenciamento do sistema de delivery da hamburgueria **PitsDog**.

O projeto foi criado com foco em arquitetura limpa, escalabilidade e separação de responsabilidades, servindo como base para integração com painel administrativo, dashboard financeiro, gerenciamento de pedidos e futuras integrações com gateway de pagamento.

---

# 🚀 Tecnologias Utilizadas

## Backend

* Java 21
* Spring Boot
* Spring Web
* Spring Data JPA
* Maven

## Banco de Dados

* PostgreSQL

## Ferramentas

* Git & GitHub
* IntelliJ IDEA
* Postman
* Docker (estrutura preparada)

---

# 📁 Estrutura do Projeto

```bash
src/main/java/com/pitsdog/api
│
├── categoria
│   ├── controller
│   ├── dto
│   ├── entity
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
└── config
```

O projeto foi organizado seguindo separação por domínio, facilitando manutenção, crescimento e legibilidade.

---

# ⚙️ Funcionalidades Implementadas

## Categorias

- Criar categoria
- Listar categorias
- Buscar categoria por ID
- Atualizar categoria
- Deletar categoria
- Controle de categoria ativa/inativa
- Organização por ordem de exibição
- Campo de imagem para exibição no frontend


## Produtos
- Criar produto
- Listar produtos
- Buscar produto por ID
- Atualizar produto
- Deletar produto
- Relacionar produto com categoria
- Controle de disponibilidade do produto
- Campo de imagem do produto
- Preço utilizando `BigDecimal`

---

# 🧠 Conceitos Aplicados

* Arquitetura em camadas
* REST API
* DTO Pattern
* Injeção de Dependência
* Repository Pattern
* Separação de responsabilidades
* HTTP Status Codes
* Boas práticas com Spring Boot

---

# 📡 Endpoints

## Produto
```http
POST /produtos
```

### Listar produtos

```http
GET /produtos
```

### Buscar produto por ID

```http
GET /produtos/{id}
```

### Atualizar produto

```http
PUT /produtos/{id}
```

### Deletar produto

```http
DELETE /produtos/{id}
```

---

## Categoria

### Criar categoria

```http
POST /categorias
```

### Listar categorias

```http
GET /categorias
```

### Buscar categoria por ID

```http
GET /categorias/{id}
```

### Atualizar categoria

```http
PUT /categorias/{id}
```

### Deletar categoria

```http
DELETE /categorias/{id}
```

---

# 🛠️ Como Rodar o Projeto

## Clone o repositório

```bash
git clone https://github.com/EmanoelCavalcante/pitsdog-api.git
```

## Entre na pasta

```bash
cd pitsdog-api
```

## Configure o banco no `application.properties`

```properties
spring.datasource.url=jdbc:postgresql://localhost:5432/pitsdog
spring.datasource.username=SEU_USUARIO
spring.datasource.password=SUA_SENHA

spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

## Execute o projeto

```bash
./mvnw spring-boot:run
```

ou rode diretamente pela IDE.

---

# 📌 Roadmap

* [ ] Sistema de pedidos
* [ ] Dashboard financeiro
* [ ] Sistema administrativo
* [ ] Upload de imagens
* [ ] Relatórios financeiros
* [ ] Docker Compose
* [ ] Deploy em VPS

---

# 🤝 Integração Frontend

A API será integrada com:

* Painel administrativo em React/TypeScript
* Sistema de pedidos
* Dashboard financeiro
* Aplicação mobile futuramente

---

# 📖 Objetivo do Projeto

Este projeto está sendo desenvolvido como:

* Projeto real para estabelecimento
* Evolução prática em backend Java
* Estudo avançado de Spring Boot
* Construção de portfólio profissional

---

# 👨‍💻 Desenvolvedor

Desenvolvido por **Emanoel Cavalcante**

* GitHub:
  https://github.com/EmanoelCavalcante

---

# ⭐ Observações

O projeto está em desenvolvimento contínuo e novas funcionalidades serão adicionadas conforme evolução da aplicação e necessidades do negócio.
