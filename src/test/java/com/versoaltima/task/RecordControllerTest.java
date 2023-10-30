package com.versoaltima.task;

import com.versoaltima.task.controller.RecordController;
import com.versoaltima.task.dto.RecordDTO;
import com.versoaltima.task.external.ExternalSystem;
import com.versoaltima.task.service.XMLParserService;
import com.versoaltima.task.service.XMLProcessingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.web.servlet.MockMvc;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class RecordControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RecordController recordController;

    @Autowired
    private MessageSource messageSource;

    @MockBean
    private XMLProcessingService xmlProcessingService;

    @MockBean
    private ExternalSystem externalSystem;

    @MockBean
    private XMLParserService xmlParserService;

    @BeforeEach
    public void setUp() {
        // Resetting mocks to ensure no leftover state from other tests
        Mockito.reset(externalSystem, xmlProcessingService, xmlParserService);

        when(externalSystem.getRecordDetails(anyString(), anyList())).thenReturn(Collections.emptyMap());
    }

    @Test
    @DisplayName("When processing XML with valid 'A' type, it should successfully save RecordA")
    public void testProcessXMLWithValidTypeA() throws Exception {
        // given
        String xmlContent = "<root><record><type>A</type><id>1</id></record></root>";
        RecordDTO recordDTO = new RecordDTO();
        recordDTO.setType("A");
        recordDTO.setId("1");

        List<RecordDTO> mockParsedRecords = List.of(recordDTO);

        when(xmlParserService.parseXML(xmlContent)).thenReturn(mockParsedRecords);

        Map<String, String> mockDetails = Collections.singletonMap("1", "B1");
        when(externalSystem.getRecordDetails(anyString(), anyList())).thenReturn(mockDetails);

        // when
        mockMvc.perform(post("/api/records/process")
                        .contentType(MediaType.APPLICATION_XML)
                        .content(xmlContent))
                .andExpect(status().isOk())
                .andExpect(content().string("XML processed successfully"));

        // then
        verify(xmlParserService, times(1)).parseXML(xmlContent);
        verify(xmlProcessingService, times(1)).processXML(mockParsedRecords);
    }

    @Test
    @DisplayName("When processing invalid XML content, it should return a BAD_REQUEST status and appropriate error message")
    public void testProcessXMLParseException() throws IOException, SAXException {
        // given
        String xmlContent = "invalid_xml_content";

        // Mock xmlParserService to throw an exception
        when(xmlParserService.parseXML(xmlContent))
                .thenThrow(new RuntimeException("Parsing Failed"));

        // when
        ResponseEntity<String> response = recordController.processXML(xmlContent);

        // then
        String expectedErrorMsg = messageSource.getMessage("xml.processing.error", null, Locale.getDefault()) + "Parsing Failed";
        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        assertEquals(expectedErrorMsg, response.getBody());
    }
}
