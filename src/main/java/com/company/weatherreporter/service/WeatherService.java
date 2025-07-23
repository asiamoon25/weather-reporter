package com.company.weatherreporter.service;

import com.company.weatherreporter.dto.WeatherApiResponse;
import com.company.weatherreporter.dto.WeatherData;
import com.company.weatherreporter.entity.Location;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Optional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WeatherService {

    @Value("${weather.api.kma.key}")
    private String key;

    private final WebClient webClient;
    private final LocationSearchService locationSearchService;

    public Mono<WeatherData> getUltraSrtNcst(double lat, double lon) { // 초 단기 실황 조회

        GridCoordinate gridCoordinate = convertToGrid(lat, lon);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getUltraSrtNcst")
                        .queryParam("serviceKey", key)
                        .queryParam("numOfRows", 1000)
                        .queryParam("pageNo", 1)
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", getCurrentDate())
                        .queryParam("base_time", getCurrentTime())
                        .queryParam("nx", gridCoordinate.getNx())
                        .queryParam("ny", gridCoordinate.getNy())
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("API 호출 오류")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new RuntimeException("서버 오류")))
                .bodyToMono(WeatherApiResponse.class)
                .map(this::convertToWeatherData)
                .timeout(Duration.ofSeconds(10))
                .retry(2);
    }

    public Mono<WeatherData> getUltraSrtFcst(double lat, double lon) { // 초 단기 예보 조회
        GridCoordinate gridCoordinate = convertToGrid(lat, lon);

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getUltraSrtFcst")
                        .queryParam("serviceKey", key)
                        .queryParam("numOfRows", 1000)
                        .queryParam("pageNo", 1)
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", getCurrentDate())
                        .queryParam("base_time", getCurrentTime())
                        .queryParam("nx", gridCoordinate.getNx())
                        .queryParam("ny", gridCoordinate.getNy())
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("API 호출 오류")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new RuntimeException("서버 오류")))
                .bodyToMono(WeatherApiResponse.class)
                .map(this::convertToWeatherData)
                .timeout(Duration.ofSeconds(10))
                .retry(2);
    }

    public Mono<WeatherData> getVilageFcst(double lat, double lon) { // 단기 예보조회

        GridCoordinate gridCoordinate = convertToGrid(lat, lon);


        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getVilageFcst")
                        .queryParam("serviceKey", key)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", 1000)
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", getCurrentDate())
                        .queryParam("base_time", getCurrentTime())
                        .queryParam("nx", gridCoordinate.getNx())
                        .queryParam("ny", gridCoordinate.getNy())
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("API 호출 오류")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new RuntimeException("서버 오류")))
                .bodyToMono(WeatherApiResponse.class)
                .map(this::convertToWeatherData)
                .timeout(Duration.ofSeconds(10))
                .retry(2);
    }

    public Mono<WeatherData> getForecastByCoordinatesFromDB(double latitude, double longitude) {
        log.info("DB 기반 좌표 예보 조회: lat={}, lon={}", latitude, longitude);

        Optional<Location> nearestLocation = locationSearchService.findNearestLocation(latitude, longitude);

        if(nearestLocation.isPresent()) {
            Location location = nearestLocation.get();
            log.info("가장 가까운 지역: {}, 격자 좌표 : ({}, {})", location.getRegionName(), location.getGridX(), location.getGridY());

            return getForecastByGridWithRegion(location.getGridX(), location.getGridY(), location.getRegionName());
        } else {
            log.warn("좌표 {}:{} 에 해당하는 지역을 찾을 수 없습니다", latitude, longitude);
            return Mono.error(new RuntimeException("해당 좌표의 지역을 찾을 수 없습니다."));
        }
    }

    public Mono<WeatherData> getForecastByLocation(String location) {
        log.info("지역명 기반 예보 조회: {}", location);

        List<Location> locations = locationSearchService.searchLocations(location);

        if(!locations.isEmpty()) {
            Location firstLocation = locations.get(0);
            return getForecastByGridWithRegion(firstLocation.getGridX(), firstLocation.getGridY(), firstLocation.getRegionName());
        } else {
            return Mono.error(new RuntimeException("해당 지역을 찾을 수 없습니다: " + location));
        }
    }

    // DB 기반 좌표 변환 날씨 조회
    public Mono<WeatherData> getWeatherByCoordinatesFromDB(double latitude, double longitude) {
        log.info("DB 기반 좌표 날씨 조회: lat={}, lon={}", latitude, longitude);

        Optional<Location> nearestLocation = locationSearchService.findNearestLocation(latitude, longitude);

        if (nearestLocation.isPresent()) {
            Location location = nearestLocation.get();
            log.info("가장 가까운 지역: {}, 격자좌표: ({}, {})",
                    location.getRegionName(), location.getGridX(), location.getGridY());

            // DB의 격자 좌표를 사용해서 날씨 조회
            return getWeatherByGridWithRegion(location.getGridX(), location.getGridY(), location.getRegionName());
        } else {
            log.warn("좌표 {}:{} 에 해당하는 지역을 찾을 수 없습니다", latitude, longitude);
            return Mono.error(new RuntimeException("해당 좌표의 지역을 찾을 수 없습니다"));
        }
    }

    public Mono<WeatherData> getCurrentWeatherByLocation(String location) {
        log.info("지역명 기반 날씨 조회: {}", location);

        List<Location> locations = locationSearchService.searchLocations(location);

        if(!locations.isEmpty()) {
            Location firstLocation = locations.get(0);
            return getWeatherByGridWithRegion(firstLocation.getGridX(), firstLocation.getGridY(), firstLocation.getRegionName());
        } else {
            return Mono.error(new RuntimeException("해당 지역을 찾을 수 없습니다: " + location));
        }
    }

    private Mono<WeatherData> getForecastByGrid(int nx, int ny) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getUltraSrtFcst")
                        .queryParam("serviceKey", key)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", 1000)
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", getCurrentDate())
                        .queryParam("base_time", getCurrentTime())
                        .queryParam("nx", nx)
                        .queryParam("ny", ny)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new RuntimeException("API 호출 오류")))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new RuntimeException("서버 오류")))
                .bodyToMono(WeatherApiResponse.class)
                .map(this::convertToWeatherData)
                .timeout(Duration.ofSeconds(10))
                .retry(2);
    }
    
    private Mono<WeatherData> getForecastByGridWithRegion(int nx, int ny, String regionName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getUltraSrtFcst")
                        .queryParam("serviceKey", key)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", 1000)
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", getCurrentDate())
                        .queryParam("base_time", getCurrentTime())
                        .queryParam("nx", nx)
                        .queryParam("ny", ny)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError, response -> Mono.error(new RuntimeException("API 호출 오류")))
                .onStatus(HttpStatusCode::is5xxServerError, response -> Mono.error(new RuntimeException("서버 오류")))
                .bodyToMono(WeatherApiResponse.class)
                .map(response -> convertToWeatherDataWithRegion(response, regionName))
                .timeout(Duration.ofSeconds(10))
                .retry(2);
    }



    private String getCurrentDate() {
        return LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    private String getCurrentTime() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour() - 1; // 1시간 전 데이터 사용
        if (hour < 0) hour = 23; // 0시 이전이면 23시로
        
        return String.format("%02d00", hour);
    }

    // 단기예보용 발표시간 계산
    private String getShortTermForecastTime() {
        LocalTime now = LocalTime.now();
        int hour = now.getHour();
        int minute = now.getMinute();

        // 단기예보 발표시간: 02, 05, 08, 11, 14, 17, 20, 23시
        int[] forecastHours = {2, 5, 8, 11, 14, 17, 20, 23};

        int targetHour = 23; // 기본값
        for (int fHour : forecastHours) {
            if (hour < fHour || (hour == fHour && minute < 10)) {
                // 아직 발표 전이면 이전 발표시간 사용
                targetHour = getPreviousForecastHour(fHour);
                break;
            } else if (hour == fHour && minute >= 10) {
                // 발표 후면 현재 발표시간 사용
                targetHour = fHour;
                break;
            } else if (hour > fHour) {
                targetHour = fHour;
            }
        }

        return String.format("%02d00", targetHour);
    }

    private int getPreviousForecastHour(int currentHour) {
        int[] forecastHours = {2, 5, 8, 11, 14, 17, 20, 23};
        for (int i = forecastHours.length - 1; i >= 0; i--) {
            if (forecastHours[i] < currentHour) {
                return forecastHours[i];
            }
        }
        return 23; // 가장 이전 시간
    }

    // WGS84 좌표를 기상청 격자 좌표로 변환
    private GridCoordinate convertToGrid(double lat, double lon) {
        // 기상청 격자 변환 공식 (Lambert Conformal Conic)
        final double RE = 6371.00877;     // 지구 반경(km)
        final double GRID = 5.0;          // 격자 간격(km)
        final double SLAT1 = 30.0;        // 투영 위도1(degree)
        final double SLAT2 = 60.0;        // 투영 위도2(degree)
        final double OLON = 126.0;        // 기준점 경도(degree)
        final double OLAT = 38.0;         // 기준점 위도(degree)
        final double XO = 43;             // 기준점 X좌표(GRID)
        final double YO = 136;            // 기준점 Y좌표(GRID)

        double DEGRAD = Math.PI / 180.0;
        double RADDEG = 180.0 / Math.PI;

        double re = RE / GRID;
        double slat1 = SLAT1 * DEGRAD;
        double slat2 = SLAT2 * DEGRAD;
        double olon = OLON * DEGRAD;
        double olat = OLAT * DEGRAD;

        double sn = Math.tan(Math.PI * 0.25 + slat2 * 0.5) / Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sn = Math.log(Math.cos(slat1) / Math.cos(slat2)) / Math.log(sn);
        double sf = Math.tan(Math.PI * 0.25 + slat1 * 0.5);
        sf = Math.pow(sf, sn) * Math.cos(slat1) / sn;
        double ro = Math.tan(Math.PI * 0.25 + olat * 0.5);
        ro = re * sf / Math.pow(ro, sn);

        double ra = Math.tan(Math.PI * 0.25 + lat * DEGRAD * 0.5);
        ra = re * sf / Math.pow(ra, sn);
        double theta = lon * DEGRAD - olon;
        if (theta > Math.PI) theta -= 2.0 * Math.PI;
        if (theta < -Math.PI) theta += 2.0 * Math.PI;
        theta *= sn;

        int nx = (int) Math.floor(ra * Math.sin(theta) + XO + 0.5);
        int ny = (int) Math.floor(ro - ra * Math.cos(theta) + YO + 0.5);

        return new GridCoordinate(nx, ny);
    }

    private WeatherData convertToWeatherData(WeatherApiResponse response) {
        log.debug("API 응답 데이터: {}", response);
        
        if(response.getResponse() == null ||
           response.getResponse().getBody() == null ||
           response.getResponse().getBody().getItems() == null ||
           response.getResponse().getBody().getItems().getItem() == null) {
            log.error("날씨 데이터 파싱 실패: {}", response);
            throw new RuntimeException("날씨 데이터가 없습니다.");
        }

        List<WeatherApiResponse.Item> items = response.getResponse().getBody().getItems().getItem();

        // 카테고리 별로 데이터 추출
        Map<String, String> weatherMap = items.stream()
                .collect(Collectors.toMap(
                        WeatherApiResponse.Item::getCategory,
                        item -> item.getObsrValue() != null ? item.getObsrValue() : item.getFcstValue(),
                        (existing, replacement) -> existing
                ));

        // 강수량 처리
        String rainfallValue = weatherMap.get("RN1") != null ? weatherMap.get("RN1") : weatherMap.get("PCP");
        RainfallData rainfallData = parseRainfall(rainfallValue);

        return WeatherData.builder()
                .temperature(parseDouble(weatherMap.get("T1H"), 0.0))
                .humidity(parseDouble(weatherMap.get("REH"), 0.0))
                .windSpeed(parseDouble(weatherMap.get("WSD"), 0.0))
                .rainProbability(parseDouble(weatherMap.get("POP"), 0.0))
                .rainfall(rainfallData.getValue())
                .rainfallText(rainfallData.getText())
                .weatherCode(getWeatherCode(weatherMap.get("PTY"), weatherMap.get("SKY")))
                .build();
    }

    private RainfallData parseRainfall(String rainfallValue) {
        if(rainfallValue == null || rainfallValue.equals("-") || rainfallValue.equals("0")) {
            return new RainfallData(0.0, "강수 없음");
        }

        try{
            double value = Double.parseDouble(rainfallValue);

            if(value < 1.0) {
                return new RainfallData(value, "1mm 미만");
            } else if(value >= 1.0 && value < 30.0) {
                return new RainfallData(value, String.format("%.1fmm", value));
            } else if(value >= 30.0 && value < 50.0) {
                return new RainfallData(value, "30.0~50.0mm");
            } else {
                return new RainfallData(value, "50.0mm 이상");
            }
        } catch (NumberFormatException e) {
            log.warn("강수량 파싱 실패 : {}", rainfallValue);
            return new RainfallData(0.0, "강수 없음");
        }
    }

    private Double parseDouble(String value, Double defaultValue) {
        try{
            return value != null ? Double.parseDouble(value) : defaultValue;
        } catch(NumberFormatException e) {
            return defaultValue;
        }
    }

    private Integer getWeatherCode(String pty, String sky) {
        if(pty != null) {
            switch(pty) {
                case "1" : case "4": return 5;
                case "2" : return 6;
                case "3": return 7;
            }
        }

        if (sky != null) {
            switch (sky) {
                case "1": return 1;           // 맑음
                case "3": return 3;           // 구름많음
                case "4": return 4;           // 흐림
            }
        }

        return 1; // 기본값: 맑음
    }
    
    // 지역명을 포함한 격자 좌표로 날씨 조회
    private Mono<WeatherData> getWeatherByGridWithRegion(int nx, int ny, String regionName) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getUltraSrtNcst")
                        .queryParam("serviceKey", key)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", 1000)
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", getCurrentDate())
                        .queryParam("base_time", getCurrentTime())
                        .queryParam("nx", nx)
                        .queryParam("ny", ny)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("API 호출 오류")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new RuntimeException("서버 오류")))
                .bodyToMono(WeatherApiResponse.class)
                .map(response -> convertToWeatherDataWithRegion(response, regionName))
                .timeout(Duration.ofSeconds(10))
                .retry(2);
    }
    
    private WeatherData convertToWeatherDataWithRegion(WeatherApiResponse response, String regionName) {
        log.debug("API 응답 데이터: {}", response);
        
        if(response.getResponse() == null ||
           response.getResponse().getBody() == null ||
           response.getResponse().getBody().getItems() == null ||
           response.getResponse().getBody().getItems().getItem() == null) {
            log.error("날씨 데이터 파싱 실패: {}", response);
            throw new RuntimeException("날씨 데이터가 없습니다.");
        }

        List<WeatherApiResponse.Item> items = response.getResponse().getBody().getItems().getItem();

        // 카테고리 별로 데이터 추출
        Map<String, String> weatherMap = items.stream()
                .collect(Collectors.toMap(
                        WeatherApiResponse.Item::getCategory,
                        item -> item.getObsrValue() != null ? item.getObsrValue() : item.getFcstValue(),
                        (existing, replacement) -> existing
                ));

        return WeatherData.builder()
                .regionName(regionName)  // 지역명 추가
                .temperature(parseDouble(weatherMap.get("T1H"), 0.0))
                .humidity(parseDouble(weatherMap.get("REH"), 0.0))
                .windSpeed(parseDouble(weatherMap.get("WSD"), 0.0))
                .rainProbability(parseDouble(weatherMap.get("POP"), 0.0))
                .weatherCode(getWeatherCode(weatherMap.get("PTY"), weatherMap.get("SKY")))
                .build();
    }
    
    // 격자 좌표로 날씨 조회
    private Mono<WeatherData> getWeatherByGrid(int nx, int ny) {

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/getUltraSrtNcst")
                        .queryParam("serviceKey", key)
                        .queryParam("pageNo", 1)
                        .queryParam("numOfRows", 1000)
                        .queryParam("dataType", "JSON")
                        .queryParam("base_date", getCurrentDate())
                        .queryParam("base_time", getCurrentTime())
                        .queryParam("nx", nx)
                        .queryParam("ny", ny)
                        .build())
                .retrieve()
                .onStatus(HttpStatusCode::is4xxClientError,
                        response -> Mono.error(new RuntimeException("API 호출 오류")))
                .onStatus(HttpStatusCode::is5xxServerError,
                        response -> Mono.error(new RuntimeException("서버 오류")))
                .bodyToMono(WeatherApiResponse.class)
                .map(this::convertToWeatherData)
                .timeout(Duration.ofSeconds(10))
                .retry(2);
    }

    // 격자 좌표 클래스
    @Data
    @AllArgsConstructor
    public static class GridCoordinate {
        private int nx;
        private int ny;
    }

    @Data
    @AllArgsConstructor
    private static class RainfallData {
        private Double value;
        private String text;
    }
}
