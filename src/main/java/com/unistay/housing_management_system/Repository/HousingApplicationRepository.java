package com.unistay.housing_management_system.Repository;

import com.unistay.housing_management_system.entity.HousingApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HousingApplicationRepository extends JpaRepository<HousingApplication, Long> {

}
