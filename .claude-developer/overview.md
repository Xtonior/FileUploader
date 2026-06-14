# FileUploader — Project Overview

## Что это

Микросервисная система загрузки файлов. Пользователь загружает файл через HTTP → файл сохраняется в MinIO (S3-совместимое хранилище) → MinIO шлёт событие в Kafka → dbapp слушает Kafka и пишет метаданные в PostgreSQL.

## Сервисы

| Сервис       | Порт  | Технология                        | Описание                                      |
|--------------|-------|-----------------------------------|-----------------------------------------------|
| `uploader`   | 8081  | Spring Boot 4.0.2 + WebFlux       | REST API для загрузки файлов в MinIO (S3)     |
| `dbapp`      | 8089  | Spring Boot + WebFlux + R2DBC     | Слушает Kafka, пишет метаданные в PostgreSQL  |
| PostgreSQL   | 5432  | Docker (`postgres:15-alpine`)     | Хранит метаданные файлов (`file_records`)     |
| Kafka        | 9094  | Docker (`apache/kafka:latest`)    | Шина событий между MinIO и dbapp             |
| MinIO        | 9000/9001 | Docker (`minio/minio:latest`) | S3-совместимое хранилище файлов               |

## Поток данных

```
Client POST /upload?userUuid=<UUID>
  → uploader (8081)
    → сохраняет файл локально (temp)
    → загружает в MinIO (bucket: s3-storage, key: s3-storage/<userUuid>/<filename>)
    → удаляет temp файл
  → MinIO генерирует событие → Kafka topic: s3-load-events-topic
  → dbapp слушает Kafka
    → парсит MinioEvent
    → проверяет дубликат
    → сохраняет в PostgreSQL (таблица file_records)
```

## Топики Kafka

| Топик                    | Назначение                           |
|--------------------------|--------------------------------------|
| `s3-load-events-topic`   | MinIO: событие PUT (загрузка файла)  |
| `s3-delete-events-topic` | MinIO: событие DELETE (удаление)     |
| `db-update-events-topic` | Зарезервирован (не используется)     |
| `sample-events-topic`    | Тестовый топик                       |

## Docker-сети

Все сервисы (MinIO, Kafka) в сети `file_uploader_network` (external). Нужно создать вручную: `docker network create file_uploader_network`.

## Тестирование

JMeter-план: `jmeter/Uploader.jmx`. Параметр файла: `file`.
