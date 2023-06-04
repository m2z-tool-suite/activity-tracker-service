package com.m2z.activity.tracker.repository.impl;

import com.m2z.activity.tracker.entity.DocumentReport;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportDocumentRepository extends MongoRepository<DocumentReport, String> {

}
