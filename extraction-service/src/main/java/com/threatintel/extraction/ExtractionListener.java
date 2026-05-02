package com.threatintel.extraction;

import com.threatintel.extraction.service.ExtractionService;
import com.threatintel.shared.IocData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ExtractionListener {

    private static final Logger logger = LoggerFactory.getLogger(ExtractionListener.class);

    @Autowired
    private ExtractionService extractionService;

    @KafkaListener(topics = "ioc_raw", groupId = "extraction-group")
    public void listen(IocData iocData) {
        logger.info("Raw IOC received: {}", iocData.getSource());
        extractionService.processIocData(iocData);
    }
}
