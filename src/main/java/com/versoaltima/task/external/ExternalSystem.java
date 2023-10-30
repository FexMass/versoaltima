package com.versoaltima.task.external;

import java.util.List;
import java.util.Map;

/**
 * External system with one implemented method to simulate data
 */
public interface ExternalSystem {
    Map<String, String> getRecordDetails(String type, List<String> recordIds);
}
