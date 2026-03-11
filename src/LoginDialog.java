import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LoginDialog extends JDialog {

    // Mga variable para sa input sa user ug status sa dialog
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton btnToggle;
    private boolean showing;
    private String resultEmail;

    // Method para ipakita ang login window ug makuha ang email sa naka-login
    public static String show(Frame owner, Map<String, String> users) {
        LoginDialog d = new LoginDialog(owner, users);
        d.setVisible(true);
        return d.resultEmail;
    }

    private LoginDialog(Frame owner, Map<String, String> users) {
        super(owner, "Sign in", true);
        // I-set ang size sa window base sa gidak-on sa screen
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        setLocation(0, 0);

        // Pag-define sa mga fonts nga gamiton sa UI
        Font tradeGothicBold = new Font("Trade Gothic", Font.BOLD, 18);
        Font tradeGothicPlain = new Font("Trade Gothic", Font.PLAIN, 18);
        Font labelFont = new Font("Trade Gothic", Font.BOLD, 15);
        Font fieldFont = new Font("Trade Gothic", Font.PLAIN, 18);
        Font headerFont = new Font("Trade Gothic", Font.BOLD, 32);
        Font smallFont = new Font("Trade Gothic", Font.PLAIN, 15);

        // Mga color theme sa application
        Color themeRed = new Color(150, 0, 0); // Maroon/Deep Red
        Color themeYellow = new Color(255, 255, 100); // Yellow
        Color lightYellow = new Color(255, 255, 240); // Pale Yellow/Cream

        // Siguraduhon nga ma-close ang window sa husto
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                resultEmail = null;
                dispose();
            }
        });

        // Overlay panel para sa background color ug layout alignment
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(lightYellow); // Pale Yellow/Cream
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0;
        g.anchor = GridBagConstraints.CENTER;

        // Main Container Panel nga gibahin sa duha (Left ug Right)
        JPanel mainPanel = new JPanel(new GridLayout(1, 2));
        mainPanel.setPreferredSize(new Dimension(850, 500));
        mainPanel.setBorder(BorderFactory.createLineBorder(themeYellow, 2));
        mainPanel.setBackground(Color.WHITE);

        // --- WALA NGA BAHIN: MAROON INFO PANEL ---
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(themeRed);
        GridBagConstraints gl = new GridBagConstraints();
        gl.insets = new Insets(10, 30, 10, 30);
        gl.gridx = 0; gl.gridy = 0;

        // Welcome text sa wala nga bahin
        JLabel lblWelcome = new JLabel("Welcome Back!", SwingConstants.CENTER);
        lblWelcome.setForeground(themeYellow);
        lblWelcome.setFont(headerFont);
        leftPanel.add(lblWelcome, gl);

        gl.gridy = 1;
        JLabel lblInfo = new JLabel("<html><center>To keep connected with us please<br>login with your personal info</center></html>", SwingConstants.CENTER);
        lblInfo.setForeground(themeYellow);
        lblInfo.setFont(smallFont);
        leftPanel.add(lblInfo, gl);

        gl.gridy = 2;
        gl.insets = new Insets(30, 30, 10, 30);
        // Button para mobalhin sa Sign Up window
        JButton btnToSignup = new JButton("SIGN UP");
        btnToSignup.setFont(tradeGothicBold);
        btnToSignup.setBackground(themeRed);
        btnToSignup.setForeground(themeYellow);
        btnToSignup.setFocusPainted(false);
        btnToSignup.setPreferredSize(new Dimension(180, 45));
        btnToSignup.setBorder(BorderFactory.createLineBorder(themeYellow, 2));
        btnToSignup.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToSignup.addActionListener(e -> {
            String createdEmail = SignupDialog.show(this, users);
            if (createdEmail != null) {
                resultEmail = createdEmail;
                dispose();
            }
        });
        leftPanel.add(btnToSignup, gl);

        // --- TUO NGA BAHIN: LIGHT FORM PANEL ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(lightYellow);
        GridBagConstraints gr = new GridBagConstraints();
        gr.insets = new Insets(10, 40, 10, 40);
        gr.fill = GridBagConstraints.HORIZONTAL;
        gr.gridx = 0; gr.gridy = 0;

        // Header text para sa Sign In form
        JLabel lblSignIn = new JLabel("Sign In", SwingConstants.CENTER);
        lblSignIn.setForeground(themeRed);
        lblSignIn.setFont(new Font("Trade Gothic", Font.BOLD, 32));
        rightPanel.add(lblSignIn, gr);

        // Input field para sa Email
        gr.gridy = 1;
        gr.insets = new Insets(30, 40, 5, 40);
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(labelFont);
        lblEmail.setForeground(themeRed);
        rightPanel.add(lblEmail, gr);

        gr.gridy = 2;
        gr.insets = new Insets(0, 40, 15, 40);
        emailField = new JTextField(20);
        emailField.setFont(fieldFont);
        emailField.setBackground(Color.WHITE);
        emailField.setForeground(themeRed);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        rightPanel.add(emailField, gr);

        // Input field para sa Password
        gr.gridy = 3;
        gr.insets = new Insets(0, 40, 5, 40);
        JLabel lblPassword = new JLabel("Password");
        lblPassword.setFont(labelFont);
        lblPassword.setForeground(themeRed);
        rightPanel.add(lblPassword, gr);

        gr.gridy = 4;
        gr.insets = new Insets(0, 40, 5, 40);
        passwordField = new JPasswordField(20);
        passwordField.setFont(fieldFont);
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(themeRed);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));
        rightPanel.add(passwordField, gr);

        // Button para ipakita o itago ang password
        gr.gridy = 5;
        gr.insets = new Insets(0, 40, 20, 40);
        btnToggle = new JButton("Show Password");
        btnToggle.setFont(smallFont);
        btnToggle.setForeground(themeRed);
        btnToggle.setBackground(lightYellow);
        btnToggle.setBorder(null);
        btnToggle.setFocusPainted(false);
        btnToggle.setContentAreaFilled(false);
        btnToggle.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToggle.addActionListener(e -> togglePassword());
        rightPanel.add(btnToggle, gr);

        // Button para sa pag-sign in
        gr.gridy = 6;
        gr.insets = new Insets(10, 40, 10, 40);
        JButton btnLogin = new JButton("SIGN IN");
        btnLogin.setFont(tradeGothicBold);
        btnLogin.setBackground(themeRed);
        btnLogin.setForeground(themeYellow);
        btnLogin.setFocusPainted(false);
        btnLogin.setPreferredSize(new Dimension(180, 45));
        btnLogin.setBorder(null);
        btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnLogin.addActionListener(e -> {
            String email = emailField.getText().trim().toLowerCase();
            String pass = new String(passwordField.getPassword()).trim();
            String expected = users.get(email);
            // I-check kung husto ang email ug password
            if (expected != null && expected.equals(pass)) {
                resultEmail = email;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });
        rightPanel.add(btnLogin, gr);

        // Link para sa nakalimot sa password
        gr.gridy = 7;
        JButton btnForgot = new JButton("Forgot your password?");
        btnForgot.setFont(smallFont);
        btnForgot.setForeground(new Color(100, 100, 100));
        btnForgot.setBackground(lightYellow);
        btnForgot.setBorder(null);
        btnForgot.setFocusPainted(false);
        btnForgot.setContentAreaFilled(false);
        btnForgot.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnForgot.addActionListener(e -> JOptionPane.showMessageDialog(this, "Password reset not implemented"));
        rightPanel.add(btnForgot, gr);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        overlay.add(mainPanel, g);
        setContentPane(overlay);
        getRootPane().setDefaultButton(btnLogin);
    }

    // Method para i-toggle ang visibility sa password
    private void togglePassword() {
        if (showing) {
            passwordField.setEchoChar('\u2022'); // Itago ang password (bullet dots)
            btnToggle.setText("Show");
            showing = false;
        } else {
            passwordField.setEchoChar((char) 0); // Ipakita ang password (plain text)
            btnToggle.setText("Hide");
            showing = true;
        }
    }
}
