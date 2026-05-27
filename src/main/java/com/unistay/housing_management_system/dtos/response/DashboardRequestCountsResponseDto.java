package com.unistay.housing_management_system.dtos.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardRequestCountsResponseDto {

    private long housingApprovedCount;

    private long housingUnderReviewCount;

    private long housingRejectedCount;
}

