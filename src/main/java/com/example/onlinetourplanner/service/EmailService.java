package com.example.onlinetourplanner.service;

import com.example.onlinetourplanner.model.Booking;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender mailSender;

    public void sendBookingConfirmation(Booking booking) {
        String subject = "Booking Confirmation - " + booking.getTrip().getTitle();
        String body = "Dear " + booking.getCustomerName() + ",\n\n" +
                "Thank you for booking with OnlineTourPlanner!\n\n" +
                "--- BOOKING RECEIPT ---\n" +
                "Booking ID: #" + booking.getId() + "\n" +
                "Package: " + booking.getTrip().getTitle() + "\n" +
                "Destination: " + booking.getTrip().getDestination() + "\n" +
                "Duration: " + booking.getTrip().getDays() + " Days\n" +
                "Total Amount Paid: ₹" + booking.getAmountPaid() + "\n" +
                "Payment Status: " + booking.getPaymentStatus() + "\n\n" +
                "INCLUSIONS:\n" +
                "• Flight: " + booking.getTrip().getFlightDetails() + "\n" +
                "• Transfer: " + booking.getTrip().getTransferDetails() + "\n" +
                "• Hotel: " + booking.getTrip().getHotelDetails() + "\n\n" +
                "Have a wonderful journey!\nOnlineTourPlanner Team";

        if (mailSender != null) {
            try {
                SimpleMailMessage message = new SimpleMailMessage();
                message.setTo(booking.getCustomerEmail());
                message.setSubject(subject);
                message.setText(body);
                mailSender.send(message);
                System.out.println("Receipt email sent successfully to " + booking.getCustomerEmail());
            } catch (Exception e) {
                System.out.println("Email dispatch log (SMTP not active):\n" + body);
            }
        } else {
            System.out.println("Mock Email Dispatch Log:\n" + body);
        }
    }
}