import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Date;

/**
 * This class HotelReservationSystem is for the main application window (JFrame).
 * It acts as the coordinator, managing the UI components, layout, and delegating 
 * logic to specialized handlers.
 */
public class HotelReservationSystem extends JFrame {

    // UI Components for input and selection
    private JComboBox<Integer> cbRoomNo, cbNights, cbGuestCount;
    private JTextField txtName, txtPrice, txtGuest, txtDiscount, txtTotalPayment;
    private JComboBox<String> cbCategory, cbStatus, cbPaymentMethod, cbFilterType, cbFilterStatus, cbFilterPayment;
    
    // UI Components for data display
    private JTable table;
    private DefaultTableModel model;
    
    // Core logic managers
    private HotelManager hotelManager;
    private AuthManager authManager;
    
    // Specialized UI logic handlers
    private RoomFormHandler formHandler;
    private RoomTableHandler tableHandler;

    // Buttons and Labels
    private JButton btnSave, btnBookOut, btnBookIn;
    private JLabel lblUser, lblTotalGuests;
    private JButton btnSignOut;

    // Date/Time spinners
    private JSpinner spBookingAt, spBookOutAt;

    // Navigation and Layout components
    private JButton btnRoomDetails, btnGuestList;
    private JPanel detailsPanel, guestListPanel;
    private JPanel actionsPanel;
    private JPanel leftContent; // This will have CardLayout
    private CardLayout leftCardLayout;


    private JSplitPane mainSplit;

    // Guest List table components
    private JTable guestTable;
    private DefaultTableModel guestModel;

    public HotelReservationSystem() {
        // This code initializes the main JFrame window properties
        setTitle("Hotel Reservation List");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initializing managers
        hotelManager = new HotelManager();
        authManager = new AuthManager();

        // This code defines the fonts and colors for a consistent UI theme
        Font tradeGothicBold = new Font("Trade Gothic", Font.BOLD, 14);
        Font tradeGothicPlain = new Font("Trade Gothic", Font.PLAIN, 14);
        Font labelFont = new Font("Trade Gothic", Font.BOLD, 13);
        Font fieldFont = new Font("Trade Gothic", Font.PLAIN, 14);
        Font headerFont = new Font("Trade Gothic", Font.BOLD, 16);

        Color themeRed = new Color(150, 0, 0); // Maroon/Deep Red
        Color themeYellow = new Color(255, 255, 100); // Yellow
        Color lightYellow = new Color(255, 255, 240); // Pale Yellow/Cream

        // This code sets up the left side panel
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(themeRed);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        // This code creates navigation buttons
        btnRoomDetails = new JButton("Room Details");
        btnRoomDetails.setPreferredSize(new Dimension(180, 35));
        btnRoomDetails.setFont(headerFont);
        btnRoomDetails.setBackground(themeYellow);
        btnRoomDetails.setForeground(themeRed);
        
        btnGuestList = new JButton("Guest List");
        btnGuestList.setPreferredSize(new Dimension(180, 35));
        btnGuestList.setFont(headerFont);
        btnGuestList.setBackground(themeYellow);
        btnGuestList.setForeground(themeRed);

        // This code creates the header panel with a grid layout for navigation
        JPanel headerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        headerPanel.setBackground(themeRed);
        headerPanel.add(btnRoomDetails);
        headerPanel.add(btnGuestList);

        // This code sets up the Room Details Form panel
        detailsPanel = new JPanel(new BorderLayout(0, 5));
        detailsPanel.setBackground(themeRed);
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(themeRed);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // This code initializes form input fields
        cbRoomNo = new JComboBox<>();
        txtName = new JTextField();
        txtName.setEditable(false);
        txtPrice = new JTextField();
        txtPrice.setEditable(false);
        txtGuest = new JTextField();
        cbNights = new JComboBox<>();
        for (int i = 1; i <= 7; i++) cbNights.addItem(i);
        txtDiscount = new JTextField("0");
        txtTotalPayment = new JTextField();
        txtTotalPayment.setEditable(false);
        cbGuestCount = new JComboBox<>();
        cbCategory = new JComboBox<>(new String[]{"VIP", "Double Bed", "Family"});
        cbStatus = new JComboBox<>(new String[]{"Free", "Booked"});
        cbPaymentMethod = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash", "Online Transfer"});

        // This code sets up the date spinners for booking times
        spBookingAt = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE));
        spBookingAt.setEditor(new JSpinner.DateEditor(spBookingAt, "yyyy-MM-dd HH:mm"));
        ((JSpinner.DefaultEditor)spBookingAt.getEditor()).getTextField().setEditable(false);
        spBookingAt.setEnabled(false);
        
        spBookOutAt = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE));
        spBookOutAt.setEditor(new JSpinner.DateEditor(spBookOutAt, "yyyy-MM-dd HH:mm"));
        ((JSpinner.DefaultEditor)spBookOutAt.getEditor()).getTextField().setEditable(false);
        spBookOutAt.setEnabled(false);

        // This code initializes the form handler to manage form logic
        formHandler = new RoomFormHandler(this, hotelManager,
            cbRoomNo, cbNights, cbGuestCount, txtName, txtPrice, txtGuest, txtDiscount,
            txtTotalPayment, cbCategory, cbStatus, cbPaymentMethod, spBookingAt, spBookOutAt);
            
        formHandler.updateGuestCountOptions();

        // This code labels and adds fields to the form panel
        String[] labels = {"Room No", "Room", "Category", "Guest Name", "Status", "Guests In", "Price", "Payment Method", "Discount", "Nights", "Total Payment", "Booking Date/Time", "Book Out"};
        JComponent[] fields = {cbRoomNo, txtName, cbCategory, txtGuest, cbStatus, cbGuestCount, txtPrice, cbPaymentMethod, txtDiscount, cbNights, txtTotalPayment, spBookingAt, spBookOutAt};
        
        for(JComponent field : fields) {
            field.setFont(fieldFont);
            field.setBackground(lightYellow);
            field.setForeground(themeRed);
            if (field instanceof JComboBox) {
                ((JComboBox<?>)field).setFocusable(false);
            }
        }
        
        // This code styles the spinner fields
        JTextField spField1 = ((JSpinner.DefaultEditor)spBookingAt.getEditor()).getTextField();
        spField1.setFont(fieldFont);
        spField1.setBackground(lightYellow);
        spField1.setForeground(themeRed);

        JTextField spField2 = ((JSpinner.DefaultEditor)spBookOutAt.getEditor()).getTextField();
        spField2.setFont(fieldFont);
        spField2.setBackground(lightYellow);
        spField2.setForeground(themeRed);

        // This code adds listeners for automatic total calculation
        java.awt.event.KeyAdapter calcKeyListener = new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { formHandler.calculateTotal(); }
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

        // This code adds labels and fields to the GridBagLayout
        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            JLabel label = new JLabel(labels[i]);
            label.setFont(labelFont);
            label.setForeground(themeYellow);
            formPanel.add(label, gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            fields[i].setPreferredSize(new Dimension(200, 30));
            formPanel.add(fields[i], gbc);
        }

        // This code creates the action buttons (Save/Book In/Book Out)
        actionsPanel = new JPanel(new GridLayout(1, 3, 10, 0));
        actionsPanel.setBackground(themeRed);
        btnSave = new JButton("Save");
        btnBookIn = new JButton("Book In");
        btnBookOut = new JButton("Book Out");

        JButton[] actionButtons = {btnSave, btnBookIn, btnBookOut};
        for (JButton btn : actionButtons) {
            btn.setFont(tradeGothicBold);
            btn.setBackground(themeYellow);
            btn.setForeground(themeRed);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(themeRed, 1));
        }

        actionsPanel.add(btnSave);
        actionsPanel.add(btnBookIn);
        actionsPanel.add(btnBookOut);
        actionsPanel.setPreferredSize(new Dimension(500, 45));

        // This code adds scrolling to the form panel
        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBackground(themeRed);
        formScroll.getViewport().setBackground(themeRed);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(25);

        detailsPanel.add(formScroll, BorderLayout.CENTER);
        detailsPanel.add(actionsPanel, BorderLayout.SOUTH);

        // This code sets up the Guest List table panel
        guestListPanel = new JPanel(new BorderLayout());
        guestListPanel.setBackground(themeRed);
        guestModel = new DefaultTableModel(new String[]{"Guest Name", "Room Name", "Booked At", "Booked Out"}, 0);
        guestTable = new JTable(guestModel);
        guestTable.setFont(tradeGothicPlain);
        guestTable.setBackground(lightYellow);
        guestTable.setForeground(themeRed);
        guestTable.setSelectionBackground(themeRed);
        guestTable.setSelectionForeground(themeYellow);
        guestTable.getTableHeader().setFont(tradeGothicBold);
        guestTable.getTableHeader().setBackground(themeRed);
        guestTable.getTableHeader().setForeground(themeYellow);
        
        // This code sets column widths for the Guest List table
        guestTable.getColumnModel().getColumn(0).setPreferredWidth(120); // Guest Name
        guestTable.getColumnModel().getColumn(1).setPreferredWidth(100); // Room Name
        guestTable.getColumnModel().getColumn(2).setPreferredWidth(130); // Booked At
        guestTable.getColumnModel().getColumn(3).setPreferredWidth(130); // Booked Out

        JScrollPane guestScroll = new JScrollPane(guestTable);
        guestScroll.getViewport().setBackground(lightYellow);
        guestListPanel.add(guestScroll, BorderLayout.CENTER);

        // This code sets up CardLayout to switch between Form and Guest List views
        leftCardLayout = new CardLayout();
        leftContent = new JPanel(leftCardLayout);
        leftContent.setBackground(themeRed);
        leftContent.add(detailsPanel, "details");
        leftContent.add(guestListPanel, "guests");

        leftPanel.add(headerPanel, BorderLayout.NORTH);
        leftPanel.add(leftContent, BorderLayout.CENTER);

        // This code sets up the right side panel with the main room table
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(lightYellow);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // This code creates the filter panel for the room table
        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        filterPanel.setBackground(lightYellow);
        cbFilterType = new JComboBox<>(new String[]{"All", "VIP", "Double Bed", "Family"});
        cbFilterType.setFont(fieldFont);
        cbFilterType.setBackground(Color.WHITE);
        cbFilterType.setForeground(themeRed);

        cbFilterStatus = new JComboBox<>(new String[]{"All", "Free", "Booked"});
        cbFilterStatus.setFont(fieldFont);
        cbFilterStatus.setBackground(Color.WHITE);
        cbFilterStatus.setForeground(themeRed);

        cbFilterPayment = new JComboBox<>(new String[]{"All", "Credit Card", "Debit Card", "Cash", "Online Transfer"});
        cbFilterPayment.setFont(fieldFont);
        cbFilterPayment.setBackground(Color.WHITE);
        cbFilterPayment.setForeground(themeRed);

        JButton btnRefresh = new JButton("Refresh");
        btnRefresh.setPreferredSize(new Dimension(100, 35));
        btnRefresh.setFont(tradeGothicBold);
        btnRefresh.setBackground(themeRed);
        btnRefresh.setForeground(themeYellow);

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setFont(tradeGothicBold);
        typeLabel.setForeground(themeRed);
        JLabel statusLabel = new JLabel("Status:");
        statusLabel.setFont(tradeGothicBold);
        statusLabel.setForeground(themeRed);
        JLabel paymentLabel = new JLabel("Payment:");
        paymentLabel.setFont(tradeGothicBold);
        paymentLabel.setForeground(themeRed);

        filterPanel.add(typeLabel);
        filterPanel.add(cbFilterType);
        filterPanel.add(statusLabel);
        filterPanel.add(cbFilterStatus);
        filterPanel.add(paymentLabel);
        filterPanel.add(cbFilterPayment);
        filterPanel.add(btnRefresh);

        lblTotalGuests = new JLabel(" | Total Guests In: 0");
        lblTotalGuests.setFont(labelFont);
        lblTotalGuests.setForeground(themeRed);
        filterPanel.add(lblTotalGuests);

        // This code initializes the main room table
        String[] columns = {"Room No", "Room Name", "Status", "Type", "Guest Name", "Price", "Nights", "Discount", "Total", "Guests In", "Payment Method"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(30);
        table.getTableHeader().setFont(tradeGothicBold);
        table.getTableHeader().setBackground(themeRed);
        table.getTableHeader().setForeground(themeYellow);
        table.setFont(tradeGothicPlain);
        table.setBackground(lightYellow);
        table.setForeground(themeRed);
        table.setSelectionBackground(themeRed);
        table.setSelectionForeground(themeYellow);
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        // This code adjusts column widths for the main room table
        table.getColumnModel().getColumn(0).setPreferredWidth(60); // Room No
        table.getColumnModel().getColumn(1).setPreferredWidth(100); // Room Name
        table.getColumnModel().getColumn(2).setPreferredWidth(80); // Status
        table.getColumnModel().getColumn(3).setPreferredWidth(100); // Type
        table.getColumnModel().getColumn(4).setPreferredWidth(120); // Guest Name
        table.getColumnModel().getColumn(5).setPreferredWidth(60); // Price
        table.getColumnModel().getColumn(6).setPreferredWidth(60); // Nights
        table.getColumnModel().getColumn(7).setPreferredWidth(70); // Discount
        table.getColumnModel().getColumn(8).setPreferredWidth(80); // Total
        table.getColumnModel().getColumn(9).setPreferredWidth(80); // Guests In
        table.getColumnModel().getColumn(10).setPreferredWidth(130); // Payment Method

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.getViewport().setBackground(lightYellow);
        rightPanel.add(filterPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        // This code initializes the table handler to manage table data
        tableHandler = new RoomTableHandler(this, hotelManager, table, model, guestTable, guestModel, lblTotalGuests, cbFilterType, cbFilterStatus, cbFilterPayment);

        // This code combines panels using a split pane
        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        mainSplit.setDividerLocation(450);
        mainSplit.setDividerSize(5);
        mainSplit.setContinuousLayout(true);
        add(mainSplit, BorderLayout.CENTER);

        // This code adds navigation action listeners
        btnRoomDetails.addActionListener(e -> {
            leftCardLayout.show(leftContent, "details");
        });
        
        btnGuestList.addActionListener(e -> {
            tableHandler.updateGuestListTable();
            leftCardLayout.show(leftContent, "guests");
        });

        // This code adds button action listeners
        btnSave.addActionListener(e -> formHandler.saveRoom());
        btnBookIn.addActionListener(e -> formHandler.bookInRoom());
        btnBookOut.addActionListener(e -> formHandler.bookOutRoom());
        btnRefresh.addActionListener(e -> tableHandler.filterRooms());

        // This code adds table selection listener to fill the form
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

        // This code handles status change and sets current time for booking
        cbStatus.addActionListener(e -> {
            boolean booked = "Booked".equals(cbStatus.getSelectedItem().toString());
            if (booked) {
                spBookingAt.setValue(new Date());
                spBookOutAt.setValue(new Date());
            }
        });

        // This code updates options when category changes
        cbCategory.addActionListener(e -> {
            formHandler.updateRoomNoOptions();
            formHandler.updateGuestCountOptions();
        });
        
        // This code selects a room when room number is chosen
        cbRoomNo.addActionListener(e -> {
            Integer val = (Integer) cbRoomNo.getSelectedItem();
            if (val != null) {
                Room r = hotelManager.findRoomByNo(val);
                if (r != null) {
                    formHandler.selectRoomInForm(r);
                }
            }
        });

        // This code sets up the footer panel with user info and sign out
        JPanel footer = new JPanel(new BorderLayout());
        footer.setBackground(themeRed);
        footer.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
        lblUser = new JLabel("Signed out");
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
        btnSignOut.addActionListener(e -> signOut());
        footer.add(lblUser, BorderLayout.WEST);
        footer.add(btnSignOut, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);

        // Initializing UI state
        formHandler.updateRoomNoOptions();
        roomUpdateTable();
        updateAuthUI();
        updateActionButtons();
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    // This method sets the user credentials
    public void setUsers(java.util.Map<String, String> creds) {
        authManager.setUsers(creds);
    }

    // This method sets the sign-in status
    public void setSignedIn(boolean v) {
        authManager.setSignedIn(v);
    }

    // This method sets the current user's email
    public void setCurrentUser(String u) {
        authManager.setCurrentUser(u);
    }

    // This method returns the map of all users
    public java.util.Map<String, String> getUsersMap() {
        return authManager.getUsers();
    }

    // This method updates the UI based on whether a user is signed in
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
        btnGuestList.setEnabled(true);
    }

    // This method updates the text and state of action buttons based on selection
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

    // This method refreshes the main room table
    public void roomUpdateTable() {
        tableHandler.roomUpdateTable();
    }

    // This method clears the selection in the main table
    public void clearTableSelection() {
        table.clearSelection();
    }

    // This method handles the sign out process (bypassed in this version)
    private void signOut() {
        authManager.setSignedIn(true); 
        authManager.setCurrentUser("geann@gmail.com");
        updateAuthUI();
        formHandler.clearFields();
    }

    // This code main method is the entry point that launches the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HotelReservationSystem system = new HotelReservationSystem();
            // Automatically signs in with a default user for testing
            system.setSignedIn(true);
            system.setCurrentUser("geann@gmail.com");
            system.updateAuthUI();
            system.setVisible(true);
        });
    }

}
