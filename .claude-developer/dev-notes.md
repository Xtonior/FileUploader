# Developer Notes

## Порядок запуска

1. Создать Docker-сеть (один раз): `docker network create file_uploader_network`
2. `cd db && docker compose up -d`
3. `cd kafka && docker compose up -d`
4. `cd minio_s3 && docker compose up -d`
5. `cd dbapp && ./mvnw spring-boot:run`
6. `cd uploader && ./start.sh`

## Известные особенности / потенциальные проблемы

### uploader
- Файл временно сохраняется локально (temp dir), потом удаляется. При сбое S3-загрузки temp файл может не удалиться.
- `@Autowired` используется вместе с `@RequiredArgsConstructor` — это избыточно (поле + конструктор). Стоит выбрать одно.
- `S3AsyncClientConfig` — конфигурация клиента. Важно убедиться, что endpoint правильно указан для MinIO (path-style access).

### dbapp
- `MinioNotificationListener.handleUploads` использует `.block()` внутри Kafka-listener — это блокирующий вызов в реактивном стеке, потенциально проблематично под нагрузкой.
- Дедупликация по `userGuid + uploadDate` — если дата округляется/меняется, дубликаты могут проходить.
- `handleLifecycle` (delete) использует `.subscribe()` — огонь и забыл, ошибки не всплывают наверх.
- Key в MinIO парсится эвристически (split по "/", поиск UUID) — хрупко, если структура пути изменится.

### Общее
- Нет Docker Compose для `uploader` и `dbapp` — только dev-запуск через mvnw.
- Нет общего `docker-compose.yml` для всей системы.
- Нет swagger/OpenAPI документации.
- Нет health-check эндпоинтов (actuator).
- `pom.xml` в корне (`kz.lab:microservices-parent`) содержит модули `common`, `product`, `delivery`, `report` — но этих модулей нет в репозитории, это артефакт от другого проекта.

## Env переменные (uploader)

| Переменная            | Дефолт                  |
|-----------------------|-------------------------|
| `MINIO_ROOT_USER`     | `myadmin`               |
| `MINIO_ROOT_PASSWORD` | `myverysecretpassword`  |
| `MINIO_ENDPOINT`      | `http://localhost:9000` |
| `MINIO_BUCKET`        | `s3-storage`            |

## Структура S3-ключа

```
s3-storage/<userUuid>/<filename>
```

Пример: `s3-storage/550e8400-e29b-41d4-a716-446655440000/photo.jpg`
