package com.versoaltima.task.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recordb")
@Getter
@Setter
@NoArgsConstructor
public class RecordB extends BaseRecord {

    @ManyToOne
    @JoinColumn(name = "recordc_fk")
    private RecordC linkedRecordC;
}
