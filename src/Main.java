import javax.swing.SwingUtilities;

/**
 * The primary entry point for launching the Hotel Reservation System application.
 * Coordinates the initialization and display of the login dialog and the main dashboard.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Instantiates the main HotelReservationSystem frame.
            HotelReservationSystem system = new HotelReservationSystem();
            
            // Displays the login dialog to verify user credentials before opening the main dashboard.
            // The system's authManager is utilized for credential validation.
            LoginDialog login = new LoginDialog(system, system.authManager);
            login.setVisible(true);
            
            // If authentication is successful, set up the user session and display the main frame.
            if (login.isAuthenticated()) {
                system.setSignedIn(true);
                system.setCurrentUser(login.getLoggedInUser());
                system.updateAuthUI();
                system.roomUpdateTable(); // Refreshes the table data immediately after a successful login.
                system.setVisible(true);
            } else {
                // If the login window is closed without authentication, terminate the application.
                System.exit(0);
            }
        });
    }
}

