# Guia de Autenticação JWT - Music API

## Visão Geral
A Music API foi criada para usar **autenticação JWT (JSON Web Token)** ao invés de HTTP Basic Auth. Este documento explica o funnciona fluxo de autenticação e como testá-lo.

---
- **Autenticação baseada em Token JWT**
- Endpoint de login para obter tokens
- Endpoints da API protegidos que requerem tokens JWT válidos
- Swagger UI acessível com ou sem tokens
- Usuário admin padrão criado automaticamente

---

## Esquema do Banco de Dados

### Tabela Users
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(100) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL,
    ativo BOOLEAN NOT NULL DEFAULT TRUE,
    roles VARCHAR(50) NOT NULL DEFAULT 'USER'
);
```

**Usuário Admin Padrão:**
- Username: `admin`
- Password: `admin123`
- Role: `ADMIN`

---

## Fluxo de Autenticação

### 1. Login (Obter Token JWT)
```
POST /api/v1/auth/login
Content-Type: application/json

{
  "username": "admin",
  "password": "admin"
}

Resposta (HTTP 200):
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "username": "admin"
}
```

### 2. Usar Token para Acessar Endpoints Protegidos
Inclua o token no cabeçalho `Authorization`:
```
GET /api/v1/artistas
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### 3. Validação de Token
- Tokens expiram após **24 horas** (configurável via `jwt.expiration`)
- Tokens inválidos/expirados retornam **401 Unauthorized**

---

## Início Rápido

### Passo 1: Build & Executar a Aplicação
```bash
./mvnw clean package
./mvnw spring-boot:run
```

A aplicação irá:
1. Criar a tabela `users` (migração Liquibase)
2. Inserir o usuário admin padrão
3. Iniciar o servidor Spring Boot na porta 8080

### Passo 2: Acessar a Página de Login
Abra no seu navegador:
```
http://localhost:8080/api/login
```

### Passo 3: Fazer Login
1. Digite o username: `admin`
2. Digite a password: `admin123`
3. Clique em "Entrar"
4. Copie o token JWT exibido

### Passo 4: Acessar Swagger UI com Token
1. Acesse: `http://localhost:8080/api/v1/swagger-ui.html`
2. Clique no botão "Authorize" (canto superior direito)
3. Cole seu token no formato: `Bearer <seu-token>`
4. Clique em "Authorize" e depois em "Close"
5. Agora você pode testar todos os endpoints da API no Swagger

---

## Endpoints Públicos (Sem Autenticação Necessária)

- `/login` — Página de login
- `/v1/auth/login` — Obter token JWT
- `/v1/auth/register` — Registrar novo usuário
- `/swagger-ui/**` — Swagger UI
- `/v3/api-docs/**` — Documentação OpenAPI
- `/swagger-resources/**` — Recursos Swagger
- `/webjars/**` — Recursos Web JAR

---

## Endpoints Protegidos (Requerem Token JWT)

Todos os outros endpoints sob `/v1/**` requerem um token JWT válido no cabeçalho `Authorization`.

Exemplo:
```bash
curl -H "Authorization: Bearer <seu-token>" \
  http://localhost:8080/api/v1/artistas
```

---

## Propriedades de Configuração

Localizadas em `src/main/resources/application.properties`:

```properties
# Configuração JWT
jwt.secret=mySecretKeyForJWTTokenGenerationAndValidationPurposesOnly12345678
jwt.expiration=86400000  # 24 horas em milissegundos

# Variáveis de ambiente (sobrescrevem os padrões):
# JWT_SECRET=sua-chave-secreta
# JWT_EXPIRATION=86400000
```

**Importante para Produção:**
- Altere `jwt.secret` para uma string longa e aleatória
- Use variáveis de ambiente para definir secrets de forma segura
- Aumente `jwt.expiration` se necessário (em milissegundos)

---

## Classes & Componentes

### Componentes de Autenticação
| Arquivo | Propósito |
|------|---------|
| `JwtTokenProvider.java` | Gerar e validar tokens JWT |
| `JwtAuthenticationFilter.java` | Extrair e validar tokens das requisições |
| `CustomUserDetailsService.java` | Carregar detalhes do usuário do banco de dados |
| `SecurityConfig.java` | Configuração do Spring Security com JWT |

### Controllers
| Arquivo | Propósito |
|------|---------|
| `AuthController.java` | Endpoints de login e registro |
| `LoginController.java` | Serve a página de login |

### Domain & Repository
| Arquivo | Propósito |
|------|---------|
| `Domain/User.java` | Entidade User |
| `Repository/UserRepository.java` | Persistência de usuários |

### DTOs
| Arquivo | Propósito |
|------|---------|
| `Controller/dto/LoginRequest.java` | Payload de requisição de login |
| `Controller/dto/JwtAuthResponse.java` | Payload de resposta JWT |

### Migrações de Banco de Dados
| Arquivo | Propósito |
|------|---------|
| `db.migracao/002-create-users-table.xml` | Criar tabela users e usuário admin padrão |

---

## Testando com cURL

### Login
```bash
curl -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}'
```

### Registrar Novo Usuário
```bash
curl -X POST http://localhost:8080/api/v1/auth/register \
  -H "Content-Type: application/json" \
  -d '{"username":"novousuario","password":"senha123"}'
```

### Acessar Endpoint Protegido com Token
```bash
# Primeiro, obtenha o token
TOKEN=$(curl -s -X POST http://localhost:8080/api/v1/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"admin123"}' \
  | jq -r '.accessToken')

# Use o token
curl -H "Authorization: Bearer $TOKEN" \
  http://localhost:8080/api/v1/artistas
```

---

## Solução de Problemas

### "Invalid credentials" no login
- Verifique username e password no banco de dados
- Certifique-se que a tabela users foi criada pelo Liquibase
- Verifique se o hash BCrypt corresponde à senha

### Erros "Unable to generate JWT token"
- Verifique se a propriedade `jwt.secret` está definida
- Verifique se a versão do Java é 11+
- Verifique os logs para erros da biblioteca JJWT

### Token não funciona no Swagger UI
- Certifique-se que o formato do token é: `Bearer <token>` (com espaço)
- Verifique se o token não expirou (padrão 24 horas)
- Tente atualizar a página e autorizar novamente

### Tabela users não foi criada
- Verifique os logs do Liquibase durante a inicialização
- Verifique se o PostgreSQL está rodando e acessível
- Revise `src/main/resources/db/changelog/db.migracao/002-create-users-table.xml`

---

## Checklist de Produção

- [ ] Alterar `jwt.secret` para uma chave aleatória forte
- [ ] Atualizar `jwt.expiration` conforme necessário
- [ ] Usar variáveis de ambiente para gerenciar secrets
- [ ] Habilitar HTTPS apenas
- [ ] Adicionar rate limiting ao endpoint de login
- [ ] Implementar refresh tokens
- [ ] Adicionar mecanismo de renovação de expiração de token
- [ ] Configurar log de auditoria para eventos de autenticação
- [ ] Configurar CORS se chamando de domínio diferente
- [ ] Adicionar bloqueio de conta após tentativas falhadas

---

## Dependências Adicionadas

A seguinte biblioteca JWT foi adicionada ao `pom.xml`:

```xml
<!-- JWT -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.12.3</version>
</dependency>
```

---

## Architecture Diagram - Diferenças entre Basic Atuth entre JWT

```
┌─────────────────────────────────────────────────────────┐
│                    Client Request                        │
└────────────────────┬────────────────────────────────────┘
                     │
         ┌───────────┴───────────┐
         │                       │
    ┌────▼─────┐          ┌──────▼──────┐
    │Login (No │          │API Request  │
    │Auth)     │          │(with JWT)   │
    └────┬─────┘          └──────┬──────┘
         │                       │
    ┌────▼──────────────┬───────┴──────┐
    │AuthController     │JwtAuthFilter │
    │.login()           │(Intercepts)  │
    └────┬──────────────┴───────┬──────┘
         │                      │
    ┌────▼────────────────┐ ┌───▼──────────────┐
    │UserDetailsService   │ │JwtTokenProvider  │
    │.authenticate()      │ │.validateToken()  │
    └────┬────────────────┘ └───┬──────────────┘
         │                      │
    ┌────▼──────────────────────▼──────────┐
    │PostgreSQL (users table)              │
    └──────────────────────────────────────┘
```

---

## Suporte

Para problemas ou questões, revise:
1. Logs da aplicação: `logs/application.log`
2. SecurityConfig para permissões de endpoint
3. Arquivos de migração do banco em `db.migracao/`
4. Documentação da biblioteca JJWT: https://github.com/jwtk/jjwt
