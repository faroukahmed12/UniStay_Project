package com.unistay.housing_management_system.services;

import com.unistay.housing_management_system.Repository.HousingApplicationRepository;
import com.unistay.housing_management_system.Repository.MaintenanceRequestRepository;
import com.unistay.housing_management_system.dtos.response.DashboardRequestCountsResponseDto;
import com.unistay.housing_management_system.enums.HousingApplicationStatus;
import com.unistay.housing_management_system.enums.MaintenanceStatus;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.EnumSet;

@Service
@RequiredArgsConstructor
public class DashboardStatsService {

	private static final Logger logger = LoggerFactory.getLogger(DashboardStatsService.class);

	private final HousingApplicationRepository housingApplicationRepository;
	private final MaintenanceRequestRepository maintenanceRequestRepository;

	public DashboardRequestCountsResponseDto getRequestCounts() {
		long approved = housingApplicationRepository.countByStatus(HousingApplicationStatus.APPROVED);
		long rejected = housingApplicationRepository.countByStatus(HousingApplicationStatus.REJECTED);

		// "قيد المراجعة" = PENDING + REQUIRES_DOCUMENTS
		long underReview = housingApplicationRepository.countByStatusIn(
				EnumSet.of(HousingApplicationStatus.PENDING, HousingApplicationStatus.REQUIRES_DOCUMENTS)
		);

		long maintenancePending = maintenanceRequestRepository.countByStatus(MaintenanceStatus.PENDING);

		logger.info("Dashboard stats — housing approved: {}, underReview: {}, rejected: {}, maintenance pending: {}",
				approved, underReview, rejected, maintenancePending);

		return DashboardRequestCountsResponseDto.builder()
				.housingApprovedCount(approved)
				.housingUnderReviewCount(underReview)
				.housingRejectedCount(rejected)
				.build();
	}
}


