# Детальное описание сервисов

## uploader (порт 8081)

**Стек:** Spring Boot 4.0.2, WebFlux (реактивный), Java 17, Lombok, AWS SDK v2 (Spring Cloud AWS 4.0.0)

**Конфиг (`application.yml`):**
- `minio.accesKey` / `minio.secretKey` / `minio.endpoint` / `minio.bucket` — env vars или дефолты
- AWS region: `us-east-1` (статически, для совместимости с MinIO)

**Ключевые классы:**
- `UploadController` — `POST /upload` принимает `multipart/form-data`, параметры: `file` (FilePart) + `userUuid` (UUID)
- `StorageServiceImpl` — сохраняет файл локально (temp dir)
- `S3ServiceImpl` — загружает файл в MinIO через `S3AsyncClient`. Key: `s3-storage/<userUuid>/<filename>`. Метаданные: `user-guid`, `date`
- `S3AsyncClientConfig` — конфигурация клиента
- `StorageProperties` — свойства хранилища

**Запуск:** `./start.sh` (внутри папки `uploader/`)

---

## dbapp (порт 8089)

**Стек:** Spring Boot, WebFlux, R2DBC (реактивный PostgreSQL), Kafka consumer, Java 17, Lombok

**Конфиг (`application.yml`):**
- PostgreSQL: `r2dbc:postgresql://localhost:5432/file_uploader_db`, user: `admin`, pass: `adminsecret`
- Kafka bootstrap: `localhost:9094`
- Топики: `s3-load-events`, `s3-delete-events`, `db-update-events`, `sample-events`

**Ключевые классы:**
- `MinioNotificationListener` — Kafka consumer. Слушает upload и delete топики. Содержит логику дедупликации (`checkDuplicate` по userGuid + uploadDate)
- `FileController` — REST: `POST /api/files`, `DELETE /api/files/{id}`, `GET /api/files`
- `FileService` — CRUD + `deleteByNameAndGuid`, `find(userGuid, uploadDate)`
- `FileRepository` — Spring Data R2DBC репозиторий
- `FileLoadEntity` — сущность `file_records` (id, name, link, userGuid, uploadDate)
- `KafkaConfig` — конфигурация consumer factory
- `KafkaSenderImpl` / `KafkaService` — отправка событий (используется для `db-update-events-topic`)
- `SampleEventHandler` / `SampleEventController` — тестовый endpoint

**Модели MinIO событий:** `MinioEvent` → `MinioRecord` → `S3Dto` → `ObjectDataDto` / `BucketDto`

---

## База данных (PostgreSQL 5432)

**Таблица `file_records`:**
```sql
id          BIGSERIAL PRIMARY KEY
name        VARCHAR(255) NOT NULL      -- имя файла
link        TEXT NOT NULL              -- полный S3-ключ (s3-storage/<uuid>/<name>)
user_guid   UUID NOT NULL              -- UUID пользователя
upload_date TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
```

---

## MinIO (порты 9000 / 9001)

- Bucket: `s3-storage`
- Уведомления настроены через `mc event`:
  - `PUT` → `s3-load-events-topic`
  - `DELETE` → `s3-delete-events-topic`
- TLS-сертификаты: `minio_s3/certs/` (private.key, public.crt)
- Console UI: http://localhost:9001

---

## Kafka (порты 9092 / 9094)

- KRaft mode (без ZooKeeper)
- 9092 — внутренняя сеть Docker
- 9094 — внешний доступ для Spring Boot
- Топики создаются автоматически при старте через init-topics контейнер
