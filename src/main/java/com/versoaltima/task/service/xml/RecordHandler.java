package com.versoaltima.task.service.xml;

import com.versoaltima.task.dto.RecordDTO;
import lombok.Getter;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

/**
 * Sax parser class responsible for parsing XML root elements
 */
public class RecordHandler extends DefaultHandler {

    @Getter
    private List<RecordDTO> records;
    private RecordDTO currentRecord;

    private final Stack<String> elementStack = new Stack<>();

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        this.elementStack.push(qName);
        if ("record".equalsIgnoreCase(qName)) {
            currentRecord = new RecordDTO();
            if (records == null) {
                records = new ArrayList<>();
            }
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        if ("record".equalsIgnoreCase(qName)) {
            records.add(currentRecord);
        }
        this.elementStack.pop();
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        String value = new String(ch, start, length).trim();
        if (currentRecord != null) {
            if (value.isEmpty()) return; // ignore whitespace

            if ("type".equalsIgnoreCase(currentElement())) {
                currentRecord.setType(value);
            } else if ("id".equalsIgnoreCase(currentElement())) {
                currentRecord.setId(value);
            }
        }
    }

    private String currentElement() {
        return this.elementStack.peek();
    }
}
