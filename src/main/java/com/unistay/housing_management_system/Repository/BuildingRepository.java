package com.unistay.housing_management_system.Repository;

import com.unistay.housing_management_system.entity.Building;
import com.unistay.housing_management_system.enums.BuildingGenderType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BuildingRepository extends JpaRepository<Building, Long> {
    Optional<Building> findByBuildingName(String buildingName);

    boolean existsByBuildingName(String buildingName);

    List<Building> findByGenderType(BuildingGenderType genderType);
}
