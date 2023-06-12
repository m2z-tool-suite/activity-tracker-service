//package com.m2z.activity.tracker.service.impl;
//
//import com.arangodb.ArangoCollection;
//import com.arangodb.ArangoDB;
//import com.arangodb.ArangoDatabase;
//import com.arangodb.ArangoGraph;
//import com.arangodb.entity.BaseDocument;
//import com.arangodb.entity.EdgeDefinition;
//import com.arangodb.entity.GraphEntity;
//import com.arangodb.internal.ArangoDatabaseImpl;
//import com.arangodb.internal.ArangoGraphImpl;
//import jakarta.annotation.PostConstruct;
//import org.hibernate.graph.Graph;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Component;
//
//import java.util.*;
//
//@Component
//public class GraphGenerator {
//
//    @Autowired
//    private ArangoDB arangoDB;
//
//    private ArangoDatabase db = arangoDB.db("mydb");
//
//    @PostConstruct
//    private void postConstruct() {
//        db.create();
//
//        ArangoCollection collection = db.collection("firstCollection");
//        System.out.println("Creating collection...");
//        collection.create();
//
//        String key = "myKey";
//        BaseDocument doc = new BaseDocument(key);
//        doc.addAttribute("a", "Foo");
//        doc.addAttribute("b", 42);
//        System.out.println("Inserting document...");
//        collection.insertDocument(doc);
//    }
//
//    public String generate(List<Map<String, Object>> rows) {
//
//        Map<String, Object> meta = rows.get(0);
//        String graphName = meta.get("graph_name").toString();
//        UUID id = UUID.randomUUID();
//
//
//        ArangoGraphImpl graph = (ArangoGraphImpl) db;
//        ArangoDatabaseImpl dbImpl = (ArangoDatabaseImpl) db;
//
//        ArangoGraph arangoGraph = dbImpl.graph(graphName);
//        arangoGraph.vertexCollection()
//
//        Map<String, Object> vertexCollections = new HashMap<>();
//
//        for (String vertexName : (List<String>) meta.get("vertex_collections")) {
//            vertexCollections.put(vertexName, graph.addVertexCollection(
//                    graphName + "_" + vertexName + "_" + id));
//
//            graph.addVertexCollection(graphName + "_" + vertexName + "_" + id);
//        }
//        EdgeDefinition edgeDefinition1 = new EdgeDefinition();
//
//
//        Map<String, EdgeDefinition> edgeDefinitions = new HashMap<>();
//
//        for (Map.Entry<String, Map<String, Object>> entry : ((Map<String, Map<String, Object>>) meta.get("edge_definitions")).entrySet()) {
//            String edgeName = entry.getKey();
//            Map<String, Object> edgeDefinition = entry.getValue();
//            EdgeDefinition edgeDefinition2 = new EdgeDefinition();
//
//            edgeDefinition2.collection(graphName + "_" + edgeName + "_" + id);
//            edgeDefinition2.from(String.valueOf(edgeDefinition.get("from_vertex_collections")));
//            edgeDefinition2.to(String.valueOf(edgeDefinition.get("to_vertex_collections")));
//
//            graph.addEdgeDefinition(edgeDefinition2);
//
//            edgeDefinitions.put(edgeName, edgeDefinition2);
//        }
//
//        for (Map<String, Object> row : rows) {
//            for (Map.Entry<String, Object> entry : vertexCollections.entrySet()) {
//                String vertexName = entry.getKey();
//                Object vertexCollection = entry.getValue();
//                List<Map<String, Object>> dataList = (List<Map<String, Object>>) meta.get("vertex_collections").get(vertexName).get("data");
//
//                for (Map<String, Object> data : dataList) {
//                    Object keyValue = row.get(data.get("_key"));
//                    if (keyValue != null && vertexCollection.get(keyValue.toString()) == null) {
//                        vertexCollection.insert(fillMetaDescription(
//                                data.get("template"), row, (List<String>) data.get("props")));
//                    }
//                }
//            }
//
//            for (Map.Entry<String, EdgeDefinition> entry : edgeDefinitions.entrySet()) {
//                String edgeName = entry.getKey();
//                EdgeDefinition edgeDefinition = entry.getValue();
//                List<Map<String, Object>> dataList = (List<Map<String, Object>>) meta.get("edge_definitions").get(edgeName).get("data");
//
//                for (Map<String, Object> data : dataList) {
//                    Object keyValue = row.get(data.get("_key"));
//                    if (keyValue != null && edgeDefinition.get(keyValue.toString()) == null) {
//                        Map<String, Object> rowWithVertexNames = new HashMap<>(row);
//                        rowWithVertexNames.put("from_vertex_collection", graphName + "_" + data.get("from_vertex_collection") + "_" + id);
//                        rowWithVertexNames.put("to_vertex_collection", graphName + "_" + data.get("to_vertex_collection") + "_" + id);
//                        edgeDefinition.insert(fillMetaDescription(
//                                data.get("template"), rowWithVertexNames, (List<String>) data.get("props")));
//                    }
//                }
//            }
//        }
//
//        Map<String, List<Map<String, Object>>> documents = new HashMap<>();
//        for (Collection collection : CollectionUtils.union(vertexCollections.values(), edgeDefinitions.values())) {
//            Map<String, Object>[] result = collection.all().toArray(Map[]::new);
//            documents.put(collection.name(), Arrays.asList(result));
//        }
//
//        return graph;
//    }
//
//    private Map<String, Object> fillMetaDescription(Map<String, Object> template, Map<String, Object> row, List<String> columns) {
//        Map<String, Object> filteredRow = new HashMap<>();
//        for (String column : columns) {
//            filteredRow.put(column, row.get(column));
//        }
//
//        Map<String, Object> document = new HashMap<>(template);
//
//        for (Map.Entry<String, Object> rowEntry : filteredRow.entrySet()) {
//            String rowKey = rowEntry.getKey();
//            Object rowValue = rowEntry.getValue();
//
//            for (Map.Entry<String, Object> metaEntry : document.entrySet()) {
//                String metaKey = metaEntry.getKey();
//                Object metaValue = metaEntry.getValue();
//
//                document.put(metaKey, replacePlaceholder(metaValue, rowKey, rowValue).toString());
//            }
//        }
//
//        return document;
//    }
//
//    private Object replacePlaceholder(Object value, String rowKey, Object rowValue) {
//        String placeholder = String.format("{%s}", rowKey);
//        if (value instanceof String) {
//            return ((String) value).replace(placeholder, rowValue.toString());
//        }
//        return value;
//    }
//}
