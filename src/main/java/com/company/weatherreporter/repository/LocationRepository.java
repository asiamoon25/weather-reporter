package com.company.weatherreporter.repository;

import com.company.weatherreporter.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {
    
    Optional<Location> findByAdminCode(String adminCode);
    
    List<Location> findByRegionNameContaining(String regionName);

    @Query(value = "SELECT * FROM locations WHERE regionName LIKE %?1% OR level1 LIKE %?1% OR level2 LIKE %?1% OR level3 LIKE %?1%", nativeQuery = true)
    List<Location> searchByKeyword(String keyword);

    @Query(value = "SELECT *, ST_Distance_Sphere(coordinates, POINT(?1, ?2)) AS distance FROM locations ORDER BY distance LIMIT 1", nativeQuery = true)
    Optional<Location> findNearestLocation(double lon, double lat);

    @Query(value = "SELECT * FROM locations WHERE ST_Distance_Sphere(coordinates, POINT(?1, ?2)) <= ?3 ORDER BY ST_Distance_Sphere(coordinates, POINT(?1, ?2))", nativeQuery = true)
    List<Location> findWithinRadius(double lon, double lat, double radiusMeters);
}