package cinema.ui;

import cinema.manager.BookingManager;
import cinema.manager.MovieManager;
import cinema.manager.SeatManager;
import cinema.manager.UserManager;
import cinema.model.Booking;
import cinema.model.Movie;
import cinema.model.Seat;
import cinema.model.User;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SeatSelectionUI extends JFrame {

    private final User           currentUser;
    private final Movie          movie;
    private final SeatManager    seatManager;
    private final BookingManager bookingManager;
    private final MovieManager   movieManager;
    private final UserManager    userManager;
    private final JFrame         parent;

    private final Map<String, JToggleButton> seatButtons = new HashMap<>();
    private String selectedSeat = null;

    public SeatSelectionUI(User user, Movie movie, SeatManager sm,
                           BookingManager bm, MovieManager mm, UserManager um, JFrame parent) {
        this.currentUser    = user;
        this.movie          = movie;
        this.seatManager    = sm;
        this.bookingManager = bm;
        this.movieManager   = mm;
        this.userManager    = um;
        this.parent         = parent;
        buildUI();
    }

    private void buildUI() {
        setTitle("Seat Selection — " + movie.getTitle() + " @ " + movie.getShowtime());
        setSize(600, 460);
        setDefaultCloseOperation(DO_NOTHING_ON_CLOSE);
        setLocationRelativeTo(null);
        setResizable(false);

        JPanel root = new JPanel(new BorderLayout(10, 10));
        root.setBackground(new Color(30, 30, 45));
        root.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // Header
        JLabel header = new JLabel("Select a Seat for: " + movie.getTitle(), SwingConstants.CENTER);
        header.setFont(new Font("Segoe UI", Font.BOLD, 16));
        header.setForeground(Color.WHITE);
        root.add(header, BorderLayout.NORTH);

        // Screen label
        JPanel screenPanel = new JPanel();
        screenPanel.setBackground(new Color(30, 30, 45));
        JLabel screenLabel = new JLabel("▬▬▬▬▬▬▬▬  SCREEN  ▬▬▬▬▬▬▬▬", SwingConstants.CENTER);
        screenLabel.setForeground(new Color(200, 200, 100));
        screenLabel.setFont(new Font("Monospaced", Font.BOLD, 13));
        screenPanel.add(screenLabel);

        // Seat grid
        List<Seat> seats = seatManager.getAllSeats(movie);
        JPanel gridPanel = new JPanel(new GridLayout(5, 8, 6, 6));
        gridPanel.setBackground(new Color(30, 30, 45));

        ButtonGroup group = new ButtonGroup();
        for (Seat s : seats) {
            JToggleButton btn = new JToggleButton(s.getSeatNumber());
            btn.setFont(new Font("Segoe UI", Font.PLAIN, 11));
            btn.setPreferredSize(new Dimension(60, 36));
            btn.setFocusPainted(false);
            btn.setOpaque(true);
            btn.setBorderPainted(false);

            if (s.isBooked()) {
                btn.setBackground(new Color(150, 50, 50));
                btn.setForeground(Color.GRAY);
                btn.setEnabled(false);
                btn.setToolTipText("Already booked");
            } else {
                btn.setBackground(new Color(50, 150, 80));
                btn.setForeground(Color.WHITE);
                btn.addActionListener(e -> {
                    selectedSeat = s.getSeatNumber();
                    btn.setBackground(new Color(80, 80, 200));
                });
                btn.setToolTipText("Available");
            }

            seatButtons.put(s.getSeatNumber(), btn);
            group.add(btn);
            gridPanel.add(btn);
        }

        // Legend
        JPanel legend = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 4));
        legend.setBackground(new Color(30, 30, 45));
        legend.add(legendItem("Available", new Color(50, 150, 80)));
        legend.add(legendItem("Selected",  new Color(80, 80, 200)));
        legend.add(legendItem("Booked",    new Color(150, 50, 50)));

        JPanel centerPanel = new JPanel(new BorderLayout(5, 8));
        centerPanel.setBackground(new Color(30, 30, 45));
        centerPanel.add(screenPanel, BorderLayout.NORTH);
        centerPanel.add(gridPanel,   BorderLayout.CENTER);
        centerPanel.add(legend,      BorderLayout.SOUTH);
        root.add(centerPanel, BorderLayout.CENTER);

        // Buttons
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 0));
        btnPanel.setBackground(new Color(30, 30, 45));

        JButton backBtn    = new JButton("← Back");
        JButton confirmBtn = redButton("Confirm Booking");

        backBtn.addActionListener(e -> { dispose(); parent.setVisible(true); });
        confirmBtn.addActionListener(e -> handleConfirm());

        btnPanel.add(backBtn);
        btnPanel.add(confirmBtn);
        root.add(btnPanel, BorderLayout.SOUTH);

        add(root);
        setVisible(true);
    }

    private void handleConfirm() {
        if (selectedSeat == null) {
            JOptionPane.showMessageDialog(this, "Please select a seat first.", "No Seat", JOptionPane.WARNING_MESSAGE);
            return;
        }
        Booking booking = bookingManager.bookSeat(currentUser, movie, selectedSeat);
        if (booking == null) {
            JOptionPane.showMessageDialog(this, "That seat was just taken! Please choose another.", "Seat Unavailable", JOptionPane.ERROR_MESSAGE);
            refreshSeats();
            return;
        }

        String summary = bookingManager.generateBookingSummary(booking);
        JOptionPane.showMessageDialog(this, summary, "Booking Confirmed ✔", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        if (parent instanceof MovieListUI) {
            ((MovieListUI) parent).refreshTable(movieManager.getAllMovies());
        }
        parent.setVisible(true);
    }

    private void refreshSeats() {
        List<Seat> seats = seatManager.getAllSeats(movie);
        for (Seat s : seats) {
            JToggleButton btn = seatButtons.get(s.getSeatNumber());
            if (btn != null && s.isBooked()) {
                btn.setEnabled(false);
                btn.setBackground(new Color(150, 50, 50));
                btn.setSelected(false);
            }
        }
        selectedSeat = null;
    }

    private JLabel legendItem(String text, Color color) {
        JLabel l = new JLabel("■ " + text);
        l.setForeground(color);
        l.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        return l;
    }

    private JButton redButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(65, 105, 225));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        b.setOpaque(true);
        b.setBorderPainted(false);
        b.setFont(new Font("Segoe UI", Font.BOLD, 13));
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        return b;
    }
}
