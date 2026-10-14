# WD Works V2 — Sistema de Gestão de Mercearia

API REST para gestão completa de mercearias e pequenos comércios em Moçambique.

[![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://openjdk.org/projects/jdk/21/)
[![Spring Boot](https://img.shields.io/badge/Spring%20Boot-4.1.1-brightgreen.svg)](https://spring.io/projects/spring-boot)
[![PostgreSQL](https://img.shields.io/badge/PostgreSQL-18-blue.svg)](https://www.postgresql.org/)
[![Flyway](https://img.shields.io/badge/Flyway-13.9-red.svg)](https://flywaydb.org/)
[![License](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)

---

## 📖 Sobre o Projeto

O **WD Works V2** é uma API REST multiempresa que permite gerir:

- **Empresas** e utilizadores com perfis distintos (ADMIN, GERENTE, FUNCIONÁRIO)
- **Produtos** e categorias
- **Estoque** com alertas automáticos de stock baixo
- **Vendas** com cálculo automático de lucro
- **Movimentos de stock** (entradas, saídas, ajustes, devoluções)
- **Auditoria** completa de todas as operações
- **Dashboard** com KPIs em tempo real

Cada empresa tem os seus próprios dados, isolados de todas as outras.

---

## ✨ Funcionalidades

### 🔐 Autenticação e Autorização
- Login com JWT
- Perfis: `ADMIN`, `GERENTE`, `FUNCIONARIO`
- Permissões granulares por perfil
- Rate limit inteligente (IP + email)
- Bloqueio temporário após tentativas falhadas
- Recuperação de senha por token seguro

### 🏢 Multiempresa
- Isolamento total de dados por empresa
- Um único ADMIN por empresa
- Auditoria centralizada

### 📦 Gestão de Produtos
- CRUD completo
- Categorias por empresa
- Ativação/desativação (soft delete)
- Filtros avançados (nome, categoria, preço, stock)
- Registro de autor de criação/alteração

### 📊 Gestão de Estoque
- Movimentos: `ENTRADA`, `SAIDA`, `AJUSTE`, `DEVOLUCAO`
- Quantidade mínima configurável por produto
- Alertas automáticos de stock baixo
- Histórico completo de movimentos
- Optimistic locking (evita conflitos)

### 💰 Gestão de Vendas
- Venda com múltiplos itens
- Cálculo automático de total e lucro
- Atualização automática de stock
- Retry automático em conflitos de concorrência
- Filtros por data, utilizador, ID

### 📈 Dashboard
- KPIs em tempo real
- Gráfico de vendas (7, 30, 365 dias)
- Top produtos mais vendidos
- Stock crítico
- Atividade recente
- **Cache de 30s** para alta performance

### 🔍 Auditoria
- Registo de todas as operações
- Filtros por tipo, gravidade, utilizador, data
- Níveis: `INFO`, `WARNING`, `CRITICAL`
- Truncamento automático de payloads grandes

---

## 🛠️ Stack Técnica

### Backend
| Tecnologia | Versão | Propósito |
|-----------|:------:|-----------|
| Java | 21 | Linguagem |
| Spring Boot | 4.1.1 | Framework |
| Spring Security | 7.1.1 | Autenticação/Autorização |
| Spring Data JPA | 4.1.1 | Persistência |
| Hibernate | 7.4.5 | ORM |
| PostgreSQL | 18 | Base de dados |
| Flyway | 13.9 | Migrations |
| Caffeine | - | Cache in-memory |
| Bucket4j | 8.19 | Rate limiting |
| JWT (jjwt) | 0.12.6 | Tokens |
| Lombok | 1.18.46 | Boilerplate |
| MapStruct | 1.6.3 | Mapeamento DTO |
| SpringDoc OpenAPI | 3.1.1 | Documentação |
| Bucket4j | 8.19 | Rate limit |

### Frontend (separado)
Ver repositório `Frontend/`

### Ferramentas
- Maven Wrapper
- Docker & docker-compose
- Git

---

## 🏗️ Arquitetura

### Estrutura de pacotes

```
WD.works.V2/
├── alertaStock/       Alertas de stock baixo
├── auditoria/         Registo de auditoria
├── categorias/        Categorias de produtos
├── configuracao/      Configuração transversal
│   ├── config/        Beans de configuração
│   ├── context/       Contextos (empresa, usuário, auditoria)
│   ├── openapi/       Swagger/OpenAPI
│   └── security/      Segurança (JWT, rate limit)
├── dashboard/         KPIs e agregações
├── empresa/           Empresas
├── estoque/           Stock
├── exception/         Exceções e handler global
├── movimentoStock/    Movimentos de stock
├── produtos/          Produtos
├── usuario/           Utilizadores e autenticação
└── venda/             Vendas
```

### Camadas (por feature)

```
controller/   → Endpoints REST
service/      → Lógica de negócio
repository/   → Acesso a dados
entity/       → Entidades JPA
dto/          → Objetos de transferência
```

### Isolamento multiempresa

- Cada requisição é associada a uma empresa via JWT
- `EmpresaContext` fornece a empresa atual
- Todos os repositórios filtram por `empresa_id`
- Impossível aceder a dados de outra empresa

---

## 📋 Pré-requisitos

- **Java 21+** ([download](https://adoptium.net/))
- **PostgreSQL 15+** ([download](https://www.postgresql.org/download/))
- **Maven 3.9+** (incluído no `mvnw`)
- **Git**

Opcional:
- Docker & Docker Compose
- IntelliJ IDEA / VS Code

---

## 🚀 Instalação

### 1. Clonar o repositório

```bash
git clone https://github.com/seu-usuario/wd-works-v2.git
cd wd-works-v2
```

### 2. Criar a base de dados

```bash
# Liga-te ao PostgreSQL
psql -U postgres

# Dentro do psql:
CREATE DATABASE wdworks;
CREATE USER wdworks_user WITH PASSWORD 'senha_segura';
GRANT ALL PRIVILEGES ON DATABASE wdworks TO wdworks_user;
\q
```

### 3. Configurar variáveis de ambiente

Cria um ficheiro `.env` na raiz (não commitado):

```properties
# .env (exemplo)
JWT_SECRET=chave-super-secreta-com-pelo-menos-32-caracteres-aqui-123456
JWT_EXPIRATION=86400000
DB_URL=jdbc:postgresql://localhost:5432/wdworks
DB_USERNAME=wdworks_user
DB_PASSWORD=senha_segura
CORS_ORIGINS=http://localhost:5173
FRONTEND_URL=http://localhost:5173
```

### 4. Compilar e correr

```bash
# Dar permissão ao Maven Wrapper (Linux/Mac)
chmod +x mvnw

# Compilar
./mvnw clean package -DskipTests

# Correr em modo desenvolvimento
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

A API fica disponível em: **`http://localhost:8080`**

---

## ⚙️ Configuração por Perfil

O projeto tem 3 perfis:

| Perfil | Uso | Ativação |
|--------|-----|----------|
| `default` | Base (sem overrides) | (nenhuma) |
| `dev` | Desenvolvimento | `-Dspring-boot.run.profiles=dev` |
| `prod` | Produção | `--spring.profiles.active=prod` |
| `test` | Testes | Automático com `./mvnw test` |

### Principais diferenças

| Propriedade | dev | prod |
|-------------|:---:|:----:|
| Swagger UI | ON | OFF |
| Logs SQL | DEBUG | OFF |
| JWT secret | Fixa | Env var |
| Rate limit | Desativado | Restritivo |
| CORS | Permissivo | Domínio real |

---

## 📚 Endpoints Principais

### Autenticação (`/auth`)

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| POST | `/auth/login` | Login |
| POST | `/auth/cadastro` | Cadastro de empresa + admin |
| GET | `/auth/me` | Utilizador autenticado |
| POST | `/auth/recuperar-senha` | Pedir recuperação |
| POST | `/auth/redefinir-senha` | Redefinir senha |

### Empresas (`/api/empresas`)

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| GET | `/api/empresas/minha` | Minha empresa |
| PUT | `/api/empresas/minha` | Atualizar minha empresa |
| PATCH | `/api/empresas/minha/ativar` | Ativar |
| PATCH | `/api/empresas/minha/desativar` | Desativar |

### Utilizadores (`/api/usuarios`)

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| POST | `/api/usuarios` | Criar utilizador |
| GET | `/api/usuarios` | Listar |
| GET | `/api/usuarios/{id}` | Buscar por ID |
| PUT | `/api/usuarios/{id}` | Atualizar |
| PATCH | `/api/usuarios/{id}/ativar` | Ativar |
| PATCH | `/api/usuarios/{id}/desativar` | Desativar |
| PATCH | `/api/usuarios/me/senha` | Alterar minha senha |

### Produtos (`/api/produtos`)

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| POST | `/api/produtos` | Criar produto |
| GET | `/api/produtos` | Listar/pesquisar |
| GET | `/api/produtos/{id}` | Buscar por ID |
| PUT | `/api/produtos/{id}` | Atualizar |
| PATCH | `/api/produtos/{id}/ativar` | Ativar |
| PATCH | `/api/produtos/{id}/desativar` | Desativar |

### Categorias (`/api/categorias`)

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| POST | `/api/categorias` | Criar |
| GET | `/api/categorias` | Listar |
| PUT | `/api/categorias/{id}` | Atualizar |
| DELETE | `/api/categorias/{id}` | Eliminar |

### Estoque (`/api/estoques`)

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| GET | `/api/estoques` | Listar |
| GET | `/api/estoques/produto/{produtoId}` | Por produto |
| PATCH | `/api/estoques/produto/{produtoId}/quantidade-minima` | Definir mínimo |

### Movimentos de Stock (`/api/movimentos-stock`)

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| POST | `/api/movimentos-stock` | Criar movimento |
| GET | `/api/movimentos-stock` | Listar |
| GET | `/api/movimentos-stock/produto/{produtoId}` | Por produto |

### Vendas (`/api/vendas`)

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| POST | `/api/vendas` | Criar venda |
| GET | `/api/vendas` | Listar/pesquisar |
| GET | `/api/vendas/{id}` | Buscar por ID |

### Alertas de Stock (`/api/alertas-stock`)

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| GET | `/api/alertas-stock` | Listar todos |
| GET | `/api/alertas-stock/ativos` | Apenas ativos |
| GET | `/api/alertas-stock/{id}` | Buscar por ID |

### Dashboard (`/api/dashboard`)

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| GET | `/api/dashboard?periodo=7` | KPIs gerais |

### Auditoria (`/api/auditorias`)

| Método | Endpoint | Descrição |
|:------:|----------|-----------|
| GET | `/api/auditorias` | Todas (ADMIN/GERENTE) |
| GET | `/api/auditorias/minhas` | Minhas |
| GET | `/api/auditorias/usuario/{id}` | Por utilizador |
| GET | `/api/auditorias/tipo/{tipo}` | Por tipo |
| GET | `/api/auditorias/gravidade/{gravidade}` | Por gravidade |

---

## 🧪 Testes

### Correr todos os testes

```bash
./mvnw test
```

### Correr um teste específico

```bash
./mvnw test -Dtest=VendaServiceTest
```

### Correr um método específico

```bash
./mvnw test -Dtest=VendaServiceTest#deveCriarVendaComSucesso
```

---

## 🐳 Docker

### Subir a stack completa

```bash
docker-compose up -d
```

Isto sobe:
- PostgreSQL na porta `5432`
- API na porta `8080`

### Ver logs

```bash
docker-compose logs -f api
```

### Parar tudo

```bash
docker-compose down
```

### Parar e apagar volumes (reset total)

```bash
docker-compose down -v
```

---

## 🚢 Deploy em Produção

### 1. Preparar o servidor

- Ubuntu 22.04+ / Debian 12+
- Java 21
- PostgreSQL 15+
- Nginx (reverse proxy + SSL)
- Certbot (Let's Encrypt)

### 2. Configurar variáveis de ambiente

```bash
export JWT_SECRET="chave-super-secreta-real-com-32-plus-caracteres"
export DB_URL="jdbc:postgresql://localhost:5432/wdworks"
export DB_USERNAME="wdworks_user"
export DB_PASSWORD="senha_muito_segura"
export CORS_ORIGINS="https://app.wdworks.co.mz"
export FRONTEND_URL="https://app.wdworks.co.mz"
export MAIL_USERNAME="brevo-user"
export MAIL_PASSWORD="brevo-password"
```

### 3. Arrancar

```bash
java -jar target/V2-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### 4. Configurar Nginx

```nginx
server {
    listen 443 ssl http2;
    server_name app.wdworks.co.mz;

    ssl_certificate /etc/letsencrypt/live/app.wdworks.co.mz/fullchain.pem;
    ssl_certificate_key /etc/letsencrypt/live/app.wdworks.co.mz/privkey.pem;

    location / {
        proxy_pass http://localhost:8080;
        proxy_set_header Host $host;
        proxy_set_header X-Real-IP $remote_addr;
        proxy_set_header X-Forwarded-For $proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto $scheme;
    }
}

server {
    listen 80;
    server_name app.wdworks.co.mz;
    return 301 https://$host$request_uri;
}
```

### 5. Certificado SSL

```bash
sudo certbot certonly --nginx -d app.wdworks.co.mz
```

---

## 📂 Estrutura do Projeto

```
Projeto-de-gestao-de-mercearia/
├── src/
│   ├── main/
│   │   ├── java/WD/works/V2/
│   │   │   ├── alertaStock/
│   │   │   ├── auditoria/
│   │   │   ├── categorias/
│   │   │   ├── configuracao/
│   │   │   ├── dashboard/
│   │   │   ├── empresa/
│   │   │   ├── estoque/
│   │   │   ├── exception/
│   │   │   ├── movimentoStock/
│   │   │   ├── produtos/
│   │   │   ├── usuario/
│   │   │   ├── venda/
│   │   │   └── V2Application.java
│   │   └── resources/
│   │       ├── db/migration/       ← Flyway
│   │       ├── application.properties
│   │       ├── application-dev.properties
│   │       └── application-prod.properties
│   └── test/
│       └── java/WD/works/V2/       ← Testes
├── Frontend/                        ← Frontend (separado)
├── scripts/                         ← Scripts Python
│   ├── seed_produtos.py
│   ├── seed_vendas.py
│   ├── seed_usuarios.py
│   ├── stress_test.py
│   └── benchmark_dashboard.py
├── Dockerfile
├── docker-compose.yml
├── pom.xml
├── mvnw
├── mvnw.cmd
└── README.md
```

---

## 🔒 Segurança

- JWT com HMAC-SHA384
- Senhas com BCrypt (12 rounds)
- Rate limit no login
- Bloqueio temporário após tentativas falhadas
- Cabeçalhos de segurança (CSP, HSTS, X-Frame, etc.)
- HTTPS forçado em produção
- CORS restritivo
- Isolamento multiempresa

---

## 🤝 Contribuir

1. Fork o projeto
2. Cria uma branch (`git checkout -b feature/nova-feature`)
3. Commit (`git commit -m 'feat: adiciona nova feature'`)
4. Push (`git push origin feature/nova-feature`)
5. Abre um Pull Request

### Convenções de commit

- `feat:` nova funcionalidade
- `fix:` correção de bug
- `docs:` documentação
- `test:` testes
- `refactor:` refactorização
- `chore:` tarefas diversas

---

## 📄 Licença

Este projeto está sob a licença **MIT**. Ver [LICENSE](LICENSE) para detalhes.

---

## 👥 Autores

- **WD Works** — Desenvolvimento inicial

---

## 📞 Suporte

Para questões, abrir uma [issue](https://github.com/Elves114) no GitHub.

---

**Feito com ❤️ em Moçambique 🇲🇿**