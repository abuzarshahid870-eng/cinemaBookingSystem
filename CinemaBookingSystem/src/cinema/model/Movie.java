package cinema.model;

public class Movie {
    private int id;
    private String title;
    private String genre;
    private int durationMinutes;
    private String showtime; // e.g. "7:00 PM"

    public Movie(int id, String title, String genre, int durationMinutes, String showtime) {
        this.id = id;
        this.title = title;
        this.genre = genre;
        this.durationMinutes = durationMinutes;
        this.showtime = showtime;
    }

    public int getId()               { return id; }
    public String getTitle()         { return title; }
    public String getGenre()         { return genre; }
    public int getDurationMinutes()  { return durationMinutes; }
    public String getShowtime()      { return showtime; }

    public void setTitle(String title)               { this.title = title; }
    public void setGenre(String genre)               { this.genre = genre; }
    public void setDurationMinutes(int duration)     { this.durationMinutes = duration; }
    public void setShowtime(String showtime)         { this.showtime = showtime; }

    @Override
    public String toString() {
        return title + " | " + genre + " | " + durationMinutes + " min | " + showtime;
    }
}
