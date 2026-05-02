# Installation and Run Guide

## Prerequisites

- Java 17+
- Maven 3.8+
- MySQL 8+ running on `localhost:3306`
  - Username: `root`, Password: `password`
  - Database `threat_intel` is created automatically on first run
- Apache Kafka running on `localhost:9092` (single broker is fine)

## Kafka Setup

**Start Zookeeper and broker** (run each in a separate terminal):
```bash
bin/zookeeper-server-start.sh config/zookeeper.properties
bin/kafka-server-start.sh config/server.properties
```

**Create required topics:**
```bash
bin/kafka-topics.sh --create --topic ioc_raw --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic ioc_validated --bootstrap-server localhost:9092
bin/kafka-topics.sh --create --topic ioc_ranked --bootstrap-server localhost:9092
```

## Build

Run from the project root (builds all 7 modules including `analytics-service`):
```bash
mvn clean install -DskipTests
```

## Run Services

Start each service in a separate terminal **in this order** (Kafka and MySQL must be running first):

1. `cd database-service && mvn spring-boot:run` — port **8084**, creates the `iocs` table
2. `cd ingestion-service && mvn spring-boot:run` — port **8081**, REST entry point
3. `cd extraction-service && mvn spring-boot:run` — port **8082**, validates IOCs
4. `cd ranking-service && mvn spring-boot:run` — port **8083**, scores and classifies IOCs
5. `cd analytics-service && mvn spring-boot:run` — port **8085**, REST query API
6. `cd consumer-service && mvn spring-boot:run` — no HTTP port, background Kafka listener

> **Why database-service first?** The analytics-service uses `ddl-auto: validate` and requires the `iocs` table to exist at startup.

## Test

**Trigger ingestion** (sends two mock IOCs through the full pipeline):
```bash
curl -X POST http://localhost:8081/api/ingest \
  -H "Content-Type: application/json" \
  -d '{"source":"abuseipdb"}'
```

**Query high-risk IOCs** (after a few seconds for Kafka to process):
```bash
curl http://localhost:8085/api/iocs/high-risk
```

**Query all IOCs:**
```bash
curl http://localhost:8085/api/iocs
```

**Query by source:**
```bash
curl http://localhost:8085/api/iocs/source/abuseipdb
```

**Check MySQL directly:**
```bash
mysql -u root -ppassword threat_intel -e "SELECT * FROM iocs LIMIT 10;"
```

## API Reference

| Method | URL | Description |
|--------|-----|-------------|
| POST | `http://localhost:8081/api/ingest` | Ingest threat data by source name |
| GET | `http://localhost:8085/api/iocs` | All IOCs (optional filters: `?severity=HIGH&type=ip&source=abuseipdb`) |
| GET | `http://localhost:8085/api/iocs/high-risk` | IOCs with severity score >= 50 |
| GET | `http://localhost:8085/api/iocs/source/{source}` | IOCs filtered by source |

## Kafka Data Flow

```
POST /api/ingest
      |
 ingestion-service --> [ioc_raw] --> extraction-service --> [ioc_validated]
                                                                   |
                                              ranking-service --> [ioc_ranked]
                                                                       |
                                                        database-service --> MySQL (iocs table)
                                                                                    |
                                                                       analytics-service (read-only)
```

## Troubleshooting

| Symptom | Likely cause | Fix |
|---------|-------------|-----|
| Kafka connection refused | Broker not started | Start Zookeeper then broker; check port 9092 |
| analytics-service fails on startup | `iocs` table missing | Start database-service first and wait for it to create the schema |
| MySQL connection error | Wrong credentials or DB not running | Update `spring.datasource` in each service's `application.yml` |
| 404 on `/ingest` | Wrong URL | Use `/api/ingest`, not `/ingest` |
| No data from analytics API | Pipeline not processed yet | Wait a few seconds after ingestion; check service logs for Kafka flow |
| Compile error | Build order issue | Run `mvn clean install -DskipTests` from the project root |
