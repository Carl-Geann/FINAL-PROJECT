import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Date;

/**
 * Kini nga class nga HotelReservationSystem kay para sa main application window (JFrame).
 * Kini nagsilbi nga coordinator, nagdumala sa mga UI component, layout, ug logic.
 */
public class HotelReservationSystem extends JFrame {

    // --- [1. UI Components para sa Inputs] ---
    private JComboBox<Integer> cbRoomNo, cbNights, cbGuestCount;
    private JTextField txtName, txtPrice, txtGuest, txtDiscount, txtTotalPayment;
    private JComboBox<String> cbCategory, cbStatus, cbPaymentMethod;
    private JSpinner spBookingAt, spBookOutAt;

    // --- [2. UI Components para sa Data Display] ---
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;
    private JLabel lblUser, lblTotalGuests;

    // --- [3. Core Logic Managers & Handlers] ---
    public HotelManager hotelManager;
    public AuthManager authManager;
    private RoomFormHandler formHandler;
    private RoomTableHandler tableHandler;
    private GuestProfilePanel guestProfilePanel;

    // --- [4. Buttons & Layout Panels] ---
    private JButton btnBookOut, btnBookIn, btnSignOut;
    private JButton btnRoomDetails, btnGuestProfile;
    private JPanel detailsPanel, headerPanel, actionsPanel, leftContent;
    private CardLayout leftCardLayout;
    private JSplitPane mainSplit;

    // --- [5. Theme Colors] ---
    private final Color THEME_RED = new Color(150, 0, 0);
    private final Color THEME_YELLOW = new Color(255, 255, 100);
    private final Color LIGHT_YELLOW = new Color(255, 255, 240);

    public HotelReservationSystem() {
        initWindowProperties(); // I-set up ang properties sa JFrame (sama sa title ug icon)
        initManagers();         // I-initialize ang mga data managers (HotelManager ug AuthManager)
        initComponents();       // I-initialize ang tanang UI components (mga buttons ug fields)
        initLayout();           // I-set up ang visual structure sa dashboard (SplitPane ug Cards)
        initListeners();        // Idugang ang mga event listeners para sa interactive actions
        
        // I-finalize ang UI state
        formHandler.updateRoomNoOptions(); // I-update ang listahan sa room numbers
        roomUpdateTable();                 // I-refresh ang data sa table
        updateAuthUI();                    // I-check kung kinsa ang naka-login
        updateActionButtons();             // I-enable o disable ang mga buttons
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH); // I-full screen ang window
    }

    // --- [Method Group: Initialization] ---

    private void initWindowProperties() {
        setTitle("UM DEL HOTEL - Reservation List");
        setExtendedState(JFrame.MAXIMIZED_BOTH); // Himoon nga full screen ang window
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // I-close ang app inig click sa 'X'
        setLayout(new BorderLayout());

        ImageIcon windowIcon = getScaledIcon("logo.png", 48, 48); // I-load ang logo sa hotel
        if (windowIcon != null) setIconImage(windowIcon.getImage());
    }

    private void initManagers() {
        hotelManager = new HotelManager(); // Class nga nagdumala sa listahan sa mga kwarto
        authManager = new AuthManager();   // Class nga nagdumala sa pag-login/logout
    }

    private void initComponents() {
        Font fieldFont = new Font("Trade Gothic", Font.BOLD, 13);
        if (fieldFont.getFamily().equals("Dialog")) fieldFont = new Font("Arial", Font.BOLD, 13);

        // Input Fields Initialization
        cbRoomNo = new JComboBox<>();
        cbRoomNo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI()); // Custom UI para sa combo box
        
        txtName = new JTextField(); txtName.setEditable(false); // Dili ma-edit ang room name
        txtPrice = new JTextField(); txtPrice.setEditable(false); // Dili ma-edit ang price
        txtGuest = new JTextField();
        
        cbNights = new JComboBox<>();
        cbNights.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        for (int i = 1; i <= 7; i++) cbNights.addItem(i); // Options para sa gidaghanon sa gabii

        txtDiscount = new JTextField("0");
        txtTotalPayment = new JTextField(); txtTotalPayment.setEditable(false);
        
        cbGuestCount = new JComboBox<>();
        cbGuestCount.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        
        cbCategory = new JComboBox<>(new String[]{"VIP BED", "FAMILY BED", "COUPLE BED"});
        cbCategory.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        
        cbStatus = new JComboBox<>(new String[]{"Free", "Booked"});
        cbStatus.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        
        cbPaymentMethod = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash", "Online Transfer"});
        cbPaymentMethod.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());

        // Date Spinners para sa pagpili og petsa ug oras
        spBookingAt = createDateSpinner();
        spBookOutAt = createDateSpinner();

        // Handlers Initialization - kini ang mga classes nga naay logic
        formHandler = new RoomFormHandler(this, hotelManager, cbRoomNo, cbNights, cbGuestCount, txtName, txtPrice, txtGuest, txtDiscount, txtTotalPayment, cbCategory, cbStatus, cbPaymentMethod, spBookingAt, spBookOutAt);
        formHandler.updateGuestCountOptions();
        guestProfilePanel = new GuestProfilePanel(this, hotelManager, formHandler);

        // I-apply ang styling sa tanang input fields
        JComponent[] fields = {cbRoomNo, txtName, cbCategory, txtGuest, cbStatus, cbGuestCount, txtPrice, cbPaymentMethod, txtDiscount, cbNights, txtTotalPayment, spBookingAt, spBookOutAt};
        for (JComponent f : fields) {
            f.setFont(fieldFont); f.setBackground(LIGHT_YELLOW); f.setForeground(THEME_RED);
            f.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            addFocusEffect(f); // Idugang ang hover/focus effect
        }
    }

    private JSpinner createDateSpinner() {
        JSpinner sp = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE));
        sp.setEditor(new JSpinner.DateEditor(sp, "yyyy-MM-dd HH:mm")); // Format sa petsa
        ((JSpinner.DefaultEditor) sp.getEditor()).getTextField().setEditable(false);
        sp.setEnabled(false); // Disabled by default
        return sp;
    }

    private void initLayout() {
        // I-set up ang panels sa wala (form) ug tuo (table)
        JPanel leftContainer = createLeftContainer();
        JPanel rightContainer = createRightPanel();

        // Gamit ang JSplitPane para ma-adjust ang gidak-on sa duha ka panel
        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftContainer, rightContainer);
        mainSplit.setDividerLocation(500); // 500 pixels ang default width sa wala
        mainSplit.setDividerSize(5);
        mainSplit.setBorder(null);
        
        add(mainSplit, BorderLayout.CENTER);
        add(createFooter(), BorderLayout.SOUTH); // Footer para sa login status
    }

    private JPanel createLeftContainer() {
        Font tradeGothicBold = new Font("Trade Gothic", Font.BOLD, 12);
        if (tradeGothicBold.getFamily().equals("Dialog")) tradeGothicBold = new Font("Arial", Font.BOLD, 12);
        
        // Navigation Buttons Panel para sa pag-switch sa views
        headerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        headerPanel.setBackground(THEME_RED);
        headerPanel.setPreferredSize(new Dimension(500, 45));
        btnRoomDetails = createNavButton("Room Details", tradeGothicBold, THEME_YELLOW, THEME_RED, "roomdetails.png");
        btnGuestProfile = createNavButton("Guest Profile", tradeGothicBold, THEME_YELLOW, THEME_RED, "guest list.png");
        headerPanel.add(btnRoomDetails); headerPanel.add(btnGuestProfile);

        // Left Content gamit ang CardLayout (para naay switch effect)
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

    private JPanel createDetailsPanel() {
        detailsPanel = new JPanel(new BorderLayout(0, 5));
        detailsPanel.setBackground(THEME_RED);

        JPanel formPanel = new JPanel(new GridBagLayout()); // Gigamit ang GridBagLayout para sa alignment
        formPanel.setBackground(THEME_RED);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL;

        String[] labels = {"Room No", "Room", "Category", "Guest Name", "Status", "Guests In", "Price", "Payment Method", "Discount", "Nights", "Total Payment", "Booking Date/Time", "Book Out"};
        String[] iconPaths = {"room no.png", "room.png", "category.png", "guest list.png", "status.png", "occupied.png", "total payment.png", "payment method.png", "discount.png", "book_in.png", "total payment.png", "book_in.png", "book out.png"};
        JComponent[] fields = {cbRoomNo, txtName, cbCategory, txtGuest, cbStatus, cbGuestCount, txtPrice, cbPaymentMethod, txtDiscount, cbNights, txtTotalPayment, spBookingAt, spBookOutAt};

        Font labelFont = new Font("Trade Gothic", Font.BOLD, 13);
        if (labelFont.getFamily().equals("Dialog")) labelFont = new Font("Arial", Font.BOLD, 13);

        // Pag-loop para sa paghimo sa mga labels ug input fields sa form
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

        // Panel para sa mga aksyon (Book In/Out)
        actionsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionsPanel.setBackground(THEME_RED);
        actionsPanel.setPreferredSize(new Dimension(500, 45));
        btnBookIn = createActionButton("Book In", labelFont, THEME_YELLOW, THEME_RED, "book_in.png");
        btnBookOut = createActionButton("Book Out", labelFont, THEME_YELLOW, THEME_RED, "book out.png");
        actionsPanel.add(btnBookIn); actionsPanel.add(btnBookOut);

        detailsPanel.add(formScroll, BorderLayout.CENTER);
        detailsPanel.add(actionsPanel, BorderLayout.SOUTH);
        return detailsPanel;
    }

    private JPanel createRightPanel() {
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(LIGHT_YELLOW);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Filter & Search Panel para sa table
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

        // I-set up ang main JTable
        initTable();
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(LIGHT_YELLOW);
        
        rightPanel.add(filterPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Table Handler Initialization - kini ang nagdumala sa interaction sa table
        tableHandler = new RoomTableHandler(this, hotelManager, table, model, lblTotalGuests, txtSearch);

        // Mga action listeners para sa filtering
        btnShowAll.addActionListener(e -> tableHandler.showRoomsByStatus(null));
        btnFree.addActionListener(e -> tableHandler.showRoomsByStatus("Free"));
        btnOccupied.addActionListener(e -> tableHandler.showRoomsByStatus("Booked"));
        btnRefresh.addActionListener(e -> { txtSearch.setText(""); roomUpdateTable(); });

        return rightPanel;
    }

    private void initTable() {
        String[] columns = {"Room No", "Room Name", "Status", "Type", "Guest Name", "Price", "Nights", "Discount", "Total", "Guests In", "Payment Method"};
        model = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int r, int c) { return false; } // Dili pwede i-edit ang table cells
        };
        
        Font tableFont = new Font("Trade Gothic", Font.BOLD, 12);
        if (tableFont.getFamily().equals("Dialog")) tableFont = new Font("Arial", Font.BOLD, 12);
        
        table = new JTable(model);
        table.setRowHeight(25);
        table.setFont(tableFont); // I-set ang table font to bold
        table.getTableHeader().setBackground(THEME_RED);
        table.getTableHeader().setForeground(THEME_YELLOW);
        table.getTableHeader().setFont(tableFont.deriveFont(Font.BOLD, 13f)); // I-set ang header font to bold
        table.setSelectionBackground(Color.RED); // Pula ang color kung naay napili nga row
        table.setSelectionForeground(Color.WHITE);
        table.setGridColor(Color.RED);

        // Custom renderer para ma-center ang text ug ma-pula ang background sa pinili
        final Font rendererFont = tableFont;
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override public Component getTableCellRendererComponent(JTable t, Object v, boolean isS, boolean hasF, int r, int c) {
                Component comp = super.getTableCellRendererComponent(t, v, isS, hasF, r, c);
                comp.setBackground(isS ? Color.RED : LIGHT_YELLOW);
                comp.setForeground(isS ? Color.WHITE : THEME_RED);
                comp.setFont(rendererFont); // Siguradohon nga bold ang font sa cells
                return comp;
            }
        };
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
    }

    private JPanel createFooter() {
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(THEME_RED);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        
        lblUser = new JLabel(""); lblUser.setForeground(THEME_YELLOW);
        btnSignOut = new JButton("Sign Out"); // Button para sa pag-logout
        btnSignOut.setBackground(THEME_YELLOW); btnSignOut.setForeground(THEME_RED);
        btnSignOut.addActionListener(e -> signOut());

        footer.add(lblUser, BorderLayout.WEST);
        footer.add(btnSignOut, BorderLayout.EAST);
        return footer;
    }

    private void initListeners() {
        // I-switch ang view ngadto sa Room Details
        btnRoomDetails.addActionListener(e -> { leftCardLayout.show(leftContent, "details"); updateActionButtons(); });
        
        // I-switch ang view ngadto sa Guest Profile ug i-load ang data
        btnGuestProfile.addActionListener(e -> { guestProfilePanel.loadGuestData(formHandler.getCurrentSelected()); leftCardLayout.show(leftContent, "guestProfile"); });
        
        // Aksyon inig click sa Book In ug Book Out
        btnBookIn.addActionListener(e -> formHandler.bookInRoom());
        btnBookOut.addActionListener(e -> formHandler.bookOutRoom());

        // Live search listener samtang nag-type sa search field
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { tableHandler.searchRooms(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { tableHandler.searchRooms(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { tableHandler.searchRooms(); }
        });

        // Listener kon naay mapili nga row sa table
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                Room r = hotelManager.findRoomByNo(Integer.parseInt(model.getValueAt(modelRow, 0).toString()));
                if (r != null) formHandler.selectRoomInForm(r);
            }
        });

        // Listener kon nausab ang status o category sa form
        cbStatus.addActionListener(e -> { if ("Booked".equals(cbStatus.getSelectedItem())) { spBookingAt.setValue(new Date()); spBookOutAt.setValue(new Date()); } });
        cbCategory.addActionListener(e -> { formHandler.updateRoomNoOptions(); formHandler.updateGuestCountOptions(); });
        cbRoomNo.addActionListener(e -> { Integer val = (Integer) cbRoomNo.getSelectedItem(); if (val != null) { Room r = hotelManager.findRoomByNo(val); if (r != null) formHandler.selectRoomInForm(r); } });
    }

   

    // I-update ang UI depende kung naay naka-login o wala
    public void updateAuthUI() {
        boolean signedIn = authManager.isSignedIn();
        JComponent[] inputs = {cbRoomNo, txtName, txtPrice, txtGuest, cbNights, txtDiscount, cbGuestCount, cbCategory, cbStatus, cbPaymentMethod, btnBookIn, btnBookOut};
        for (JComponent c : inputs) c.setEnabled(signedIn); // I-disable ang form kung walay naka-login
        lblUser.setText(signedIn ? ("Signed in as " + authManager.getCurrentUser()) : "Signed out");
        updateActionButtons();
    }

    // I-update ang status sa Book In ug Book Out buttons base sa status sa kwarto
    public void updateActionButtons() {
        boolean signedIn = authManager.isSignedIn();
        Room r = formHandler.getCurrentSelected();
        if (r != null) {
            boolean isBooked = "Booked".equals(r.getStatus());
            btnBookIn.setEnabled(signedIn && !isBooked);
            btnBookOut.setEnabled(signedIn && isBooked);
        } else {
            btnBookIn.setEnabled(false); btnBookOut.setEnabled(false);
        }
    }

    // Logic para sa pag-logout ug pagpakita sa login dialog
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

    // Method Group: Utilities

    public void roomUpdateTable() { tableHandler.roomUpdateTable(); } // I-refresh ang data sa table
    public void showRoomDetails() { leftCardLayout.show(leftContent, "details"); } // I-show ang details form

    // Method para sa pag-adjust sa size sa mga icons
    private ImageIcon getScaledIcon(String path, int w, int h) {
        try {
            java.net.URL url = getClass().getResource("/pic/" + path);
            if (url != null) return new ImageIcon(new ImageIcon(url).getImage().getScaledInstance(w, h, Image.SCALE_SMOOTH));
        } catch (Exception e) {}
        return null;
    }

    //  methods para sa paghimo og buttons nga naay styling
    private JButton createNavButton(String t, Font f, Color bg, Color fg, String p) {
        JButton b = new JButton(t); b.setFont(f); b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setBorder(BorderFactory.createLineBorder(fg, 1));
        ImageIcon i = getScaledIcon(p, 20, 20); if (i != null) { b.setIcon(i); b.setIconTextGap(10); }
        return b;
    }

    private JButton createActionButton(String t, Font f, Color bg, Color fg, String p) {
        JButton b = new JButton(t); b.setFont(f); b.setBackground(bg); b.setForeground(fg);
        b.setFocusPainted(false); b.setBorder(BorderFactory.createLineBorder(fg, 1));
        ImageIcon i = getScaledIcon(p, 20, 20); if (i != null) { b.setIcon(i); b.setIconTextGap(10); }
        return b;
    }

    private JButton createFilterButton(String t, Font f, Color bg, Color fg, int w, String p) {
        JButton b = new JButton(t); b.setPreferredSize(new Dimension(w, 35));
        b.setBackground(bg); b.setForeground(fg); b.setFont(f);
        ImageIcon i = getScaledIcon(p, 20, 20); if (i != null) { b.setIcon(i); b.setIconTextGap(5); }
        return b;
    }

    // Idugang ang focus ug hover effect sa mga components (sama sa red border)
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

    // Authentication methods para sa Main access
    public void setUsers(java.util.Map<String, String> c) { authManager.setUsers(c); }
    public void setSignedIn(boolean v) { authManager.setSignedIn(v); }
    public void setCurrentUser(String u) { authManager.setCurrentUser(u); }
    public java.util.Map<String, String> getUsersMap() { return authManager.getUsers(); }
}
