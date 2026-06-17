🍔 PitsDog API

API RESTful desenvolvida em Java 21 com Spring Boot para gerenciamento do sistema de delivery da hamburgueria PitsDog.

O projeto foi criado com foco em organização, escalabilidade, boas práticas de backend e preparação para uso real em produção, com integração entre site público, painel administrativo, banco PostgreSQL no Supabase, upload de imagens no Supabase Storage e deploy na Railway.

⸻

📚 Sumário

* Sobre o Projeto
* Objetivos
* Tecnologias Utilizadas
* Arquitetura
* Funcionalidades Implementadas
* Regras de Negócio
* Status dos Pedidos
* Pagamentos
* Upload de Imagens
* Endpoints
* Exemplos de Requisições
* Variáveis de Ambiente
* Segurança
* CORS
* Como Rodar o Projeto
* Swagger
* Deploy
* Testes
* Integração com Frontend
* Status do Projeto
* Atenção Antes de Usar
* Desenvolvedor
* Considerações Finais

⸻

📌 Sobre o Projeto

A PitsDog API é o backend principal de um sistema de delivery voltado para uma hamburgueria.

Ela permite o gerenciamento completo de:

* Categorias
* Produtos
* Adicionais
* Combos
* Pedidos
* Pagamentos manuais
* Status da loja
* Upload de imagens
* Painel administrativo
* Cardápio público

O backend serve como base para dois ambientes principais:

* Site público, usado pelos clientes para visualizar o cardápio e realizar pedidos.
* Painel administrativo, usado pela equipe da hamburgueria para gerenciar pedidos, produtos, combos, adicionais, categorias e operação da loja.

O projeto está sendo desenvolvido de forma incremental, mantendo uma estrutura limpa, organizada e preparada para crescimento.

⸻

🎯 Objetivos

Os principais objetivos da API são:

* Centralizar a lógica de pedidos da hamburgueria
* Permitir cadastro e gerenciamento de produtos, categorias, adicionais e combos
* Permitir criação de pedidos online
* Permitir pedidos dos tipos ENTREGA, RETIRADA e MESA
* Calcular valores automaticamente
* Controlar status dos pedidos
* Controlar pagamentos manuais
* Permitir upload e armazenamento de imagens
* Separar rotas públicas e administrativas
* Proteger o painel administrativo com JWT
* Facilitar integração com frontend web
* Preparar o sistema para deploy real
* Servir como projeto comercial e portfólio profissional em Java/Spring Boot

⸻

🚀 Tecnologias Utilizadas

Backend

* Java 21
* Spring Boot
* Spring Web
* Spring Security
* Spring Data JPA
* Hibernate
* Bean Validation
* JWT
* Maven

Banco de Dados

* PostgreSQL
* Supabase PostgreSQL
* Supabase Pooler
* HikariCP

Storage

* Supabase Storage
* Upload de imagens via API

Documentação

* Swagger
* Springdoc OpenAPI

Deploy e Infraestrutura

* Railway
* Supabase
* Netlify
* Render
* Variáveis de ambiente
* Maven Wrapper

Ferramentas de Desenvolvimento

* Git
* GitHub
* IntelliJ IDEA
* Postman
* Insomnia
* Swagger UI
* Python/Pytest para testes externos

⸻

🧱 Arquitetura

O projeto segue uma arquitetura em camadas, com separação por domínio.

Cada módulo possui suas próprias responsabilidades:

* controller: camada responsável por receber requisições HTTP
* service: camada responsável pelas regras de negócio
* repository: camada de acesso ao banco de dados
* entity: representação das tabelas do banco
* dto: objetos de entrada e saída da API
* enums: tipos fixos usados nas regras do sistema
* config: configurações gerais da aplicação
* exception: tratamento de erros específicos e globais

Estrutura base:

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

⸻

⚙️ Funcionalidades Implementadas

📁 Categorias

Funcionalidades relacionadas ao gerenciamento das categorias do cardápio.

* Criar categoria
* Listar categorias
* Buscar categoria por ID
* Atualizar categoria
* Deletar categoria
* Ativar e desativar categoria
* Definir ordem de exibição
* Definir descrição
* Definir URL de imagem
* Exibir categoria no cardápio público

Campos principais:

{
  "nome": "Hambúrgueres",
  "descricao": "Categoria de hambúrgueres artesanais",
  "ativo": true,
  "ordem": 1,
  "imagemUrl": "https://exemplo.com/imagem.png"
}

⸻

🍔 Produtos

Funcionalidades relacionadas ao gerenciamento dos produtos vendidos pela hamburgueria.

* Criar produto
* Listar produtos
* Buscar produto por ID
* Atualizar produto
* Deletar produto
* Relacionar produto com categoria
* Controlar disponibilidade
* Definir preço
* Definir imagem
* Definir descrição
* Exibir produto no cardápio público

Campos principais:

{
  "nome": "X-Bacon",
  "descricao": "Hambúrguer com bacon, queijo e molho especial",
  "preco": 22.90,
  "disponivel": true,
  "imagemUrl": "https://exemplo.com/x-bacon.png",
  "categoriaId": 1
}

⸻

➕ Adicionais

Funcionalidades relacionadas aos adicionais que podem ser colocados nos produtos.

Exemplos:

* Bacon extra
* Cheddar
* Ovo
* Carne extra
* Molho especial
* Queijo
* Milho
* Cebola

Funcionalidades:

* Criar adicional
* Listar adicionais
* Buscar adicional por ID
* Atualizar adicional
* Deletar adicional
* Controlar adicional ativo/inativo
* Definir preço
* Definir imagem
* Associar adicionais aos itens do pedido
* Somar valor dos adicionais no total do pedido

Campos principais:

{
  "nome": "Bacon extra",
  "descricao": "Porção extra de bacon",
  "preco": 4.00,
  "ativo": true,
  "imagemUrl": "https://exemplo.com/bacon.png"
}

⸻

🍱 Combos

Funcionalidades relacionadas aos combos vendidos pela hamburgueria.

* Criar combo
* Listar combos
* Buscar combo por ID
* Atualizar combo
* Deletar combo
* Controlar disponibilidade
* Definir preço
* Definir descrição
* Definir imagem
* Exibir combo no cardápio público
* Permitir pedido com item do tipo combo

Campos principais:

{
  "nome": "Combo Casal",
  "descricao": "Combo com dois hambúrgueres, batata e refrigerante",
  "preco": 59.90,
  "disponivel": true,
  "imagemUrl": "https://exemplo.com/combo-casal.png"
}

⸻

🧾 Pedidos

Funcionalidades relacionadas ao fluxo principal de venda.

A API permite criar pedidos com diferentes tipos:

* ENTREGA
* RETIRADA
* MESA

Funcionalidades implementadas:

* Criar pedido
* Criar pedido com produtos
* Criar pedido com combos
* Adicionar adicionais aos itens
* Calcular subtotal
* Calcular taxa de entrega
* Calcular total
* Gerar informações do pedido
* Controlar status do pedido
* Editar pedido pelo painel administrativo
* Cancelar pedido
* Restaurar pedido cancelado
* Listar pedidos no painel admin com paginação
* Filtrar pedidos por status, tipo e data
* Separar pedidos em andamento dos finalizados

⸻

🏪 Loja

Funcionalidades relacionadas ao status operacional da loja.

* Verificar se a loja está aberta
* Controlar se aceita entrega
* Controlar se aceita retirada
* Controlar se aceita pedidos de mesa
* Retornar mensagem operacional para o frontend
* Permitir que o site bloqueie pedidos quando a loja estiver fechada

Rota principal:

GET /loja/status

⸻

🖼️ Upload de Imagens

Funcionalidades relacionadas ao upload de imagens no Supabase Storage.

* Upload de imagens via API
* Validação de tipo de arquivo
* Validação de tamanho máximo
* Integração com Supabase Storage
* Retorno de URL pública da imagem
* Uso da URL em produtos, categorias, adicionais e combos

Rota principal:

POST /upload/imagem

Formatos aceitos:

image/jpeg
image/png

Tamanho máximo:

5MB

⸻

🧮 Regras de Negócio

A API possui regras internas para manter pedidos, pagamentos e itens consistentes.

Entre elas:

* Um pedido pode possuir vários itens
* Um item pode ser um produto ou um combo
* Um item pode possuir vários adicionais
* O valor dos adicionais entra no cálculo do item
* O subtotal considera produtos, combos, quantidades e adicionais
* A taxa de entrega é somada apenas quando o pedido for do tipo ENTREGA
* O total final é calculado automaticamente
* Produtos, combos e adicionais precisam existir antes de serem associados ao pedido
* Produtos indisponíveis não devem ser vendidos
* Combos indisponíveis não devem ser vendidos
* Adicionais inativos não devem ser utilizados
* Pedido de mesa não exige telefone obrigatório
* Pedido cancelado não pode ser editado normalmente
* Apenas o admin pode restaurar pedido cancelado
* Pagamento pode ser confirmado ou ter confirmação cancelada manualmente
* Pedidos finalizados são separados dos pedidos em andamento no painel

Exemplo de cálculo:

Valor do item = preço do produto ou combo + soma dos adicionais
Subtotal = soma de todos os itens considerando quantidade
Total = subtotal + taxa de entrega

⸻

🔄 Status dos Pedidos

Os pedidos possuem um fluxo controlado por status.

Status disponíveis:

AGUARDANDO_APROVACAO
EM_PREPARO
PRONTO_PARA_RETIRADA
SAIU_PARA_ENTREGA
CONCLUIDO
FINALIZADO
CANCELADO

Regras importantes:

* Todo pedido novo inicia como AGUARDANDO_APROVACAO
* O admin pode aprovar e movimentar o pedido
* Pedido de retirada pode ir para PRONTO_PARA_RETIRADA
* Pedido de entrega pode ir para SAIU_PARA_ENTREGA
* Pedido concluído pode ser marcado como CONCLUIDO
* Pedido encerrado no painel pode ser marcado como FINALIZADO
* Pedido cancelado fica bloqueado para edição comum
* Pedido cancelado pode ser restaurado pelo admin

⸻

💰 Pagamentos

O sistema possui controle manual de pagamento.

Formas de pagamento suportadas:

DINHEIRO
PIX
CARTAO_CREDITO
CARTAO_DEBITO

Status de pagamento:

PENDENTE
CONFIRMADO
CANCELADO

Funcionalidades:

* Definir forma de pagamento no pedido
* Confirmar pagamento pelo painel admin
* Cancelar confirmação de pagamento
* Registrar troco para pagamento em dinheiro
* Controlar status de pagamento separado do status do pedido

Rotas principais:

PATCH /admin/pedidos/{id}/pagamento/confirmar
PATCH /admin/pedidos/{id}/pagamento/cancelar-confirmacao

⸻

🧠 Conceitos Aplicados

Durante o desenvolvimento foram aplicados conceitos importantes de backend:

* REST API
* Arquitetura em camadas
* DTO Pattern
* Repository Pattern
* Service Layer
* Injeção de Dependência
* Separação de responsabilidades
* Validação de dados
* Relacionamentos com JPA
* Paginação
* Filtros dinâmicos
* HTTP Status Codes
* Tratamento global de exceções
* Autenticação com JWT
* Proteção de rotas administrativas
* Configuração de CORS
* Upload de arquivos
* Integração com serviço externo de storage
* Uso de variáveis de ambiente
* Configuração para produção
* Documentação com Swagger
* Deploy em ambiente cloud

⸻

📡 Endpoints

Os endpoints são divididos entre rotas públicas e rotas administrativas.

⸻

🌐 Rotas Públicas

Método	Rota	Descrição
GET	/loja/status	Consulta o status operacional da loja
GET	/categorias	Lista categorias públicas
GET	/produtos	Lista produtos públicos
GET	/adicionais	Lista adicionais públicos
GET	/combos	Lista combos públicos
GET	/cardapio	Retorna o cardápio completo
POST	/pedidos	Cria um novo pedido
GET	/pedidos/{id}	Consulta um pedido por ID

⸻

🔐 Autenticação

Método	Rota	Descrição
POST	/auth/login	Realiza login administrativo e retorna JWT

⸻

🧾 Pedidos Admin

Método	Rota	Descrição
GET	/admin/pedidos	Lista pedidos com paginação e filtros
GET	/admin/pedidos/{id}	Busca pedido por ID
PATCH	/admin/pedidos/{id}/status	Atualiza status do pedido
PUT	/admin/pedidos/{id}	Edita dados do pedido
PATCH	/admin/pedidos/{id}/pagamento/confirmar	Confirma pagamento
PATCH	/admin/pedidos/{id}/pagamento/cancelar-confirmacao	Cancela confirmação de pagamento
PATCH	/admin/pedidos/{id}/restaurar	Restaura pedido cancelado

⸻

📁 Categorias Admin

Método	Rota	Descrição
POST	/admin/categorias	Cria uma nova categoria
GET	/admin/categorias	Lista categorias no painel admin
PUT	/admin/categorias/{id}	Atualiza uma categoria
DELETE	/admin/categorias/{id}	Remove uma categoria
PATCH	/admin/categorias/{id}/status	Ativa ou desativa uma categoria

⸻

🍔 Produtos Admin

Método	Rota	Descrição
POST	/admin/produtos	Cria um novo produto
GET	/admin/produtos	Lista produtos no painel admin
GET	/admin/produtos/{id}	Busca produto por ID
PUT	/admin/produtos/{id}	Atualiza um produto
DELETE	/admin/produtos/{id}	Remove um produto

⸻

➕ Adicionais Admin

Método	Rota	Descrição
POST	/admin/adicionais	Cria um novo adicional
GET	/admin/adicionais	Lista adicionais no painel admin
GET	/admin/adicionais/{id}	Busca adicional por ID
PUT	/admin/adicionais/{id}	Atualiza um adicional
DELETE	/admin/adicionais/{id}	Remove um adicional

⸻

🍱 Combos Admin

Método	Rota	Descrição
POST	/admin/combos	Cria um novo combo
GET	/admin/combos	Lista combos no painel admin
GET	/admin/combos/{id}	Busca combo por ID
PUT	/admin/combos/{id}	Atualiza um combo
DELETE	/admin/combos/{id}	Remove um combo

⸻

🖼️ Upload

Método	Rota	Descrição
POST	/upload/imagem	Faz upload de imagem para o Supabase Storage

⸻

📦 Exemplos de Requisições

Login administrativo

POST /auth/login
Content-Type: application/json
{
  "email": "admin@email.com",
  "senha": "sua_senha"
}

Resposta esperada:

{
  "token": "jwt_gerado_pela_api",
  "tipo": "Bearer"
}

⸻

Criar categoria

POST /admin/categorias
Authorization: Bearer SEU_TOKEN_AQUI
Content-Type: application/json
{
  "nome": "Hambúrgueres",
  "descricao": "Lanches artesanais da casa",
  "ativo": true,
  "ordem": 1,
  "imagemUrl": "https://exemplo.com/hamburgueres.png"
}

⸻

Criar produto

POST /admin/produtos
Authorization: Bearer SEU_TOKEN_AQUI
Content-Type: application/json
{
  "nome": "X-Bacon",
  "descricao": "Pão, carne, queijo, bacon e molho especial",
  "preco": 22.90,
  "disponivel": true,
  "imagemUrl": "https://exemplo.com/x-bacon.png",
  "categoriaId": 1
}

⸻

Criar adicional

POST /admin/adicionais
Authorization: Bearer SEU_TOKEN_AQUI
Content-Type: application/json
{
  "nome": "Cheddar extra",
  "descricao": "Porção extra de cheddar cremoso",
  "preco": 3.50,
  "ativo": true,
  "imagemUrl": "https://exemplo.com/cheddar.png"
}

⸻

Criar pedido para entrega

POST /pedidos
Content-Type: application/json
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

⸻

Criar pedido com combo

POST /pedidos
Content-Type: application/json
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

⸻

Criar pedido de mesa

POST /pedidos
Content-Type: application/json
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

⸻

Atualizar status do pedido

PATCH /admin/pedidos/{id}/status
Authorization: Bearer SEU_TOKEN_AQUI
Content-Type: application/json
{
  "status": "EM_PREPARO"
}

⸻

Confirmar pagamento

PATCH /admin/pedidos/{id}/pagamento/confirmar
Authorization: Bearer SEU_TOKEN_AQUI
Content-Type: application/json
{
  "formaPagamento": "DINHEIRO",
  "trocoPara": 100.00
}

⸻

🔐 Variáveis de Ambiente

Para evitar exposição de dados sensíveis, o projeto utiliza variáveis de ambiente.

Banco de dados

spring.datasource.url=${DATABASE_URL}
spring.datasource.username=${DATABASE_USERNAME}
spring.datasource.password=${DATABASE_PASSWORD}

Variáveis necessárias

Variável	Descrição
DATABASE_URL	URL de conexão com o banco PostgreSQL
DATABASE_USERNAME	Usuário do banco de dados
DATABASE_PASSWORD	Senha do banco de dados
PORT	Porta usada no ambiente de deploy
ADMIN_EMAIL	Email do administrador
ADMIN_PASSWORD_HASH	Hash BCrypt da senha do administrador
JWT_EXPIRATION_SECONDS	Tempo de expiração do token JWT
APP_CORS_ALLOWED_ORIGINS	Lista de origens permitidas no CORS
SUPABASE_URL	URL do projeto Supabase
SUPABASE_SERVICE_ROLE_KEY	Chave service role do Supabase
SUPABASE_BUCKET	Bucket usado para armazenar imagens

Exemplo local:

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

⸻

⚠️ Segurança

Nunca envie senhas, tokens, hashes, chaves privadas ou URLs sensíveis do banco para o GitHub.

Arquivos que devem ficar fora do versionamento:

application.properties
application.yml
application-dev.properties
application-prod.properties
.env

Recomenda-se manter essas configurações no .gitignore.

Exemplo:

# Configurações da aplicação
src/main/resources/application.properties
src/main/resources/application.yml
src/main/resources/application-dev.properties
src/main/resources/application-prod.properties
# Variáveis locais
.env

Caso alguma senha, token ou URL sensível seja enviada para o GitHub por engano, é recomendado:

1. Revogar ou trocar imediatamente a credencial exposta
2. Atualizar as variáveis de ambiente no Railway/Supabase
3. Remover a credencial do código
4. Garantir que o arquivo sensível esteja no .gitignore
5. Fazer novo commit sem a credencial exposta

⸻

🌐 CORS

A API possui configuração de CORS para permitir acesso do frontend local e dos ambientes publicados.

Exemplos de origens usadas no projeto:

http://localhost:5173
http://localhost:5174
http://localhost:3000
https://pitsdog-site.netlify.app
https://pitsdog-painel-admin.onrender.com

As origens permitidas devem ser configuradas na variável:

APP_CORS_ALLOWED_ORIGINS=

Exemplo:

APP_CORS_ALLOWED_ORIGINS=http://localhost:5173,http://localhost:5174,http://localhost:3000,https://pitsdog-site.netlify.app,https://pitsdog-painel-admin.onrender.com

⸻

🛠️ Como Rodar o Projeto

1. Clone o repositório

git clone https://github.com/EmanoelCavalcante/pitsdog-api.git

2. Entre na pasta do projeto

cd pitsdog-api

3. Configure as variáveis de ambiente

Configure as variáveis necessárias para banco de dados, autenticação, CORS e Supabase Storage.

Exemplo com PostgreSQL local:

spring.datasource.url=jdbc:postgresql://localhost:5432/pitsdog
spring.datasource.username=postgres
spring.datasource.password=sua_senha
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
spring.jpa.properties.hibernate.format_sql=true

4. Execute a aplicação

No Windows:

./mvnw.cmd spring-boot:run

No Linux/Mac:

./mvnw spring-boot:run

Também é possível rodar diretamente pela IDE.

⸻

📖 Swagger

A API possui documentação via Swagger.

Com o projeto rodando, acesse:

http://localhost:8080/swagger-ui/index.html

ou:

http://localhost:8080/swagger-ui.html

Documentação OpenAPI:

http://localhost:8080/v3/api-docs

O Swagger permite:

* Visualizar endpoints
* Testar requisições
* Ver parâmetros esperados
* Ver modelos de request e response
* Facilitar integração com frontend

⸻

☁️ Deploy

O projeto está preparado para deploy na Railway.

Principais pontos configurados:

* Uso de PostgreSQL em produção
* Banco hospedado no Supabase
* Uso de Supabase Pooler
* Upload de imagens com Supabase Storage
* Variáveis de ambiente
* Build com Maven
* Deploy a partir do GitHub
* Configuração externa do banco
* Configuração de CORS para frontend publicado
* Execução usando a variável PORT

Configuração de porta:

server.port=${PORT:8080}

Configuração recomendada do HikariCP para Railway/Supabase:

spring.datasource.hikari.minimum-idle=0
spring.datasource.hikari.maximum-pool-size=3
spring.datasource.hikari.idle-timeout=30000
spring.datasource.hikari.max-lifetime=240000
spring.datasource.hikari.connection-timeout=30000
spring.datasource.hikari.keepalive-time=0

Fluxo geral do deploy:

1. Subir o projeto no GitHub
2. Conectar o repositório na Railway
3. Configurar as variáveis de ambiente
4. Configurar banco PostgreSQL do Supabase
5. Configurar Supabase Storage
6. Realizar build com Maven
7. Executar a aplicação em produção
8. Acessar a API pela URL gerada pela Railway

⸻

🧪 Testes

A API pode ser testada usando:

* Swagger UI
* Postman
* Insomnia
* Scripts automatizados com Python
* Pytest

Exemplo de execução com Maven:

./mvnw test

Exemplo de testes externos com Python:

pytest

⸻

🧾 Fluxo de Teste Manual

Uma ordem recomendada para testar a API:

1. Verificar status da loja em /loja/status
2. Criar ou listar categorias
3. Criar ou listar produtos
4. Criar ou listar adicionais
5. Criar ou listar combos
6. Consultar o cardápio em /cardapio
7. Criar pedido do tipo RETIRADA
8. Criar pedido do tipo ENTREGA
9. Criar pedido do tipo MESA
10. Fazer login administrativo
11. Listar pedidos no painel admin
12. Atualizar status do pedido
13. Confirmar pagamento
14. Cancelar pedido
15. Restaurar pedido cancelado
16. Finalizar pedido
17. Testar upload de imagem
18. Conferir se a URL da imagem foi salva corretamente

⸻

🤝 Integração com Frontend

A API foi pensada para ser consumida por um frontend moderno.

Ambientes usados no projeto:

* Site público em Netlify
* Painel administrativo em Render
* Frontend local em Vite

O frontend público consome principalmente:

GET /loja/status
GET /cardapio
POST /pedidos
GET /pedidos/{id}

O painel administrativo consome principalmente:

POST /auth/login
GET /admin/pedidos
PATCH /admin/pedidos/{id}/status
PATCH /admin/pedidos/{id}/pagamento/confirmar
GET /admin/produtos
GET /admin/categorias
GET /admin/adicionais
GET /admin/combos
POST /upload/imagem

Na listagem de pedidos administrativos, o retorno é paginado.

O frontend deve consumir os pedidos dentro do campo:

{
  "content": []
}

⸻

📦 Status do Projeto

O backend principal está funcional e preparado para integração final com o frontend.

Status atual:

Backend principal funcional
Módulos principais criados
JWT implementado
Rotas admin protegidas
Deploy configurado na Railway
Banco PostgreSQL conectado no Supabase
Supabase Storage integrado
Upload de imagens implementado
Swagger configurado
CORS configurado
Paginação de pedidos implementada
Filtros administrativos implementados
Controle manual de pagamento implementado
API preparada para integração final com frontend

⸻

🗺️ Roadmap

Concluído

* Criar estrutura base do projeto
* Configurar Spring Boot
* Configurar banco PostgreSQL
* Criar módulo de categorias
* Criar módulo de produtos
* Criar módulo de adicionais
* Criar módulo de combos
* Criar módulo de pedidos
* Implementar criação de pedidos
* Implementar pedidos por entrega, retirada e mesa
* Implementar cálculo de subtotal
* Implementar cálculo de adicionais
* Implementar taxa de entrega
* Implementar status dos pedidos
* Implementar controle manual de pagamento
* Implementar autenticação JWT
* Proteger rotas administrativas
* Separar rotas públicas e administrativas
* Criar documentação Swagger
* Preparar deploy no Railway
* Usar variáveis de ambiente
* Configurar CORS
* Implementar upload de imagens
* Integrar Supabase Storage
* Implementar paginação no painel admin
* Implementar filtros de pedidos
* Implementar tratamento global de erros

Em ajuste / melhoria

* Ajustes finais de integração com frontend
* Melhorias finas no painel administrativo
* Melhorias de experiência no fluxo de pedido
* Testes finais em produção
* Ajustes de performance conforme uso real

Possíveis funcionalidades futuras

* Dashboard financeiro
* Relatórios de vendas
* Produtos mais vendidos
* Ticket médio
* Controle de caixa
* Integração com gateway de pagamento
* Integração com Mercado Pago
* Notificações em tempo real
* WebSocket ou polling otimizado
* Integração com bot externo
* CI/CD com GitHub Actions
* Docker Compose

⸻

⚠️ Atenção Antes de Usar

Antes de executar ou contribuir com o projeto, verifique os seguintes pontos:

Pacote base da aplicação

Confirme se o pacote principal do projeto está como:

com.pitsdog.api

Rotas administrativas

As rotas administrativas usam o prefixo:

/admin

Exemplos:

/admin/categorias
/admin/produtos
/admin/adicionais
/admin/combos
/admin/pedidos

Token JWT

As rotas administrativas exigem o header:

Authorization: Bearer SEU_TOKEN_AQUI

Retorno paginado

A listagem de pedidos administrativos retorna uma página.

Os dados ficam dentro de:

{
  "content": []
}

Campo de imagem

O campo usado para armazenar imagens é:

"imagemUrl"

Campo de adicionais no pedido

O DTO do pedido utiliza o campo:

"adicionaisIds"

⸻

👨‍💻 Desenvolvedor

Desenvolvido por Emanoel Cavalcante

GitHub:

https://github.com/EmanoelCavalcante

⸻

⭐ Considerações Finais

A PitsDog API é um projeto real em evolução, desenvolvido com foco em aprendizado prático, aplicação comercial e construção de portfólio profissional.

O sistema já possui os principais recursos de um backend de delivery moderno, incluindo cardápio público, painel administrativo, autenticação, controle de pedidos, pagamentos manuais, upload de imagens, deploy em cloud e integração com banco PostgreSQL.

O projeto continuará recebendo melhorias conforme novas necessidades surgirem no uso real da hamburgueria.