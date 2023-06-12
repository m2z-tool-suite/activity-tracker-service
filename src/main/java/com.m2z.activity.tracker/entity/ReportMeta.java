package com.m2z.activity.tracker.entity;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Document("report")
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ReportMeta {

    @Id
    private String id;

    private String graph_name;

    private List<VertexCollection> vertex_collections;
    private List<EdgeCollection> edge_definitions;


    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class VertexCollection {
        private String key_prefix;
        private String id;
        private String generateId;
        private List<String> props;
    }

    @NoArgsConstructor
    @AllArgsConstructor
    @Data
    public static class EdgeCollection {
        private String from_vertex_collections;
        private String to_vertex_collections;
    }

}
