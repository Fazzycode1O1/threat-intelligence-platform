package com.threatintel.database;

import com.threatintel.database.entity.IocEntity;
import com.threatintel.database.repository.IocRepository;
import com.threatintel.shared.IocData;
import com.threatintel.shared.KafkaConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class DatabaseListener {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseListener.class);

    @Autowired
    private IocRepository iocRepository;

    @KafkaListener(topics = KafkaConfig.IOC_RANKED_TOPIC, groupId = "database-group")
    public void listen(IocData iocData) {
        logger.info("Ranked IOC received for storage: {}", iocData.getSource());

        IocEntity entity = new IocEntity();

        if (iocData.getIp() != null) {
            entity.setType("IP");
            entity.setValue(iocData.getIp());
        } else if (iocData.getDomain() != null) {
            entity.setType("DOMAIN");
            entity.setValue(iocData.getDomain());
        }

        entity.setSource(iocData.getSource());
        entity.setSeverityScore(iocData.getSeverity());
        entity.setRiskLevel(iocData.getClassification());
        entity.setTimestamp(LocalDateTime.now());

        iocRepository.save(entity);
        logger.info("IOC stored: type={}, value={}, risk={}", entity.getType(), entity.getValue(), entity.getRiskLevel());
    }
}
