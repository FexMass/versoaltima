package com.versoaltima.task.service;

import com.versoaltima.task.dto.RecordDTO;
import com.versoaltima.task.entity.BaseRecord;
import com.versoaltima.task.entity.RecordA;
import com.versoaltima.task.entity.RecordB;
import com.versoaltima.task.entity.RecordC;
import com.versoaltima.task.enums.RecordType;
import com.versoaltima.task.external.ExternalSystem;
import com.versoaltima.task.repository.RecordARepository;
import com.versoaltima.task.repository.RecordBRepository;
import com.versoaltima.task.repository.RecordCRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Service responsible for processing XML content and persisting records based on the parsed data.
 * <p>
 * The service interacts with external systems to fetch details for the records and persists
 * them into the appropriate repositories based on their types.
 * </p>
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class XMLProcessingService {

    private final RecordARepository recordARepository;
    private final RecordBRepository recordBRepository;
    private final RecordCRepository recordCRepository;
    private final ExternalSystem externalSystem;

    /**
     * Processes the provided list of {@link RecordDTO} objects by fetching additional details,
     * linking related records, and persisting them.
     * <p>
     * This method initiates a transaction to ensure atomicity of the operations.
     * </p>
     *
     * @param records List of {@link RecordDTO} objects to be processed.
     */
    @Transactional
    public void processXML(List<RecordDTO> records) {
        log.info("Transaction started...");
        Map<String, String> allDetails = fetchRecordDetails(records);
        processRecords(records, allDetails);
        log.info("Process completed successfully, records saved.");
    }

    /**
     * Fetches additional details for the provided list of {@link RecordDTO} objects.
     * <p>
     * Details are fetched in batches from an external system.
     * </p>
     *
     * @param records List of {@link RecordDTO} objects for which details need to be fetched.
     * @return A map containing the record ID and its corresponding detail.
     */
    private Map<String, String> fetchRecordDetails(List<RecordDTO> records) {
        Map<String, List<RecordDTO>> groupedRecords = records.stream().collect(Collectors.groupingBy(RecordDTO::getType));
        Map<String, String> allDetails = new HashMap<>();

        for (Map.Entry<String, List<RecordDTO>> entry : groupedRecords.entrySet()) {
            String type = entry.getKey();
            List<RecordDTO> recordList = entry.getValue();

            // batching by 10 to improve performance and avoid potential timeout defined by external system
            for (int record = 0; record < recordList.size(); record += 10) {
                List<RecordDTO> batch = recordList.subList(record, Math.min(record + 10, recordList.size()));
                List<String> batchIds = batch.stream().map(RecordDTO::getId).collect(Collectors.toList());

                Map<String, String> details = externalSystem.getRecordDetails(type, batchIds);
                allDetails.putAll(details);
            }
        }
        return allDetails;
    }

    private void processRecords(List<RecordDTO> records, Map<String, String> allDetails) {
        records.forEach(recordDTO -> this.processRecord(recordDTO, allDetails));
    }

    /**
     * Processes an individual {@link RecordDTO} object and persists it.
     * <p>
     * Based on the record type, the appropriate repository is used for persistence.
     * Additionally, linked records are set based on the details fetched from the external system.
     * </p>
     *
     * @param recordDTO The {@link RecordDTO} object to be processed.
     * @param allDetails Map containing the record ID and its corresponding detail.
     */
    private void processRecord(RecordDTO recordDTO, Map<String, String> allDetails) {
        RecordType type = RecordType.valueOf(recordDTO.getType());

        String linkedId = allDetails.get(recordDTO.getId());

        switch (type) {
            case A:
                RecordA recordA = new RecordA();
                if (linkedId != null) {
                    recordA.setLinkedRecordB(recordBRepository.findById(linkedId).orElse(null));
                }
                processRecord(recordA, recordDTO.getId(), recordARepository);
                break;
            case B:
                RecordB recordB = new RecordB();
                if (linkedId != null) {
                    recordB.setLinkedRecordC(recordCRepository.findById(linkedId).orElse(null));
                }
                processRecord(recordB, recordDTO.getId(), recordBRepository);
                break;
            case C:
                RecordC recordC = new RecordC();
                processRecord(recordC, recordDTO.getId(), recordCRepository);
                break;
        }
    }

    private <T extends BaseRecord> void processRecord(T record, String id, JpaRepository<T, Long> repository) {
        record.setId(id);
        repository.save(record);
    }
}
