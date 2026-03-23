import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Date;

/**
 * The primary application window (JFrame) that serves as the system's central coordinator.
 * Manages the layout, navigation, and integration between various UI components and data handlers.
 */
public class HotelReservationSystem extends JFrame {

    // UI Components for Inputs
    private JComboBox<Integer> cbRoomNo, cbNights, cbGuestCount;
    private JTextField txtName, txtPrice, txtGuest, txtDiscount, txtTotalPayment, txtStatus;
    private JComboBox<String> cbCategory, cbPaymentMethod;
    private JSpinner spBookingAt, spBookOutAt;

    // UI Components for Data Display
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JLabel lblUser, lblTotalGuests;

    // Managers Handlers
    public HotelManager hotelManager;
    public AuthManager authManager;
    private RoomFormHandler formHandler;
    private RoomTableHandler tableHandler;
    private GuestProfilePanel guestProfilePanel;

    // Buttons & Layout Panels
    private JButton btnBookIn, btnSignOut;
    private JButton btnRoomDetails, btnGuestProfile;
    private JPanel detailsPanel, headerPanel, actionsPanel, leftContent;
    private CardLayout leftCardLayout;
    private JSplitPane mainSplit;

    // Theme Colors
    private final Color THEME_RED = new Color(150, 0, 0);
    private final Color THEME_YELLOW = new Color(255, 255, 100);
    private final Color LIGHT_YELLOW = new Color(255, 255, 240);

    /**
     * Constructor that initializes the main system frame and its various components.
     */
    public HotelReservationSystem() {
        initWindowProperties(); // Configures basic JFrame properties like title and icons
        initManagers();         // Initializes the core data management classes
        initComponents();       // Creates and styles all UI components
        initLayout();           // Sets up the visual dashboard structure
        initListeners();        // Attaches action listeners to interactive elements
        
        // Finalize UI state
        formHandler.updateRoomNoOptions(); // Populates the room number list
        roomUpdateTable();                 // Refreshes the data display table
        updateAuthUI();                    // Synchronizes the UI with current authentication state
        updateActionButtons();             // Enables or disables context-sensitive buttons
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH); // Maximizes the application window
    }

    // Initialization

    /**
     * Configures the main application window's properties.
     */
    private void initWindowProperties() {
        setTitle("UM DEL HOTEL - Reservation List");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Maximizes the window to full screen
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Ensures the application exits on close
        setLayout(new BorderLayout());

        ImageIcon windowIcon = getScaledIcon("logo.png", 48, 48); // Loads the application logo
        if (windowIcon != null) setIconImage(windowIcon.getImage());
    }

    /**
     * Initializes the core business logic managers.
     */
    private void initManagers() {
        hotelManager = new HotelManager(); // Manages the hotel room inventory
        authManager = new AuthManager();   // Manages user authentication and session
    }

    /**
     * Initializes and configures all UI components and their initial states.
     */
    private void initComponents() {
        Font fieldFont = new Font("Trade Gothic", Font.BOLD, 13);
        if (fieldFont.getFamily().equals("Dialog")) fieldFont = new Font("Arial", Font.BOLD, 13);

        // Input Fields Initialization
        cbRoomNo = new JComboBox<>();
        cbRoomNo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI()); // Custom UI for the room number dropdown
        
        txtName = new JTextField(); txtName.setEditable(false); // Room name field (read-only)
        txtPrice = new JTextField(); txtPrice.setEditable(false); // Nightly rate field (read-only)
        txtGuest = new JTextField();
        
        cbNights = new JComboBox<>();
        cbNights.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        for (int i = 1; i <= 7; i++) cbNights.addItem(i); // Populates the stay duration options

        txtDiscount = new JTextField("0");
        txtTotalPayment = new JTextField(); txtTotalPayment.setEditable(false);
        
        cbGuestCount = new JComboBox<>();
        cbGuestCount.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        
        cbCategory = new JComboBox<>(new String[]{"VIP BED", "FAMILY BED", "COUPLE BED"});
        cbCategory.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        
        txtStatus = new JTextField("Free"); txtStatus.setEditable(false); // Current room status (read-only)
        
        cbPaymentMethod = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash", "Online Transfer"});
        cbPaymentMethod.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());

        // Date Spinners for selecting booking timestamps
        spBookingAt = createDateSpinner();
        spBookOutAt = createDateSpinner();

        // Handlers and sub-panels initialization
        formHandler = new RoomFormHandler(this, hotelManager, cbRoomNo, cbNights, cbGuestCount, txtName, txtPrice, txtGuest, txtDiscount, txtTotalPayment, cbCategory, txtStatus, cbPaymentMethod, spBookingAt, spBookOutAt);
        formHandler.updateGuestCountOptions();
        guestProfilePanel = new GuestProfilePanel(this, hotelManager, formHandler);

        // Batch processing to apply consistent styling to all input components
        JComponent[] fields = {cbRoomNo, txtName, cbCategory, txtGuest, txtStatus, cbGuestCount, txtPrice, cbPaymentMethod, txtDiscount, cbNights, txtTotalPayment, spBookingAt, spBookOutAt};
        for (JComponent f : fields) {
            f.setFont(fieldFont); f.setBackground(LIGHT_YELLOW); f.setForeground(THEME_RED);
            f.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            addFocusEffect(f); // Attaches focus and hover feedback effects
        }
    }

    /**
     * Creates and configures a standard JSpinner for date and time selection.
     */
    private JSpinner createDateSpinner() {
        JSpinner sp = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE));
        sp.setEditor(new JSpinner.DateEditor(sp, "yyyy-MM-dd HH:mm")); // Sets the display format
        ((JSpinner.DefaultEditor) sp.getEditor()).getTextField().setEditable(false);
        sp.setEnabled(false); // Initializes as disabled
        return sp;
    }

    /**
     * Sets up the main layout and partitioning of the dashboard.
     */
    private void initLayout() {
        // Creates the left-side configuration panel and right-side data table
        JPanel leftContainer = createLeftContainer();
        JPanel rightContainer = createRightPanel();

        // Partition the screen into two adjustable sections
        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftContainer, rightContainer);
        mainSplit.setDividerLocation(500); // Initial partition width
        mainSplit.setDividerSize(5);
        mainSplit.setBorder(null);
        
        add(mainSplit, BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH); // Adds the status bar footer
    }

    /**
     * Creates the left-side container that hosts navigation and sub-panels.
     */
    private JPanel createLeftContainer() {
        Font tradeGothicBold = new Font("Trade Gothic", Font.BOLD, 12);
        if (tradeGothicBold.getFamily().equals("Dialog")) tradeGothicBold = new Font("Arial", Font.BOLD, 12);
        
        // Navigation bar for switching between 'Room Details' and 'Guest Profile' views
        headerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        headerPanel.setBackground(THEME_RED);
        headerPanel.setPreferredSize(new Dimension(500, 45));
        btnRoomDetails = createNavButton("Room Details", tradeGothicBold, THEME_YELLOW, THEME_RED, "roomdetails.png");
        btnGuestProfile = createNavButton("Guest Profile", tradeGothicBold, THEME_YELLOW, THEME_RED, "guest list.png");
        headerPanel.add(btnRoomDetails); headerPanel.add(btnGuestProfile);

        // Content area that uses CardLayout to swap between different functional panels
        leftCardLayout = new CardLayout();
        leftContent = new JPanel(leftCardLayout);
        leftContent.add(createDetailsPanel(), "details");
        leftContent.add(guestProfilePanel, "guestProfile");

        JPanel leftContainer = new JPanel(new BorderLayout());
        leftContainer.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        leftContainer.setBackground(THEME_RED);
        leftContainer.add(headerPanel, BorderLayout.NORTH);
        leftContainer.add(leftContent, BorderLayout.CENTER);
        return leftContainer;
    }

    /**
     * Creates the detailed form panel for viewing and managing room information.
     */
    private JPanel createDetailsPanel() {
        detailsPanel = new JPanel(new BorderLayout(0, 5));
        detailsPanel.setBackground(THEME_RED);

        JPanel formPanel = new JPanel(new GridBagLayout()); // Organized form layout
        formPanel.setBackground(THEME_RED);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Room No", "Room", "Category", "Guest Name", "Status", "Guests In", "Price", "Payment Method", "Discount", "Nights", "Total Payment", "Booking Date/Time", "Book Out"};
        String[] iconPaths = {"room no.png", "room.png", "category.png", "guest list.png", "status.png", "occupied.png", "total payment.png", "payment method.png", "discount.png", "book_in.png", "total payment.png", "book_in.png", "book out.png"};
        JComponent[] fields = {cbRoomNo, txtName, cbCategory, txtGuest, txtStatus, cbGuestCount, txtPrice, cbPaymentMethod, txtDiscount, cbNights, txtTotalPayment, spBookingAt, spBookOutAt};

        Font labelFont = new Font("Trade Gothic", Font.BOLD, 13);
        if (labelFont.getFamily().equals("Dialog")) labelFont = new Font("Arial", Font.BOLD, 13);

        // Iteratively adds labeled input fields with corresponding icons to the form
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0; gbc.gridy = i; gbc.weightx = 0;
            JLabel lbl = new JLabel(labels[i]);
            lbl.setFont(labelFont); lbl.setForeground(THEME_YELLOW);
            ImageIcon icon = getScaledIcon(iconPaths[i], 20, 20);
            if (icon != null) { lbl.setIcon(icon); lbl.setIconTextGap(10); }
            formPanel.add(lbl, gbc);

            gbc.gridx = 1; gbc.weightx = 1.0;
            fields[i].setPreferredSize(new Dimension(200, 30));
            formPanel.add(fields[i], gbc);
        }

        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBackground(THEME_RED); formScroll.getViewport().setBackground(THEME_RED);
        formScroll.setBorder(null);

        // Action panel for the primary "Book In" function
        actionsPanel = new JPanel(new GridLayout(1, 1, 10, 0)); 
        actionsPanel.setBackground(THEME_RED);
        actionsPanel.setPreferredSize(new Dimension(500, 45));
        btnBookIn = createActionButton("Book In", labelFont, THEME_YELLOW, THEME_RED, "book_in.png");
        actionsPanel.add(btnBookIn); 

        detailsPanel.add(formScroll, BorderLayout.CENTER);
        detailsPanel.add(actionsPanel, BorderLayout.SOUTH);
        return detailsPanel;
    }

    /**
     * Creates the right-side dashboard panel containing search, filters, and the main data table.
     */
    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(LIGHT_YELLOW);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter and Search controls for the room table
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(LIGHT_YELLOW);
        
        txtSearch = new JTextField(20);
        txtSearch.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        addFocusEffect(txtSearch);
        
        Font filterFont = new Font("Trade Gothic", Font.BOLD, 12);
        if (filterFont.getFamily().equals("Dialog")) filterFont = new Font("Arial", Font.BOLD, 12);

        JButton btnShowAll = createFilterButton("Show All", filterFont, THEME_RED, THEME_YELLOW, 120, "showall.png");
        JButton btnFree = createFilterButton("Free Rooms", filterFont, THEME_RED, THEME_YELLOW, 140, "free room.png");
        JButton btnOccupied = createFilterButton("Occupied", filterFont, THEME_RED, THEME_YELLOW, 140, "occupied.png");
        JButton btnRefresh = createFilterButton("Refresh", filterFont, THEME_RED, THEME_YELLOW, 120, "refresh.png");

        lblTotalGuests = new JLabel(" | Total Guests In: 0");
        lblTotalGuests.setFont(filterFont);
        lblTotalGuests.setForeground(THEME_RED);

        filterPanel.add(new JLabel("Search:")); filterPanel.add(txtSearch);
        filterPanel.add(btnShowAll); filterPanel.add(btnFree); filterPanel.add(btnOccupied); filterPanel.add(btnRefresh);
        filterPanel.add(lblTotalGuests);

        // Initializes the main JTable component
        initTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(LIGHT_YELLOW);
        
        rightPanel.add(filterPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Table Handler Initialization - manages table-specific interaction logic
        tableHandler = new RoomTableHandler(this, hotelManager, table, model, lblTotalGuests, txtSearch);

        // Attaches event listeners for filtering table rows
        btnShowAll.addActionListener(e -> tableHandler.showRoomsByStatus(null));
        btnFree.addActionListener(e -> tableHandler.showRoomsByStatus("Free"));
        btnOccupied.addActionListener(e -> tableHandler.showRoomsByStatus("Booked"));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); roomUpdateTable(); });

        return rightPanel;
    }

    /**
     * Configures the JTable properties, models, and custom renderers.
     */
    private void initTable() {
        String[] columns = {"Room No", "Room Name", "Status", "Type", "Guest Name", "Price", "Nights", "Discount", "Total", "Guests In", "Payment Method"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; } // Disables direct cell editing
        };
        
        Font tableFont = new Font("Trade Gothic", Font.BOLD, 12);
        if (tableFont.getFamily().equals("Dialog")) tableFont = new Font("Arial", Font.BOLD, 12);
        
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(tableFont); 
        table.getTableHeader().setBackground(THEME_RED);
        table.getTableHeader().setForeground(THEME_YELLOW);
        table.getTableHeader().setFont(tableFont.deriveFont(Font.BOLD, 13f)); 
        table.setSelectionBackground(Color.RED); // Visual feedback for row selection
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(Color.RED);

        // Custom renderer for cell centering and selection feedback
        final Font rendererFont = tableFont;
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                comp.setBackground(isS ? Color.RED : LIGHT_YELLOW);
                comp.setForeground(isS ? Color.WHITE : THEME_RED);
                comp.setFont(rendererFont); 
                return comp;
            }
        };
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    /**
     * Creates the application's footer bar for session status and logout controls.
     */
    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(THEME_RED);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        lblUser = new JLabel(""); lblUser.setForeground(THEME_YELLOW);
        btnSignOut = new JButton("Sign Out"); // Session termination control
        btnSignOut.setBackground(THEME_YELLOW); btnSignOut.setForeground(THEME_RED);
        btnSignOut.addActionListener(e -> signOut());

        footer.add(lblUser, BorderLayout.WEST);
        footer.add(btnSignOut, BorderLayout.EAST);
        return footer;
    }

    /**
     * Attaches interaction listeners to all primary UI controls.
     */
    private void initListeners() {
        // View switching controls
        btnRoomDetails.addActionListener(e -> { leftCardLayout.show(leftContent, "details"); updateActionButtons(); });
        
        // Navigation to guest profile and data synchronization
        btnGuestProfile.addActionListener(e -> { guestProfilePanel.loadGuestData(formHandler.getCurrentSelected()); leftCardLayout.show(leftContent, "guestProfile"); });
        
        // Primary action control
        btnBookIn.addActionListener(e -> formHandler.bookInRoom());

        // Real-time search/filter feedback
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { tableHandler.searchRooms(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { tableHandler.searchRooms(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { tableHandler.searchRooms(); }
        });

        // Row selection feedback to update form inputs
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                Room r = hotelManager.findRoomByNo(Integer.parseInt(model.getValueAt(modelRow, 0).toString()));
                if (r != null) formHandler.selectRoomInForm(r);
            }
        });

        // Dynamic form updates based on selection changes
        cbCategory.addActionListener(e -> { 
            if (formHandler.isUpdating()) return;
            formHandler.updateRoomNoOptions(); 
            formHandler.updateGuestCountOptions(); 
            formHandler.updatePriceFromCategory(); 
        });
        
        cbRoomNo.addActionListener(e -> { 
            if (formHandler.isUpdating()) return;
            Integer val = (Integer) cbRoomNo.getSelectedItem(); 
            if (val != null) { 
                Room r = hotelManager.findRoomByNo(val); 
                if (r != null) formHandler.selectRoomInForm(r); 
            } 
        });

        // Live calculation updates based on user inputs
        cbNights.addActionListener(e -> {
            if (formHandler.isUpdating()) return;
            formHandler.calculateTotal();
        });
        cbGuestCount.addActionListener(e -> {
            if (formHandler.isUpdating()) return;
            formHandler.calculateTotal();
        });
        txtDiscount.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) { formHandler.calculateTotal(); }
        });
    }

    //Auth

    /**
     * Synchronizes the UI component availability with the current authentication state.
     */
    public void updateAuthUI() {
        boolean signedIn = authManager.isSignedIn();
        JComponent[] inputs = {cbRoomNo, txtName, txtPrice, txtGuest, cbNights, txtDiscount, cbGuestCount, cbCategory, txtStatus, cbPaymentMethod, btnBookIn};
        for (JComponent c : inputs) c.setEnabled(signedIn); // Restricts access if not authenticated
        lblUser.setText(signedIn ? ("Signed in as " + authManager.getCurrentUser()) : "Signed out");
        updateActionButtons();
    }

    /**
     * Enables or disables action buttons based on context, such as current room status.
     */
    public void updateActionButtons() {
        boolean signedIn = authManager.isSignedIn();
        Room r = formHandler.getCurrentSelected();
        if (r != null) {
            boolean isBooked = "Booked".equals(r.getStatus());
            btnBookIn.setEnabled(signedIn && !isBooked);
        } else {
            btnBookIn.setEnabled(false); 
        }
    }

    /**
     * Processes user sign-out and returns to the login screen.
     */
    private void signOut() {
        authManager.setSignedIn(false);
        setVisible(false);
        LoginDialog login = new LoginDialog(this, authManager);
        login.setVisible(true);
        if (login.isAuthenticated()) {
            authManager.setSignedIn(true); authManager.setCurrentUser(login.getLoggedInUser());
            updateAuthUI(); roomUpdateTable(); setVisible(true);
        } else System.exit(0);
    }

    // Utilities

    /**
     * Triggers a refresh of the room inventory table display.
     */
    public void roomUpdateTable() { tableHandler.roomUpdateTable(); } 
    
    /**
     * Switches the active view to the room details form.
     */
    public void showRoomDetails() { leftCardLayout.show(leftContent, "details"); } 

    /**
     * Helper method to load and scale icons smoothly for UI components.
     */
    private ImageIcon getScaledIcon(String path, int w, int h) {
        try {
            java.net.URL url = getClass().getResource("/pic/" + path);
            if (url != null) return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        return null;
    }

    /**
     * Factory method for creating and styling navigation buttons.
     */
    private JButton createNavButton(String t, Font f, Color bg, Color fg, String p) {
        JButton b = new JButton(t); b.setFont(f); b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setBorder(BorderFactory.createLineBorder(fg, 1));
        ImageIcon i = getScaledIcon(p, 20, 20); if (i != null) { b.setIcon(i); b.setIconTextGap(10); }
        return b;
    }

    /**
     * Factory method for creating and styling primary action buttons.
     */
    private JButton createActionButton(String t, Font f, Color bg, Color fg, String p) {
        JButton b = new JButton(t); b.setFont(f); b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setBorder(BorderFactory.createLineBorder(fg, 1));
        ImageIcon i = getScaledIcon(p, 20, 20); if (i != null) { b.setIcon(i); b.setIconTextGap(10); }
        return b;
    }

    /**
     * Factory method for creating and styling table filter buttons.
     */
    private JButton createFilterButton(String t, Font f, Color bg, Color fg, int w, String p) {
        JButton b = new JButton(t); b.setPreferredSize(new Dimension(w, 35));
        b.setBackground(bg); b.setForeground(fg); b.setFont(f);
        ImageIcon i = getScaledIcon(p, 20, 20); if (i != null) { b.setIcon(i); b.setIconTextGap(5); }
        return b;
    }

    /**
     * Attaches interactive focus and hover feedback to a component.
     */
    private void addFocusEffect(JComponent c) {
        c.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent e) { c.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); }
            public void focusLost(java.awt.event.FocusEvent e) { c.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); }
        });
        c.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent e) { if (!c.isFocusOwner()) c.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); }
            public void mouseExited(java.awt.event.MouseEvent e) { if (!c.isFocusOwner()) c.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); }
        });
    }

    // Authentication methods for Main access
    public void setUsers(java.util.Map<String, String> c) { authManager.setUsers(c); }
    public void setSignedIn(boolean v) { authManager.setSignedIn(v); }
    public void setCurrentUser(String u) { authManager.setCurrentUser(u); }
    public java.util.Map<String, String> getUsersMap() { return authManager.getUsers(); }
}

