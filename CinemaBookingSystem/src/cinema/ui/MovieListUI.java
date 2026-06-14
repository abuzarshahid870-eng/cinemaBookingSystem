package cinema.ui;

import cinema.manager.BookingManager;
import cinema.manager.MovieManager;
import cinema.manager.SeatManager;
import cinema.manager.UserManager;
import cinema.model.Movie;
import cinema.model.User;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.List;

public class MovieListUI extends JFrame {

    private final User           currentUser;
    private final MovieManager   movieManager;
    private final SeatManager    seatManager;
    private final BookingManager bookingManager;
    private final UserManager    userManager;

    private JTable           movieTable;
    private DefaultTableModel tableModel;
    private JTextField       searchField;

    public MovieListUI(User user, MovieManager mm, SeatManager sm,
                       BookingManager bm, UserManager um) {
        this.currentUser    = user;
        this.movieManager   = mm;
        this.seatManager    = sm;
        this.bookingManager = bm;
        this.userManager    = um;
        buildUI();
    }

    private void buildUI() {
        setTitle("Now Showing — Logged in as: " + currentUser.getUsername());
        setSize(750, 500);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Top bar
        JPanel topBar = new JPanel(new BorderLayout(10, 0));
        topBar.setBackground(new Color(30, 30, 45));
        topBar.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));

        JLabel titleLabel = new JLabel("Now Showing");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 18));
        titleLabel.setForeground(Color.WHITE);

        searchField = new JTextField(18);
        searchField.setBackground(new Color(50, 50, 70));
        searchField.setForeground(Color.WHITE);
        searchField.setCaretColor(Color.WHITE);
        searchField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(100, 100, 140)),
            BorderFactory.createEmptyBorder(4, 6, 4, 6)));

        JButton searchBtn = redButton("Search");
        searchBtn.addActionListener(e -> refreshTable(movieManager.search(searchField.getText().trim())));

        JButton clearBtn  = redButton("Clear");
        clearBtn.addActionListener(e -> { searchField.setText(""); refreshTable(movieManager.getAllMovies()); });

        JPanel searchPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT, 6, 0));
        searchPanel.setOpaque(false);
        searchPanel.add(new JLabel("  ") {{ setForeground(Color.LIGHT_GRAY); }});
        searchPanel.add(searchField);
        searchPanel.add(searchBtn);
        searchPanel.add(clearBtn);

        topBar.add(titleLabel,   BorderLayout.WEST);
        topBar.add(searchPanel,  BorderLayout.EAST);

        // Table
        String[] cols = {"ID", "Title", "Genre", "Duration (min)", "Showtime", "Seats Left"};
        tableModel = new DefaultTableModel(cols, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; }
        };
        movieTable = new JTable(tableModel);
        movieTable.setRowHeight(26);
        movieTable.setBackground(new Color(40, 40, 58));
        movieTable.setForeground(Color.WHITE);
        movieTable.setGridColor(new Color(60, 60, 80));
        movieTable.getTableHeader().setBackground(new Color(65, 105, 225));
        movieTable.getTableHeader().setForeground(Color.WHITE);
        movieTable.getTableHeader().setOpaque(true);
        movieTable.getTableHeader().setFont(new Font("Segoe UI", Font.BOLD, 13));
        movieTable.getTableHeader().setPreferredSize(new java.awt.Dimension(0, 32));
        movieTable.getTableHeader().setReorderingAllowed(false);
        movieTable.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);
        // Force header color on Windows Look & Feel
        movieTable.getTableHeader().setDefaultRenderer(new javax.swing.table.DefaultTableCellRenderer() {
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
        movieTable.setSelectionBackground(new Color(100, 60, 180));
        movieTable.setFillsViewportHeight(true);

        refreshTable(movieManager.getAllMovies());

        JScrollPane scrollPane = new JScrollPane(movieTable);
        scrollPane.getViewport().setBackground(new Color(40, 40, 58));

        // Bottom buttons
        JPanel bottomBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 10, 10));
        bottomBar.setBackground(new Color(30, 30, 45));

        JButton bookBtn   = redButton("Book Selected Movie");
        JButton myBooksBtn = redButton("My Bookings");
        JButton logoutBtn = new JButton("Logout");
        logoutBtn.setFocusPainted(false);

        bookBtn.addActionListener(e -> openSeatSelection());
        myBooksBtn.addActionListener(e -> showMyBookings());
        logoutBtn.addActionListener(e -> { dispose(); new LoginUI(userManager, movieManager, seatManager, bookingManager); });

        bottomBar.add(myBooksBtn);
        bottomBar.add(bookBtn);
        bottomBar.add(logoutBtn);

        // Layout
        setLayout(new BorderLayout());
        add(topBar,    BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomBar, BorderLayout.SOUTH);
        getContentPane().setBackground(new Color(30, 30, 45));

        // Refresh seats count whenever this window becomes visible again
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowActivated(java.awt.event.WindowEvent e) {
                refreshTable(movieManager.getAllMovies());
            }
        });

        setVisible(true);
    }

    public void refreshTable(List<Movie> movies) {
        tableModel.setRowCount(0);
        for (Movie m : movies) {
            seatManager.initSeatsForMovie(m.getId());
            tableModel.addRow(new Object[]{
                m.getId(), m.getTitle(), m.getGenre(),
                m.getDurationMinutes(), m.getShowtime(),
                seatManager.getAvailableCount(m)
            });
        }
    }

    private void openSeatSelection() {
        int row = movieTable.getSelectedRow();
        if (row < 0) {
            JOptionPane.showMessageDialog(this, "Please select a movie first.");
            return;
        }
        int movieId = (int) tableModel.getValueAt(row, 0);
        Movie movie = movieManager.getMovieById(movieId);
        if (movie == null) return;
        new SeatSelectionUI(currentUser, movie, seatManager, bookingManager, movieManager, userManager, this);
        setVisible(false);
    }

    private void showMyBookings() {
        var bookings = bookingManager.getBookingsByUser(currentUser);
        if (bookings.isEmpty()) {
            JOptionPane.showMessageDialog(this, "You have no bookings yet.");
            return;
        }
        StringBuilder sb = new StringBuilder();
        for (var b : bookings) sb.append(b).append("\n");
        JOptionPane.showMessageDialog(this, sb.toString(), "My Bookings", JOptionPane.INFORMATION_MESSAGE);
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
