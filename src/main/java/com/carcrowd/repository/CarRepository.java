package com.carcrowd.repository;

import com.carcrowd.entity.Car;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface CarRepository extends JpaRepository<Car, Long>, JpaSpecificationExecutor<Car> {

    @EntityGraph(attributePaths = {"brand"})
    List<Car> findAllByActiveTrueOrderByModelNameAsc();

    @EntityGraph(attributePaths = {"brand"})
    List<Car> findByBrandIdAndActiveTrueOrderByModelNameAsc(Long brandId);

    @EntityGraph(attributePaths = {"brand"})
    Optional<Car> findByIdAndActiveTrue(Long id);

    @EntityGraph(attributePaths = {"brand"})
    Optional<Car> findById(Long id);

    Optional<Car> findByBrandIdAndModelNameIgnoreCase(Long brandId, String modelName);

    @Override
    @EntityGraph(attributePaths = {"brand"})
    Page<Car> findAll(Specification<Car> spec, Pageable pageable);
}
