package com.example.onlinetourplanner.controller;

import com.example.onlinetourplanner.model.Booking;
import com.example.onlinetourplanner.model.Trip;
import com.example.onlinetourplanner.model.User;
import com.example.onlinetourplanner.repository.BookingRepository;
import com.example.onlinetourplanner.repository.TripRepository;
import com.example.onlinetourplanner.repository.UserRepository;
import com.example.onlinetourplanner.service.EmailService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Optional;

@Controller
public class MainController {

    @Autowired private TripRepository tripRepository;
    @Autowired private BookingRepository bookingRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private EmailService emailService;

    @GetMapping("/")
    public String loginPage(@RequestParam(value = "signup", required = false) String signup,
                            @RequestParam(value = "login", required = false) String login,
                            @RequestParam(value = "error", required = false) String error,
                            Model model) {
        model.addAttribute("showSignupSuccess", "success".equals(signup));
        model.addAttribute("autoOpenLogin", "true".equals(login) || "success".equals(signup) || "true".equals(error));
        model.addAttribute("loginError", "true".equals(error));
        return "login";
    }

    @GetMapping("/signup")
    public String signupPage(Model model) {
        model.addAttribute("user", new User());
        return "signup";
    }

    @PostMapping("/signup")
    public String processSignup(@ModelAttribute User user) {
        user.setRole("USER");
        userRepository.save(user);
        // Redirect home with signup=success parameter to auto-open login popup
        return "redirect:/?signup=success";
    }

    @PostMapping("/login")
    public String processLogin(@RequestParam String role,
                               @RequestParam String username,
                               @RequestParam String password,
                               HttpSession session) {

        // Allow default ADMIN access for testing
        if ("ADMIN".equals(role) && "admin".equalsIgnoreCase(username)) {
            session.setAttribute("role", "ADMIN");
            session.setAttribute("username", username);
            return "redirect:/admin/dashboard";
        }

        // Strict authentication check against database for USER role
        Optional<User> userOpt = userRepository.findByUsername(username);
        if (userOpt.isPresent() && userOpt.get().getPassword().equals(password)) {
            User user = userOpt.get();
            session.setAttribute("role", user.getRole());
            session.setAttribute("username", user.getUsername());
            if ("ADMIN".equals(user.getRole())) {
                return "redirect:/admin/dashboard";
            }
            return "redirect:/user/dashboard";
        }

        // Invalid credentials - redirect back to landing page with error flag
        return "redirect:/?error=true";
    }

    @GetMapping("/user/dashboard")
    public String userDashboard(Model model, HttpSession session) {
        if (session.getAttribute("role") == null) return "redirect:/";
        model.addAttribute("packages", tripRepository.findAll());
        model.addAttribute("username", session.getAttribute("username"));
        return "user-dashboard";
    }

    @GetMapping("/package/{id}")
    public String packageDetail(@PathVariable Long id, Model model, HttpSession session) {
        if (session.getAttribute("role") == null) return "redirect:/";
        Trip trip = tripRepository.findById(id).orElse(null);
        model.addAttribute("trip", trip);
        return "package-detail";
    }

    @PostMapping("/checkout")
    public String proceedToPayment(@RequestParam Long tripId,
                                   @RequestParam String name,
                                   @RequestParam String email,
                                   @RequestParam String phone,
                                   Model model) {
        Trip trip = tripRepository.findById(tripId).orElse(null);
        model.addAttribute("trip", trip);
        model.addAttribute("name", name);
        model.addAttribute("email", email);
        model.addAttribute("phone", phone);
        return "payment";
    }

    @PostMapping("/confirm-booking")
    public String confirmBooking(@RequestParam Long tripId,
                                 @RequestParam String name,
                                 @RequestParam String email,
                                 @RequestParam String phone,
                                 Model model) {
        Trip trip = tripRepository.findById(tripId).orElse(null);

        Booking booking = new Booking();
        booking.setCustomerName(name);
        booking.setCustomerEmail(email);
        booking.setCustomerPhone(phone);
        booking.setTrip(trip);
        booking.setAmountPaid(trip.getPrice());
        booking.setPaymentStatus("CONFIRMED");
        booking.setBookingTime(LocalDateTime.now());

        bookingRepository.save(booking);
        emailService.sendBookingConfirmation(booking);

        model.addAttribute("booking", booking);
        return "booking-success";
    }

    @GetMapping("/admin/dashboard")
    public String adminDashboard(Model model, HttpSession session) {
        if (!"ADMIN".equals(session.getAttribute("role"))) return "redirect:/";
        model.addAttribute("packages", tripRepository.findAll());
        model.addAttribute("bookings", bookingRepository.findAll());
        model.addAttribute("newTrip", new Trip());
        return "admin-dashboard";
    }

    @PostMapping("/admin/add-package")
    public String addPackage(@ModelAttribute Trip trip) {
        tripRepository.save(trip);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/admin/delete-package/{id}")
    public String deletePackage(@PathVariable Long id) {
        tripRepository.deleteById(id);
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/";
    }
}