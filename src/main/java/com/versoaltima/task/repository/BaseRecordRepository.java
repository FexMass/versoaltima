package com.versoaltima.task.repository;

import com.versoaltima.task.entity.BaseRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

import java.util.Optional;

/**
 * Base repository interface for all record types.
 * <p>
 * This interface provides basic JPA operations for any entity that extends {@link BaseRecord}.
 * It also defines a custom method to fetch records by a string ID.
 * </p>
 *
 * @param <T> Type of the record. This type should extend {@link BaseRecord}.
 */
@NoRepositoryBean
public interface BaseRecordRepository<T extends BaseRecord> extends JpaRepository<T, Long> {
    Optional<T> findById(String id);
}
