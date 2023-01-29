package com.m2z.activity.tracker.repository.definition;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface BaseRepository<T, I> extends CrudRepository<T, I> {
    T getById(I id);
}
