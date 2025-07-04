
# Filespark Storage Service

A lightweight **Spring Boot REST API** for secure file storage with **MinIO** and metadata in **MongoDB**.  
All endpoints are documented via **Swagger**.

---

## ✨ Key Features
- Upload files with:
  - Custom name, PUBLIC / PRIVATE visibility
  - Up to 5 tags
  - Predefined file type (no free text)
- Prevent duplicates (by filename or content hash)
- Rename files without re-upload
- List files:
  - Public or owned files
  - Filter by tag (case-insensitive, tag from list)
  - Sort by filename, date, tag, type, size
  - Paginated results
- Secure download via unique link
- Delete only your own files

---

## ⚙ Tech & Build
- Java 17, Spring Boot 3.x, MongoDB, MinIO
- Springdoc OpenAPI (Swagger)
- Maven + Docker support

### Build & Run
```bash
mvn clean package
docker build -t filespark-storage .
docker run -p 8080:8080 filespark-storage
```

---

## 🔗 API Docs
Swagger UI: [http://localhost:8080/swagger-ui.html](http://localhost:8080/swagger-ui.html)

---

## Example API Endpoints
| Method | Path | Description |
|---------|------|-------------|
| POST | `/files/upload` | Upload a file |
| PUT | `/files/{id}/rename` | Rename file |
| GET | `/files/public` | List public files |
| GET | `/files/user` | List your files |
| GET | `/files/download/{link}` | Download file |
| DELETE | `/files/{id}` | Delete file |

---

## ⚙ Config (application.properties)
```properties
spring.data.mongodb.uri=mongodb://localhost:27017/filesparkdb
minio.endpoint=http://localhost:9000
minio.access-key=minio_user
minio.secret-key=minio_password
minio.bucket=filespark-bucket
```

---

## Requirements
- Java 17+
- MongoDB
- MinIO
