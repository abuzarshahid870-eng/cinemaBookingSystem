package cinema;

import cinema.manager.BookingManager;
import cinema.manager.MovieManager;
import cinema.manager.SeatManager;
import cinema.manager.UserManager;
import cinema.ui.LoginUI;

import javax.swing.*;

public class Main {
    public static void main(String[] args) {

        UserManager    userManager    = new UserManager();
        MovieManager   movieManager   = new MovieManager();
        SeatManager    seatManager    = new SeatManager();
        BookingManager bookingManager = new BookingManager(seatManager);
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            } catch (Exception ignored) {}

            new LoginUI(userManager, movieManager, seatManager, bookingManager);
        });
    }
}
