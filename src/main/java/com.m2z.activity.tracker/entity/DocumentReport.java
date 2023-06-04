package com.m2z.activity.tracker.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("report")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class DocumentReport {

    @Id
    private String id;

    private Object title;

    private Object report;

}
