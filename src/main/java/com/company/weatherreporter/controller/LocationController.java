package com.company.weatherreporter.controller;

import com.company.weatherreporter.entity.Location;
import com.company.weatherreporter.service.LocationDataService;
import com.company.weatherreporter.service.LocationSearchService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/locations")
@RequiredArgsConstructor
public class LocationController {
    
    private final LocationDataService locationDataService;
    private final LocationSearchService locationSearchService;

    @GetMapping("/import")
    public ResponseEntity<String> importLocationData() {
        try {
            String filePath = "LatLonList.xlsx";
            locationDataService.importLocationDataFromExcel(filePath);
            return ResponseEntity.ok("위치 데이터 가져오기 완료");
        } catch (IOException e) {
            return ResponseEntity.badRequest().body("파일 읽기 오류: " + e.getMessage());
        }
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Location>> searchLocations(@RequestParam String keyword) {
        List<Location> locations = locationDataService.searchLocations(keyword);
        return ResponseEntity.ok(locations);
    }
    
    @GetMapping
    public ResponseEntity<List<Location>> getAllLocations() {
        List<Location> locations = locationDataService.getAllLocations();
        return ResponseEntity.ok(locations);
    }

    @GetMapping("/nearest")
    public ResponseEntity<?> getNearestLocation(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude
    ) {
        Optional<Location> location = locationSearchService.findNearestLocation(latitude, longitude);

        return location.isPresent() ? ResponseEntity.ok(location.get()) : ResponseEntity.notFound().build();
    }

    @GetMapping("/nearby")
    public ResponseEntity<List<Location>> getNearbyLocations(
            @RequestParam("latitude") double latitude,
            @RequestParam("longitude") double longitude,
            @RequestParam(defaultValue = "10") double radiusKm
    ) {
        List<Location> locations = locationSearchService.findLocationWithinRadius(latitude, longitude, radiusKm);

        return ResponseEntity.ok(locations);
    }
}