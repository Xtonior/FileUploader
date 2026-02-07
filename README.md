# Запуск

## Database
```
cd db
docker compose up -d
```

## Kafka
```
cd kafka
docker compose up -d
```

## Minio
```
cd minio_s3
docker compose up -d
```

## Database app
```
cd dbapp
./mvnw spring-boot:run
```

## Uploader app
```
cd uploader
./start.sh
```

# Тестирование
1. Откройте jmeter/Uploader.jmx
2. Подставьте файл в Add File -> File Uploads, Parameter Name: file 

![fileloader.png](https://github.com/Xtonior/FileUploader/blob/main/img/fileloader.png?raw=true)
