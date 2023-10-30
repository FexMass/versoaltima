package com.versoaltima.task.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "recorda")
@Getter
@Setter
@NoArgsConstructor
public class RecordA extends BaseRecord {

    @ManyToOne
    @JoinColumn(name = "recordb_fk")
    private RecordB linkedRecordB;
}
