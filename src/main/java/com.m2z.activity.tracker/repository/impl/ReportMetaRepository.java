package com.m2z.activity.tracker.repository.impl;

import com.m2z.activity.tracker.entity.ReportMeta;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ReportMetaRepository extends MongoRepository<ReportMeta, String> {

}
