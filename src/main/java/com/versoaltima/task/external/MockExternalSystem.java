package com.versoaltima.task.external;

import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * This one shouldn't exist, but since configuration about external system was not provided, this Mocked one was written
 */
@Service
public class MockExternalSystem implements ExternalSystem {

    @Override
    public Map<String, String> getRecordDetails(String type, List<String> recordIds) {
        Map<String, String> resultMap = new HashMap<>();

        // Check for connection timeout scenario
        if (recordIds.size() > 10) {
            throw new RuntimeException("Connection timeout due to too many record IDs");
        }

        // Introduce a delay based on the number of record IDs: 200ms + 50ms * number of record IDs
        try {
            Thread.sleep(200 + 50 * recordIds.size());
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Failed to simulate the delay", e);
        }

        // Mocking data based on record type
        switch (type) {
            case "A":
                for (String id : recordIds) {
                    if (isNumeric(id)) {
                        resultMap.put(id, Integer.parseInt(id) % 3 == 0 ? null : "B" + id); // adding null value every third record
                    } else {
                        // Handle non-numeric IDs
                        resultMap.put(id, "B" + id);
                    }
                }
                break;
            case "B":
                for (String id : recordIds) {
                    if (isNumeric(id)) {
                        resultMap.put(id, Integer.parseInt(id) % 4 == 0 ? null : "C" + id); // adding null value every fourth record
                    } else {
                        // Handle non-numeric IDs
                        resultMap.put(id, "C" + id);
                    }
                }
                break;

            case "C":
                for (String id : recordIds) {
                    resultMap.put(id, null);  // "C" type records have no linked records
                }
                break;
        }

        return resultMap;
    }

    private static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
