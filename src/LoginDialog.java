import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Map;

/**
 * UI dialog class that displays the hotel system's login screen to the user.
 * Validates user credentials against the AuthManager before granting access to the main application.
 */
public class LoginDialog extends JDialog {
    private JTextField txtUsername;
    private JPasswordField txtPassword;
    private JLabel lblShowPassword;
    private JButton btnLogin, btnExit;
    private boolean authenticated = false;
    private String loggedInUser;
    private AuthManager authManager; // Reference to the AuthManager which handles the core authentication business logic

    public LoginDialog(JFrame parent, AuthManager authManager) {
        super(parent, "Hotel Login", true);
        this.authManager = authManager;
        
        // Configures the dimensions and initial properties of the login dialog window.
        setSize(1200, 700);
        setLocationRelativeTo(null); // Centers the login dialog window on the screen.
        setUndecorated(false); // Configures the window to display standard OS title bars and control buttons.
        setResizable(false); 
        setLayout(new BorderLayout());

        // Loads and applies a custom icon to the login dialog's window frame.
        ImageIcon windowIcon = getScaledIcon("logo.png", 48, 48);
        if (windowIcon != null) {
            setIconImage(windowIcon.getImage()); // Sets the application logo as the small icon in the window title bar.
        }

        // Color theme para sa login (Red matching the hotel theme)
        Color themeRed = new Color(150, 0, 0); // Primary red theme color.
        Color lightYellow = new Color(255, 255, 240); // Secondary yellow background color for the login box.
        Color textColor = Color.WHITE; // Standard white text color.

        // Font configuration section defining the typography styles for the login interface.
        Font fontHeader = new Font("Brush Script MT", Font.BOLD, 72); 
        if (fontHeader.getFamily().equals("Dialog")) fontHeader = new Font("Comic Sans MS", Font.BOLD, 72); 
        
        Font fontLabel = new Font("Trade Gothic", Font.BOLD, 18);
        if (fontLabel.getFamily().equals("Dialog")) fontLabel = new Font("Arial", Font.BOLD, 18);
        
        Font fontField = new Font("Trade Gothic", Font.PLAIN, 18);
        if (fontField.getFamily().equals("Dialog")) fontField = new Font("Arial", Font.PLAIN, 18);
        
        Font fontButton = new Font("Trade Gothic", Font.BOLD, 18);
        if (fontButton.getFamily().equals("Dialog")) fontButton = new Font("Arial", Font.BOLD, 18);

        // Header Panel - The top section of the dialog displaying the hotel's branding and logo.
        JPanel headerPanel = new JPanel(new GridBagLayout());
        headerPanel.setBackground(themeRed);
        headerPanel.setPreferredSize(new Dimension(0, 220)); 

        // Loads and adds a prominent version of the hotel logo to the header panel.
        ImageIcon logoIcon = getScaledIcon("logo.png", 200, 200); 
        if (logoIcon != null) {
            JLabel lblLogo = new JLabel(logoIcon);
            lblLogo.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 30));
            headerPanel.add(lblLogo);
        }

        JLabel lblHeader = new JLabel("UM DEL HOTEL");
        lblHeader.setFont(new Font("Brush Script MT", Font.BOLD, 100)); 
        if (lblHeader.getFont().getFamily().equals("Dialog")) lblHeader.setFont(new Font("Comic Sans MS", Font.BOLD, 100));
        lblHeader.setForeground(textColor);
        headerPanel.add(lblHeader);

        // Center Wrapper Panel - The central container that features a custom-drawn background image.
        JPanel centerWrapper = new JPanel(new GridBagLayout()) {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                java.net.URL imgURL = getClass().getResource("/pic/background login.jpg");
                if (imgURL != null) {
                    ImageIcon icon = new ImageIcon(imgURL);
                    Image img = icon.getImage();
                    // Custom painting logic to scale the background image to fit the panel's dimensions.
                    g.drawImage(img, 0, 0, getWidth(), getHeight(), null);
                }
            }
        };
        centerWrapper.setBackground(themeRed);

        // Login Box - Stylized container for the credential input fields.
        JPanel loginBox = new JPanel(new BorderLayout(0, 10)) {
            @Override
            protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(getBackground());
                g2.fillRoundRect(0, 0, getWidth(), getHeight(), 30, 30); // Applies anti-aliased rounded corners to the login box.
                g2.setColor(Color.YELLOW);
                g2.setStroke(new BasicStroke(3));
                g2.drawRoundRect(0, 0, getWidth() - 1, getHeight() - 1, 30, 30); // Draws a thick yellow border around the perimeter.
                g2.dispose();
            }
        };
        loginBox.setOpaque(false);
        loginBox.setBackground(lightYellow);
        loginBox.setPreferredSize(new Dimension(500, 320)); 
        loginBox.setBorder(BorderFactory.createEmptyBorder(20, 35, 20, 35));

        // Form Panel inside Login Box - Internal layout container for organizing input fields.
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setOpaque(false);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); 
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Username Label ug Field
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
        txtUsername.setFont(fontButton); 
        txtUsername.setForeground(themeRed);
        txtUsername.setBackground(Color.WHITE);
        txtUsername.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); 
        txtUsername.setPreferredSize(new Dimension(280, 32)); 
        addFocusEffect(txtUsername, themeRed); // Attaches visual feedback listeners for interaction.
        formPanel.add(txtUsername, gbc);

        // Password Label ug Field
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
        txtPassword.setFont(fontButton);
        txtPassword.setForeground(themeRed);
        txtPassword.setBackground(Color.WHITE);
        txtPassword.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        txtPassword.setPreferredSize(new Dimension(280, 32)); 
        addFocusEffect(txtPassword, themeRed); // Attaches visual feedback listeners for interaction.
        formPanel.add(txtPassword, gbc);

        // Show/Hide Password Toggle Label - Clickable text to toggle password visibility.
        gbc.gridx = 1; gbc.gridy = 2;
        JPanel toggleContainer = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        toggleContainer.setOpaque(false);
        toggleContainer.setPreferredSize(new Dimension(280, 25)); 
        
        lblShowPassword = new JLabel("Show Password");
        lblShowPassword.setFont(new Font("Trade Gothic", Font.BOLD, 13));
        lblShowPassword.setForeground(themeRed);
        lblShowPassword.setCursor(new Cursor(Cursor.HAND_CURSOR));
        
        lblShowPassword.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                // Implementation logic for switching between masked and plain-text password display.
                if (lblShowPassword.getText().equals("Show Password")) {
                    txtPassword.setEchoChar((char) 0);
                    lblShowPassword.setText("Hide Password");
                } else {
                    txtPassword.setEchoChar('•');
                    lblShowPassword.setText("Show Password");
                }
            }
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                lblShowPassword.setForeground(Color.RED);
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                lblShowPassword.setForeground(themeRed);
            }
        });
        toggleContainer.add(lblShowPassword);
        formPanel.add(toggleContainer, gbc);

        // Buttons Panel inside Login Box - Container for primary action buttons.
        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 0)); 
        buttonPanel.setOpaque(false);

        btnLogin = new JButton("Login");
        btnLogin.setBackground(themeRed);
        btnLogin.setForeground(textColor);
        btnLogin.setFocusPainted(false);
        btnLogin.setFont(fontButton);
        btnLogin.setPreferredSize(new Dimension(120, 40)); 
        btnLogin.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));
        
        // Attaches visual feedback and cursor changes when hovering over the Login button.
        btnLogin.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(Color.RED);
                btnLogin.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnLogin.setBackground(themeRed);
            }
        });

        btnExit = new JButton("Exit");
        btnExit.setBackground(themeRed);
        btnExit.setForeground(textColor);
        btnExit.setFocusPainted(false);
        btnExit.setFont(fontButton);
        btnExit.setPreferredSize(new Dimension(120, 40));
        btnExit.setBorder(BorderFactory.createLineBorder(Color.YELLOW, 2));

        // Attaches visual feedback and cursor changes when hovering over the Exit button.
        btnExit.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) {
                btnExit.setBackground(new Color(100, 0, 0));
                btnExit.setCursor(new Cursor(Cursor.HAND_CURSOR));
            }
            public void mouseExited(java.awt.event.MouseEvent e) {
                btnExit.setBackground(themeRed);
            }
        });

        buttonPanel.add(btnLogin);
        buttonPanel.add(btnExit);

        loginBox.add(formPanel, BorderLayout.CENTER);
        loginBox.add(buttonPanel, BorderLayout.SOUTH);

        centerWrapper.add(loginBox);

        // Idugang ang mga panel sa dialog
        add(headerPanel, BorderLayout.NORTH);
        add(centerWrapper, BorderLayout.CENTER);

        // Footer Panel - The bottom section providing visual balance.
        JPanel footerPanel = new JPanel();
        footerPanel.setBackground(themeRed);
        footerPanel.setPreferredSize(new Dimension(0, 60)); 
        footerPanel.setBorder(BorderFactory.createMatteBorder(3, 0, 0, 0, Color.YELLOW)); 
        add(footerPanel, BorderLayout.SOUTH);

        // Defines the action listener for verifying user credentials and handling logins.
        btnLogin.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String user = txtUsername.getText();
                String pass = new String(txtPassword.getPassword());

                // Compares user-entered credentials against the registered accounts in the AuthManager.
                Map<String, String> users = authManager.getUsers();
                if (users.containsKey(user) && users.get(user).equals(pass)) {
                    authenticated = true;
                    loggedInUser = user;
                    dispose(); // Closes the login window and proceeds upon successful authentication.
                } else {
                    // Displays a popup alert informing the user of incorrect login credentials.
                    JOptionPane.showMessageDialog(LoginDialog.this, 
                        "Wrong email or password!", "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        // Defines the action listener for terminating the application.
        btnExit.addActionListener(e -> {
            authenticated = false;
            System.exit(0); // Terminates the JVM and exits the program.
        });
    }

    public boolean isAuthenticated() {
        return authenticated;
    }

    public String getLoggedInUser() {
        return loggedInUser;
    }

    // Helper method to load images from resources and scale them to specific dimensions.
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

    // Attaches focus and mouse listeners to UI components to provide interactive visual feedback.
    private void addFocusEffect(JComponent c, Color defaultColor) {
        c.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                c.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Red border when focused.
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                c.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            }
        });
        c.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!c.isFocusOwner()) {
                    c.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Red border when hovering.
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!c.isFocusOwner()) {
                    c.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                }
            }
        });
    }
}

