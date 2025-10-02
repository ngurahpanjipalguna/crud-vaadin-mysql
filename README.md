# CRUD Vaadin MySQL

Proyek ini adalah aplikasi CRUD berbasis Vaadin yang terhubung dengan database MySQL, dikemas menggunakan Docker dan docker-compose untuk kemudahan deployment.

## Fitur
- CRUD data menggunakan Vaadin
- Database MySQL
- Konfigurasi Docker dan docker-compose

## Struktur Utama
- `Dockerfile`: Build dan jalankan aplikasi Java Vaadin
- `docker-compose.yml`: Orkestrasi container aplikasi dan database

## Cara Menjalankan

### 1. Prasyarat
- Docker & Docker Compose terinstal

### 2. Build & Jalankan dengan Docker Compose
```bash
docker-compose up --build
```

### 3. Akses Aplikasi
- Vaadin App: [http://localhost:8080](http://localhost:8080)
- MySQL: Port lokal 3308 (default user: root, password: root, database: cruddb)

## Konfigurasi Penting
- Database environment diatur pada `docker-compose.yml`
- Aplikasi Vaadin membaca konfigurasi database dari environment variable

## Tahapan Dockerfile
1. **Build**: Menggunakan Maven untuk build aplikasi Java
2. **Run**: Menjalankan aplikasi dari JAR hasil build

## Catatan
- Data MySQL disimpan di volume `mysql-data`
- Pastikan port 8080 dan 3308 tidak digunakan oleh aplikasi lain

## Lisensi
Proyek ini menggunakan lisensi open source.