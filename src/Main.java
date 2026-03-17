import javax.swing.SwingUtilities;

/**
 * Kini nga class nga Main mao ang entry point sa tibuok application
 * Kini ang nag-coordinate sa pag-launch sa LoginDialog ug sa main HotelReservationSystem
 */
public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            // Pag-initialize sa main system frame.
            HotelReservationSystem system = new HotelReservationSystem();
            
            // Pagpakita sa login dialog sa dili pa ablihan ang main dashboard
            // Ang system.authManager gigamit para sa pag-verify sa credentials
            LoginDialog login = new LoginDialog(system, system.authManager);
            login.setVisible(true);
            
            //  access for authentication, i-setup ang user session ug ipakita ang main frame
            if (login.isAuthenticated()) {
                system.setSignedIn(true);
                system.setCurrentUser(login.getLoggedInUser());
                system.updateAuthUI();
                system.roomUpdateTable(); // Sigurohon nga updated ang table pagkahuman og login
                system.setVisible(true);
            } else {
                //  i-close ang login window nga wala ka-authenticate, i-exit ang program
                System.exit(0);
            }
        });
    }
}
