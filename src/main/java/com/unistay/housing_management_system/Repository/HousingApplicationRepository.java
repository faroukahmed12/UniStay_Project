package com.unistay.housing_management_system.Repository;

import com.unistay.housing_management_system.entity.HousingApplication;
import com.unistay.housing_management_system.enums.HousingApplicationStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Collection;
import java.util.List;

@Repository
public interface HousingApplicationRepository extends JpaRepository<HousingApplication, Long> {

    boolean existsByStudentIdAndStatus(Long id, HousingApplicationStatus housingApplicationStatus);

    long countByStatus(HousingApplicationStatus status);

    long countByStatusIn(Collection<HousingApplicationStatus> statuses);

    List<HousingApplication> findByStudentId(Long id);
}
