import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * Kini nga class nga LoginDialog kay para sa pagpakita sa login screen sa hotel.
 * Kini ang nag-verify sa user credentials sa dili pa ma-access ang main system.
 */
public class LoginDialog extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JButton btnLogin, btnExit;
    private boolean authenticated = false;
    private String loggedInUser;
    private AuthManager authManager;
    private final Color THEME_RED = new Color(150, 0, 0), LIGHT_YELLOW = new Color(255, 255, 240);

    public LoginDialog(JFrame parent, AuthManager authManager) {
        super(parent, "Hotel Login", true);
        this.authManager = authManager;
        setSize(1200, 700); setLocationRelativeTo(null); setResizable(false); setLayout(new BorderLayout());
        ImageIcon icon = getScaledIcon("logo.png", 64, 64); if (icon != null) setIconImage(icon.getImage());
        initUI(); setupListeners();
    }

    private Font getFont(String name, int style, int size) {
        Font f = new Font(name, style, size);
        return f.getFamily().equals("Dialog") ? new Font("Arial", style, size) : f;
    }

    private void initUI() {
        Font fHead = getFont("Brush Script MT", Font.BOLD, 72), fLabel = getFont("Trade Gothic", Font.BOLD, 18), fBtn = getFont("Trade Gothic", Font.BOLD, 18);
        if (fHead.getFamily().equals("Arial")) fHead = getFont("Comic Sans MS", Font.BOLD, 72);

        JPanel header = new JPanel(new GridBagLayout()); header.setBackground(THEME_RED); header.setPreferredSize(new Dimension(0, 140));
        ImageIcon logo = getScaledIcon("logo.png", 130, 130);
        if (logo != null) { JLabel lblLogo = new JLabel(logo); lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30)); header.add(lblLogo); }
        JLabel lblHeader = new JLabel("UM DEL HOTEL"); lblHeader.setFont(fHead); lblHeader.setForeground(Color.WHITE); header.add(lblHeader);

        JPanel center = new JPanel(new GridBagLayout()) {
            @Override protected void paintComponent(Graphics g) {
                super.paintComponent(g); java.net.URL url = getClass().getResource("/pic/background login.jpg");
                if (url != null) g.drawImage(new ImageIcon(url).getImage(), 0, 0, getWidth(), getHeight(), null);
            }
        };

        JPanel loginBox = new JPanel(new BorderLayout(0, 10)); loginBox.setBackground(LIGHT_YELLOW); loginBox.setPreferredSize(new Dimension(500, 280));
        loginBox.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.YELLOW, 3), BorderFactory.createEmptyBorder(15, 35, 15, 35)));

        JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints(); gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL;
        txtUsername = createField(fBtn); txtPassword = new JPasswordField(15); txtPassword.setFont(fBtn); txtPassword.setForeground(THEME_RED); txtPassword.setBorder(BorderFactory.createLineBorder(THEME_RED, 1)); txtPassword.setPreferredSize(new Dimension(280, 32));
        addLabelField(form, "Username/Email:", txtUsername, "guest list.png", fLabel, gbc, 0);
        addLabelField(form, "Password:", txtPassword, "password.png", fLabel, gbc, 1);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); btns.setOpaque(false);
        btnLogin = createBtn("Login", fBtn); btnExit = createBtn("Exit", fBtn); btnExit.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));
        btns.add(btnLogin); btns.add(btnExit);
        loginBox.add(form, BorderLayout.CENTER); loginBox.add(btns, BorderLayout.SOUTH);
        center.add(loginBox);

        JPanel footer = new JPanel(); footer.setBackground(THEME_RED); footer.setPreferredSize(new Dimension(0, 60)); footer.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Color.YELLOW));
        add(header, BorderLayout.NORTH); add(center, BorderLayout.CENTER); add(footer, BorderLayout.SOUTH);
    }

    private JTextField createField(Font f) {
        JTextField tf = new JTextField(15); tf.setFont(f); tf.setForeground(THEME_RED); tf.setBackground(Color.WHITE); tf.setBorder(BorderFactory.createLineBorder(THEME_RED, 1)); tf.setPreferredSize(new Dimension(280, 32));
        return tf;
    }

    private void addLabelField(JPanel p, String text, JComponent field, String icon, Font f, GridBagConstraints gbc, int y) {
        gbc.gridx = 0; gbc.gridy = y; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(text); lbl.setFont(f); lbl.setForeground(THEME_RED);
        ImageIcon img = getScaledIcon(icon, 24, 24); if (img != null) { lbl.setIcon(img); lbl.setIconTextGap(10); }
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; p.add(field, gbc);
    }

    private JButton createBtn(String t, Font f) {
        JButton b = new JButton(t); b.setBackground(THEME_RED); b.setForeground(Color.WHITE); b.setFocusPainted(false); b.setFont(f); b.setPreferredSize(new Dimension(100, 32));
        return b;
    }

    private void setupListeners() {
        btnLogin.addActionListener(e -> {
            String u = txtUsername.getText(), p = new String(txtPassword.getPassword());
            if (authManager.getUsers().containsKey(u) && authManager.getUsers().get(u).equals(p)) { authenticated = true; loggedInUser = u; dispose(); }
            else JOptionPane.showMessageDialog(this, "Wrong email or password!", "Error", JOptionPane.ERROR_MESSAGE);
        });
        btnExit.addActionListener(e -> System.exit(0));
    }

    public boolean isAuthenticated() { return authenticated; }
    public String getLoggedInUser() { return loggedInUser; }

    private ImageIcon getScaledIcon(String path, int w, int h) {
        try {
            java.net.URL url = getClass().getResource("/pic/" + path);
            if (url != null) return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) { System.err.println("Could not load icon: " + path); }
        return null;
    }
}
