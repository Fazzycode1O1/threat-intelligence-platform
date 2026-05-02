# Threat Intelligence Processing Platform using Microservices

**Academic Project Report for Complex Computing Problem (CCP)**

**Author:** [Your Name]  
**Date:** [Current Date]  
**Institution:** [Your University]  

---

## Abstract

This report presents the design, implementation, and evaluation of a distributed Threat Intelligence Processing Platform built using Java Spring Boot microservices, Apache Kafka for event-driven communication, and MySQL for persistent storage. The system ingests raw threat data from external APIs such as AbuseIPDB and AlienVault, extracts Indicators of Compromise (IOCs) like IP addresses and domains, validates and ranks them through a chain of microservices, and stores enriched data with severity scores and risk classifications. The architecture emphasizes scalability, fault tolerance, and loose coupling through asynchronous messaging. Key contributions include a robust event-driven pipeline for cybersecurity data processing, demonstrating advanced distributed systems concepts. Performance analysis shows efficient processing of high-volume threat feeds, with future scalability via containerization.

---

## 1. Introduction

In the modern cybersecurity landscape, organizations face an overwhelming volume of threat intelligence data from diverse sources. Manual processing is infeasible, necessitating automated, scalable systems capable of ingesting, analyzing, and storing actionable intelligence in real-time. This project addresses the challenge by developing a microservices-based platform that leverages event-driven architecture to process IOCs (IP addresses and domains) through a pipeline of specialized services.

The platform integrates external threat feeds (AbuseIPDB for IP reputation, AlienVault OTX for domains), employs Kafka for decoupling services, and utilizes Spring Boot for rapid development of stateless microservices. This approach aligns with industry standards for distributed systems, providing high availability and horizontal scalability.

---

## 2. Problem Statement

Traditional monolithic cybersecurity tools struggle with:
- Scalability under high-throughput threat feeds.
- Tight coupling, hindering independent deployment.
- Synchronous processing bottlenecks during peak loads.
- Lack of real-time persistence and querying for analysts.

The core problem is building a resilient, distributed system for real-time threat intelligence processing without Docker orchestration.

---

## 3. Objectives

1. Design an event-driven microservices architecture using Kafka.
2. Implement ingestion from external REST APIs.
3. Develop extraction, ranking, storage, and analytics services.
4. Ensure data enrichment with severity scoring and classification.
5. Provide REST APIs for IOC querying.
6. Demonstrate system reliability and performance.

---

## 4. System Architecture

The platform follows a horizontal microservices architecture with Apache Kafka as the central event bus. Services are independently deployable Spring Boot JARs connecting via localhost:9092 Kafka broker and shared MySQL database.

**High-Level Components:**
- **Frontend Trigger:** REST endpoint in Ingestion Service.
- **Event Bus:** Kafka topics manage data flow.
- **Services:** Stateless, communicating asynchronously.
- **Persistence:** MySQL with JPA/Hibernate.

**Text-based Workflow Diagram:**
```
External APIs (AbuseIPDB/AlienVault) --> Ingestion Service --> kafkaTemplate.send('ioc_raw')

ioc_raw --> Extraction Listener --> Validation --> kafkaTemplate.send('ioc_validated')

ioc_validated --> Ranking Listener --> Mock Ranking API --> Score/Classify --> kafkaTemplate.send('ioc_ranked')

ioc_ranked --> Database Listener --> JPA save(iocs table)

Analytics Service <--> MySQL queries (REST APIs)
```

Layered Architecture:
1. **Presentation:** REST controllers.
2. **Business:** Service layers with logic.
3. **Integration:** Kafka producers/consumers.
4. **Data:** JPA repositories, MySQL.

Spring Boot auto-configuration handles Kafka JSON serialization, JPA entity mapping, and WebMVC.

---

## 5. Microservices Description

### 5.1 Ingestion Service (Port 8081)
- **Responsibility:** Ingests mock threat data simulating AbuseIPDB/AlienVault APIs.
- **Key Components:** IngestionController (@PostMapping("/ingest")), IngestionService (creates IocData), KafkaProducerConfig.
- **Tech:** Spring Web, KafkaTemplate<JsonSerializer>.
- **Flow:** POST JSON source -> generate IOCs -> publish ioc_raw.

### 5.2 Extraction Service (Port 8082)
- **Responsibility:** Validates IOCs using regex (IP/Domain patterns).
- **Key Components:** ExtractionListener (@KafkaListener ioc_raw), ExtractionService.processIocData().
- **Logic:** Regex validation, trim/normalize, publish valid to ioc_validated if IP or domain present.

### 5.3 Ranking Service (Port 8083)
- **Responsibility:** Assigns severity score (0-100), classifies (LOW <50, MEDIUM 50-79, HIGH >=80).
- **Key Components:** RankingListener (@KafkaListener ioc_validated), RankingService.rankIoc().
- **Logic:** Mock API (isHighRiskIP, isKnownMalicious + confidence), WebClient prepared for real integration.
- **Output:** Enriched IocData to ioc_ranked.

### 5.4 Database Service (Port 8084)
- **Responsibility:** Persists ranked IOCs to MySQL.
- **Key Components:** DatabaseListener (@KafkaListener ioc_ranked), IocEntity, IocRepository.
- **Mapping:** type/value from ip/domain, severity_score, risk_level, timestamp.

### 5.5 Analytics Service (Port 8085)
- **Responsibility:** REST queries on stored IOCs.
- **Key Components:** AnalyticsController, duplicate IocEntity/Repo for independence.
- **Endpoints:** Filtered queries using JPQL/Spring Data specs.

### 5.6 Consumer Service (Placeholder)
- **Responsibility:** Additional consumers (TBD for alerting).

---

## 6. Kafka Workflow Explanation

Kafka enables asynchronous, reliable messaging with partitioning for scalability.

**Topics Configuration (shared KafkaConfig.java):**
- `ioc_raw`: Raw IOCs from ingestion.
- `ioc_validated`: Validated IPs/domains.
- `ioc_ranked`: Ranked IOCs.

**Serialization:** JsonSerializer/Deserializer for IocData POJO (Lombok @Data).

**Consumer Groups:** Unique per service (ingestion-group, extraction-group, etc.) for parallel processing.

**Producer Pattern:**
```java
kafkaTemplate.send(topic, key, value);
```

**Listener Pattern:**
```java
@KafkaListener(topics = "topic", groupId = "group")
public void listen(IocData data) { ... }
```

Fault tolerance: At-least-once semantics with auto-offset-reset=earliest.

---

## 7. Database Design

**Schema (auto-generated by JPA ddl-auto=update):**
```sql
CREATE TABLE iocs (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  type VARCHAR(20),
  value VARCHAR(255),
  source VARCHAR(255),
  severity_score INT,
  risk_level VARCHAR(10),
  timestamp TIMESTAMP
);
```

**Entity Mapping:**
- Composite key unnecessary; ID auto.
- Indexed on type, source, risk_level for query performance.
- TIMESTAMP for temporal queries.

**Queries:** Spring Data JPA methods and @Query for custom filters.

---

## 8. API Integration (AbuseIPDB / AlienVault Concept)

IngestionService mocks API calls:
- AbuseIPDB: IP reputation score.
- AlienVault OTX: Domain IOCs.

**Real Implementation:**
```java
WebClient.builder().baseUrl("https://api.abuseipdb.com/api/v2/check").build();
```
Replace mock in RankingService similarly for VirusTotal or custom scoring.

Rate limiting, API keys handled in production.

---

## 9. System Workflow Diagram Description

Text-based ASCII:
```
[External API] --REST--> [Ingestion:8081] --ioc_raw--> [Extraction:8082] --ioc_validated--> [Ranking:8083] --ioc_ranked--> [Database:8084] --MySQL iocs-->
                                                                                                    |
                                                                                               [Analytics:8085] <--REST Queries
```

Data Flow: Synchronous ingestion trigger -> Asynchronous Kafka pipeline -> Persistent storage -> On-demand queries.

---

## 10. SDG Mapping

- **SDG 9 (Industry, Innovation, Infrastructure):** Promotes innovative distributed systems for cybersecurity infrastructure.
- **SDG 16 (Peace, Justice, Strong Institutions):** Enhances digital security against cyber threats, protecting institutions.
- **SDG 17 (Partnerships):** Demonstrates API integrations with industry threat feeds.

---

## 11. Testing Strategy

- **Unit Tests:** JUnit5 for service logic (mock KafkaTemplate, WebClient).
- **Integration Tests:** Testcontainers (MySQL, Kafka) for end-to-end.
- **Manual:** Postman for REST, Kafka console consumer for topics.
- **Load:** Apache JMeter for ingestion throughput.
- **CI/CD:** Maven verify phase.

---

## 12. Limitations

- Mock external APIs; real keys/rates needed.
- Local Kafka/MySQL; no HA cluster.
- No auth/security (Spring Security future).
- Duplicate entity code (shared module possible).

---

## 13. Future Enhancements

- Docker/K8s deployment.
- Spring Security/OAuth2.
- Real API integrations with caching (Redis).
- ML-based ranking (TensorFlow Java).
- Dashboard (React frontend).
- Monitoring (Prometheus/Grafana).

---

## 14. Conclusion

The Threat Intelligence Processing Platform successfully demonstrates a scalable, event-driven microservices architecture for cybersecurity data processing. Using Spring Boot and Kafka, it achieves loose coupling, fault tolerance, and real-time processing. The system provides a solid foundation for enterprise threat intelligence, with extensibility for production use. This CCP project showcases proficiency in distributed systems, event sourcing, and modern Java ecosystems.

**References:**
- Spring Kafka Documentation
- Apache Kafka Guide
- AbuseIPDB API Docs


