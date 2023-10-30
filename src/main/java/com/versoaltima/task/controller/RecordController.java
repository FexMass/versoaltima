package com.versoaltima.task.controller;

import com.versoaltima.task.dto.RecordDTO;
import com.versoaltima.task.service.XMLParserService;
import com.versoaltima.task.service.XMLProcessingService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/records")
public class RecordController {

    private final XMLProcessingService xmlProcessingService;
    private final XMLParserService xmlParserService;
    private final MessageSource messageSource;

    public RecordController(XMLProcessingService xmlProcessingService, XMLParserService xmlParserService, MessageSource messageSource) {
        this.xmlProcessingService = xmlProcessingService;
        this.xmlParserService = xmlParserService;
        this.messageSource = messageSource;
    }

    @PostMapping("/process")
    public ResponseEntity<String> processXML(@RequestBody String xmlContent) {
        log.info("XML process started...");
        List<RecordDTO> records;
        try {
            records = xmlParserService.parseXML(xmlContent);
        } catch (Exception e) {
            String errorMsg = messageSource.getMessage("xml.processing.error", null, Locale.getDefault()) + e.getMessage();
            return ResponseEntity.badRequest().body(errorMsg);
        }
        xmlProcessingService.processXML(records);
        return ResponseEntity.ok(messageSource.getMessage("xml.processed.successfully", null, Locale.getDefault()));
    }
}
