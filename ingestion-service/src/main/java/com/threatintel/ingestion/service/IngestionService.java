package com.threatintel.ingestion.service;

import com.threatintel.shared.IocData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class IngestionService {

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;

    public List<IocData> ingestAndPublish(String source) {
        List<IocData> iocs = new ArrayList<>();

        IocData ioc1 = new IocData();
        ioc1.setIp("8.8.8.8");
        ioc1.setDomain("example-malicious.com");
        ioc1.setSource(source);
        ioc1.setSeverity(5);
        iocs.add(ioc1);

        IocData ioc2 = new IocData();
        ioc2.setIp("1.1.1.1");
        ioc2.setDomain("test-bad.net");
        ioc2.setSource(source);
        ioc2.setSeverity(3);
        iocs.add(ioc2);

        iocs.forEach(ioc -> kafkaTemplate.send("ioc_raw", UUID.randomUUID().toString(), ioc));

        return iocs;
    }
}
