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
    private JButton btnSave, btnBookOut, btnBookIn;
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

        // Kini nga code nag-set sa icon sa window.
        ImageIcon windowIcon = getScaledIcon("logo.png", 64, 64);
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

        Color themeRed = new Color(150, 0, 0); // Maroon/Deep Red
        Color themeYellow = new Color(255, 255, 100); // Yellow
        Color lightYellow = new Color(255, 255, 240); // Pale Yellow/Cream

        // Kini nga code nag-set up sa Room Details Form panel
        detailsPanel = new JPanel(new BorderLayout(0, 5));
        detailsPanel.setBackground(themeRed);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(themeRed);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Kini nga code nag-initialize sa mga input field sa form
        cbRoomNo = new JComboBox<>();
        txtName = new JTextField();
        txtName.setEditable(false);
        txtPrice = new JTextField();
        txtPrice.setEditable(false);
        txtGuest = new JTextField();
        cbNights = new JComboBox<>();
        for (int i = 1; i <= 7; i++) {
            cbNights.addItem(i);
        }
        txtDiscount = new JTextField("0");
        txtTotalPayment = new JTextField();
        txtTotalPayment.setEditable(false);
        cbGuestCount = new JComboBox<>();
        cbCategory = new JComboBox<>(new String[]{"VIP BED", "FAMILY BED", "COUPLE BED"});
        cbStatus = new JComboBox<>(new String[]{"Free", "Booked"});
        cbPaymentMethod = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash", "Online Transfer"});

        // Kini nga code nag-set up sa mga date spinner para sa mga oras sa booking.
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

        formHandler.updateGuestCountOptions();

        // Kini nga code nag-initialize sa guest profile panel.
        guestProfilePanel = new GuestProfilePanel(this, hotelManager, formHandler);

        // Kini nga code nagdugang og label ug nagdugang og mga field sa form panel.
        String[] labels = {"Room No", "Room", "Category", "Guest Name", "Status", "Guests In", "Price", "Payment Method", "Discount", "Nights", "Total Payment", "Booking Date/Time", "Book Out"};
        String[] iconPaths = {"room no.png", "room.png", "category.png", "guest list.png", "status.png", "occupied.png", "total payment.png", "payment method.png", "discount.png", "book_in.png", "total payment.png", "book_in.png", "book out.png"};
        JComponent[] fields = {cbRoomNo, txtName, cbCategory, txtGuest, cbStatus, cbGuestCount, txtPrice, cbPaymentMethod, txtDiscount, cbNights, txtTotalPayment, spBookingAt, spBookOutAt};

        for (JComponent field : fields) {
            field.setFont(fieldFont);
            field.setBackground(lightYellow);
            field.setForeground(themeRed);
            if (field instanceof JComboBox) {
                ((JComboBox<?>) field).setFocusable(false);
            }
        }

        // Kini nga code nag-style sa mga spinner field.
        JTextField spField1 = ((JSpinner.DefaultEditor) spBookingAt.getEditor()).getTextField();
        spField1.setFont(fieldFont);
        spField1.setBackground(lightYellow);
        spField1.setForeground(themeRed);

        JTextField spField2 = ((JSpinner.DefaultEditor) spBookOutAt.getEditor()).getTextField();
        spField2.setFont(fieldFont);
        spField2.setBackground(lightYellow);
        spField2.setForeground(themeRed);

        // Kini nga code nagdugang og mga listener para sa awtomatiko nga pagkalkula sa total.
        java.awt.event.KeyAdapter calcKeyListener = new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) {
                formHandler.calculateTotal();
            }
        };
        java.awt.event.ActionListener calcActionListener = e -> formHandler.calculateTotal();

        txtPrice.addKeyListener(calcKeyListener);
        cbNights.addActionListener(e -> {
            formHandler.calculateTotal();
            formHandler.updateBookOutDate();
        });
        txtDiscount.addKeyListener(calcKeyListener);
        cbGuestCount.addActionListener(calcActionListener);

        spBookingAt.addChangeListener(e -> formHandler.updateBookOutDate());

        // Kini nga code nagdugang og mga label ug field sa GridBagLayout
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            JLabel label = new JLabel(labels[i]);
            label.setFont(labelFont);
            label.setForeground(themeYellow);
            
            // Add Icon to Label
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

        // Kini nga code naghimo sa mga action button (Save/Book In/Book Out)
        actionsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        actionsPanel.setBackground(themeRed);
        btnSave = createActionButton("Save", tradeGothicBold, themeYellow, themeRed, "update.png");
        btnBookIn = createActionButton("Book In", tradeGothicBold, themeYellow, themeRed, "book_in.png");
        btnBookOut = createActionButton("Book Out", tradeGothicBold, themeYellow, themeRed, "book out.png");

        actionsPanel.add(btnSave);
        actionsPanel.add(btnBookIn);
        actionsPanel.add(btnBookOut);
        actionsPanel.setPreferredSize(new Dimension(500, 45));

        // Kini nga code nag-set up sa panel sa wala nga bahin
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(themeRed);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Kini nga code nag-initialize sa header panel nga adunay navigation button
        headerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        headerPanel.setBackground(themeRed);
        headerPanel.setPreferredSize(new Dimension(500, 45));
        btnRoomDetails = createNavButton("Room Details", tradeGothicBold, themeYellow, themeRed, "roomdetails.png");
        btnGuestProfile = createNavButton("Guest Profile", tradeGothicBold, themeYellow, themeRed, "guest list.png");

        headerPanel.add(btnRoomDetails);
        headerPanel.add(btnGuestProfile);

        leftCardLayout = new CardLayout();
        leftContent = new JPanel(leftCardLayout);
        leftContent.setBackground(themeRed);
        
        leftContent.add(detailsPanel, "details");
        leftContent.add(guestProfilePanel, "guestProfile");

        leftPanel.add(headerPanel, BorderLayout.NORTH);
        leftPanel.add(leftContent, BorderLayout.CENTER);

        // Kini nga code nagdugang og scrolling sa form panel
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
        
        // Kini nga code naga-refresh sa table samtang nag-type ang user sa search field
        txtSearch.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            @Override
            public void insertUpdate(javax.swing.event.DocumentEvent e) { tableHandler.searchRooms(); }
            @Override
            public void removeUpdate(javax.swing.event.DocumentEvent e) { tableHandler.searchRooms(); }
            @Override
            public void changedUpdate(javax.swing.event.DocumentEvent e) { tableHandler.searchRooms(); }
        });

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

        // Kini nga code nag-initialize sa main room table
        String[] columns = {"Room No", "Room Name", "Status", "Type", "Guest Name", "Price", "Nights", "Discount", "Total", "Guests In", "Payment Method"};
        model = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
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
        table.setSelectionBackground(themeRed);
        table.setSelectionForeground(themeYellow);
        table.setShowGrid(true);
        table.setGridColor(themeRed);

        // Kini nga code nag-sentro sa teksto sa mga cell sa table
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Kini nga code nag-sentro sa teksto sa header sa table
        DefaultTableCellRenderer headerRenderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
        headerRenderer.setHorizontalAlignment(JLabel.CENTER);

        // Kini nga code nag-adjust sa mga gilapdon sa kolum para sa main room table.
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

        // Kini nga code nag-combine sa mga panel gamit ang split pane
        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        mainSplit.setDividerLocation(500); // Increased from 450 for better full-screen balance
        mainSplit.setDividerSize(5);
        mainSplit.setContinuousLayout(true);
        mainSplit.setBorder(null);
        add(mainSplit, BorderLayout.CENTER);

        // Kini nga code nagdugang og mga navigation action listener
        btnRoomDetails.addActionListener(e -> {
            leftCardLayout.show(leftContent, "details");
            updateActionButtons();
        });

        btnGuestProfile.addActionListener(e -> {
            Room selected = formHandler.getCurrentSelected();
            guestProfilePanel.loadGuestData(selected);
            leftCardLayout.show(leftContent, "guestProfile");
        });

        // Kini nga code nagdugang og mga action listener sa mga button.
        btnSave.addActionListener(e -> formHandler.saveRoom());
        btnBookIn.addActionListener(e -> formHandler.bookInRoom());
        btnBookOut.addActionListener(e -> formHandler.bookOutRoom());
        btnRefresh.addActionListener(e -> {
            txtSearch.setText("");
            tableHandler.roomUpdateTable(); // This resets the status filter and shows all
        });

        // Kini nga code nagdugang og table selection listener para mapuno ang form.
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

        // Kini nga code nag-handle sa pagbag-o sa status ug nag-set sa karon nga oras para sa booking
        cbStatus.addActionListener(e -> {
            boolean booked = "Booked".equals(cbStatus.getSelectedItem().toString());
            if (booked) {
                spBookingAt.setValue(new Date());
                spBookOutAt.setValue(new Date());
            }
        });

        // Kini nga code naga-update sa mga option kung naay mausab sa kategorya
        cbCategory.addActionListener(e -> {
            formHandler.updateRoomNoOptions();
            formHandler.updateGuestCountOptions();
        });

        // Kini nga code nagpili og kwarto kung mapili ang numero sa kwarto
        cbRoomNo.addActionListener(e -> {
            Integer val = (Integer) cbRoomNo.getSelectedItem();
            if (val != null) {
                Room r = hotelManager.findRoomByNo(val);
                if (r != null) {
                    formHandler.selectRoomInForm(r);
                }
            }
        });

        // Kini nga code nag-set up sa footer panel uban ang info sa user ug sign out
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
        
        btnSignOut.addActionListener(e -> signOut());
        footer.add(lblUser, BorderLayout.WEST);
        footer.add(btnSignOut, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);

        // Kini nga code nag-initialize sa UI 
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

    // Kini nga method nag-set sa email sa user karon
    public void setCurrentUser(String u) {
        authManager.setCurrentUser(u);
    }

    // Kini nga method nag-return sa map sa tanang user
    public java.util.Map<String, String> getUsersMap() {
        return authManager.getUsers();
    }

    // Kini nga method naga-update sa UI base sa kung ang user naka-sign in ba
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
        btnSave.setEnabled(enabled);
        btnBookIn.setEnabled(enabled);
        btnBookOut.setEnabled(enabled);
        lblUser.setText(enabled ? ("Signed in as " + authManager.getCurrentUser()) : "Signed out");
        btnSignOut.setEnabled(true);
        btnRoomDetails.setEnabled(true);
        
    }

    // Kini nga method naga-update sa text ug state sa mga action button base sa pagpili
    public void updateActionButtons() {
        boolean signedIn = authManager.isSignedIn();
        Room currentSelected = formHandler.getCurrentSelected();
        boolean selected = currentSelected != null;
        btnSave.setText(selected ? "Update" : "Save");

        btnSave.setEnabled(signedIn);

        if (selected) {
            boolean isBooked = "Booked".equals(currentSelected.getStatus());
            btnBookIn.setEnabled(signedIn && !isBooked);
            btnBookOut.setEnabled(signedIn && isBooked);
           
        } else {
            btnBookIn.setEnabled(false);
            btnBookOut.setEnabled(false);
           
        }
    }

    // Kini nga method naga-refresh sa main room table
    public void roomUpdateTable() {
        tableHandler.roomUpdateTable();
    }

    // Kini nga method naglimpyo sa gipili sa main table
    public void clearTableSelection() {
        table.clearSelection();
    }

    // Kini nga method nag-switch sa UI ngadto sa Room Details view
    public void showRoomDetails() {
        leftCardLayout.show(leftContent, "details");
    }

    // Kini nga method nag-handle sa proseso sa pag-sign out
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
            roomUpdateTable(); // Refresh the table after re-login
            setVisible(true);
        } else {
            System.exit(0);
        }
    }

    // Helper methods for UI Construction to clean up constructor
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

}
