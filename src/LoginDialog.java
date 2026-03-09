import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class LoginDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField;
    private JButton btnToggle;
    private boolean showing;
    private String resultEmail;

    public static String show(Frame owner, Map<String, String> users) {
        LoginDialog d = new LoginDialog(owner, users);
        d.setVisible(true);
        return d.resultEmail;
    }

    private LoginDialog(Frame owner, Map<String, String> users) {
        super(owner, "Sign in", true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        setLocation(0, 0);

        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();

        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.BLACK);
        box.setPreferredSize(new Dimension(520, 300));

        JLabel lblTitle = new JLabel("Sign in", SwingConstants.CENTER);
        lblTitle.setForeground(Color.WHITE);
        lblTitle.setBorder(BorderFactory.createEmptyBorder(12, 0, 12, 0));
        box.add(lblTitle, BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout());
        form.setBackground(Color.BLACK);
        GridBagConstraints gc = new GridBagConstraints();
        gc.insets = new Insets(6, 8, 6, 8);
        gc.anchor = GridBagConstraints.WEST;

        JLabel lblEmail = new JLabel("Email");
        lblEmail.setForeground(Color.WHITE);
        emailField = new JTextField(20);

        JLabel lblPassword = new JLabel("Password");
        lblPassword.setForeground(Color.WHITE);
        passwordField = new JPasswordField(20);

        btnToggle = new JButton("Show");
        btnToggle.addActionListener(e -> togglePassword());

        gc.gridx = 0; gc.gridy = 0; form.add(lblEmail, gc);
        gc.gridx = 1; form.add(emailField, gc);
        gc.gridx = 0; gc.gridy = 1; form.add(lblPassword, gc);
        gc.gridx = 1; form.add(passwordField, gc);
        gc.gridx = 2; form.add(btnToggle, gc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        actions.setBackground(Color.BLACK);
        JButton btnLogin = coloredButton("Login");
        JButton btnSignup = coloredButton("Signup");

        btnLogin.addActionListener(e -> {
            String email = emailField.getText().trim().toLowerCase();
            String pass = new String(passwordField.getPassword()).trim();
            if (users.get(email) != null && users.get(email).equals(pass)) {
                resultEmail = email;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid credentials");
            }
        });

        btnSignup.addActionListener(e -> {
            String createdEmail = SignupDialog.show(this, users);
            if (createdEmail != null) {
                resultEmail = createdEmail;
                dispose();
            }
        });

        actions.add(btnLogin);
        actions.add(btnSignup);
        box.add(form, BorderLayout.CENTER);
        box.add(actions, BorderLayout.SOUTH);
        overlay.add(box, g);
        setContentPane(overlay);
    }

    private JButton coloredButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(200, 0, 0));
        b.setForeground(Color.WHITE);
        return b;
    }

    private void togglePassword() {
        if (showing) {
            passwordField.setEchoChar('\u2022');
            btnToggle.setText("Show");
            showing = false;
        } else {
            passwordField.setEchoChar((char) 0);
            btnToggle.setText("Hide");
            showing = true;
        }
    }
}