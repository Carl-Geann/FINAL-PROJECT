import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SignupDialog extends JDialog {
    private JTextField emailField;
    private JPasswordField passwordField, confirmField;
    private String resultEmail;

    public static String show(Dialog owner, Map<String, String> users) {
        SignupDialog d = new SignupDialog(owner, users);
        d.setVisible(true);
        return d.resultEmail;
    }

    private SignupDialog(Dialog owner, Map<String, String> users) {
        super(owner, "Sign up", true);
        setSize(500, 400);
        setLocationRelativeTo(owner);

        JPanel main = new JPanel(new GridBagLayout());
        main.setBackground(Color.BLACK);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        JLabel lblEmail = new JLabel("Email:"); lblEmail.setForeground(Color.WHITE);
        emailField = new JTextField(20);
        JLabel lblPass = new JLabel("Password:"); lblPass.setForeground(Color.WHITE);
        passwordField = new JPasswordField(20);
        JLabel lblConf = new JLabel("Confirm:"); lblConf.setForeground(Color.WHITE);
        confirmField = new JPasswordField(20);

        gbc.gridx = 0; gbc.gridy = 0; main.add(lblEmail, gbc);
        gbc.gridx = 1; main.add(emailField, gbc);
        gbc.gridx = 0; gbc.gridy = 1; main.add(lblPass, gbc);
        gbc.gridx = 1; main.add(passwordField, gbc);
        gbc.gridx = 0; gbc.gridy = 2; main.add(lblConf, gbc);
        gbc.gridx = 1; main.add(confirmField, gbc);

        JButton btnCreate = new JButton("Create");
        btnCreate.addActionListener(e -> {
            String email = emailField.getText().trim();
            String pass = new String(passwordField.getPassword());
            String conf = new String(confirmField.getPassword());
            if (pass.equals(conf) && !email.isEmpty()) {
                users.put(email, pass);
                resultEmail = email;
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Passwords mismatch or empty email");
            }
        });

        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        main.add(btnCreate, gbc);

        add(main);
    }
}