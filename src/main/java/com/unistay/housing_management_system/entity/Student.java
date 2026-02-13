package com.unistay.housing_management_system.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
@ToString
@EqualsAndHashCode(callSuper = true)
@DiscriminatorValue("STUDENT")
@PrimaryKeyJoinColumn(name = "id")
@Table(name="students")
public class Student extends User{

    @Column(name = "national_id", unique = true, nullable = false, length = 14)
    private String nationalId;

    @Column(name = "university_id", unique = true, nullable = false, length = 20)
    private String universityId;

    @Column(name = "secondary_code")
    private int secondaryCode;

    @Column(name = "academic_year", nullable = false)
    private String academicYear;

    @Column(name = "faculty", nullable = false)
    private String faculty;

    @Column(name = "gpa", precision = 3, scale = 2)
    private BigDecimal gpa;

    @Column(name = "address")
    private String address;

    @OneToMany(mappedBy = "student", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<HousingApplication> housingApplicationList = new ArrayList<>();;

}
