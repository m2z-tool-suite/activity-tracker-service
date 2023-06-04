package com.m2z.activity.tracker.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestReport {

    private String estimatedStartDate;
    private String estimatedEndDate;
}
