# notification-service
 
Microsserviço de envio de notificações (e-mail e SMS) construído com Spring Boot 4, RabbitMQ e PostgreSQL.
 
---
 
## Tecnologias
 
| Tecnologia | Versão |
|---|---|
| Java | 21 |
| Spring Boot | 4.0.6 |
| Spring AMQP (RabbitMQ) | via Boot starter |
| Spring Data JPA + Hibernate | via Boot starter |
| PostgreSQL | 16 |
| Flyway | via Boot starter |
| MapStruct | 1.5.5.Final |
| Lombok | via Boot starter |
| Spring Retry | 2.0.11 |
| Docker Compose | — |
 
---
 
## Arquitetura
 
```
POST /notifications
        │
        ▼
NotificationController
        │
        ▼
NotificationService ──► salva status PENDING (PostgreSQL)
        │
        ▼
MessageSender (EmailSender / SmsSender)
        │
        ▼
RabbitMQ — DirectExchange (notification.exchange)
        │
        ├──► email.queue ──► EmailConsumer ──► EmailNotificationProcessor
        │                                              │
        │                                    @Retryable (3x, 2s)
        │                                              │
        │                                    status = SENT / FAILED
        │
        └──► sms.queue ──► SmsConsumer ──► SmsNotificationProcessor
                                                       │
                                             @Retryable (3x, 2s)
                                                       │
                                             status = SENT / FAILED
 
Falha total → Dead Letter Queue (notifications.dlq)
```
 
---
 
## Estrutura de pacotes
 
```
src/main/java/com/notification/notification_service/
├── config/
│   └── RabbitMQConfig.java          # Exchange, filas, bindings, DLQ
├── controller/
│   └── NotificationController.java  # Endpoints REST
├── dto/
│   ├── NotificationRequest.java
│   ├── NotificationResponse.java
│   └── NotificationDetailsResponse.java
├── enums/
│   └── NotificationStatus.java      # PENDING | SENT | FAILED
├── exception/
│   └── NotificationProcessingException.java
├── mapper/
│   └── NotificationMapper.java      # MapStruct
├── message/
│   ├── MessageSender.java           # Interface
│   ├── EmailSender.java
│   ├── SmsSender.java
│   ├── EmailConsumer.java           # @RabbitListener
│   ├── SmsConsumer.java
│   ├── EmailNotificationProcessor.java  # @Retryable + @Recover
│   └── SmsNotificationProcessor.java
├── model/
│   └── Notification.java
├── repository/
│   └── NotificationRepository.java  # JpaRepository + JpaSpecificationExecutor
├── service/
│   └── NotificationService.java
└── specification/
    └── NotificationSpecification.java  # Filtros dinâmicos
```
 
---
 
## Endpoints
 
### POST `/api/v1/notifications`
 
Cria e publica uma notificação.
 
**Request:**
```json
{
  "type": "EMAIL",
  "recipient": "usuario@email.com",
  "subject": "Assunto",
  "body": "Corpo da mensagem",
  "priority": "HIGH",
  "metadata": {}
}
```
 
**Response `201`:**
```json
{
  "id": "uuid",
  "type": "EMAIL",
  "recipient": "usuario@email.com",
  "status": "PENDING",
  "createdAt": "2026-05-25T11:00:00"
}
```
 
---
 
### GET `/api/v1/notifications/{id}`
 
Retorna os detalhes de uma notificação pelo ID.
 
**Response `200`:**
```json
{
  "id": "uuid",
  "type": "EMAIL",
  "recipient": "usuario@email.com",
  "subject": "Assunto",
  "sentAt": "2026-05-25T11:00:01",
  "errorMessage": null,
  "status": "SENT",
  "createdAt": "2026-05-25T11:00:00"
}
```
 
---
 
### GET `/api/v1/notifications`
 
Listagem paginada com filtros opcionais.
 
**Query params:**
 
| Parâmetro | Tipo | Obrigatório |
|---|---|---|
| `status` | `PENDING` \| `SENT` \| `FAILED` | não |
| `type` | `EMAIL` \| `SMS` | não |
| `recipient` | string | não |
| `page` | int (default 0) | não |
| `size` | int (default 20) | não |
| `sort` | ex: `createdAt,desc` | não |
 
**Exemplo:**
```
GET /api/v1/notifications?status=SENT&type=EMAIL&page=0&size=10
```
 
**Response `200`:**
```json
{
  "content": [...],
  "totalElements": 4,
  "totalPages": 1,
  "number": 0,
  "size": 10
}
```
 
---
 
## RabbitMQ
 
| Recurso | Nome |
|---|---|
| Exchange | `notification.exchange` (Direct) |
| Fila e-mail | `email.queue` |
| Fila SMS | `sms.queue` |
| Routing key e-mail | `email.key` |
| Routing key SMS | `sms.key` |
| Dead Letter Queue | `notifications.dlq` |
 
Ambas as filas são `durable` e configuram `x-dead-letter-routing-key: notifications.dlq` — após falha total do consumer (3 tentativas com 2s de intervalo), a mensagem é encaminhada automaticamente para a DLQ.
 
---
 
## Banco de dados
 
Gerenciado pelo **Flyway**. Migration inicial em `db/migration/V1__create_notifications_table.sql`.
 
**Tabela `notifications`:**
 
| Coluna | Tipo | Descrição |
|---|---|---|
| `id` | UUID | PK gerado automaticamente |
| `type` | VARCHAR(10) | EMAIL ou SMS |
| `recipient` | VARCHAR(255) | Destinatário |
| `subject` | VARCHAR(150) | Assunto |
| `body` | TEXT | Corpo |
| `priority` | VARCHAR(10) | Prioridade |
| `status` | VARCHAR(10) | PENDING / SENT / FAILED |
| `metadata` | JSONB | Dados adicionais |
| `created_at` | TIMESTAMP | Data de criação |
| `sent_at` | TIMESTAMP | Data de envio |
| `error_message` | TEXT | Mensagem de erro (FAILED) |
| `version` | BIGINT | Controle de concorrência (Optimistic Lock) |
 
Índices: `idx_notifications_status`, `idx_notifications_type`.
 
---
 
## Como rodar localmente
 
**Pré-requisitos:** Docker, Java 21, Maven.
 
**1. Suba os serviços de infraestrutura:**
```bash
docker compose up -d
```
 
Isso inicia:
- PostgreSQL na porta `5432`
- RabbitMQ na porta `5672` (management UI: `15672`)
**2. Execute a aplicação:**
```bash
./mvnw spring-boot:run
```
 
A aplicação sobe na porta `8080`. O Flyway executa as migrations automaticamente.
 
**3. Acesse o RabbitMQ Management:**
```
http://localhost:15672
usuário: rabbit_user
senha:   rabbit_pass
```
 
---
 
## Variáveis de configuração (`application.yml`)
 
| Propriedade | Valor padrão |
|---|---|
| `spring.datasource.url` | `jdbc:postgresql://localhost:5432/notification_db` |
| `spring.datasource.username` | `notification_user` |
| `spring.datasource.password` | `notification_pass` |
| `spring.rabbitmq.host` | `localhost` |
| `spring.rabbitmq.port` | `5672` |
| `spring.rabbitmq.username` | `rabbit_user` |
| `spring.rabbitmq.password` | `rabbit_pass` |
| `rabbitmq.exchange` | `notification.exchange` |
| `rabbitmq.queues.email` | `email.queue` |
| `rabbitmq.queues.sms` | `sms.queue` |
| `rabbitmq.routing-keys.email` | `email.key` |
| `rabbitmq.routing-keys.sms` | `sms.key` |
