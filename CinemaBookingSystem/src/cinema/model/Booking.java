package cinema.model;
//Booking
public class Booking {
    private int bookingId;
    private User user;
    private Movie movie;
    private Seat seat;

    public Booking(int bookingId, User user, Movie movie, Seat seat) {
        this.bookingId = bookingId;
        this.user = user;
        this.movie = movie;
        this.seat = seat;
    }

    public int getBookingId() { return bookingId; }
    public User getUser()     { return user; }
    public Movie getMovie()   { return movie; }
    public Seat getSeat()     { return seat; }

    @Override
    public String toString() {
        return "Booking #" + bookingId
                + " | User: " + user.getUsername()
                + " | Movie: " + movie.getTitle()
                + " | Seat: " + seat.getSeatNumber()
                + " | Show: " + movie.getShowtime();
    }
}
