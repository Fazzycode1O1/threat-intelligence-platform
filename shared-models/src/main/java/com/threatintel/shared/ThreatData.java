package com.threatintel.shared;

import lombok.Data;

@Data
public class ThreatData {
    private String id;
    private String ip;
    private String domain;
    private String source;
    private int severity;
    private long timestamp;
}
