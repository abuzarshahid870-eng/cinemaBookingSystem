package cinema.manager;

import cinema.model.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BookingManager {

    private final List<Booking> bookings = new ArrayList<>();
    private int nextBookingId = 1001;

    private final SeatManager seatManager;

    public BookingManager(SeatManager seatManager) {
        this.seatManager = seatManager;
    }

    /**
     * Creates a booking for the user on the given movie and seat.
     * @return the new Booking, or null if the seat is unavailable.
     */
    public Booking bookSeat(User user, Movie movie, String seatNumber) {
        Seat seat = seatManager.bookSeat(movie, seatNumber);
        if (seat == null) return null; // seat taken or invalid

        Booking booking = new Booking(nextBookingId++, user, movie, seat);
        bookings.add(booking);
        return booking;
    }

    /**
     * Cancels a booking by ID.
     * @return true if found and cancelled, false otherwise.
     */
    public boolean cancelBooking(int bookingId) {
        for (Booking b : bookings) {
            if (b.getBookingId() == bookingId) {
                seatManager.releaseSeat(b.getMovie(), b.getSeat().getSeatNumber());
                bookings.remove(b);
                return true;
            }
        }
        return false;
    }

    public List<Booking> getAllBookings() {
        return new ArrayList<>(bookings);
    }

    public List<Booking> getBookingsByUser(User user) {
        return bookings.stream()
                       .filter(b -> b.getUser().getId() == user.getId())
                       .collect(Collectors.toList());
    }

    /** Generates a readable summary string for a booking. */
    public String generateBookingSummary(Booking booking) {
        return "========== BOOKING SUMMARY ==========\n"
             + "Booking ID : #" + booking.getBookingId() + "\n"
             + "Customer   : " + booking.getUser().getUsername() + "\n"
             + "Movie      : " + booking.getMovie().getTitle() + "\n"
             + "Genre      : " + booking.getMovie().getGenre() + "\n"
             + "Duration   : " + booking.getMovie().getDurationMinutes() + " min\n"
             + "Showtime   : " + booking.getMovie().getShowtime() + "\n"
             + "Seat       : " + booking.getSeat().getSeatNumber() + "\n"
             + "=====================================";
    }

    public int getTotalBookings() {
        return bookings.size();
    }
}
