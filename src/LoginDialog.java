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

    public LoginDialog(JFrame parent, AuthManager authManager) {
        super(parent, "Hotel Login", true);
        this.authManager = authManager;
        
        // Kini nga code nag-initialize sa layout ug size sa login dialog.
        // Set to full screen size as requested
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setSize(screenSize.width, screenSize.height);
        setLocationRelativeTo(null);
        setResizable(true); // Allow resizing if maximized
        setLayout(new BorderLayout());

        // Set Window Icon
        ImageIcon windowIcon = getScaledIcon("background log in.jpg", 64, 64);
        if (windowIcon != null) {
            setIconImage(windowIcon.getImage());
        }

        // Color theme para sa login (Red matching the hotel theme)
        Color themeRed = new Color(150, 0, 0);
        Color lightYellow = new Color(255, 255, 240);
        Color textColor = Color.WHITE;

        // Trade Gothic Fonts
        Font fontHeader = new Font("Brush Script MT", Font.BOLD, 72); // Cursive font
        if (fontHeader.getFamily().equals("Dialog")) fontHeader = new Font("Comic Sans MS", Font.BOLD, 72); // Fallback
        
        Font fontLabel = new Font("Trade Gothic", Font.BOLD, 18);
        if (fontLabel.getFamily().equals("Dialog")) fontLabel = new Font("Arial", Font.BOLD, 18);
        
        Font fontField = new Font("Trade Gothic", Font.PLAIN, 18);
        if (fontField.getFamily().equals("Dialog")) fontField = new Font("Arial", Font.PLAIN, 18);
        
        Font fontButton = new Font("Trade Gothic", Font.BOLD, 18);
        if (fontButton.getFamily().equals("Dialog")) fontButton = new Font("Arial", Font.BOLD, 18);

        // Header Panel
        JPanel headerPanel = new JPanel();
        headerPanel.setBackground(themeRed);
        headerPanel.setPreferredSize(new Dimension(0, 150));
        headerPanel.setLayout(new GridBagLayout());
        JLabel lblHeader = new JLabel("UM DEL HOTEL");
        lblHeader.setFont(fontHeader);
        lblHeader.setForeground(textColor);
        headerPanel.add(lblHeader);

        // Center Wrapper Panel (to center the login box)
        JPanel centerWrapper = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                ImageIcon bgIcon = getScaledIcon("background log in.jpg", getWidth(), getHeight());
                if (bgIcon != null) {
                    g.drawImage(bgIcon.getImage(), 0, 0, null);
                }
            }
        };
        centerWrapper.setBackground(themeRed);

        // Login Box (The "normal box" adjusted)
        JPanel loginBox = new JPanel(new BorderLayout(0, 10));
        loginBox.setBackground(lightYellow);
        loginBox.setPreferredSize(new Dimension(600, 350)); // Adjusted dimensions
        loginBox.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.YELLOW, 4),
            BorderFactory.createEmptyBorder(30, 40, 30, 40)
        ));

        // Form Panel inside Login Box
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username
        gbc.gridx = 0; gbc.gridy = 0;
        gbc.weightx = 0.3;
        JLabel lblUser = new JLabel("Username/Email:");
        lblUser.setFont(fontLabel);
        lblUser.setForeground(themeRed);
        
        ImageIcon userIcon = getScaledIcon("guest list.png", 24, 24);
        if (userIcon != null) {
            lblUser.setIcon(userIcon);
            lblUser.setIconTextGap(10);
        }
        
        formPanel.add(lblUser, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtUsername = new JTextField(15);
        txtUsername.setFont(fontButton); // Use bold font for text
        txtUsername.setForeground(themeRed);
        txtUsername.setBackground(Color.WHITE);
        txtUsername.setBorder(BorderFactory.createLineBorder(themeRed, 1));
        txtUsername.setPreferredSize(new Dimension(300, 40));
        formPanel.add(txtUsername, gbc);

        // Password
        gbc.gridx = 0; gbc.gridy = 1;
        gbc.weightx = 0.3;
        JLabel lblPass = new JLabel("Password:");
        lblPass.setFont(fontLabel);
        lblPass.setForeground(themeRed);
        
        ImageIcon passIcon = getScaledIcon("password.png", 24, 24);
        if (passIcon != null) {
            lblPass.setIcon(passIcon);
            lblPass.setIconTextGap(10);
        }
        
        formPanel.add(lblPass, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        txtPassword = new JPasswordField(15);
        txtPassword.setFont(fontButton); // Use bold font for text
        txtPassword.setForeground(themeRed);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createLineBorder(themeRed, 1));
        txtPassword.setPreferredSize(new Dimension(300, 40));
        formPanel.add(txtPassword, gbc);

        // Buttons Panel inside Login Box
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0));
        buttonPanel.setOpaque(false);

        btnLogin = new JButton("Login");
        btnLogin.setBackground(themeRed);
        btnLogin.setForeground(textColor);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(fontButton);
        btnLogin.setPreferredSize(new Dimension(150, 50));

        btnExit = new JButton("Exit");
        btnExit.setBackground(themeRed);
        btnExit.setForeground(textColor);
        btnExit.setFocusPainted(false);
        btnExit.setFont(fontButton);
        btnExit.setPreferredSize(new Dimension(150, 50));
        btnExit.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 1));

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        loginBox.add(formPanel, BorderLayout.CENTER);
        loginBox.add(buttonPanel, BorderLayout.SOUTH);

        centerWrapper.add(loginBox);

        // Add panels to dialog
        add(headerPanel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

        // Footer for background color
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(themeRed);
        footerPanel.setPreferredSize(new Dimension(0, 100));
        add(footerPanel, BorderLayout.SOUTH);

        // Login Button Logic
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = txtUsername.getText();
                String pass = new String(txtPassword.getPassword());

                Map<String, String> users = authManager.getUsers();
                if (users.containsKey(user) && users.get(user).equals(pass)) {
                    authenticated = true;
                    loggedInUser = user;
                    dispose();
                } else {
                    JOptionPane.showMessageDialog(LoginDialog.this, 
                        "Wrong email or password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Exit Button Logic
        btnExit.addActionListener(e -> {
            authenticated = false;
            System.exit(0);
        });
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    private ImageIcon getScaledIcon(String path, int width, int height) {
        try {
            java.net.URL imgURL = getClass().getResource("/pic/" + path);
            if (imgURL != null) {
                ImageIcon icon = new ImageIcon(imgURL);
                Image img = icon.getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH);
                return new ImageIcon(img);
            }
        } catch (Exception e) {
            System.err.println("Could not load icon: " + path);
        }
        return null;
    }
}
