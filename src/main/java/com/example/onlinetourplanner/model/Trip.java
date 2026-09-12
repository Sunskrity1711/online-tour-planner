package com.example.onlinetourplanner.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trip {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String destination;
    private double price;
    private int days;

    private String flightDetails;
    private String transferDetails;
    private String hotelDetails;

    @Column(length = 2000)
    private String itinerary;
    private String imageUrl;
}