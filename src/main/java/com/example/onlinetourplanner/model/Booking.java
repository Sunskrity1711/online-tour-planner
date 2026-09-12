package com.example.onlinetourplanner.model;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String customerName;
    private String customerEmail;
    private String customerPhone;

    @ManyToOne
    private Trip trip;

    private double amountPaid;
    private String paymentStatus;
    private LocalDateTime bookingTime;
}