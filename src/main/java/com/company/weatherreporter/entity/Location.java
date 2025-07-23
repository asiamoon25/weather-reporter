package com.company.weatherreporter.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "locations")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Location {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 10)
    private String category;
    
    @Column(nullable = false, length = 20)
    private String adminCode;
    
    @Column(nullable = false, length = 100)
    private String regionName;
    
    @Column(length = 50)
    private String level1;
    
    @Column(length = 50)
    private String level2;
    
    @Column(length = 50)
    private String level3;
    
    @Column(nullable = false)
    private Integer gridX;
    
    @Column(nullable = false)
    private Integer gridY;
    
    @Column(nullable = false)
    private Double longitude;
    
    @Column(nullable = false)
    private Double latitude;
    
    @Column(nullable = false, columnDefinition = "POINT")
    private String coordinates;
}