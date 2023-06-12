package com.m2z.activity.tracker.repository.impl;

import com.arangodb.springframework.repository.ArangoRepository;
import com.m2z.activity.tracker.entity.DocumentReport;
import org.springframework.stereotype.Repository;

@Repository
public interface ReportGraphRepository extends ArangoRepository<DocumentReport, String> {

}
