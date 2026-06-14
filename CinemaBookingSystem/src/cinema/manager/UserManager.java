package cinema.manager;

import cinema.model.User;

import java.util.ArrayList;
import java.util.List;

public class UserManager {

    private final List<User> users = new ArrayList<>();
    private int nextId = 1;

    public UserManager() {
        // Default accounts
        addUser("admin",   "admin123", "admin");
        addUser("alice",   "pass1",    "user");
        addUser("bob",     "pass2",    "user");
    }

    public User addUser(String username, String password, String role) {
        User u = new User(nextId++, username, password, role);
        users.add(u);
        return u;
    }

    /**
     * Validates credentials and returns the matching User, or null on failure.
     */
    public User validateLogin(String username, String password) {
        return users.stream()
                    .filter(u -> u.getUsername().equals(username)
                              && u.getPassword().equals(password))
                    .findFirst()
                    .orElse(null);
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(users);
    }
}
