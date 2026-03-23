import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Manages user authentication processes and maintains the current session state.
 * Keeps track of the currently authenticated user and their authorization status.
 */
public class AuthManager {
    // Boolean flag indicating whether a user is currently signed into the application.
    private boolean signedIn = false;
    // Stores the username or email address of the currently authenticated user.
    private String currentUser;
    // A map containing registered usernames as keys and their corresponding passwords as values.
    private Map<String, String> users = new LinkedHashMap<>();

    public AuthManager() {
        // Populates the user map with a set of default administrative and staff accounts.
        users.put("admin", "123");
        users.put("staff@hotel.com", "staff123");
    }

    // Returns the current authentication status (true if signed in, false otherwise).
    public boolean isSignedIn() {
        return signedIn;
    }

    // Updates the current session's authentication status.
    public void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
    }

    // Retrieves the identifier (username or email) of the currently signed-in user.
    public String getCurrentUser() {
        return currentUser;
    }

    // Sets the identifier for the currently authenticated user.
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    // Returns the complete map of registered users and their credentials.
    public Map<String, String> getUsers() {
        return users;
    }

    // Updates the entire collection of registered users by replacing the current map with a new one.
    public void setUsers(Map<String, String> users) {
        this.users.clear();
        this.users.putAll(users);
    }
}

