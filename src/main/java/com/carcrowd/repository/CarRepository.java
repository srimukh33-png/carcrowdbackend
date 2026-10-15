package com.carcrowd.repository;

import com.carcrowd.entity.Car;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long> {

    @EntityGraph(attributePaths = {"brand"})
    List<Car> findAllByActiveTrueOrderByModelNameAsc();

    @EntityGraph(attributePaths = {"brand"})
    List<Car> findByBrandIdAndActiveTrueOrderByModelNameAsc(Long brandId);

    @EntityGraph(attributePaths = {"brand"})
    Optional<Car> findByIdAndActiveTrue(Long id);

    Optional<Car> findByBrandIdAndModelNameIgnoreCase(Long brandId, String modelName);
}
