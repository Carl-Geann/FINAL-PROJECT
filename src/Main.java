

import javax.swing.*;
import java.util.LinkedHashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Map<String, String> creds = new LinkedHashMap<>();
            creds.put("geann@gmail.com", "geann123");
            creds.put("staff@example.com", "staff123");
            String email = LoginDialog.show(null, creds);
            if (email == null) System.exit(0);
            HotelReservationSystem app = new HotelReservationSystem();
            app.setUsers(creds);
            app.setSignedIn(true);
            app.setCurrentUser(email);
            app.updateAuthUI();
            app.setVisible(true);
            app.setExtendedState(app.getExtendedState() | JFrame.MAXIMIZED_BOTH);
        });
    }
}