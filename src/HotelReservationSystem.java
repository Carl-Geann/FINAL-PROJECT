
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
    private JPanel detailsPanel, guestListPanel, headerPanel;
    private JPanel actionsPanel;
    private JPanel leftContent; // This will have CardLayout
    private CardLayout leftCardLayout;

    private JSplitPane mainSplit;

    // Guest List table components
    private JTable guestTable;
    private DefaultTableModel guestModel;

    public HotelReservationSystem() {
        // Kini nga code nag-initialize sa mga properties sa main JFrame window.
        setTitle("Hotel Reservation List");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Initializing managers
        hotelManager = new HotelManager();
        authManager = new AuthManager();

        // Kini nga code nag-define sa mga font ug color para sa usa ka consistent nga UI theme.
        Font tradeGothicBold = new Font("Arial", Font.BOLD, 12);
        Font tradeGothicPlain = new Font("Arial", Font.BOLD, 12);
        Font labelFont = new Font("Arial", Font.BOLD, 13);
        Font fieldFont = new Font("Arial", Font.BOLD, 13);
        Font headerFont = new Font("Arial", Font.BOLD, 15);

        Color themeRed = new Color(150, 0, 0); // Maroon/Deep Red
        Color themeYellow = new Color(255, 255, 100); // Yellow
        Color lightYellow = new Color(255, 255, 240); // Pale Yellow/Cream

        // Kini nga code nag-set up sa Room Details Form panel.
        detailsPanel = new JPanel(new BorderLayout(0, 5));
        detailsPanel.setBackground(themeRed);
        
        // Kini nga code nag-set up sa Guest List table panel.
        guestListPanel = new JPanel(new BorderLayout());
        guestListPanel.setBackground(themeRed);

        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(themeRed);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Kini nga code nag-initialize sa mga input field sa form.
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
        cbCategory = new JComboBox<>(new String[]{"VIP", "Double Bed", "Family"});
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

        // Kini nga code nag-initialize sa form handler para sa pagdumala sa logic sa form.
        formHandler = new RoomFormHandler(this, hotelManager,
                cbRoomNo, cbNights, cbGuestCount, txtName, txtPrice, txtGuest, txtDiscount,
                txtTotalPayment, cbCategory, cbStatus, cbPaymentMethod, spBookingAt, spBookOutAt);

        formHandler.updateGuestCountOptions();

        // Kini nga code nagbutang og label ug nagdugang og mga field sa form panel.
        String[] labels = {"Room No", "Room", "Category", "Guest Name", "Status", "Guests In", "Price", "Payment Method", "Discount", "Nights", "Total Payment", "Booking Date/Time", "Book Out"};
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

        // Kini nga code nagdugang og mga label ug field sa GridBagLayout.
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

        // Kini nga code naghimo sa mga action button (Save/Book In/Book Out).
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

        // Kini nga code nag-set up sa panel sa wala nga bahin.
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBackground(themeRed);
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        // Kini nga code nag-initialize sa header panel nga adunay mga navigation button.
        headerPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        headerPanel.setBackground(themeRed);
        headerPanel.setPreferredSize(new Dimension(500, 45));
        btnRoomDetails = new JButton("Room Details");
        btnGuestList = new JButton("Guest List");

        JButton[] navButtons = {btnRoomDetails, btnGuestList};
        for (JButton btn : navButtons) {
            btn.setFont(tradeGothicBold);
            btn.setBackground(themeYellow);
            btn.setForeground(themeRed);
            btn.setFocusPainted(false);
            btn.setBorder(BorderFactory.createLineBorder(themeRed, 1));
        }
        headerPanel.add(btnRoomDetails);
        headerPanel.add(btnGuestList);

        leftCardLayout = new CardLayout();
        leftContent = new JPanel(leftCardLayout);
        leftContent.setBackground(themeRed);
        
        leftContent.add(detailsPanel, "details");
        leftContent.add(guestListPanel, "guests");

        leftPanel.add(headerPanel, BorderLayout.NORTH);
        leftPanel.add(leftContent, BorderLayout.CENTER);

        // Kini nga code nagdugang og scrolling sa form panel.
        JScrollPane formScroll = new JScrollPane(formPanel);
        formScroll.setBackground(themeRed);
        formScroll.getViewport().setBackground(themeRed);
        formScroll.setBorder(null);
        formScroll.getVerticalScrollBar().setUnitIncrement(25);

        detailsPanel.add(formScroll, BorderLayout.CENTER);
        detailsPanel.add(actionsPanel, BorderLayout.SOUTH);

        // Kini nga code nag-set up sa Guest List table panel.
        guestModel = new DefaultTableModel(new String[]{"Room No", "Guest Name", "Room Name", "Booked At", "Booked Out"}, 0);
        guestTable = new JTable(guestModel);
        guestTable.setFont(tradeGothicPlain);
        guestTable.setBackground(lightYellow);
        guestTable.setForeground(themeRed);
        guestTable.setSelectionBackground(themeRed);
        guestTable.setSelectionForeground(themeYellow);
        guestTable.getTableHeader().setFont(tradeGothicBold);
        guestTable.getTableHeader().setBackground(themeRed);
        guestTable.getTableHeader().setForeground(themeYellow);
        
        // Kini nga code nag-set sa mga gilapdon sa kolum para sa Guest List table.
        guestTable.getColumnModel().getColumn(0).setMinWidth(0);
        guestTable.getColumnModel().getColumn(0).setMaxWidth(0);
        guestTable.getColumnModel().getColumn(0).setPreferredWidth(0);
        guestTable.getColumnModel().getColumn(1).setPreferredWidth(130); // Guest Name
        guestTable.getColumnModel().getColumn(2).setPreferredWidth(100); // Room Name
        guestTable.getColumnModel().getColumn(3).setPreferredWidth(130); // Booked At
        guestTable.getColumnModel().getColumn(4).setPreferredWidth(130); // Booked Out

        JScrollPane guestScroll = new JScrollPane(guestTable);
        guestScroll.getViewport().setBackground(lightYellow);
        guestListPanel.add(guestScroll, BorderLayout.CENTER);

        // Kini nga code nag-set up sa panel sa tuo nga bahin uban ang main room table.
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBackground(lightYellow);
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Kini nga code naghimo sa filter panel para sa room table.
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

        // Kini nga code nag-initialize sa main room table.
        String[] columns = {"Room No", "Room Name", "Status", "Type", "Guest Name", "Price", "Nights", "Discount", "Total", "Guests In", "Payment Method"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(25);
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

        // Kini nga code nag-sentro sa teksto sa mga cell sa table.
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // Kini nga code nag-sentro sa teksto sa header sa table.
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

        // Kini nga code nag-initialize sa table handler para sa pagdumala sa data sa table.
        tableHandler = new RoomTableHandler(this, hotelManager, table, model, guestTable, guestModel, lblTotalGuests, cbFilterType, cbFilterStatus, cbFilterPayment);

        // Kini nga code nag-combine sa mga panel gamit ang split pane.
        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        mainSplit.setDividerLocation(450);
        mainSplit.setDividerSize(5);
        mainSplit.setContinuousLayout(true);
        mainSplit.setBorder(null);
        add(mainSplit, BorderLayout.CENTER);

        // Kini nga code nagdugang og mga navigation action listener.
        btnRoomDetails.addActionListener(e -> {
            leftCardLayout.show(leftContent, "details");
            updateActionButtons();
        });

        btnGuestList.addActionListener(e -> {
            tableHandler.updateGuestListTable();
            leftCardLayout.show(leftContent, "guests");
            updateActionButtons();
        });

        // Kini nga code nagdugang og selection listener sa guest table para sa sync.
        guestTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && guestTable.getSelectedRow() != -1) {
                try {
                    int modelRow = guestTable.convertRowIndexToModel(guestTable.getSelectedRow());
                    Object roomNoObj = guestModel.getValueAt(modelRow, 0);
                    if (roomNoObj != null) {
                        int roomNo = Integer.parseInt(roomNoObj.toString());
                        Room room = hotelManager.findRoomByNo(roomNo);
                        if (room != null) {
                            formHandler.selectRoomInForm(room);
                        }
                    }
                } catch (Exception ex) {
                    // Ignored
                }
            }
        });

        // Kini nga code nagdugang og mga action listener sa mga button.
        btnSave.addActionListener(e -> formHandler.saveRoom());
        btnBookIn.addActionListener(e -> formHandler.bookInRoom());
        btnBookOut.addActionListener(e -> formHandler.bookOutRoom());
        btnRefresh.addActionListener(e -> tableHandler.filterRooms());

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

        // Kini nga code nag-handle sa pagbag-o sa status ug nag-set sa karon nga oras para sa booking.
        cbStatus.addActionListener(e -> {
            boolean booked = "Booked".equals(cbStatus.getSelectedItem().toString());
            if (booked) {
                spBookingAt.setValue(new Date());
                spBookOutAt.setValue(new Date());
            }
        });

        // Kini nga code naga-update sa mga option kung naay mausab sa kategorya.
        cbCategory.addActionListener(e -> {
            formHandler.updateRoomNoOptions();
            formHandler.updateGuestCountOptions();
        });

        // Kini nga code nagpili og kwarto kung mapili ang numero sa kwarto.
        cbRoomNo.addActionListener(e -> {
            Integer val = (Integer) cbRoomNo.getSelectedItem();
            if (val != null) {
                Room r = hotelManager.findRoomByNo(val);
                if (r != null) {
                    formHandler.selectRoomInForm(r);
                }
            }
        });

        // Kini nga code nag-set up sa footer panel uban ang info sa user ug sign out.
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

    // Kini nga method nag-set sa mga credential sa user.
    public void setUsers(java.util.Map<String, String> creds) {
        authManager.setUsers(creds);
    }

    // Kini nga method nag-set sa status sa sign-in.
    public void setSignedIn(boolean v) {
        authManager.setSignedIn(v);
    }

    // Kini nga method nag-set sa email sa user karon.
    public void setCurrentUser(String u) {
        authManager.setCurrentUser(u);
    }

    // Kini nga method nag-return sa map sa tanang user.
    public java.util.Map<String, String> getUsersMap() {
        return authManager.getUsers();
    }

    // Kini nga method naga-update sa UI base sa kung ang user naka-sign in ba.
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

    // Kini nga method naga-update sa text ug state sa mga action button base sa pagpili.
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

    // Kini nga method naga-refresh sa main room table.
    public void roomUpdateTable() {
        tableHandler.roomUpdateTable();
        tableHandler.updateGuestListTable();
    }

    // Kini nga method naglimpyo sa gipili sa main table.
    public void clearTableSelection() {
        table.clearSelection();
    }

    // Kini nga method nag-handle sa proseso sa pag-sign out.
    // Kini mopatuman sa pag-reset sa user status ug mobalik sa login flow.
    private void signOut() {
        // I-set ang sign-in status ngadto sa false
        authManager.setSignedIn(false);
        authManager.setCurrentUser(null);
        
        // I-hide ang main window ug limpyohan ang mga field
        setVisible(false);
        formHandler.clearFields();
        
        // Pag-restart sa login process gamit ang LoginDialog
        String email = LoginDialog.show(this, authManager.getUsers());
        
        if (email != null) {
            // Kung malampuson ang bag-ong login
            authManager.setSignedIn(true);
            authManager.setCurrentUser(email);
            updateAuthUI();
            setVisible(true);
        } else {
            // Kung gi-close ang login dialog nga wala naka-login
            System.exit(0);
        }
    }

    // Kini nga code nga main method mao ang entry point kung daganon ang file.
    
    

}
