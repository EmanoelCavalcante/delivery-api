# 🍔 PitsDog API

![Java](https://img.shields.io/badge/Java-21-red?style=for-the-badge&logo=openjdk)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-API-green?style=for-the-badge&logo=springboot)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-Database-blue?style=for-the-badge&logo=postgresql)
![Maven](https://img.shields.io/badge/Maven-Build-orange?style=for-the-badge&logo=apachemaven)
![Railway](https://img.shields.io/badge/Railway-Deploy-purple?style=for-the-badge&logo=railway)
![Status](https://img.shields.io/badge/Status-Em%20desenvolvimento-yellow?style=for-the-badge)

API RESTful desenvolvida em **Java 21** com **Spring Boot** para gerenciamento do sistema de delivery da hamburgueria **PitsDog**.

O projeto foi criado com foco em organização, escalabilidade, boas práticas de backend e preparação para integração com frontend, painel administrativo, dashboard financeiro, gateway de pagamento e futuras automações do negócio.

---

## 📚 Sumário

- [Sobre o Projeto](#-sobre-o-projeto)
- [Objetivos](#-objetivos)
- [Tecnologias Utilizadas](#-tecnologias-utilizadas)
- [Arquitetura](#-arquitetura)
- [Funcionalidades Implementadas](#️-funcionalidades-implementadas)
- [Regras de Negócio](#-regras-de-negócio)
- [Conceitos Aplicados](#-conceitos-aplicados)
- [Endpoints](#-endpoints)
- [Exemplos de Requisições](#-exemplos-de-requisições)
- [Variáveis de Ambiente](#-variáveis-de-ambiente)
- [Segurança](#️-segurança)
- [Como Rodar o Projeto](#️-como-rodar-o-projeto)
- [Swagger](#-swagger)
- [Deploy](#️-deploy)
- [Testes](#-testes)
- [Fluxo de Teste Manual](#-fluxo-de-teste-manual)
- [Roadmap](#️-roadmap)
- [Integração com Frontend](#-integração-com-frontend)
- [Possíveis Módulos Futuros](#-possíveis-módulos-futuros)
- [Status do Projeto](#-status-do-projeto)
- [Atenção Antes de Usar](#️-atenção-antes-de-usar)
- [Desenvolvedor](#-desenvolvedor)
- [Considerações Finais](#-considerações-finais)

---

## 📌 Sobre o Projeto

A **PitsDog API** é o backend principal de um sistema de delivery voltado para uma hamburgueria.

Ela permite o gerenciamento de categorias, produtos, adicionais e pedidos, servindo como base para:

- Cardápio digital
- Sistema de pedidos online
- Painel administrativo
- Dashboard financeiro
- Controle operacional da hamburgueria
- Integração com bot, frontend e gateway de pagamento

O projeto está sendo desenvolvido de forma incremental, com preocupação em manter uma estrutura limpa, organizada e preparada para crescimento.

---

## 🎯 Objetivos

Os principais objetivos da API são:

- Centralizar a lógica de pedidos da hamburgueria
- Permitir cadastro e gerenciamento de produtos
- Permitir criação de pedidos com itens e adicionais
- Calcular valores automaticamente
- Separar responsabilidades entre cliente e administrador
- Facilitar integração com frontend web/mobile
- Preparar o sistema para deploy real
- Servir como projeto de portfólio profissional em Java/Spring Boot

---

## 🚀 Tecnologias Utilizadas

### Backend

- Java 21
- Spring Boot
- Spring Web
- Spring Data JPA
- Bean Validation
- Maven

### Banco de Dados

- PostgreSQL
- Supabase PostgreSQL
- Railway PostgreSQL

### Documentação

- Swagger
- Springdoc OpenAPI

### Deploy e Infraestrutura

- Railway
- Variáveis de ambiente
- Maven Wrapper

### Ferramentas de Desenvolvimento

- Git
- GitHub
- IntelliJ IDEA
- Postman
- Insomnia
- Swagger UI
- Python/Pytest para testes externos

---

## 🧱 Arquitetura

O projeto segue uma arquitetura em camadas, com separação por domínio.

Cada módulo possui suas próprias responsabilidades:

- `controller`: camada responsável por receber requisições HTTP
- `service`: camada responsável pelas regras de negócio
- `repository`: camada de acesso ao banco de dados
- `entity`: representação das tabelas do banco
- `dto`: objetos de entrada e saída da API
- `config`: configurações gerais da aplicação

Estrutura base:

```bash
src/main/java/com/pitsdog/api
│
├── adicional
│   ├── controller
│   ├── dto
│   ├── entity
│   ├── repository
│   └── service
│
├── categoria
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

---

## ⚙️ Funcionalidades Implementadas

### 📁 Categorias

Funcionalidades relacionadas ao gerenciamento das categorias do cardápio.

- Criar categoria
- Listar categorias
- Buscar categoria por ID
- Atualizar categoria
- Deletar categoria
- Controlar categoria ativa/inativa
- Definir ordem de exibição
- Definir imagem da categoria
- Definir descrição da categoria

Campos principais:

```json
{
  "nome": "Hambúrgueres",
  "descricao": "Categoria de hambúrgueres artesanais",
  "ativo": true,
  "ordem": 1,
  "imageUrl": "https://exemplo.com/imagem.png"
}
```

---

### 🍔 Produtos

Funcionalidades relacionadas ao gerenciamento dos produtos vendidos pela hamburgueria.

- Criar produto
- Listar produtos
- Buscar produto por ID
- Atualizar produto
- Deletar produto
- Relacionar produto com categoria
- Controlar disponibilidade
- Definir preço
- Definir imagem
- Definir descrição
- Preparação para exibição no cardápio digital

Campos principais:

```json
{
  "nome": "X-Bacon",
  "descricao": "Hambúrguer com bacon, queijo e molho especial",
  "preco": 22.90,
  "disponivel": true,
  "imageUrl": "https://exemplo.com/x-bacon.png",
  "categoriaId": 1
}
```

---

### ➕ Adicionais

Funcionalidades relacionadas aos adicionais que podem ser colocados nos produtos.

Exemplos:

- Bacon extra
- Cheddar
- Ovo
- Carne extra
- Molho especial

Funcionalidades:

- Criar adicional
- Listar adicionais
- Buscar adicional por ID
- Atualizar adicional
- Deletar adicional
- Controlar adicional ativo/inativo
- Associar adicionais aos itens do pedido
- Somar valor dos adicionais no total do pedido

Campos principais:

```json
{
  "nome": "Bacon extra",
  "descricao": "Porção extra de bacon",
  "preco": 4.00,
  "ativo": true
}
```

---

### 🧾 Pedidos

Funcionalidades relacionadas ao fluxo principal de venda.

A API permite criar pedidos com diferentes tipos:

- Entrega
- Retirada
- Mesa

Funcionalidades implementadas:

- Criar pedido
- Adicionar produtos ao pedido
- Adicionar adicionais aos itens
- Atualizar quantidade de item
- Atualizar adicionais de um item
- Remover item do pedido
- Calcular subtotal
- Calcular taxa de entrega
- Calcular desconto
- Calcular total
- Gerar número do pedido
- Controlar status do pedido
- Retornar resposta detalhada do pedido

---

## 🧮 Regras de Negócio

A API possui regras internas para manter o pedido consistente.

Entre elas:

- Um pedido pode possuir vários itens
- Um item pertence a um produto
- Um item pode possuir vários adicionais
- O valor dos adicionais entra no cálculo do item
- O subtotal considera produtos, quantidades e adicionais
- A taxa de entrega é somada apenas quando o pedido for do tipo entrega
- Descontos podem ser aplicados ao pedido
- O total final é calculado automaticamente
- Produtos e adicionais precisam existir antes de serem associados ao pedido
- O pedido retorna um DTO de resposta com as informações organizadas

Exemplo de cálculo:

```text
Valor do item = preço do produto + soma dos adicionais

Subtotal = soma de todos os itens * quantidade

Total = subtotal + taxa de entrega - desconto
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
- HTTP Status Codes
- Organização por domínio
- Uso de variáveis de ambiente
- Configuração para produção
- Documentação com Swagger
- Deploy em ambiente cloud

---

## 📡 Endpoints

> Os endpoints podem variar conforme a divisão entre rotas públicas e administrativas.

### 📁 Categorias

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/admin/categorias` | Cria uma nova categoria |
| `GET` | `/categorias` | Lista todas as categorias |
| `GET` | `/categorias/{id}` | Busca uma categoria por ID |
| `PUT` | `/admin/categorias/{id}` | Atualiza uma categoria |
| `DELETE` | `/admin/categorias/{id}` | Remove uma categoria |

### 🍔 Produtos

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/admin/produtos` | Cria um novo produto |
| `GET` | `/produtos` | Lista todos os produtos |
| `GET` | `/produtos/{id}` | Busca um produto por ID |
| `PUT` | `/admin/produtos/{id}` | Atualiza um produto |
| `DELETE` | `/admin/produtos/{id}` | Remove um produto |

### ➕ Adicionais

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/admin/adicionais` | Cria um novo adicional |
| `GET` | `/adicionais` | Lista todos os adicionais |
| `GET` | `/adicionais/{id}` | Busca um adicional por ID |
| `PUT` | `/admin/adicionais/{id}` | Atualiza um adicional |
| `DELETE` | `/admin/adicionais/{id}` | Remove um adicional |

### 🧾 Pedidos

| Método | Rota | Descrição |
|---|---|---|
| `POST` | `/pedidos` | Cria um novo pedido |
| `GET` | `/pedidos` | Lista pedidos |
| `GET` | `/pedidos/{id}` | Busca pedido por ID |
| `POST` | `/pedidos/{pedidoId}/itens` | Adiciona item ao pedido |
| `PATCH` | `/pedidos/{pedidoId}/itens/{itemId}/quantidade` | Atualiza quantidade do item |
| `PATCH` | `/pedidos/{pedidoId}/itens/{itemId}/adicionais` | Atualiza adicionais do item |
| `DELETE` | `/pedidos/{pedidoId}/itens/{itemId}` | Remove item do pedido |

---

## 📦 Exemplos de Requisições

### Criar categoria

```http
POST /admin/categorias
Content-Type: application/json
```

```json
{
  "nome": "Hambúrgueres",
  "descricao": "Lanches artesanais da casa",
  "ativo": true,
  "ordem": 1,
  "imageUrl": "https://exemplo.com/hamburgueres.png"
}
```

### Criar produto

```http
POST /admin/produtos
Content-Type: application/json
```

```json
{
  "nome": "X-Bacon",
  "descricao": "Pão, carne, queijo, bacon e molho especial",
  "preco": 22.90,
  "disponivel": true,
  "imageUrl": "https://exemplo.com/x-bacon.png",
  "categoriaId": 1
}
```

### Criar adicional

```http
POST /admin/adicionais
Content-Type: application/json
```

```json
{
  "nome": "Cheddar extra",
  "descricao": "Porção extra de cheddar cremoso",
  "preco": 3.50,
  "ativo": true
}
```

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
      "produtoId": 1,
      "quantidade": 2,
      "observacao": "Sem cebola",
      "adicionaisIds": [1, 2]
    }
  ]
}
```

### Criar pedido para retirada

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
  "itens": [
    {
      "produtoId": 1,
      "quantidade": 1,
      "observacao": "Caprichar no molho",
      "adicionaisIds": [1]
    }
  ]
}
```

### Criar pedido de mesa

```http
POST /pedidos
Content-Type: application/json
```

```json
{
  "tipoPedido": "MESA",
  "numeroMesa": 5,
  "formaPagamento": "CARTAO",
  "itens": [
    {
      "produtoId": 2,
      "quantidade": 1,
      "observacao": "Bem passado",
      "adicionaisIds": []
    }
  ]
}
```

### Atualizar quantidade de item

```http
PATCH /pedidos/{pedidoId}/itens/{itemId}/quantidade
Content-Type: application/json
```

```json
{
  "quantidade": 3
}
```

### Atualizar adicionais de item

```http
PATCH /pedidos/{pedidoId}/itens/{itemId}/adicionais
Content-Type: application/json
```

```json
{
  "adicionaisIds": [1, 3, 4]
}
```

---

## 🔐 Variáveis de Ambiente

Para evitar exposição de dados sensíveis, o projeto utiliza variáveis de ambiente.

Exemplo:

```properties
spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}
```

Variáveis necessárias:

| Variável | Descrição |
|---|---|
| `DATABASE_URL` | URL de conexão com o banco PostgreSQL |
| `DATABASE_USERNAME` | Usuário do banco de dados |
| `DATABASE_PASSWORD` | Senha do banco de dados |
| `PORT` | Porta usada no ambiente de deploy, se necessário |

Exemplo local:

```properties
DATABASE_URL=jdbc:postgresql://localhost:5432/pitsdog
DATABASE_USERNAME=postgres
DATABASE_PASSWORD=sua_senha
```

---

## ⚠️ Segurança

Nunca envie senhas, tokens ou URLs privadas do banco para o GitHub.

Arquivos que devem ficar fora do versionamento:

```bash
application.properties
application.yml
application-dev.properties
application-prod.properties
.env
```

Recomenda-se manter essas configurações no `.gitignore`.

Exemplo:

```gitignore
# Configurações da aplicação
src/main/resources/application.properties
src/main/resources/application.yml
src/main/resources/application-dev.properties
src/main/resources/application-prod.properties

# Variáveis locais
.env
```

Caso alguma senha ou URL sensível seja enviada para o GitHub por engano, é recomendado:

1. Revogar ou trocar imediatamente a senha exposta
2. Atualizar as variáveis de ambiente no Railway/Supabase
3. Remover a credencial do código
4. Garantir que o arquivo sensível esteja no `.gitignore`
5. Fazer novo commit sem a credencial exposta

---

## 🛠️ Como Rodar o Projeto

### 1. Clone o repositório

```bash
git clone https://github.com/EmanoelCavalcante/pitsdog-api.git
```

### 2. Entre na pasta do projeto

```bash
cd pitsdog-api
```

### 3. Configure o banco de dados

Configure as variáveis de ambiente ou crie um arquivo local de configuração.

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

Também é possível rodar diretamente pela IDE.

---

## 📖 Swagger

A API possui documentação via Swagger.

Com o projeto rodando, acesse:

```http
http://localhost:8080/swagger-ui/index.html
```

ou:

```http
http://localhost:8080/swagger-ui.html
```

O Swagger permite:

- Visualizar endpoints
- Testar requisições
- Ver parâmetros esperados
- Ver modelos de request e response
- Facilitar integração com frontend

---

## ☁️ Deploy

O projeto está preparado para deploy na Railway.

Principais pontos configurados:

- Uso de PostgreSQL em produção
- Variáveis de ambiente
- Build com Maven
- Deploy a partir do GitHub
- Configuração externa do banco
- Preparação para execução em ambiente cloud

Fluxo geral do deploy:

1. Subir o projeto no GitHub
2. Conectar o repositório na Railway
3. Configurar as variáveis de ambiente
4. Configurar banco PostgreSQL
5. Realizar build com Maven
6. Executar a aplicação em produção
7. Acessar a API pela URL gerada pela Railway

---

## 🧪 Testes

A API pode ser testada usando:

- Swagger UI
- Postman
- Insomnia
- Scripts automatizados com Python
- Pytest

Exemplo de execução com Maven:

```bash
./mvnw test
```

Exemplo de testes externos com Python:

```bash
pytest
```

---

## 🧾 Fluxo de Teste Manual

Uma ordem recomendada para testar a API:

1. Criar categorias
2. Criar produtos vinculados às categorias
3. Criar adicionais
4. Criar pedido com produto e adicionais
5. Atualizar quantidade de um item
6. Atualizar adicionais de um item
7. Remover item do pedido
8. Conferir cálculo final do pedido

---

## 🗺️ Roadmap

### Concluído

- [x] Criar estrutura base do projeto
- [x] Configurar Spring Boot
- [x] Configurar banco PostgreSQL
- [x] Criar módulo de categorias
- [x] Criar módulo de produtos
- [x] Criar módulo de adicionais
- [x] Criar módulo de pedidos
- [x] Implementar itens do pedido
- [x] Implementar cálculo de subtotal
- [x] Implementar cálculo de adicionais
- [x] Implementar taxa de entrega
- [x] Implementar desconto no pedido
- [x] Separar controllers de admin e usuário
- [x] Criar documentação Swagger
- [x] Preparar deploy no Railway
- [x] Usar variáveis de ambiente

### Em desenvolvimento

- [ ] Melhorar tratamento global de erros
- [ ] Criar autenticação JWT
- [ ] Criar controle de usuários
- [ ] Criar controle de permissões admin
- [ ] Melhorar validações dos DTOs
- [ ] Criar respostas padronizadas de erro
- [ ] Criar testes automatizados de integração
- [ ] Criar dashboard financeiro
- [ ] Integrar com frontend React/TypeScript

### Futuras funcionalidades

- [ ] Integração com gateway de pagamento
- [ ] Integração com Mercado Pago
- [ ] Upload de imagens
- [ ] Relatórios financeiros
- [ ] Controle de caixa
- [ ] Controle de status em tempo real
- [ ] Notificações para pedidos
- [ ] Integração com bot de atendimento
- [ ] Docker Compose
- [ ] CI/CD com GitHub Actions
- [ ] Deploy definitivo em VPS ou cloud

---

## 🤝 Integração com Frontend

A API foi pensada para ser consumida por um frontend moderno.

Possíveis integrações:

- Painel administrativo em React/TypeScript
- Página pública de cardápio
- Sistema de pedidos online
- Dashboard financeiro
- Aplicação mobile futuramente

O frontend poderá consumir a API para:

- Listar categorias
- Listar produtos
- Exibir adicionais disponíveis
- Criar pedidos
- Atualizar itens de pedido
- Gerenciar produtos
- Gerenciar categorias
- Gerenciar adicionais
- Acompanhar pedidos
- Exibir métricas financeiras

---

## 📊 Possíveis Módulos Futuros

### Dashboard Administrativo

- Total de vendas
- Pedidos por período
- Produtos mais vendidos
- Ticket médio
- Faturamento diário, semanal e mensal

### Sistema de Pagamento

- Pix
- Dinheiro
- Cartão
- Mercado Pago
- Confirmação de pagamento

### Autenticação

- Login de administrador
- Cadastro de usuários
- JWT
- Rotas protegidas
- Controle de permissões

### Pedidos em Tempo Real

- Atualização de status
- Pedido recebido
- Pedido em preparo
- Pedido saiu para entrega
- Pedido finalizado
- Pedido cancelado

---

## 📦 Status do Projeto

O projeto está atualmente em desenvolvimento ativo.

Status atual:

```text
Backend principal funcional
Módulos principais criados
Deploy inicial realizado
Swagger configurado
Banco PostgreSQL conectado
API preparada para integração com frontend
```

---

## ⚠️ Atenção Antes de Usar

Antes de executar ou contribuir com o projeto, verifique os seguintes pontos:

### Pacote base da aplicação

Confirme se o pacote principal do projeto está como:

```bash
com.pitsdog.api
```

Caso o projeto ainda esteja usando outro pacote, como:

```bash
com.pitsdogdelivery.pitsdogapi
```

ajuste a documentação ou refatore os pacotes conforme a estrutura atual do código.

### Rotas administrativas

Confirme se as rotas administrativas estão usando o prefixo:

```http
/admin
```

Exemplos:

```http
/admin/categorias
/admin/produtos
/admin/adicionais
```

Caso os controllers ainda estejam sem esse prefixo, ajuste os endpoints no README ou nos controllers.

### Campo de adicionais no pedido

Confirme se o DTO do pedido utiliza o campo:

```json
"adicionaisIds"
```

Caso esteja diferente, como:

```json
"adicionaisId"
```

ajuste o README ou o DTO para manter consistência entre documentação e código.

---

## 👨‍💻 Desenvolvedor

Desenvolvido por **Emanoel Cavalcante**

GitHub:

```text
https://github.com/EmanoelCavalcante
```

---

## ⭐ Considerações Finais

A **PitsDog API** é um projeto real em evolução, desenvolvido com foco em aprendizado prático, aplicação comercial e construção de portfólio profissional.

O sistema continuará recebendo melhorias conforme novas necessidades surgirem, incluindo autenticação, dashboard financeiro, integração com frontend, pagamentos e automações.
