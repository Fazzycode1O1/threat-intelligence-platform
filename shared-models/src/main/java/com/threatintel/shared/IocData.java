package com.threatintel.shared;

import java.io.Serializable;

public class IocData implements Serializable {

    private String ip;
    private String domain;
    private String source;
    private int severity;           // 0–100
    private String classification;  // LOW / MEDIUM / HIGH

    public String getIp() { return ip; }
    public void setIp(String ip) { this.ip = ip; }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public String getSource() { return source; }
    public void setSource(String source) { this.source = source; }

    public int getSeverity() { return severity; }
    public void setSeverity(int severity) { this.severity = severity; }

    public String getClassification() { return classification; }
    public void setClassification(String classification) { this.classification = classification; }

    @Override
    public String toString() {
        return "IocData{ip='" + ip + "', domain='" + domain + "', source='" + source +
               "', severity=" + severity + ", classification='" + classification + "'}";
    }
}
