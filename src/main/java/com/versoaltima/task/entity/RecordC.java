package com.versoaltima.task.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recordc")
@NoArgsConstructor
public class RecordC extends BaseRecord {
    // Since RecordC doesn't have any additional fields, it just extends BaseRecord.
}
