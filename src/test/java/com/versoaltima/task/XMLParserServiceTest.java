package com.versoaltima.task;

import com.versoaltima.task.dto.RecordDTO;
import com.versoaltima.task.service.XMLParserService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.xml.sax.SAXException;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
public class XMLParserServiceTest {

    @Autowired
    private XMLParserService xmlParserService;

    @Test
    @DisplayName("When parsing a valid XML content, it should return the correct RecordDTO representation")
    public void testValidXMLParsing() throws Exception {
        // given
        String validXmlContent = "<root><record><type>A</type><id>1</id></record></root>";

        // when
        List<RecordDTO> records = xmlParserService.parseXML(validXmlContent);

        // then
        assertEquals(1, records.size());
        assertEquals("A", records.get(0).getType());
        assertEquals("1", records.get(0).getId());
    }

    @Test
    @DisplayName("When parsing a malformed XML content, it should throw a SAXException")
    public void testInvalidXMLParsing() {
        // given
        String invalidXmlContent = "<root><record><type>A</type><id>1</record></root>";  // malformed XML

        // when & then
        assertThrows(SAXException.class, () -> xmlParserService.parseXML(invalidXmlContent));
    }
}