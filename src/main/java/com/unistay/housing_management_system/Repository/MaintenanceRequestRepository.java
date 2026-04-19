package com.unistay.housing_management_system.Repository;

import com.unistay.housing_management_system.entity.MaintenanceRequest;
import com.unistay.housing_management_system.enums.MaintenanceStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MaintenanceRequestRepository extends JpaRepository<MaintenanceRequest, Long> {

    List<MaintenanceRequest> findAllByStatus(MaintenanceStatus status);

    List<MaintenanceRequest> findAllByStudent_Id(Long studentId);

    List<MaintenanceRequest> findAllByAssignedStaff_Id(Long staffId);
}
