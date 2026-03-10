import java.util.LinkedHashMap;
import java.util.Map;

/**
 * This class AuthManager is for handling user authentication and session state.
 * It manages the currently signed-in user and their login status.
 */
public class AuthManager {
    // This boolean keeps track if a user is currently logged into the system
    private boolean signedIn = false;
    // This string stores the email/username of the currently logged-in user
    private String currentUser;
    // This map stores registered users and their passwords
    private Map<String, String> users = new LinkedHashMap<>();

    public AuthManager() {
        // Initializing default user accounts
        users.put("geann@gmail.com", "geann123");
        users.put("staff@example.com", "staff123");
    }

    // This method checks if anyone is signed in
    public boolean isSignedIn() {
        return signedIn;
    }

    // This method updates the sign-in status
    public void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
    }

    // This method gets the current user's email
    public String getCurrentUser() {
        return currentUser;
    }

    // This method sets who the current user is
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    // This method returns the map of all registered users
    public Map<String, String> getUsers() {
        return users;
    }

    // This method allows updating the entire user database
    public void setUsers(Map<String, String> users) {
        this.users.clear();
        this.users.putAll(users);
    }
}
