Claro. Como o **WD Works V2 já está com o backend completo e testado**, eu faria um README com aparência de projeto profissional, mas sem inventar endpoints ou funcionalidades que não existem.

Também vou usar a estrutura que já definiste (`controller`, `dto`, `config`, etc.) para que o README realmente represente o projeto.

# WD Works V2

**WD Works V2** é uma API REST desenvolvida em **Java e Spring Boot** para gestão de uma mercearia.

O projeto foi desenvolvido com foco em **arquitetura organizada, segurança, separação de responsabilidades, regras de negócio e escalabilidade**, permitindo gerir empresas, utilizadores, produtos, categorias, vendas e estoque através de uma API.

O WD Works V2 é uma evolução de um projeto anterior desenvolvido durante os estudos de Java e Spring Boot, tendo como objetivo aplicar conceitos de desenvolvimento de software de forma mais estruturada e próxima de um projeto real.

---

## 🚀 Funcionalidades

### 🔐 Autenticação e Segurança

* Autenticação de utilizadores
* Login através da API
* Autenticação baseada em **JWT**
* Proteção dos endpoints com **Spring Security**
* Passwords armazenadas de forma segura
* Controlo de acesso baseado em perfis
* Gestão de permissões de acordo com o utilizador
* Sessões **stateless**
* Isolamento das informações entre empresas

### 🏢 Gestão de Empresas

* Criação de empresas
* Consulta de empresas
* Atualização de informações
* Ativação e desativação
* Validação das regras relacionadas à empresa

### 👤 Gestão de Utilizadores

* Criação e gestão de utilizadores
* Associação do utilizador a uma empresa
* Perfis de acesso
* Gestão do estado do utilizador
* Autorização baseada no perfil

Perfis suportados:

* `ADMIN`
* `GERENTE`
* `FUNCIONARIO`

Estados do utilizador:

* `ATIVO`
* `INATIVO`
* `BLOQUEADO`

### 📦 Gestão de Produtos

* Criação de produtos
* Consulta de produtos
* Atualização de produtos
* Associação de produtos a categorias
* Gestão dos dados relacionados ao estoque

### 🏷️ Gestão de Categorias

* Criação de categorias
* Consulta de categorias
* Atualização de categorias
* Organização dos produtos por categoria

### 🛒 Gestão de Vendas

* Registo de vendas
* Registo dos itens de cada venda
* Consulta de vendas
* Cálculo e gestão das informações relacionadas à venda

### 📊 Gestão de Estoque

* Consulta do estoque
* Controle da quantidade disponível
* Atualização do estoque através das operações realizadas
* Gestão das movimentações de estoque

### 🔄 Movimentações de Estoque

O sistema mantém o registo das movimentações realizadas no estoque, permitindo acompanhar operações como entradas e saídas de produtos.

### 📝 Auditoria

O projeto possui estrutura para registo e acompanhamento das operações relevantes realizadas no sistema, permitindo maior rastreabilidade das ações.

---

## 🏗️ Arquitetura

O projeto segue uma organização baseada na separação de responsabilidades.

```text
src/main/java/com/wdworks/v2
│
├── config
│   ├── SecurityConfig
│   └── CorsConfig
│
├── controller
│   ├── AuthController
│   ├── CategoryController
│   ├── CompanyController
│   ├── ProductController
│   ├── SaleController
│   ├── StockController
│   ├── StockMovementController
│   └── UserController
│
├── dto
│   ├── auth
│   ├── category
│   ├── company
│   ├── product
│   ├── sale
│   └── stock
│
└── ...
```

A aplicação utiliza **DTOs (Data Transfer Objects)** para separar os dados recebidos/enviados pela API dos modelos utilizados internamente pela aplicação.

Essa organização facilita a manutenção, evolução e segurança do sistema.

---

## 🛠️ Tecnologias

| Tecnologia            | Utilização                     |
| --------------------- | ------------------------------ |
| **Java**              | Linguagem principal            |
| **Spring Boot**       | Desenvolvimento da API         |
| **Spring Security**   | Segurança e autorização        |
| **JWT**               | Autenticação baseada em tokens |
| **Spring Data JPA**   | Persistência de dados          |
| **Hibernate**         | ORM                            |
| **MySQL**             | Banco de dados                 |
| **Maven**             | Gestão de dependências e build |
| **Lombok**            | Redução de código repetitivo   |
| **Docker**            | Containerização                |
| **OpenAPI / Swagger** | Documentação da API            |

---

## 🔒 Segurança

A segurança é uma das partes fundamentais do WD Works V2.

A aplicação utiliza:

```text
Cliente
   │
   │ Login
   ▼
AuthController
   │
   ▼
Autenticação
   │
   ▼
JWT
   │
   ▼
Authorization Header
   │
   ▼
JwtAuthenticationFilter
   │
   ▼
Spring Security
   │
   ▼
Autorização
```

Após a autenticação, o utilizador recebe um **JWT**, que é utilizado para autenticar as requisições seguintes.

A API não utiliza sessões tradicionais, seguindo uma abordagem **stateless**.

Além de verificar se o utilizador está autenticado, o sistema considera o **perfil do utilizador e a empresa à qual pertence** para controlar o acesso aos recursos.

---

## 🏢 Multiempresa

Uma das preocupações do WD Works V2 é permitir que o sistema seja utilizado por diferentes empresas mantendo os seus dados isolados.

O conceito pode ser representado da seguinte forma:

```text
Empresa A
├── Utilizadores
├── Produtos
├── Categorias
├── Estoque
└── Vendas

Empresa B
├── Utilizadores
├── Produtos
├── Categorias
├── Estoque
└── Vendas
```

Um utilizador de uma empresa não deve conseguir aceder aos dados pertencentes a outra empresa.

Esse isolamento é uma parte importante da arquitetura do projeto.

---

## 📋 Regras de negócio

O projeto não foi desenvolvido apenas como um CRUD.

As operações passam por uma camada de regras de negócio responsável por garantir que as operações realizadas respeitam as regras definidas pelo sistema.

Por exemplo:

```text
Controller
    ↓
Service
    ↓
Regras de negócio
    ↓
Repository
    ↓
Database
```

Isso permite manter os controllers mais simples e concentrar a lógica de negócio nos locais apropriados.

---

## 🧪 Testes

O backend foi desenvolvido e **testado**, garantindo o funcionamento das principais funcionalidades e regras implementadas.

Os testes fazem parte da estratégia de desenvolvimento do projeto e têm como objetivo reduzir regressões e aumentar a confiabilidade da aplicação.

---

## ⚙️ Como executar o projeto

### Pré-requisitos

Antes de executar o projeto, é necessário ter instalado:

* Java
* Maven
* MySQL

Caso seja utilizada a configuração com Docker:

* Docker
* Docker Compose

### 1. Clonar o repositório

```bash
git clone <URL_DO_REPOSITORIO>
```

### 2. Entrar no projeto

```bash
cd WD-Works-V2
```

### 3. Configurar as variáveis de ambiente

A aplicação utiliza configurações sensíveis através de variáveis de ambiente.

Por exemplo, a chave utilizada para assinar os JWTs é configurada através de:

```properties
security.jwt.secret=${JWT_SECRET}
```

Portanto, a variável `JWT_SECRET` deve estar definida no ambiente onde a aplicação será executada.

> **Nunca coloque chaves JWT, passwords ou outras credenciais diretamente no repositório.**

### 4. Configurar o banco de dados

Configure as informações de conexão com o MySQL de acordo com o ambiente em que a aplicação será executada.

### 5. Executar a aplicação

Com Maven:

```bash
./mvnw spring-boot:run
```

No Windows:

```bash
mvnw.cmd spring-boot:run
```

---

## 📚 Documentação da API

A API foi preparada para utilização com **OpenAPI / Swagger**, permitindo visualizar e testar os endpoints de forma interativa.

Após iniciar a aplicação, a documentação pode ser acedida através da configuração do Swagger/OpenAPI do projeto.

---

## 📁 Organização do projeto

A estrutura principal segue uma organização por responsabilidade:

```text
com.wdworks.v2
│
├── config
│   └── Configurações da aplicação
│
├── controller
│   └── Endpoints REST
│
├── dto
│   └── Objetos utilizados na comunicação da API
│
├── entity
│   └── Entidades do domínio
│
├── repository
│   └── Acesso aos dados
│
├── service
│   └── Regras de negócio
│
├── security
│   └── Componentes relacionados à segurança
│
└── ...
```

A separação dessas responsabilidades facilita a manutenção e permite que novas funcionalidades sejam adicionadas sem comprometer a estrutura existente.

---

## 🎯 Objetivos do projeto

O WD Works V2 foi desenvolvido com dois objetivos principais:

### 1. Construir uma solução real

Criar uma API capaz de servir como base para um sistema de gestão de uma mercearia, cobrindo áreas como:

* utilizadores;
* empresas;
* produtos;
* categorias;
* estoque;
* vendas;
* segurança;
* auditoria.

### 2. Evoluir tecnicamente

Aplicar na prática conceitos importantes de desenvolvimento backend:

* Arquitetura de software
* API REST
* Spring Boot
* Spring Security
* JWT
* Autenticação e autorização
* JPA / Hibernate
* DTOs
* Regras de negócio
* Validações
* Testes
* Isolamento multiempresa
* Boas práticas de desenvolvimento

---

## 🔮 Próximos passos

O backend do WD Works V2 encontra-se implementado e testado.

A evolução do projeto pode incluir:

* [x] Desenvolvimento do frontend
* [x] Integração completa frontend + API
* [x] Dockerização completa da aplicação
* [x] Deploy da aplicação
* [x] Evolução da documentação da API
* [x] Melhorias contínuas de segurança e performance

---

## 👨‍💻 Autor

**WD Works**

Projeto desenvolvido como parte da evolução prática dos conhecimentos em **Java, Spring Boot e desenvolvimento de APIs REST**.

---

## 📄 Licença

Este projeto não possui uma licença open source definida neste momento.

O código disponibilizado neste repositório permanece sujeito aos direitos autorais do autor.

---

## ⭐ Sobre o projeto

O **WD Works V2** representa uma evolução de um projeto inicial de estudos para uma aplicação backend estruturada, com preocupação não apenas com o funcionamento das funcionalidades, mas também com **segurança, arquitetura, manutenção, escalabilidade e regras de negócio**.

> **Construído com Java + Spring Boot para transformar uma ideia de gestão de mercearia em uma API estruturada e preparada para evoluir.**
