package cinema.ui;

import cinema.manager.BookingManager;
import cinema.manager.MovieManager;
import cinema.manager.SeatManager;
import cinema.manager.UserManager;
import cinema.model.User;

import javax.swing.*;
import java.awt.*;

public class LoginUI extends JFrame {

    private final UserManager    userManager;
    private final MovieManager   movieManager;
    private final SeatManager    seatManager;
    private final BookingManager bookingManager;

    private JTextField  usernameField;
    private JPasswordField passwordField;

    public LoginUI(UserManager um, MovieManager mm, SeatManager sm, BookingManager bm) {
        this.userManager    = um;
        this.movieManager   = mm;
        this.seatManager    = sm;
        this.bookingManager = bm;
        buildUI();
    }

    private void buildUI() {
        setTitle("Cinema Ticket Booking — Login");
        setSize(400, 260);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel panel = new JPanel(new GridBagLayout());
        panel.setBorder(BorderFactory.createEmptyBorder(20, 30, 20, 30));
        panel.setBackground(new Color(30, 30, 45));

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 8, 8, 8);
        gbc.fill   = GridBagConstraints.HORIZONTAL;

        // Title
        JLabel title = new JLabel("Cinema Booking", SwingConstants.CENTER);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        title.setForeground(Color.WHITE);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        panel.add(title, gbc);

        // Username
        gbc.gridwidth = 1; gbc.gridy = 1; gbc.gridx = 0;
        JLabel userLabel = styledLabel("Username:");
        panel.add(userLabel, gbc);

        usernameField = new JTextField(15);
        styleField(usernameField);
        gbc.gridx = 1;
        panel.add(usernameField, gbc);

        // Password
        gbc.gridy = 2; gbc.gridx = 0;
        panel.add(styledLabel("Password:"), gbc);

        passwordField = new JPasswordField(15);
        styleField(passwordField);
        gbc.gridx = 1;
        panel.add(passwordField, gbc);

        // Login button
        JButton loginBtn = new JButton("Login");
        loginBtn.setBackground(new Color(65, 105, 225));
        loginBtn.setForeground(Color.WHITE);
        loginBtn.setFont(new Font("Segoe UI", Font.BOLD, 14));
        loginBtn.setFocusPainted(false);
        loginBtn.setOpaque(true);
        loginBtn.setBorderPainted(false);
        loginBtn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        gbc.gridy = 3; gbc.gridx = 0; gbc.gridwidth = 2;
        panel.add(loginBtn, gbc);

        // Hint
        JLabel hint = new JLabel("Admin: admin / admin123  |  User: alice / pass1", SwingConstants.CENTER);
        hint.setFont(new Font("Segoe UI", Font.ITALIC, 10));
        hint.setForeground(Color.LIGHT_GRAY);
        gbc.gridy = 4;
        panel.add(hint, gbc);

        loginBtn.addActionListener(e -> handleLogin());
        getRootPane().setDefaultButton(loginBtn);

        add(panel);
        setVisible(true);
    }

    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = new String(passwordField.getPassword()).trim();

        User user = userManager.validateLogin(username, password);
        if (user == null) {
            JOptionPane.showMessageDialog(this,
                "Invalid username or password.", "Login Failed",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        dispose();
        if (user.isAdmin()) {
            new AdminDashboardUI(user, movieManager, seatManager, bookingManager, userManager);
        } else {
            new MovieListUI(user, movieManager, seatManager, bookingManager, userManager);
        }
    }

    private JLabel styledLabel(String text) {
        JLabel l = new JLabel(text);
        l.setForeground(Color.LIGHT_GRAY);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 13));
        return l;
    }

    private void styleField(JTextField f) {
        f.setBackground(new Color(50, 50, 70));
        f.setForeground(Color.WHITE);
        f.setCaretColor(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 140)),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)));
    }
}
