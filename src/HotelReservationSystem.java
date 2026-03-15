import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.util.Date;

/**
 * Kini nga class nga HotelReservationSystem kay para sa main application window
 * (JFrame). Kini nagsilbi nga coordinator, nagdumala sa mga UI component,
 * layout, ug nag-delegate sa logic ngadto sa mga specialized handler.
 */
public class HotelReservationSystem extends JFrame {

    // UI Components for input and selection
    private JComboBox<Integer> cbRoomNo, cbNights, cbGuestCount;
    private JTextField txtName, txtPrice, txtGuest, txtDiscount, txtTotalPayment;
    private JComboBox<String> cbCategory, cbStatus, cbPaymentMethod;

    // UI Components for data display
    private JTable table;
    private DefaultTableModel model;
    private JTextField txtSearch;

    // Core logic managers - Changed to public for Main access
    public HotelManager hotelManager;
    public AuthManager authManager;

    // Specialized UI logic handlers
    private RoomFormHandler formHandler;
    private RoomTableHandler tableHandler;
    private GuestProfilePanel guestProfilePanel;

    // Buttons and Labels
    private JButton btnBookOut, btnBookIn;
    private JLabel lblUser, lblTotalGuests;
    private JButton btnSignOut;

    // Date/Time spinners
    private JSpinner spBookingAt, spBookOutAt;

    // Navigation and Layout components
    private JButton btnRoomDetails, btnGuestProfile;
    private JPanel detailsPanel, headerPanel;
    private JPanel actionsPanel;
    private JPanel leftContent; // This will have CardLayout
    private CardLayout leftCardLayout;

    private JSplitPane mainSplit;

    public HotelReservationSystem() {
        // Kini nga code nag-initialize sa mga properties sa main JFrame window
        setTitle("UM DEL HOTEL - Reservation List");
        
        // Kini nga code nag-set sa window sa full screen.
        setExtendedState(JFrame.MAXIMIZED_BOTH);
        setUndecorated(false); // Atong ibilin ang title bar para sa main dashboard para naay window controls
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Kini nga code nag-set sa icon sa window title bar
        ImageIcon windowIcon = getScaledIcon("logo.png", 48, 48);
        if (windowIcon != null) {
            setIconImage(windowIcon.getImage());
        }

        // Pag-initialize sa mga manager para sa data ug authentication
        hotelManager = new HotelManager();
        authManager = new AuthManager();

        // Kini nga code nag-define sa mga font ug color para sa usa ka consistent nga UI theme
        Font tradeGothicBold = new Font("Trade Gothic", Font.BOLD, 12);
        if (tradeGothicBold.getFamily().equals("Dialog")) tradeGothicBold = new Font("Arial", Font.BOLD, 12);
        
        Font tradeGothicPlain = new Font("Trade Gothic", Font.PLAIN, 12);
        if (tradeGothicPlain.getFamily().equals("Dialog")) tradeGothicPlain = new Font("Arial", Font.PLAIN, 12);
        
        Font labelFont = new Font("Trade Gothic", Font.BOLD, 13);
        if (labelFont.getFamily().equals("Dialog")) labelFont = new Font("Arial", Font.BOLD, 13);
        
        Font fieldFont = new Font("Trade Gothic", Font.BOLD, 13);
        if (fieldFont.getFamily().equals("Dialog")) fieldFont = new Font("Arial", Font.BOLD, 13);
        
        Font headerFont = new Font("Trade Gothic", Font.BOLD, 15);
        if (headerFont.getFamily().equals("Dialog")) headerFont = new Font("Arial", Font.BOLD, 15);

        Color themeRed = new Color(150, 0, 0); // Maroon/Deep Red nga theme
        Color themeYellow = new Color(255, 255, 100); // Dalag nga color
        Color lightYellow = new Color(255, 255, 240); // Mas hayag nga dalag para sa fields

        // Kini nga code nag-set up sa Room Details Form panel (Ang form sa wala)
        detailsPanel = new JPanel(new BorderLayout(0, 5));
        detailsPanel.setBackground(themeRed);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(themeRed);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Kini nga code nag-initialize sa mga input field sa form (Combo boxes ug Text fields)
        cbRoomNo = new JComboBox<>();
        cbRoomNo.setUI(new javax.swing.plaf.basic.BasicComboBoxUI()); // I-flat ang hitsura sa combo box
        txtName = new JTextField();
        txtName.setEditable(false);
        txtPrice = new JTextField();
        txtPrice.setEditable(false);
        txtGuest = new JTextField();
        cbNights = new JComboBox<>();
        cbNights.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        for (int i = 1; i <= 7; i++) {
            cbNights.addItem(i); // Idugang ang options sa nights (1-7)
        }
        txtDiscount = new JTextField("0");
        txtTotalPayment = new JTextField();
        txtTotalPayment.setEditable(false);
        cbGuestCount = new JComboBox<>();
        cbGuestCount.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        cbCategory = new JComboBox<>(new String[]{"VIP BED", "FAMILY BED", "COUPLE BED"});
        cbCategory.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        cbStatus = new JComboBox<>(new String[]{"Free", "Booked"});
        cbStatus.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());
        cbPaymentMethod = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash", "Online Transfer"});
        cbPaymentMethod.setUI(new javax.swing.plaf.basic.BasicComboBoxUI());

        // Kini nga code nag-set up sa mga date spinner para sa booking ug book out dates
        spBookingAt = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE));
        spBookingAt.setEditor(new JSpinner.DateEditor(spBookingAt, "yyyy-MM-dd HH:mm"));
        ((JSpinner.DefaultEditor) spBookingAt.getEditor()).getTextField().setEditable(false);
        spBookingAt.setEnabled(false);

        spBookOutAt = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE));
        spBookOutAt.setEditor(new JSpinner.DateEditor(spBookOutAt, "yyyy-MM-dd HH:mm"));
        ((JSpinner.DefaultEditor) spBookOutAt.getEditor()).getTextField().setEditable(false);
        spBookOutAt.setEnabled(false);

        // Kini nga code nag-initialize sa form handler para sa pagdumala sa logic sa form
        formHandler = new RoomFormHandler(this, hotelManager,
                cbRoomNo, cbNights, cbGuestCount, txtName, txtPrice, txtGuest, txtDiscount,
                txtTotalPayment, cbCategory, cbStatus, cbPaymentMethod, spBookingAt, spBookOutAt);

        formHandler.updateGuestCountOptions(); // I-update ang options sa guest count depende sa category

        // Kini nga code nag-initialize sa guest profile panel.
        guestProfilePanel = new GuestProfilePanel(this, hotelManager, formHandler);

        // Kini nga code nagdugang og label ug nagdugang og mga field sa form panel.
        String[] labels = {"Room No", "Room", "Category", "Guest Name", "Status", "Guests In", "Price", "Payment Method", "Discount", "Nights", "Total Payment", "Booking Date/Time", "Book Out"};
        String[] iconPaths = {"room no.png", "room.png", "category.png", "guest list.png", "status.png", "occupied.png", "total payment.png", "payment method.png", "discount.png", "book_in.png", "total payment.png", "book_in.png", "book out.png"};
        JComponent[] fields = {cbRoomNo, txtName, cbCategory, txtGuest, cbStatus, cbGuestCount, txtPrice, cbPaymentMethod, txtDiscount, cbNights, txtTotalPayment, spBookingAt, spBookOutAt};

        // Kini nga loop nag-style sa tanang input fields
        for (JComponent field : fields) {
            field.setFont(fieldFont);
            field.setBackground(lightYellow);
            field.setForeground(themeRed);
            field.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Pula nga border sa tanang grids
            addFocusEffect(field, themeRed);
            if (field instanceof JComboBox) {
                ((JComboBox<?>) field).setFocusable(true); 
            }
        }

        // Kini nga code nag-style sa mga spinner text fields
        JTextField spField1 = ((JSpinner.DefaultEditor) spBookingAt.getEditor()).getTextField();
        spField1.setFont(fieldFont);
        spField1.setBackground(lightYellow);
        spField1.setForeground(themeRed);
        spField1.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        addFocusEffect(spField1, themeRed);

        JTextField spField2 = ((JSpinner.DefaultEditor) spBookOutAt.getEditor()).getTextField();
        spField2.setFont(fieldFont);
        spField2.setBackground(lightYellow);
        spField2.setForeground(themeRed);
        spField2.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        addFocusEffect(spField2, themeRed);

        // Kini nga code nagdugang og mga listener para sa awtomatiko nga pagkalkula sa total.
        java.awt.event.KeyAdapter calcKeyListener = new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                formHandler.calculateTotal(); // Kalkulahon ang total inig type sa user
            }
        };
        java.awt.event.ActionListener calcActionListener = e -> formHandler.calculateTotal();

        txtPrice.addKeyListener(calcKeyListener);
        cbNights.addActionListener(e -> {
            formHandler.calculateTotal();
            formHandler.updateBookOutDate(); // I-update ang book out date base sa nights
        });
        txtDiscount.addKeyListener(calcKeyListener);
        cbGuestCount.addActionListener(calcActionListener);

        spBookingAt.addChangeListener(e -> formHandler.updateBookOutDate());

        // Kini nga code nagdugang og mga label ug field sa GridBagLayout sa form
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            JLabel label = new JLabel(labels[i]);
            label.setFont(labelFont);
            label.setForeground(themeYellow);
            
            // Idugang ang Icon sa Label
            ImageIcon icon = getScaledIcon(iconPaths[i], 20, 20);
            if (icon != null) {
                label.setIcon(icon);
                label.setIconTextGap(10);
            }
            
            formPanel.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            fields[i].setPreferredSize(new Dimension(200, 30));
            formPanel.add(fields[i], gbc);
        }

        // Kini nga code nag-himo sa mga action button (Book In/Book Out)
        actionsPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        actionsPanel.setBackground(themeRed);
        btnBookIn = createActionButton("Book In", tradeGothicBold, themeYellow, themeRed, "book_in.png");
        btnBookOut = createActionButton("Book Out", tradeGothicBold, themeYellow, themeRed, "book out.png");

        // Idugang ang hover effect sa mga buttons
        JButton[] actionBtns = {btnBookIn, btnBookOut};
        for (JButton btn : actionBtns) {
            btn.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseEntered(java.awt.event.MouseEvent e) {
                    btn.setBackground(Color.WHITE); // Puti inig hover
                    btn.setCursor(new Cursor(Cursor.HAND_CURSOR));
                }
                public void mouseExited(java.awt.event.MouseEvent e) {
                    btn.setBackground(themeYellow); // Balik sa yellow
                }
            });
        }

        actionsPanel.add(btnBookIn);
        actionsPanel.add(btnBookOut);
        actionsPanel.setPreferredSize(new Dimension(500, 45));

        // Kini nga code nag-set up sa panel sa wala nga bahin (Side Panel)
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(themeRed);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Kini nga code nag-initialize sa header panel nga adunay navigation buttons
        headerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        headerPanel.setBackground(themeRed);
        headerPanel.setPreferredSize(new Dimension(500, 45));
        btnRoomDetails = createNavButton("Room Details", tradeGothicBold, themeYellow, themeRed, "roomdetails.png");
        btnGuestProfile = createNavButton("Guest Profile", tradeGothicBold, themeYellow, themeRed, "guest list.png");

        headerPanel.add(btnRoomDetails);
        headerPanel.add(btnGuestProfile);

        // CardLayout para sa pag-switch switch sa Room Details ug Guest Profile
        leftCardLayout = new CardLayout();
        leftContent = new JPanel(leftCardLayout);
        leftContent.setBackground(themeRed);
        
        leftContent.add(detailsPanel, "details");
        leftContent.add(guestProfilePanel, "guestProfile");

        leftPanel.add(headerPanel, BorderLayout.NORTH);
        leftPanel.add(leftContent, BorderLayout.CENTER);

        // Kini nga code nagdugang og scrolling sa form panel aron ma-scroll kung daghan ang fields
        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBackground(themeRed);
        formScroll.getViewport().setBackground(themeRed);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(25);

        detailsPanel.add(formScroll, BorderLayout.CENTER);
        detailsPanel.add(actionsPanel, BorderLayout.SOUTH);

        // Kini nga code nag-set up sa panel sa tuo nga bahin uban ang main room table
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(lightYellow);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Kini nga code naghimo sa filter panel para sa room table uban ang search field
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(lightYellow);
        
        JLabel searchLabel = new JLabel("Search:");
        searchLabel.setFont(tradeGothicBold);
        searchLabel.setForeground(themeRed);
        
        txtSearch = new JTextField(20);
        txtSearch.setFont(fieldFont);
        txtSearch.setBackground(Color.WHITE);
        txtSearch.setForeground(themeRed);
        txtSearch.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
        addFocusEffect(txtSearch, themeRed);
        
        // Kini nga code naga-refresh sa table samtang nag-type ang user sa search field (Real-time search)
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { tableHandler.searchRooms(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { tableHandler.searchRooms(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { tableHandler.searchRooms(); }
        });

        // Filter buttons para sa pag-filter sa mga kwarto
        JButton btnShowAll = createFilterButton("Show All", tradeGothicBold, themeRed, themeYellow, 120, "showall.png");
        btnShowAll.addActionListener(e -> tableHandler.showRoomsByStatus(null));

        JButton btnFreeRooms = createFilterButton("Free Rooms", tradeGothicBold, themeRed, themeYellow, 140, "free room.png");
        btnFreeRooms.addActionListener(e -> tableHandler.showRoomsByStatus("Free"));

        JButton btnOccupied = createFilterButton("Occupied Rooms", tradeGothicBold, themeRed, themeYellow, 180, "occupied.png");
        btnOccupied.addActionListener(e -> tableHandler.showRoomsByStatus("Booked"));

        JButton btnRefresh = createFilterButton("Refresh", tradeGothicBold, themeRed, themeYellow, 120, "refresh.png");

        filterPanel.add(searchLabel);
        filterPanel.add(txtSearch);
        filterPanel.add(btnShowAll);
        filterPanel.add(btnFreeRooms);
        filterPanel.add(btnOccupied);
        filterPanel.add(btnRefresh);

        lblTotalGuests = new JLabel(" | Total Guests In: 0");
        lblTotalGuests.setFont(labelFont);
        lblTotalGuests.setForeground(themeRed);
        filterPanel.add(lblTotalGuests);

        // Kini nga code nag-initialize sa main room table diin makita ang listahan sa mga kwarto
        String[] columns = {"Room No", "Room Name", "Status", "Type", "Guest Name", "Price", "Nights", "Discount", "Total", "Guests In", "Payment Method"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Dili pwede i-edit ang cells direkta sa table
            }
        };
        table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(tradeGothicBold);
        table.getTableHeader().setBackground(themeRed);
        table.getTableHeader().setForeground(themeYellow);
        table.setFont(tradeGothicBold);
        table.setBackground(lightYellow);
        table.setForeground(themeRed);
        table.setSelectionBackground(Color.RED); // Pula nga background inig click sa row
        table.setSelectionForeground(Color.WHITE);
        table.setShowGrid(true);
        table.setGridColor(Color.RED);
        table.setFocusable(false); 
        
        // Kini nga code nag-style sa mga cells sa table para ma-sentro ang text
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
                Component c = super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                if (isSelected) {
                    c.setBackground(Color.RED);
                    c.setForeground(Color.WHITE);
                } else {
                    c.setBackground(lightYellow);
                    c.setForeground(themeRed);
                }
                if (c instanceof JComponent) {
                    ((JComponent) c).setBorder(null); 
                }
                return c;
            }
        };
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Kini nga code nag-sentro sa text sa header sa table
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // I-set ang gilapdon sa mga columns sa table
        table.getColumnModel().getColumn(0).setPreferredWidth(80); // Room No
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Room Name
        table.getColumnModel().getColumn(2).setPreferredWidth(70); // Status
        table.getColumnModel().getColumn(3).setPreferredWidth(80); // Type
        table.getColumnModel().getColumn(4).setPreferredWidth(100); // Guest Name
        table.getColumnModel().getColumn(5).setPreferredWidth(60); // Price
        table.getColumnModel().getColumn(6).setPreferredWidth(60); // Nights
        table.getColumnModel().getColumn(7).setPreferredWidth(70); // Discount
        table.getColumnModel().getColumn(8).setPreferredWidth(80); // Total
        table.getColumnModel().getColumn(9).setPreferredWidth(80); // Guests In
        table.getColumnModel().getColumn(10).setPreferredWidth(120); // Payment Method

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(lightYellow);
        rightPanel.add(filterPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // Kini nga code nag-initialize sa table handler para sa pagdumala sa data sa table
        tableHandler = new RoomTableHandler(this, hotelManager, table, model, lblTotalGuests, txtSearch);

        // Gamit ang JSplitPane para ma-adjust ang gidak-on sa wala ug tuo nga panel
        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        mainSplit.setDividerLocation(500); 
        mainSplit.setDividerSize(5);
        mainSplit.setContinuousLayout(true);
        mainSplit.setBorder(null);
        add(mainSplit, BorderLayout.CENTER);

        // Listeners para sa navigation buttons
        btnRoomDetails.addActionListener(e -> {
            leftCardLayout.show(leftContent, "details");
            updateActionButtons();
        });

        btnGuestProfile.addActionListener(e -> {
            Room selected = formHandler.getCurrentSelected();
            guestProfilePanel.loadGuestData(selected);
            leftCardLayout.show(leftContent, "guestProfile");
        });

        // Action listeners para sa Book In ug Book Out buttons
        btnBookIn.addActionListener(e -> formHandler.bookInRoom());
        btnBookOut.addActionListener(e -> formHandler.bookOutRoom());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            tableHandler.roomUpdateTable(); 
        });

        // Selection listener para mapuno ang form inig click sa table row
        table.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && table.getSelectedRow() != -1) {
                try {
                    int modelRow = table.convertRowIndexToModel(table.getSelectedRow());
                    int roomNo = Integer.parseInt(model.getValueAt(modelRow, 0).toString());
                    Room room = hotelManager.findRoomByNo(roomNo);
                    if (room != null) {
                        formHandler.selectRoomInForm(room);
                    }
                } catch (Exception ex) {
                    formHandler.clearFields();
                }
            }
        });

        // Logic para sa status change
        cbStatus.addActionListener(e -> {
            boolean booked = "Booked".equals(cbStatus.getSelectedItem().toString());
            if (booked) {
                spBookingAt.setValue(new Date());
                spBookOutAt.setValue(new Date());
            }
        });

        // Logic inig usab sa category
        cbCategory.addActionListener(e -> {
            formHandler.updateRoomNoOptions();
            formHandler.updateGuestCountOptions();
        });

        // Logic inig pili sa room number
        cbRoomNo.addActionListener(e -> {
            Integer val = (Integer) cbRoomNo.getSelectedItem();
            if (val != null) {
                Room r = hotelManager.findRoomByNo(val);
                if (r != null) {
                    formHandler.selectRoomInForm(r);
                }
            }
        });

        // Footer panel diin makita ang Signed In user ug Sign Out button
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(themeRed);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        lblUser = new JLabel("");
        lblUser.setFont(labelFont);
        lblUser.setForeground(themeYellow);
        btnSignOut = new JButton("Sign Out");
        btnSignOut.setFont(tradeGothicBold);
        btnSignOut.setBackground(themeYellow);
        btnSignOut.setForeground(themeRed);
        btnSignOut.setFocusPainted(false);
        btnSignOut.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(themeRed),
                BorderFactory.createEmptyBorder(5, 15, 5, 15)
        ));
        
        ImageIcon signOutIcon = getScaledIcon("sign out.png", 20, 20);
        if (signOutIcon != null) {
            btnSignOut.setIcon(signOutIcon);
            btnSignOut.setIconTextGap(10);
        }
        
        btnSignOut.addActionListener(e -> signOut()); // Sign out logic
        footer.add(lblUser, BorderLayout.WEST);
        footer.add(btnSignOut, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);

        // I-initialize ang UI sa pagsugod
        formHandler.updateRoomNoOptions();
        roomUpdateTable();
        updateAuthUI();
        updateActionButtons();
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    // Kini nga method nag-set sa mga credential sa user
    public void setUsers(java.util.Map<String, String> creds) {
        authManager.setUsers(creds);
    }

    // Kini nga method nag-set sa status sa sign-in
    public void setSignedIn(boolean v) {
        authManager.setSignedIn(v);
    }

    // Kini nga method nag-set sa username sa user karon
    public void setCurrentUser(String u) {
        authManager.setCurrentUser(u);
    }

    // Kini nga method nag-return sa map sa tanang user
    public java.util.Map<String, String> getUsersMap() {
        return authManager.getUsers();
    }

    // Kini nga method naga-update sa UI base sa status sa login
    public void updateAuthUI() {
        boolean enabled = authManager.isSignedIn();
        cbRoomNo.setEnabled(enabled);
        txtName.setEnabled(enabled);
        txtPrice.setEnabled(enabled);
        txtGuest.setEnabled(enabled);
        cbNights.setEnabled(enabled);
        txtDiscount.setEnabled(enabled);
        cbGuestCount.setEnabled(enabled);
        cbCategory.setEnabled(enabled);
        cbStatus.setEnabled(enabled);
        cbPaymentMethod.setEnabled(enabled);
        btnBookIn.setEnabled(enabled);
        btnBookOut.setEnabled(enabled);
        lblUser.setText(enabled ? ("Signed in as " + authManager.getCurrentUser()) : "Signed out");
        btnSignOut.setEnabled(true);
        btnRoomDetails.setEnabled(true);
        
    }

    // Kini nga method naga-update sa state sa mga action buttons
    public void updateActionButtons() {
        boolean signedIn = authManager.isSignedIn();
        Room currentSelected = formHandler.getCurrentSelected();
        boolean selected = currentSelected != null;

        if (selected) {
            boolean isBooked = "Booked".equals(currentSelected.getStatus());
            btnBookIn.setEnabled(signedIn && !isBooked);
            btnBookOut.setEnabled(signedIn && isBooked);
           
        } else {
            btnBookIn.setEnabled(false);
            btnBookOut.setEnabled(false);
           
        }
    }

    // Kini nga method naga-refresh sa table data
    public void roomUpdateTable() {
        tableHandler.roomUpdateTable();
    }

    // Kini nga method naglimpyo sa gipili sa table
    public void clearTableSelection() {
        table.clearSelection();
    }

    // Kini nga method nag-switch ngadto sa Room Details view
    public void showRoomDetails() {
        leftCardLayout.show(leftContent, "details");
    }

    // Kini nga method nag-handle sa pag-sign out
    private void signOut() {
        authManager.setSignedIn(false);
        authManager.setCurrentUser(null);
        updateAuthUI();
        formHandler.clearFields();
        setVisible(false);
        
        // Pagkahuman og sign out, ipakita pag-usab ang login dialog
        LoginDialog login = new LoginDialog(this, authManager);
        login.setVisible(true);
        
        if (login.isAuthenticated()) {
            setSignedIn(true);
            setCurrentUser(login.getLoggedInUser());
            updateAuthUI();
            roomUpdateTable(); 
            setVisible(true);
        } else {
            System.exit(0);
        }
    }

    // Method para sa pag-load ug pag-scale sa mga icons
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

    // Method para sa pag-create og navigation buttons
    private JButton createNavButton(String text, Font font, Color bg, Color fg, String iconPath) {
        JButton btn = new JButton(text);
        btn.setFont(font);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(fg, 1));
        
        ImageIcon icon = getScaledIcon(iconPath, 20, 20);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(10);
        }
        
        return btn;
    }

    // Method para sa pag-create og action buttons
    private JButton createActionButton(String text, Font font, Color bg, Color fg, String iconPath) {
        JButton btn = new JButton(text);
        btn.setFont(font);
        btn.setBackground(bg);
        btn.setForeground(fg);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createLineBorder(fg, 1));
        
        ImageIcon icon = getScaledIcon(iconPath, 20, 20);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(10);
        }
        
        return btn;
    }

    // Method para sa pag-create og filter buttons
    private JButton createFilterButton(String text, Font font, Color bg, Color fg, int width, String iconPath) {
        JButton btn = new JButton(text);
        btn.setPreferredSize(new Dimension(width, 35));
        btn.setFont(font);
        btn.setBackground(bg);
        btn.setForeground(fg);
        
        ImageIcon icon = getScaledIcon(iconPath, 20, 20);
        if (icon != null) {
            btn.setIcon(icon);
            btn.setIconTextGap(5);
        }
        
        return btn;
    }

    // Kini nga method nagdugang og visual effect inig click o hover sa mga input fields
    private void addFocusEffect(JComponent c, Color defaultColor) {
        c.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                c.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Pula nga border inig focus
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                c.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
            }
        });
        
        // Siguroon nga walay blue selection color sa ComboBox
        if (c instanceof JComboBox) {
            JComboBox<?> cb = (JComboBox<?>) c;
            cb.setFocusable(true);
            cb.setRequestFocusEnabled(true);
            UIManager.put("ComboBox.selectionBackground", Color.RED);
            UIManager.put("ComboBox.selectionForeground", Color.WHITE);
        }

        c.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!c.isFocusOwner()) {
                    c.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); // Pula nga border inig hover
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
