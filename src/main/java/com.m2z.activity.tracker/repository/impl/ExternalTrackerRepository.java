package com.m2z.activity.tracker.repository.impl;


import com.m2z.activity.tracker.entity.ExternalTracker;
import com.m2z.activity.tracker.entity.ProjectAdmin;
import com.m2z.activity.tracker.repository.definition.BaseRepository;

import java.util.List;
import java.util.Optional;

public interface ExternalTrackerRepository extends BaseRepository<ExternalTracker, Long> {

    List<ExternalTracker> findAllByEmail(String email);
}
