package com.threatintel.ranking.service;

import com.threatintel.shared.IocData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class RankingService {

    private static final Logger logger = LoggerFactory.getLogger(RankingService.class);

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public void rankIoc(IocData iocData) {
        logger.info("Ranking IOC: IP={}, Domain={}", iocData.getIp(), iocData.getDomain());

        int score = calculateScore(iocData);
        iocData.setSeverity(score);
        iocData.setClassification(getClassification(score));

        logger.info("Ranked IOC score={}, classification={}", score, iocData.getClassification());
        String key = iocData.getIp() != null ? iocData.getIp() : iocData.getDomain();
        kafkaTemplate.send("ioc_ranked", key, iocData);
    }

    private int calculateScore(IocData iocData) {
        int score = 30;
        if (iocData.getIp() != null && isHighRiskIp(iocData.getIp())) score += 40;
        if (iocData.getDomain() != null && isKnownMalicious(iocData.getDomain())) score += 50;
        return Math.min(score, 100);
    }

    private boolean isHighRiskIp(String ip) {
        return ip.startsWith("192.168.") || ip.startsWith("10.");
    }

    private boolean isKnownMalicious(String domain) {
        return domain.contains("malware") || domain.contains("phish");
    }

    private String getClassification(int score) {
        if (score >= 80) return "HIGH";
        if (score >= 50) return "MEDIUM";
        return "LOW";
    }
}
