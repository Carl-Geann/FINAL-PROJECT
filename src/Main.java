import javax.swing.SwingUtilities;
import java.util.Map;

/**
 * Kini nga class nga Main mao ang entry point sa Hotel Reservation System.
 * Kini nagdumala sa pagpakita sa login dialog sa dili pa ablihan ang main system.
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Pag-initialize sa main system frame
            HotelReservationSystem system = new HotelReservationSystem();
            
            // Pagkuha sa listahan sa mga rehistradong user gikan sa AuthManager
            Map<String, String> users = system.getUsersMap();
            
            // Pagpakita sa Login Dialog ug pagkuha sa email kung naka-login
            String email = LoginDialog.show(system, users);
            
            if (email != null) {
                // Kung malampuson ang login, i-set ang user status
                system.setSignedIn(true);
                system.setCurrentUser(email);
                system.updateAuthUI();
                system.setVisible(true);
            } else {
                // Kung wala naka-login (gi-close ang dialog), i-exit ang programa
                System.exit(0);
            }
        });
    }
}
