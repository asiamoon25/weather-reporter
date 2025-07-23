package com.company.weatherreporter.controller;

import com.company.weatherreporter.dto.WeatherData;
import com.company.weatherreporter.service.WeatherService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/weather")
@RequiredArgsConstructor
@Slf4j
@CrossOrigin(origins = {"http://localhost:3000", "http://localhost:8081"})
public class WeatherController {
    
    private final WeatherService weatherService;

    @GetMapping("/current")
    public Mono<ResponseEntity<WeatherData>> getCurrentWeather(
            @RequestParam(required = false) Double lat, // 위도
            @RequestParam(required = false) Double lon,  // 경도
            @RequestParam(required = false) String location
    ) {
        log.info("현재 날씨 조회: lat={}, lon={}, location={}", lat, lon, location);

        // 좌표가 있으면 좌표 기반 조회
        if (lat != null && lon != null) {
            return weatherService.getWeatherByCoordinatesFromDB(lat, lon)
                    .map(ResponseEntity::ok)
                    .onErrorReturn(ResponseEntity.badRequest().build());
        }

        // 지역명이 있으면 지역명 기반 조회
        if (location != null && !location.isEmpty()) {
            return weatherService.getCurrentWeatherByLocation(location)  // 이 메서드 필요
                    .map(ResponseEntity::ok)
                    .onErrorReturn(ResponseEntity.badRequest().build());
        }

        return Mono.just(ResponseEntity.badRequest().build());
    }
    
    @GetMapping("/forecast")
    public Mono<ResponseEntity<WeatherData>> getForecast(
            @RequestParam(name = "lat", required = false) Double lat, // 위도
            @RequestParam(name = "lon", required = false) Double lon,  // 경도
            @RequestParam(name = "location", required = false) String location
    ) {
        log.info("좌표 기반 예보 조회: lat={}, lon={}, location={}", lat, lon, location);
        System.out.println("lat is not null !!!!!!" + (lat != null));
        System.out.println("lon is not null !!!!!!" + (lon != null));

        System.out.println("lat is : " + lat);
        System.out.println("lon is : " + lon);


        if(lat != null && lon != null) {
            return weatherService.getForecastByCoordinatesFromDB(lat, lon)
                    .map(ResponseEntity::ok)
                    .onErrorReturn(ResponseEntity.badRequest().build());
        }

        if(location != null && !location.isEmpty()) {
            return weatherService.getForecastByLocation(location)
                    .map(ResponseEntity::ok)
                    .onErrorReturn(ResponseEntity.badRequest().build());
        }
        
        return Mono.just(ResponseEntity.badRequest().build());
    }
}
