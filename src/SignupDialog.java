import javax.swing.*;
import java.awt.*;
import java.util.Map;

public class SignupDialog extends JDialog {

    private JTextField emailField;
    private JPasswordField passwordField;
    private JPasswordField confirmField;
    private String resultEmail;

    public static String show(Dialog owner, Map<String, String> users) {
        SignupDialog d = new SignupDialog(owner, users);
        d.setVisible(true);
        return d.resultEmail;
    }

    private SignupDialog(Dialog owner, Map<String, String> users) {
        super(owner, "Sign up", true);
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screen.width, screen.height);
        setLocation(0, 0);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        addWindowListener(new java.awt.event.WindowAdapter() {
            @Override
            public void windowClosing(java.awt.event.WindowEvent e) {
                resultEmail = null;
                dispose();
            }
        });

        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.gridx = 0; g.gridy = 0;
        g.anchor = GridBagConstraints.CENTER;

        JPanel box = new JPanel(new BorderLayout());
        box.setBackground(Color.BLACK);
        box.setPreferredSize(new Dimension(520, 340));

        JLabel lblTitle = new JLabel("Sign up", SwingConstants.CENTER);
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

        JLabel lblConfirm = new JLabel("Confirm Password");
        lblConfirm.setForeground(Color.WHITE);
        confirmField = new JPasswordField(20);

        gc.gridx = 0; gc.gridy = 0;
        form.add(lblEmail, gc);
        gc.gridx = 1;
        form.add(emailField, gc);

        gc.gridx = 0; gc.gridy = 1;
        form.add(lblPassword, gc);
        gc.gridx = 1;
        form.add(passwordField, gc);

        gc.gridx = 0; gc.gridy = 2;
        form.add(lblConfirm, gc);
        gc.gridx = 1;
        form.add(confirmField, gc);

        JPanel actions = new JPanel(new FlowLayout(FlowLayout.CENTER, 12, 12));
        actions.setBackground(Color.BLACK);
        JButton btnCreate = coloredButton("Create");
        JButton btnCancel = coloredButton("Cancel");

        btnCreate.addActionListener(e -> {
            String email = emailField.getText().trim().toLowerCase();
            String pass = new String(passwordField.getPassword()).trim();
            String conf = new String(confirmField.getPassword()).trim();
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
            users.put(email, pass);
            resultEmail = email;
            dispose();
        });

        btnCancel.addActionListener(e -> {
            resultEmail = null;
            dispose();
        });

        actions.add(btnCreate);
        actions.add(btnCancel);

        box.add(form, BorderLayout.CENTER);
        box.add(actions, BorderLayout.SOUTH);

        overlay.add(box, g);
        setContentPane(overlay);
        getRootPane().setDefaultButton(btnCreate);
    }

    private JButton coloredButton(String text) {
        JButton b = new JButton(text);
        b.setBackground(new Color(200, 0, 0));
        b.setForeground(Color.WHITE);
        b.setFocusPainted(false);
        return b;
    }
}