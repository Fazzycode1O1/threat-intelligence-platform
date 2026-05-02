package com.threatintel.analytics.controller;

import com.threatintel.analytics.entity.IocEntity;
import com.threatintel.analytics.repository.IocRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api")
public class AnalyticsController {

    @Autowired
    private IocRepository iocRepository;

    @GetMapping("/iocs")
    public List<IocEntity> getIocs(
            @RequestParam(required = false) String severity,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) String source) {
        if (severity != null && type != null && source != null) {
            return iocRepository.findByRiskLevelAndTypeAndSource(severity, type, source);
        }
        return iocRepository.findAll();
    }

    @GetMapping("/iocs/high-risk")
    public List<IocEntity> getHighRisk() {
        return iocRepository.findHighRisk();
    }

    @GetMapping("/iocs/source/{source}")
    public List<IocEntity> getBySource(@PathVariable String source) {
        return iocRepository.findBySource(source);
    }
}

