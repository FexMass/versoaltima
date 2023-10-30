package com.versoaltima.task;

import com.versoaltima.task.dto.RecordDTO;
import com.versoaltima.task.entity.RecordA;
import com.versoaltima.task.entity.RecordB;
import com.versoaltima.task.entity.RecordC;
import com.versoaltima.task.external.ExternalSystem;
import com.versoaltima.task.repository.RecordARepository;
import com.versoaltima.task.repository.RecordBRepository;
import com.versoaltima.task.repository.RecordCRepository;
import com.versoaltima.task.service.XMLProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@SpringBootTest
public class XMLProcessingServiceTest {

    @Autowired
    private XMLProcessingService xmlProcessingService;

    @MockBean
    private ExternalSystem externalSystem;

    @MockBean
    private RecordARepository recordARepository;

    @MockBean
    private RecordBRepository recordBRepository;

    @MockBean
    private RecordCRepository recordCRepository;

    @BeforeEach
    public void setUp() {
        // Resetting mocks to ensure no leftover state from other tests
        Mockito.reset(externalSystem, recordARepository, recordBRepository, recordCRepository);

        when(externalSystem.getRecordDetails(anyString(), anyList())).thenReturn(Collections.emptyMap());
    }

    @Test
    @DisplayName("Given records of type A, B, and C, the service should process and save each record correctly")
    public void testProcessXMLBasicFlow() {
        // given
        RecordDTO record1 = new RecordDTO();
        record1.setType("A");
        record1.setId("1");

        RecordDTO record2 = new RecordDTO();
        record2.setType("B");
        record2.setId("2");

        RecordDTO record3 = new RecordDTO();
        record3.setType("C");
        record3.setId("3");

        List<RecordDTO> records = List.of(record1, record2, record3);

        // Mocking the external system to return some predefined details
        when(externalSystem.getRecordDetails("A", List.of("1"))).thenReturn(Map.of("1", "B1"));
        when(externalSystem.getRecordDetails("B", List.of("2"))).thenReturn(Map.of("2", "C2"));

        Map<String, String> resultMap = new HashMap<>();
        resultMap.put("3", null);
        when(externalSystem.getRecordDetails("C", List.of("3"))).thenReturn(resultMap);

        // when
        xmlProcessingService.processXML(records);

        // then
        verify(recordARepository, times(1)).save(any(RecordA.class));
        verify(recordBRepository, times(1)).save(any(RecordB.class));
        verify(recordCRepository, times(1)).save(any(RecordC.class));
    }

    @Test
    @DisplayName("Given a list of 15 records of type A, the service should batch the external system calls twice")
    public void testBatchingMechanism() {
        // given
        List<RecordDTO> records = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            RecordDTO record = new RecordDTO();
            record.setType("A");
            record.setId(String.valueOf(i));
            records.add(record);
        }

        // Mocking the external system to return empty details
        when(externalSystem.getRecordDetails(eq("A"), anyList())).thenReturn(Collections.emptyMap());

        // when
        xmlProcessingService.processXML(records);

        // then
        verify(externalSystem, times(2)).getRecordDetails(eq("A"), anyList());  // Should be called twice because of batching
    }

    @Test
    @DisplayName("Given a mock external system with delay, the service should throw a runtime exception")
    public void testDelayInMockExternalSystem() {
        // given
        List<RecordDTO> records = new ArrayList<>();
        for (int i = 0; i < 15; i++) {
            RecordDTO recordDTO = new RecordDTO();
            recordDTO.setType("A");
            recordDTO.setId(String.valueOf(i));
            records.add(recordDTO);
        }

        // Mock the behavior to throw an exception
        doThrow(new RuntimeException("Connection timeout due to too many record IDs"))
                .when(externalSystem).getRecordDetails(eq("A"), anyList());

        // when & then
        assertThrows(RuntimeException.class, () -> xmlProcessingService.processXML(records));
    }
}
