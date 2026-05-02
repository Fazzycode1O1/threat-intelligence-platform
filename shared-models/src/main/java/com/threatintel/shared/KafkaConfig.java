package com.threatintel.shared;

public final class KafkaConfig {
    public static final String BOOTSTRAP_SERVERS = "localhost:9092";
    public static final String IOC_RAW_TOPIC = "ioc_raw";
    public static final String IOC_VALIDATED_TOPIC = "ioc_validated";
    public static final String IOC_RANKED_TOPIC = "ioc_ranked";
    
    private KafkaConfig() {}
}
