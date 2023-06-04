package com.m2z.activity.tracker.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;


@NoArgsConstructor
@Getter
@Setter
public class TicketTypeDto {
    private String dbId;
    private String ticketTypeId;
    private String name;
    private List<TicketDtoInType> tickets;
}
