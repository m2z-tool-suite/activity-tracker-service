package com.m2z.activity.tracker.entity;

import com.arangodb.springframework.annotation.ArangoId;
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
public class GraphReport {

    @Id
    private String id;

    @ArangoId
    private String arangoId;

    private Object report;

}
