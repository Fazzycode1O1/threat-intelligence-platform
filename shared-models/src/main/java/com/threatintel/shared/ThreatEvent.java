package com.threatintel.shared;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ThreatEvent {
private String id;
    private String ip;
    private String domain;
    private String source;
    private int severity;
    private LocalDateTime timestamp;
}
