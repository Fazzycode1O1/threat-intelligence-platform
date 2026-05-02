package com.threatintel.ingestion.controller;

import com.threatintel.ingestion.dto.IngestRequest;
import com.threatintel.ingestion.service.IngestionService;
import com.threatintel.shared.IocData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class IngestionController {

    @Autowired
    private IngestionService ingestionService;

    @PostMapping("/ingest")
    public ResponseEntity<List<IocData>> ingestThreatData(@RequestBody IngestRequest request) {
        try {
            List<IocData> iocData = ingestionService.ingestAndPublish(request.getSource());
            return ResponseEntity.ok(iocData);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
