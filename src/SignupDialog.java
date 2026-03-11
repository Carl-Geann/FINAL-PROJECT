import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SignupDialog extends JDialog {

    // Mga variable para sa input sa user (email ug password)
    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private String resultEmail;

    // Method para ipakita ang signup window ug makuha ang email sa bag-ong account
    public static String show(Dialog owner, Map<String, String> users) {
        SignupDialog d = new SignupDialog(owner, users);
        d.setVisible(true);
        return d.resultEmail;
    }

    private SignupDialog(Dialog owner, Map<String, String> users) {
        super(owner, "Sign up", true);
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
        mainPanel.setPreferredSize(new Dimension(850, 550));
        mainPanel.setBorder(BorderFactory.createLineBorder(themeYellow, 2));
        mainPanel.setBackground(Color.WHITE);

        // --- WALA NGA BAHIN: MAROON INFO PANEL ---
        JPanel leftPanel = new JPanel(new GridBagLayout());
        leftPanel.setBackground(themeRed);
        GridBagConstraints gl = new GridBagConstraints();
        gl.insets = new Insets(10, 30, 10, 30);
        gl.gridx = 0; gl.gridy = 0;

        // Greetings sa wala nga bahin
        JLabel lblHello = new JLabel("Hello, Friend!", SwingConstants.CENTER);
        lblHello.setForeground(themeYellow);
        lblHello.setFont(headerFont);
        leftPanel.add(lblHello, gl);

        gl.gridy = 1;
        JLabel lblInfo = new JLabel("<html><center>Enter your personal details<br>and start journey with us</center></html>", SwingConstants.CENTER);
        lblInfo.setForeground(themeYellow);
        lblInfo.setFont(smallFont);
        leftPanel.add(lblInfo, gl);

        gl.gridy = 2;
        gl.insets = new Insets(30, 30, 10, 30);
        // Button para mobalik sa Login window
        JButton btnToLogin = new JButton("SIGN IN");
        btnToLogin.setFont(tradeGothicBold);
        btnToLogin.setBackground(themeRed);
        btnToLogin.setForeground(themeYellow);
        btnToLogin.setFocusPainted(false);
        btnToLogin.setPreferredSize(new Dimension(180, 45));
        btnToLogin.setBorder(BorderFactory.createLineBorder(themeYellow, 2));
        btnToLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnToLogin.addActionListener(e -> {
            resultEmail = null;
            dispose();
        });
        leftPanel.add(btnToLogin, gl);

        // --- TUO NGA BAHIN: LIGHT FORM PANEL ---
        JPanel rightPanel = new JPanel(new GridBagLayout());
        rightPanel.setBackground(lightYellow);
        GridBagConstraints gr = new GridBagConstraints();
        gr.insets = new Insets(8, 40, 8, 40);
        gr.fill = GridBagConstraints.HORIZONTAL;
        gr.gridx = 0; gr.gridy = 0;

        // Header text para sa Create Account form
        JLabel lblCreate = new JLabel("Create Account", SwingConstants.CENTER);
        lblCreate.setForeground(themeRed);
        lblCreate.setFont(new Font("Trade Gothic", Font.BOLD, 32));
        rightPanel.add(lblCreate, gr);

        // Input field para sa Email
        gr.gridy = 1;
        gr.insets = new Insets(20, 40, 5, 40);
        JLabel lblEmail = new JLabel("Email Address");
        lblEmail.setFont(labelFont);
        lblEmail.setForeground(themeRed);
        rightPanel.add(lblEmail, gr);

        gr.gridy = 2;
        gr.insets = new Insets(0, 40, 10, 40);
        emailField = new JTextField(20);
        emailField.setFont(fieldFont);
        emailField.setBackground(Color.WHITE);
        emailField.setForeground(themeRed);
        emailField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
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
        gr.insets = new Insets(0, 40, 10, 40);
        passwordField = new JPasswordField(20);
        passwordField.setFont(fieldFont);
        passwordField.setBackground(Color.WHITE);
        passwordField.setForeground(themeRed);
        passwordField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        rightPanel.add(passwordField, gr);

        // Input field para sa Confirm Password
        gr.gridy = 5;
        gr.insets = new Insets(0, 40, 5, 40);
        JLabel lblConfirm = new JLabel("Confirm Password");
        lblConfirm.setFont(labelFont);
        lblConfirm.setForeground(themeRed);
        rightPanel.add(lblConfirm, gr);

        gr.gridy = 6;
        gr.insets = new Insets(0, 40, 20, 40);
        confirmField = new JPasswordField(20);
        confirmField.setFont(fieldFont);
        confirmField.setBackground(Color.WHITE);
        confirmField.setForeground(themeRed);
        confirmField.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200), 1),
            BorderFactory.createEmptyBorder(8, 10, 8, 10)
        ));
        rightPanel.add(confirmField, gr);

        // Button para sa pag-create og account (SIGN UP)
        gr.gridy = 7;
        gr.insets = new Insets(10, 40, 10, 40);
        JButton btnCreate = new JButton("SIGN UP");
        btnCreate.setFont(tradeGothicBold);
        btnCreate.setBackground(themeRed);
        btnCreate.setForeground(themeYellow);
        btnCreate.setFocusPainted(false);
        btnCreate.setPreferredSize(new Dimension(180, 45));
        btnCreate.setBorder(null);
        btnCreate.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnCreate.addActionListener(e -> {
            String email = emailField.getText().trim().toLowerCase();
            String pass = new String(passwordField.getPassword()).trim();
            String conf = new String(confirmField.getPassword()).trim();
            
            // Mga validation sa input fields
            if (email.isEmpty() || pass.isEmpty() || conf.isEmpty()) {
                JOptionPane.showMessageDialog(this, "All fields required");
                return;
            }
            if (!email.contains("@") || !email.contains(".")) {
                JOptionPane.showMessageDialog(this, "Invalid email");
                return;
            }
            if (users.containsKey(email)) {
                JOptionPane.showMessageDialog(this, "Email already exists");
                return;
            }
            if (pass.length() < 6) {
                JOptionPane.showMessageDialog(this, "Password must be at least 6 characters");
                return;
            }
            if (!pass.equals(conf)) {
                JOptionPane.showMessageDialog(this, "Passwords do not match");
                return;
            }
            
            // I-save ang bag-ong user sa map
            users.put(email, pass);
            resultEmail = email;
            dispose();
        });
        rightPanel.add(btnCreate, gr);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        overlay.add(mainPanel, g);
        setContentPane(overlay);
        getRootPane().setDefaultButton(btnCreate);
    }

}
