import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Kini nga class nga AuthManager kay para sa pag-handle sa user authentication ug session state.
 * Kini ang nagdumala sa user nga naka-sign in karon ug ang ilang status sa pag-login.
 */
public class AuthManager {
    // Kini nga boolean kay nagsubay kung ang user naka-login ba karon sa sistema.
    private boolean signedIn = false;
    // Kini nga string kay nagtipig sa email o username sa user nga naka-login karon.
    private String currentUser;
    // Kini nga map kay nagtipig sa mga rehistradong user ug ang ilang mga password.
    private Map<String, String> users = new LinkedHashMap<>();

    public AuthManager() {
        // Nag-initialize sa mga default nga account sa user.
        users.put("geann@gmail.com", "geann123");
        users.put("staff@example.com", "staff123");
    }

    // Kini nga method kay nag-check kung naay naka-sign in.
    public boolean isSignedIn() {
        return signedIn;
    }

    // Kini nga method kay naga-update sa status sa sign-in.
    public void setSignedIn(boolean signedIn) {
        this.signedIn = signedIn;
    }

    // Kini nga method kay nagkuha sa email sa user karon.
    public String getCurrentUser() {
        return currentUser;
    }

    // Kini nga method kay nag-set kung kinsa ang user karon.
    public void setCurrentUser(String currentUser) {
        this.currentUser = currentUser;
    }

    // Kini nga method kay nag-return sa map sa tanang rehistradong user.
    public Map<String, String> getUsers() {
        return users;
    }

    // Kini nga method kay nagtugot sa pag-update sa tibuok database sa mga user.
    public void setUsers(Map<String, String> users) {
        this.users.clear();
        this.users.putAll(users);
    }
}
