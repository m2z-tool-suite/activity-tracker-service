package com.m2z.activity.tracker.repository.impl;


import com.m2z.activity.tracker.entity.TicketType;
import com.m2z.activity.tracker.repository.definition.BaseRepository;

public interface TicketTypeRepository extends BaseRepository<TicketType, Long> {

    TicketType getByName(String name);
}
