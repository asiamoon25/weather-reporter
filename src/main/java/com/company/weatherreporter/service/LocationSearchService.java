package com.company.weatherreporter.service;

import com.company.weatherreporter.entity.Location;
import com.company.weatherreporter.repository.LocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LocationSearchService {

    private final LocationRepository locationRepository;


    public Optional<Location> findNearestLocation(double lat, double lon) {
        return locationRepository.findNearestLocation(lon, lat);
    }

    public List<Location> findLocationWithinRadius(double lat, double lon, double radiusKm) {
        double radiusMeters = radiusKm * 1000;
        return locationRepository.findWithinRadius(lon, lat, radiusMeters);
    }

    public List<Location> searchLocations(String location) {
        return locationRepository.searchByKeyword(location);
    }
}
