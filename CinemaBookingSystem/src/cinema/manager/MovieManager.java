package cinema.manager;

import cinema.interfaces.Searchable;
import cinema.model.Movie;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MovieManager implements Searchable<Movie> {

    private final List<Movie> movies = new ArrayList<>();
    private int nextId = 1;

    public MovieManager() {
        // Seed with sample movies
        addMovie("Inception",       "Sci-Fi",  148, "7:00 PM");
        addMovie("The Dark Knight", "Action",  152, "9:30 PM");
        addMovie("Interstellar",    "Sci-Fi",  169, "4:00 PM");
        addMovie("Joker",           "Drama",   122, "6:00 PM");
        addMovie("Avengers: Endgame","Action", 181, "8:00 PM");
    }

    public Movie addMovie(String title, String genre, int duration, String showtime) {
        Movie m = new Movie(nextId++, title, genre, duration, showtime);
        movies.add(m);
        return m;
    }

    public boolean removeMovie(int id) {
        return movies.removeIf(m -> m.getId() == id);
    }

    public Movie getMovieById(int id) {
        return movies.stream()
                     .filter(m -> m.getId() == id)
                     .findFirst()
                     .orElse(null);
    }

    public List<Movie> getAllMovies() {
        return new ArrayList<>(movies);
    }

    /** Searchable implementation — matches title or genre (case-insensitive) */
    @Override
    public List<Movie> search(String keyword) {
        String kw = keyword.toLowerCase();
        return movies.stream()
                     .filter(m -> m.getTitle().toLowerCase().contains(kw)
                               || m.getGenre().toLowerCase().contains(kw))
                     .collect(Collectors.toList());
    }
}
