package cinema.model;

public class Seat {
    private String seatNumber; // e.g. "A1", "B3"
    private boolean booked;

    public Seat(String seatNumber) {
        this.seatNumber = seatNumber;
        this.booked = false;
    }

    public String getSeatNumber() { return seatNumber; }
    public boolean isBooked()     { return booked; }

    public void book()   { this.booked = true; }
    public void release(){ this.booked = false; }

    @Override
    public String toString() {
        return seatNumber + (booked ? "[X]" : "[O]");
    }
}
