package com.threatintel.ranking;

import com.threatintel.ranking.service.RankingService;
import com.threatintel.shared.IocData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class RankingListener {

    private static final Logger logger = LoggerFactory.getLogger(RankingListener.class);

    @Autowired
    private RankingService rankingService;

    @KafkaListener(topics = "ioc_validated", groupId = "ranking-group")
    public void listen(IocData iocData) {
        logger.info("Validated IOC received for ranking: {}", iocData.getSource());
        rankingService.rankIoc(iocData);
    }
}
