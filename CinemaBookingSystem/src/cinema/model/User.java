package cinema.model;

public class User {
    private int id;
    private String username;
    private String password;
    private String role; // "admin" or "user"

    public User(int id, String username, String password, String role) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.role = role;
    }

    public int getId()           { return id; }
    public String getUsername()  { return username; }
    public String getPassword()  { return password; }
    public String getRole()      { return role; }

    public boolean isAdmin()     { return "admin".equalsIgnoreCase(role); }

    @Override
    public String toString() {
        return "User{id=" + id + ", username='" + username + "', role='" + role + "'}";
    }
}
