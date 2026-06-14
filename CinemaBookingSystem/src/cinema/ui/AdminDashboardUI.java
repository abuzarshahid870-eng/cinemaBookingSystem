package cinema.ui;

import cinema.manager.BookingManager;
import cinema.manager.MovieManager;
import cinema.manager.SeatManager;
import cinema.manager.UserManager;
import cinema.model.Booking;
import cinema.model.Movie;
import cinema.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class AdminDashboardUI extends JFrame {

    private final User           admin;
    private final MovieManager   movieManager;
    private final SeatManager    seatManager;
    private final BookingManager bookingManager;
    private final UserManager    userManager;

    private JTable           movieTable;
    private DefaultTableModel movieTableModel;

    public AdminDashboardUI(User admin, MovieManager mm, SeatManager sm,
                            BookingManager bm, UserManager um) {
        this.admin          = admin;
        this.movieManager   = mm;
        this.seatManager    = sm;
        this.bookingManager = bm;
        this.userManager    = um;
        buildUI();
    }

    private void buildUI() {
        setTitle("Admin Dashboard — " + admin.getUsername());
        setSize(850, 560);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Tabbed pane
        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(new Color(40, 40, 58));
        tabs.setForeground(Color.BLACK);
        tabs.addTab("Manage Movies",   buildMoviePanel());
        tabs.addTab("All Bookings",   buildBookingsPanel());
        tabs.addTab("Report",          buildReportPanel());

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout());
        topBar.setBackground(new Color(30, 30, 45));
        topBar.setBorder(BorderFactory.createEmptyBorder(8, 15, 8, 15));

        JLabel titleLbl = new JLabel("Admin Dashboard");
        titleLbl.setFont(new Font("Segoe UI", Font.BOLD, 17));
        titleLbl.setForeground(Color.WHITE);

        JButton logoutBtn = new JButton("Logout");
        logoutBtn.addActionListener(e -> {
            dispose();
            new LoginUI(userManager, movieManager, seatManager, bookingManager);
        });

        topBar.add(titleLbl,  BorderLayout.WEST);
        topBar.add(logoutBtn, BorderLayout.EAST);

        setLayout(new BorderLayout());
        add(topBar, BorderLayout.NORTH);
        add(tabs,   BorderLayout.CENTER);
        setVisible(true);
    }

    // ─── Movie management tab ────────────────────────────────────────────────

    private JPanel buildMoviePanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(35, 35, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"ID", "Title", "Genre", "Duration (min)", "Showtime", "Seats Left"};
        movieTableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        movieTable = styledTable(movieTableModel);
        refreshMovieTable();

        JScrollPane scroll = new JScrollPane(movieTable);
        scroll.getViewport().setBackground(new Color(40, 40, 58));
        panel.add(scroll, BorderLayout.CENTER);

        // Action buttons
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(new Color(35, 35, 50));

        JButton addBtn    = redButton("Add Movie");
        JButton editBtn   = redButton("Edit Movie");
        JButton removeBtn = redButton("Remove Movie");

        addBtn.addActionListener(e    -> showAddMovieDialog());
        editBtn.addActionListener(e   -> showEditMovieDialog());
        removeBtn.addActionListener(e -> removeSelectedMovie());

        btnRow.add(addBtn);
        btnRow.add(editBtn);
        btnRow.add(removeBtn);
        panel.add(btnRow, BorderLayout.SOUTH);
        return panel;
    }

    private void refreshMovieTable() {
        movieTableModel.setRowCount(0);
        for (Movie m : movieManager.getAllMovies()) {
            seatManager.initSeatsForMovie(m.getId());
            movieTableModel.addRow(new Object[]{
                m.getId(), m.getTitle(), m.getGenre(),
                m.getDurationMinutes(), m.getShowtime(),
                seatManager.getAvailableCount(m)
            });
        }
    }

    private void showAddMovieDialog() {
        JTextField title    = new JTextField();
        JTextField genre    = new JTextField();
        JTextField duration = new JTextField();
        JTextField showtime = new JTextField();

        Object[] fields = { "Title:", title, "Genre:", genre, "Duration (min):", duration, "Showtime:", showtime };
        int result = JOptionPane.showConfirmDialog(this, fields, "Add Movie", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            movieManager.addMovie(title.getText().trim(), genre.getText().trim(),
                                  Integer.parseInt(duration.getText().trim()),
                                  showtime.getText().trim());
            refreshMovieTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Duration must be a number.");
        }
    }

    private void showEditMovieDialog() {
        int row = movieTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a movie first."); return; }
        int movieId = (int) movieTableModel.getValueAt(row, 0);
        Movie m = movieManager.getMovieById(movieId);
        if (m == null) return;

        JTextField title    = new JTextField(m.getTitle());
        JTextField genre    = new JTextField(m.getGenre());
        JTextField duration = new JTextField(String.valueOf(m.getDurationMinutes()));
        JTextField showtime = new JTextField(m.getShowtime());

        Object[] fields = { "Title:", title, "Genre:", genre, "Duration (min):", duration, "Showtime:", showtime };
        int result = JOptionPane.showConfirmDialog(this, fields, "Edit Movie", JOptionPane.OK_CANCEL_OPTION);
        if (result != JOptionPane.OK_OPTION) return;

        try {
            m.setTitle(title.getText().trim());
            m.setGenre(genre.getText().trim());
            m.setDurationMinutes(Integer.parseInt(duration.getText().trim()));
            m.setShowtime(showtime.getText().trim());
            refreshMovieTable();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Duration must be a number.");
        }
    }

    private void removeSelectedMovie() {
        int row = movieTable.getSelectedRow();
        if (row < 0) { JOptionPane.showMessageDialog(this, "Select a movie first."); return; }
        int movieId = (int) movieTableModel.getValueAt(row, 0);
        int confirm = JOptionPane.showConfirmDialog(this,
            "Remove movie ID " + movieId + "?", "Confirm", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            movieManager.removeMovie(movieId);
            refreshMovieTable();
        }
    }

    // ─── All bookings tab ────────────────────────────────────────────────────

    private JPanel buildBookingsPanel() {
        JPanel panel = new JPanel(new BorderLayout(0, 8));
        panel.setBackground(new Color(35, 35, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] cols = {"Booking ID", "User", "Movie", "Seat", "Showtime"};
        DefaultTableModel model = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        JTable table = styledTable(model);

        JButton refreshBtn = redButton("Refresh");
        refreshBtn.addActionListener(e -> {
            model.setRowCount(0);
            for (Booking b : bookingManager.getAllBookings()) {
                model.addRow(new Object[]{
                    b.getBookingId(), b.getUser().getUsername(),
                    b.getMovie().getTitle(), b.getSeat().getSeatNumber(),
                    b.getMovie().getShowtime()
                });
            }
        });
        refreshBtn.doClick(); // load on open

        JButton cancelBtn = redButton("Cancel Selected Booking");
        cancelBtn.addActionListener(e -> {
            int row = table.getSelectedRow();
            if (row < 0) { JOptionPane.showMessageDialog(this, "Select a booking first."); return; }
            int bookingId = (int) model.getValueAt(row, 0);
            int confirm   = JOptionPane.showConfirmDialog(this, "Cancel booking #" + bookingId + "?",
                "Confirm", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                bookingManager.cancelBooking(bookingId);
                refreshBtn.doClick();
            }
        });

        JScrollPane scroll = new JScrollPane(table);
        scroll.getViewport().setBackground(new Color(40, 40, 58));

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        btnRow.setBackground(new Color(35, 35, 50));
        btnRow.add(refreshBtn);
        btnRow.add(cancelBtn);

        panel.add(scroll, BorderLayout.CENTER);
        panel.add(btnRow, BorderLayout.SOUTH);
        return panel;
    }

    // ─── Report tab ──────────────────────────────────────────────────────────

    private JPanel buildReportPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.setBackground(new Color(35, 35, 50));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        JTextArea area = new JTextArea();
        area.setEditable(false);
        area.setFont(new Font("Monospaced", Font.PLAIN, 13));
        area.setBackground(new Color(40, 40, 58));
        area.setForeground(Color.LIGHT_GRAY);

        JButton genBtn = redButton("Generate Report");
        genBtn.addActionListener(e -> {
            StringBuilder sb = new StringBuilder();
            sb.append("===== CINEMA REPORT =====\n\n");
            sb.append("Total Bookings: ").append(bookingManager.getTotalBookings()).append("\n\n");
            sb.append(String.format("%-4s %-22s %-10s %-14s %-10s\n", "ID", "Title", "Genre", "Showtime", "Seats Left"));
            sb.append("-".repeat(65)).append("\n");
            for (Movie m : movieManager.getAllMovies()) {
                seatManager.initSeatsForMovie(m.getId());
                sb.append(String.format("%-4d %-22s %-10s %-14s %-10d\n",
                    m.getId(), m.getTitle(), m.getGenre(),
                    m.getShowtime(), seatManager.getAvailableCount(m)));
            }
            area.setText(sb.toString());
        });

        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.LEFT));
        btnRow.setBackground(new Color(35, 35, 50));
        btnRow.add(genBtn);
        panel.add(btnRow, BorderLayout.SOUTH);
        return panel;
    }

    // ─── Helpers ─────────────────────────────────────────────────────────────

    private JTable styledTable(DefaultTableModel model) {
        JTable t = new JTable(model);
        t.setRowHeight(26);
        t.setBackground(new Color(40, 40, 58));
        t.setForeground(Color.WHITE);
        t.setGridColor(new Color(60, 60, 80));
        t.getTableHeader().setBackground(new Color(65, 105, 225));
        t.getTableHeader().setForeground(Color.WHITE);
        t.getTableHeader().setOpaque(true);
        t.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        t.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 32));
        t.getTableHeader().setReorderingAllowed(false);
        t.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        t.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
            @Override
            public java.awt.Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int col) {
                JLabel lbl = (JLabel) super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, col);
                lbl.setBackground(new Color(65, 105, 225));
                lbl.setForeground(Color.WHITE);
                lbl.setFont(new Font("Segoe UI", Font.BOLD, 13));
                lbl.setOpaque(true);
                lbl.setBorder(BorderFactory.createMatteBorder(0, 0, 1, 1, new Color(40, 70, 180)));
                return lbl;
            }
        });
        t.setSelectionBackground(new Color(100, 60, 180));
        t.setFillsViewportHeight(true);
        return t;
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
