package com.m2z.activity.tracker.repository.impl;


import com.m2z.activity.tracker.entity.ProjectAdmin;
import com.m2z.activity.tracker.repository.definition.BaseRepository;

import java.util.Optional;

public interface ProjectAdminRepository extends BaseRepository<ProjectAdmin, Long> {

    Optional<ProjectAdmin> findByEmailAndPassword(String email, String password);

    Optional<ProjectAdmin> findByEmail(String email);
}
