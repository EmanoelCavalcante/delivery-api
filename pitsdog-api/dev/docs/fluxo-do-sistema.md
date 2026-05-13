# Pit's Dog - Fluxo do Sistema (Visao Geral)

Este documento descreve o fluxo principal do projeto Pit's Dog, do cardapio ao gerenciamento administrativo.

---

## Fluxo atual (MVP)

1. Cliente acessa o cardapio (frontend).
2. Frontend consome rotas publicas da API para exibir categorias e produtos.
3. Cliente escolhe os produtos e monta o pedido.
4. Frontend envia o pedido para a API.
5. API valida e salva os dados no Supabase/PostgreSQL.
6. Painel administrativo consome rotas administrativas para acompanhar e gerenciar os pedidos (status, atendimento e organizacao operacional).

---

## Evolucoes previstas

1. Gateway de pagamento entra no fluxo apos o cliente confirmar o pedido.
2. Confirmacao/atualizacoes podem integrar com bot de WhatsApp para comunicacao automatizada com o cliente.

