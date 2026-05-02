package com.threatintel.extraction.service;

import com.threatintel.shared.IocData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class ExtractionService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    private static final Pattern IP_PATTERN =
        Pattern.compile("^(\\d{1,3}\\.){3}\\d{1,3}$");
    private static final Pattern DOMAIN_PATTERN =
        Pattern.compile("^[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?(\\.[a-zA-Z0-9]([a-zA-Z0-9\\-]{0,61}[a-zA-Z0-9])?)*\\.[a-zA-Z]{2,}$");

    public void processIocData(IocData iocData) {
        IocData validated = new IocData();
        validated.setSource(iocData.getSource());
        validated.setSeverity(iocData.getSeverity());

        if (iocData.getIp() != null && IP_PATTERN.matcher(iocData.getIp()).matches()) {
            validated.setIp(iocData.getIp().trim());
        }

        if (iocData.getDomain() != null && DOMAIN_PATTERN.matcher(iocData.getDomain()).matches()) {
            validated.setDomain(iocData.getDomain().toLowerCase().trim());
        }

        if (validated.getIp() != null || validated.getDomain() != null) {
            String key = validated.getIp() != null ? validated.getIp() : validated.getDomain();
            kafkaTemplate.send("ioc_validated", key, validated);
        }
    }
}
