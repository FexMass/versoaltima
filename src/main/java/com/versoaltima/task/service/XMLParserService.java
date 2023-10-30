package com.versoaltima.task.service;

import com.versoaltima.task.dto.RecordDTO;
import com.versoaltima.task.service.xml.RecordHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.io.StringReader;
import java.util.List;

/**
 * Service responsible for parsing XML content.
 * <p>
 * This service utilizes the SAX (Simple API for XML) parser to read XML content and extract record details.
 * </p>
 */
@Service
public class XMLParserService {

    private final XMLReader reader;

    public XMLParserService() throws SAXException, ParserConfigurationException {
        this.reader = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
    }

    /**
     * Parses the provided XML content and extracts record details.
     *
     * @param xmlContent The XML content to be parsed.
     * @return A list of {@link RecordDTO} objects containing the details of the parsed records.
     * @throws SAXException If any SAX errors occur during processing.
     * @throws IOException  If any IO errors occur.
     */
    public List<RecordDTO> parseXML(String xmlContent) throws SAXException, IOException {
        RecordHandler handler = new RecordHandler();
        reader.setContentHandler(handler);
        reader.parse(new InputSource(new StringReader(xmlContent)));
        return handler.getRecords();
    }
}