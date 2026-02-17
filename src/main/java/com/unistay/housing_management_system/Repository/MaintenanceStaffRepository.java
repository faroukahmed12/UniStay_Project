package com.unistay.housing_management_system.Repository;

import com.unistay.housing_management_system.entity.MaintenanceStaff;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MaintenanceStaffRepository extends JpaRepository<MaintenanceStaff, Long> {
}
