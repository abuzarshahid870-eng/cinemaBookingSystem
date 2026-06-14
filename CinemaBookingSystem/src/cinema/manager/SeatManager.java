package cinema.manager;

import cinema.model.Movie;
import cinema.model.Seat;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Manages seat availability per movie (keyed by movie ID).
 * Layout: rows A-E, columns 1-8  →  40 seats per show.
 */
public class SeatManager {

    // movieId → list of seats
    private final Map<Integer, List<Seat>> seatMap = new HashMap<>();

    private static final String[] ROWS    = {"A", "B", "C", "D", "E"};
    private static final int      COLUMNS = 8;

    /** Initialise a fresh seat grid for the given movie if not already present. */
    public void initSeatsForMovie(int movieId) {
        seatMap.computeIfAbsent(movieId, id -> {
            List<Seat> seats = new ArrayList<>();
            for (String row : ROWS) {
                for (int col = 1; col <= COLUMNS; col++) {
                    seats.add(new Seat(row + col));
                }
            }
            return seats;
        });
    }

    public List<Seat> getAllSeats(Movie movie) {
        initSeatsForMovie(movie.getId());
        return Collections.unmodifiableList(seatMap.get(movie.getId()));
    }

    public List<Seat> getAvailableSeats(Movie movie) {
        return getAllSeats(movie).stream()
                                .filter(s -> !s.isBooked())
                                .collect(Collectors.toList());
    }

    /**
     * Attempts to book the named seat for a movie.
     * @return the Seat object if successful, null if already booked or not found.
     */
    public Seat bookSeat(Movie movie, String seatNumber) {
        initSeatsForMovie(movie.getId());
        for (Seat s : seatMap.get(movie.getId())) {
            if (s.getSeatNumber().equalsIgnoreCase(seatNumber)) {
                if (s.isBooked()) return null; // already taken
                s.book();
                return s;
            }
        }
        return null; // seat not found
    }

    /** Releases a seat back to available. */
    public boolean releaseSeat(Movie movie, String seatNumber) {
        initSeatsForMovie(movie.getId());
        for (Seat s : seatMap.get(movie.getId())) {
            if (s.getSeatNumber().equalsIgnoreCase(seatNumber) && s.isBooked()) {
                s.release();
                return true;
            }
        }
        return false;
    }

    public int getAvailableCount(Movie movie) {
        return getAvailableSeats(movie).size();
    }
}
