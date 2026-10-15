package com.carcrowd.repository;

import com.carcrowd.entity.CarBrand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarBrandRepository extends JpaRepository<CarBrand, Long> {

    List<CarBrand> findAllByActiveTrueOrderByNameAsc();

    Optional<CarBrand> findByNameIgnoreCase(String name);
}
