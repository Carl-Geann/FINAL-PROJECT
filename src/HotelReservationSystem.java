import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HotelReservationSystem extends JFrame {

    private JComboBox<Integer> cbRoomNo;
    private JTextField txtName, txtPrice, txtGuest;
    private JComboBox<String> cbCategory, cbStatus, cbPaymentMethod, cbFilterType, cbFilterStatus, cbFilterPayment;
    private JTable table;
    private DefaultTableModel model;
    private ArrayList<Room> rooms;

    private boolean signedIn = false;
    private String currentUser;
    private java.util.Map<String, String> users = new java.util.LinkedHashMap<>();

    private JButton btnAdd, btnEdit, btnDelete;
    private JLabel lblUser;
    private JButton btnSignOut;

    private JSpinner spBookingAt;

    private JButton btnRoomDetails;
    private JButton btnReservationList;
    private JPanel detailsPanel;
    private JPanel actionsPanel;
    private JPanel leftStack;

    private JSplitPane mainSplit;

    private JPanel rightCards;
    private JPanel mapPanel;

    private String currentRightView = "table";
    private Room currentSelected;

    public HotelReservationSystem() {
        setTitle("Hotel Reservation List");
        setSize(1100, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        rooms = new ArrayList<>();

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(6, 6, 6, 6));

        btnRoomDetails = new JButton("Room Details");
        btnReservationList = new JButton("Reservation List");
        Font headerFont = baseFont().deriveFont(Font.BOLD, 14f);
        btnRoomDetails.setFont(headerFont);
        btnReservationList.setFont(headerFont);
        btnRoomDetails.setPreferredSize(new Dimension(180, 36));
        btnReservationList.setPreferredSize(new Dimension(180, 36));
        JPanel headerButtons = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        headerButtons.add(btnRoomDetails);
        headerButtons.add(btnReservationList);

        detailsPanel = new JPanel(new GridLayout(15, 1, 5, 5));

        cbRoomNo = new JComboBox<>();
        txtName = new JTextField();
        txtPrice = new JTextField();
        txtGuest = new JTextField();

        cbCategory = new JComboBox<>(new String[]{"VIP", "Double Bed", "Family"});
        cbStatus = new JComboBox<>(new String[]{"Free", "Booked"});
        cbPaymentMethod = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash", "Online Transfer"});

        spBookingAt = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE));
        spBookingAt.setEditor(new JSpinner.DateEditor(spBookingAt, "yyyy-MM-dd HH:mm"));

        detailsPanel.add(new JLabel("Room No"));
        detailsPanel.add(cbRoomNo);
        detailsPanel.add(new JLabel("Room"));
        detailsPanel.add(txtName);
        detailsPanel.add(new JLabel("Category"));
        detailsPanel.add(cbCategory);
        detailsPanel.add(new JLabel("Status"));
        detailsPanel.add(cbStatus);
        detailsPanel.add(new JLabel("Price"));
        detailsPanel.add(txtPrice);
        detailsPanel.add(new JLabel("Payment Method"));
        detailsPanel.add(cbPaymentMethod);
        detailsPanel.add(new JLabel("Guest Name"));
        detailsPanel.add(txtGuest);
        detailsPanel.add(new JLabel("Booking Date/Time"));
        detailsPanel.add(spBookingAt);

        Font labelFont = baseFont().deriveFont(Font.PLAIN, 14f);
        Font fieldFont = baseFont().deriveFont(Font.PLAIN, 14f);
        Dimension fieldSize = new Dimension(220, 36);
        cbRoomNo.setFont(fieldFont);
        txtName.setFont(fieldFont);
        cbCategory.setFont(fieldFont);
        cbStatus.setFont(fieldFont);
        txtPrice.setFont(fieldFont);
        cbPaymentMethod.setFont(fieldFont);
        txtGuest.setFont(fieldFont);
        spBookingAt.setFont(fieldFont);
        ((JSpinner.DefaultEditor) spBookingAt.getEditor()).getTextField().setFont(fieldFont);
        cbRoomNo.setPreferredSize(fieldSize);
        txtName.setPreferredSize(fieldSize);
        cbCategory.setPreferredSize(fieldSize);
        cbStatus.setPreferredSize(fieldSize);
        txtPrice.setPreferredSize(fieldSize);
        cbPaymentMethod.setPreferredSize(fieldSize);
        txtGuest.setPreferredSize(fieldSize);
        spBookingAt.setPreferredSize(fieldSize);
        for (Component c : detailsPanel.getComponents()) {
            if (c instanceof JLabel) c.setFont(labelFont);
        }

        actionsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints agc = new GridBagConstraints();
        agc.insets = new Insets(4, 0, 4, 0);
        agc.fill = GridBagConstraints.HORIZONTAL;

        btnAdd = new JButton("Add");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Delete");
        Font actionFont = baseFont().deriveFont(Font.BOLD, 14f);
        btnAdd.setFont(actionFont);
        btnEdit.setFont(actionFont);
        btnDelete.setFont(actionFont);
        btnAdd.setPreferredSize(new Dimension(140, 40));
        btnEdit.setPreferredSize(new Dimension(140, 40));
        btnDelete.setPreferredSize(new Dimension(240, 46));

        JPanel row1 = new JPanel(new FlowLayout(FlowLayout.LEFT, 8, 0));
        row1.add(btnAdd);
        row1.add(btnEdit);

        agc.gridx = 0;
        agc.gridy = 0;
        actionsPanel.add(row1, agc);

        agc.gridx = 0;
        agc.gridy = 1;
        actionsPanel.add(btnDelete, agc);

        leftStack = new JPanel(new BorderLayout(0, 8));
        leftStack.add(detailsPanel, BorderLayout.CENTER);
        leftStack.add(actionsPanel, BorderLayout.SOUTH);

        leftPanel.add(headerButtons, BorderLayout.NORTH);
        leftPanel.add(leftStack, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        cbFilterType = new JComboBox<>(new String[]{"All", "VIP", "Double Bed", "Family"});
        cbFilterStatus = new JComboBox<>(new String[]{"All", "Free", "Booked"});
        cbFilterPayment = new JComboBox<>(new String[]{"All", "Credit Card", "Debit Card", "Cash", "Online Transfer"});
        JButton btnRefresh = new JButton("Refresh");
        Font filterFont = baseFont().deriveFont(Font.PLAIN, 13f);
        cbFilterType.setFont(filterFont);
        cbFilterStatus.setFont(filterFont);
        cbFilterPayment.setFont(filterFont);
        cbFilterType.setPreferredSize(new Dimension(130, 32));
        cbFilterStatus.setPreferredSize(new Dimension(130, 32));
        cbFilterPayment.setPreferredSize(new Dimension(150, 32));
        btnRefresh.setFont(actionFont);
        btnRefresh.setPreferredSize(new Dimension(100, 34));

        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(cbFilterType);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(cbFilterStatus);
        filterPanel.add(new JLabel("Payment:"));
        filterPanel.add(cbFilterPayment);
        filterPanel.add(btnRefresh);

        String[] columns = {"Room No", "Room Name", "Guest Name", "Type", "Status", "Price", "Payment Method", "Booked At"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(22);
        table.getTableHeader().setFont(baseFont().deriveFont(Font.BOLD, 13f));
        table.setFont(baseFont().deriveFont(Font.PLAIN, 13f));
        table.setAutoResizeMode(JTable.AUTO_RESIZE_ALL_COLUMNS);

        JScrollPane scrollPane = new JScrollPane(table);

        rightCards = new JPanel(new CardLayout());
        JPanel tableCard = new JPanel(new BorderLayout());
        tableCard.add(scrollPane, BorderLayout.CENTER);
        rightCards.add(tableCard, "table");

        rightPanel.add(filterPanel, BorderLayout.NORTH);
        rightPanel.add(rightCards, BorderLayout.CENTER);

        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        mainSplit.setContinuousLayout(true);
        mainSplit.setResizeWeight(0.28);
        add(mainSplit, BorderLayout.CENTER);

        btnRoomDetails.addActionListener(e -> {
            cbFilterType.setSelectedIndex(0);
            cbFilterStatus.setSelectedItem("All");
            cbFilterPayment.setSelectedIndex(0);
            filterRooms();
            ((CardLayout) rightCards.getLayout()).show(rightCards, "table");
            currentRightView = "table";
            mainSplit.setDividerLocation(0.28);
        });

        btnReservationList.addActionListener(e -> {
            cbFilterType.setSelectedIndex(0);
            cbFilterStatus.setSelectedItem("All");
            cbFilterPayment.setSelectedIndex(0);
            mapPanel = buildMapPanel();
            rightCards.add(mapPanel, "map");
            ((CardLayout) rightCards.getLayout()).show(rightCards, "map");
            currentRightView = "map";
            mainSplit.setDividerLocation(0.28);
        });

        btnAdd.addActionListener(e -> {
            addRoom();
            refreshMapIfVisible();
        });

        btnEdit.addActionListener(e -> {
            editRoom();
            refreshMapIfVisible();
        });

        btnDelete.addActionListener(e -> {
            deleteRoom();
            refreshMapIfVisible();
        });

        btnRefresh.addActionListener(e -> filterRooms());

        cbStatus.addActionListener(e -> {
            boolean booked = "Booked".equals(cbStatus.getSelectedItem().toString());
            spBookingAt.setEnabled(booked && signedIn);
            if (booked) {
                spBookingAt.setValue(new Date());
            }
        });

        cbCategory.addActionListener(e -> updateRoomNoOptions());
        cbRoomNo.addActionListener(e -> {
            Integer val = (Integer) cbRoomNo.getSelectedItem();
            if (val != null) {
                Room r = findRoomByNo(val);
                if (r != null) {
                    selectRoomInForm(r);
                }
            }
        });

        setJMenuBar(createMenuBar());

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(4, 8, 4, 8));
        lblUser = new JLabel("Signed out");
        btnSignOut = new JButton("Sign Out");
        btnSignOut.addActionListener(e -> signOut());
        footer.add(lblUser, BorderLayout.WEST);
        footer.add(btnSignOut, BorderLayout.EAST);
        add(footer, BorderLayout.SOUTH);

        users.put("geann@gmail.com", "geann123");
        users.put("staff@example.com", "staff123");

        seedInventory();
        updateRoomNoOptions();
        updateTable();
        updateAuthUI();
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    public void setUsers(java.util.Map<String, String> creds) {
        users.clear();
        users.putAll(creds);
    }

    public void setSignedIn(boolean v) {
        signedIn = v;
    }

    public void setCurrentUser(String u) {
        currentUser = u;
    }

    public void updateAuthUI() {
        boolean enabled = signedIn;
        cbRoomNo.setEnabled(enabled);
        txtName.setEnabled(enabled);
        txtPrice.setEnabled(enabled);
        txtGuest.setEnabled(enabled);
        cbCategory.setEnabled(enabled);
        cbStatus.setEnabled(enabled);
        cbPaymentMethod.setEnabled(enabled);
        boolean booked = cbStatus.getSelectedItem() != null && "Booked".equals(cbStatus.getSelectedItem().toString());
        spBookingAt.setEnabled(enabled && booked);
        btnAdd.setEnabled(enabled);
        btnEdit.setEnabled(enabled);
        btnDelete.setEnabled(enabled);
        lblUser.setText(enabled ? ("Signed in as " + currentUser) : "Signed out");
        btnSignOut.setEnabled(enabled);
        btnRoomDetails.setEnabled(true);
        btnReservationList.setEnabled(true);
    }

    public void showRoomDetailsOnLogin() {
        updateRoomNoOptions();
        cbCategory.setSelectedIndex(0);
        int firstNo = rangeForType(cbCategory.getSelectedItem().toString())[0];
        cbRoomNo.setSelectedItem(firstNo);
        Room r = findRoomByNo(firstNo);
        if (r != null) selectRoomInForm(r);
        ((CardLayout) rightCards.getLayout()).show(rightCards, "table");
        currentRightView = "table";
        mainSplit.setDividerLocation(0.28);
    }

    private void refreshMapIfVisible() {
        if ("map".equals(currentRightView)) {
            mapPanel = buildMapPanel();
            rightCards.add(mapPanel, "map");
            ((CardLayout) rightCards.getLayout()).show(rightCards, "map");
        }
    }

    private void seedInventory() {
        for (int i = 1; i <= 10; i++) {
            rooms.add(new Room(i, "VIP " + i, "VIP", "Free", "100", "Cash", null));
        }
        for (int i = 11; i <= 20; i++) {
            rooms.add(new Room(i, "Family " + (i - 10), "Family", "Free", "80", "Cash", null));
        }
        for (int i = 21; i <= 30; i++) {
            rooms.add(new Room(i, "Double " + (i - 20), "Double Bed", "Free", "60", "Cash", null));
        }
    }

    private JPanel buildMapPanel() {
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.gridx = 0;
        g.gridy = 0;
        overlay.add(buildRow("VIP", "VIP"), g);
        g.gridy = 1;
        overlay.add(buildRow("Family", "Family"), g);
        g.gridy = 2;
        overlay.add(buildRow("Double Bed", "Double"), g);
        return overlay;
    }

    private JPanel buildRow(String type, String base) {
        JPanel row = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel(type);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        row.add(lbl, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 10, 8, 8));
        int[] range = rangeForType(type);
        for (int i = range[0]; i <= range[1]; i++) {
            String label = base + " " + (i - range[0] + 1);
            Room r = findRoomByNo(i);
            JButton tile = new JButton(label);
            tile.setPreferredSize(new Dimension(120, 60));
            tile.setForeground(Color.WHITE);
            if (r != null) {
                Color freeColor = new Color(0, 160, 0);
                Color bookedColor = new Color(200, 0, 0);
                tile.setBackground("Free".equals(r.getStatus()) ? freeColor : bookedColor);
                tile.setEnabled(true);
                tile.addActionListener(e -> selectRoomInForm(r));
            } else {
                tile.setBackground(new Color(180, 180, 180));
                tile.setEnabled(false);
            }
            grid.add(tile);
        }
        row.add(grid, BorderLayout.CENTER);
        return row;
    }

    private void selectRoomInForm(Room r) {
        currentSelected = r;
        cbCategory.setSelectedItem(r.getType());
        updateRoomNoOptions();
        cbRoomNo.setSelectedItem(r.getRoomNo());
        txtName.setText(r.getName());
        cbStatus.setSelectedItem(r.getStatus());
        txtPrice.setText(r.getPrice());
        cbPaymentMethod.setSelectedItem(r.getPaymentMethod());
        txtGuest.setText(r.getGuestName() != null ? r.getGuestName() : "");
        if (r.getBookedAt() != null) {
            spBookingAt.setValue(r.getBookedAt());
        }
    }

    private JMenuBar createMenuBar() {
        JMenuBar mb = new JMenuBar();
        JMenu account = new JMenu("Account");
        JMenuItem miSignIn = new JMenuItem("Sign In");
        JMenuItem miSignOut = new JMenuItem("Sign Out");
        JMenuItem miExit = new JMenuItem("Exit");
        miSignIn.addActionListener(e -> {
            String email = LoginDialog.show(this, users);
            if (email != null) {
                signedIn = true;
                currentUser = email;
                updateAuthUI();
                showRoomDetailsOnLogin();
                setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
                JOptionPane.showMessageDialog(this, "Signed in as " + email);
            }
        });
        miSignOut.addActionListener(e -> signOut());
        miExit.addActionListener(e -> System.exit(0));
        account.add(miSignIn);
        account.add(miSignOut);
        account.addSeparator();
        account.add(miExit);
        mb.add(account);
        return mb;
    }

    private void signOut() {
        signedIn = false;
        currentUser = null;
        updateAuthUI();
        JOptionPane.showMessageDialog(this, "Signed out");
        setVisible(false);
        String email = LoginDialog.show(null, users);
        if (email != null) {
            signedIn = true;
            currentUser = email;
            updateAuthUI();
            showRoomDetailsOnLogin();
            setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
            setVisible(true);
            JOptionPane.showMessageDialog(this, "Signed in as " + email);
        } else {
            System.exit(0);
        }
    }

    private void filterRooms() {
        String typeFilter = cbFilterType.getSelectedItem().toString();
        String statusFilter = cbFilterStatus.getSelectedItem().toString();
        String paymentFilter = cbFilterPayment.getSelectedItem().toString();

        model.setRowCount(0);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");

        for (Room room : rooms) {
            boolean visible = true;
            if (!typeFilter.equals("All") && !room.getType().equals(typeFilter)) visible = false;
            if (!statusFilter.equals("All") && !room.getStatus().equals(statusFilter)) visible = false;
            if (!paymentFilter.equals("All") && !room.getPaymentMethod().equals(paymentFilter)) visible = false;
            if (visible) {
                String bookedStr = room.getBookedAt() != null ? fmt.format(room.getBookedAt()) : "";
                model.addRow(new Object[]{
                        room.getRoomNo(),
                        room.getName(),
                        room.getGuestName(),
                        room.getType(),
                        room.getStatus(),
                        room.getPrice(),
                        room.getPaymentMethod(),
                        bookedStr
                });
            }
        }
    }

    private void updateTable() {
        model.setRowCount(0);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Room room : rooms) {
            String bookedStr = room.getBookedAt() != null ? fmt.format(room.getBookedAt()) : "";
            model.addRow(new Object[]{
                    room.getRoomNo(),
                    room.getName(),
                    room.getGuestName(),
                    room.getType(),
                    room.getStatus(),
                    room.getPrice(),
                    room.getPaymentMethod(),
                    bookedStr
            });
        }
    }

    private void addRoom() {
        String name = txtName.getText();
        String type = cbCategory.getSelectedItem().toString();
        String status = cbStatus.getSelectedItem().toString();
        String price = txtPrice.getText();
        String paymentMethod = cbPaymentMethod.getSelectedItem().toString();
        String guestName = txtGuest.getText().trim();
        Integer roomNoVal = (Integer) cbRoomNo.getSelectedItem();

        if (name.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }
        if (roomNoVal == null) {
            JOptionPane.showMessageDialog(this, "Select a Room No.");
            return;
        }

        try { spBookingAt.commitEdit(); } catch (ParseException ignored) {}
        Date bookedAt = "Booked".equals(status) ? (Date) spBookingAt.getValue() : null;

        Room target = findRoomByNo(roomNoVal);
        if (target != null) {
            target.setName(name);
            target.setType(type);
            target.setStatus(status);
            target.setPrice(price);
            target.setPaymentMethod(paymentMethod);
            target.setGuestName(guestName);
            target.setBookedAt(bookedAt);
        } else {
            Room room = new Room(roomNoVal, name, type, status, price, paymentMethod, bookedAt);
            room.setGuestName(guestName);
            rooms.add(room);
        }

        updateTable();
        clearFields();
    }

    private void editRoom() {
        Integer prefill = (Integer) cbRoomNo.getSelectedItem();
        if (prefill == null && table.getSelectedRow() != -1) {
            try {
                prefill = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString());
            } catch (Exception ignored) {}
        }
        String input = (String) JOptionPane.showInputDialog(
                this,
                "Enter Room No to edit:",
                "Edit Which Room",
                JOptionPane.QUESTION_MESSAGE,
                null,
                null,
                prefill != null ? prefill.toString() : ""
        );
        if (input == null) return;
        int targetNo;
        try { targetNo = Integer.parseInt(input.trim()); }
        catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Room No");
            return;
        }

        Room room = findRoomByNo(targetNo);
        if (room == null) {
            JOptionPane.showMessageDialog(this, "Room No " + targetNo + " not found");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Edit room " + targetNo + " (" + room.getName() + ")?",
                "Confirm Edit",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (ok != JOptionPane.OK_OPTION) return;

        room.setName(txtName.getText());
        room.setType(cbCategory.getSelectedItem().toString());
        room.setStatus(cbStatus.getSelectedItem().toString());
        room.setPrice(txtPrice.getText());
        room.setPaymentMethod(cbPaymentMethod.getSelectedItem().toString());
        room.setGuestName(txtGuest.getText().trim());
        try { spBookingAt.commitEdit(); } catch (ParseException ignored) {}
        Date bookedAt = "Booked".equals(cbStatus.getSelectedItem().toString()) ? (Date) spBookingAt.getValue() : null;
        room.setBookedAt(bookedAt);

        updateTable();
        JOptionPane.showMessageDialog(this, "Edit confirmed");
    }

    private void deleteRoom() {
        int viewRow = table.getSelectedRow();
        Integer roomNoVal = (Integer) cbRoomNo.getSelectedItem();
        int targetNo = -1;

        if (roomNoVal != null) {
            targetNo = roomNoVal;
        } else if (viewRow != -1) {
            try {
                targetNo = Integer.parseInt(table.getValueAt(viewRow, 0).toString());
            } catch (Exception e) {
                JOptionPane.showMessageDialog(this, "Select a valid row");
                return;
            }
        } else {
            String input = (String) JOptionPane.showInputDialog(
                    this,
                    "Enter Room No to empty:",
                    "Empty Which Row",
                    JOptionPane.QUESTION_MESSAGE,
                    null,
                    null,
                    ""
            );
            if (input == null) return;
            try { targetNo = Integer.parseInt(input.trim()); }
            catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Room No");
                return;
            }
        }

        Room room = findRoomByNo(targetNo);
        if (room == null) {
            JOptionPane.showMessageDialog(this, "Room No " + targetNo + " not found");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                this,
                "Empty row " + targetNo + " (" + room.getName() + ")?",
                "Confirm Empty",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (ok != JOptionPane.OK_OPTION) return;

        emptyRoom(room);
        updateTable();
        cbRoomNo.setSelectedItem(targetNo);
        JOptionPane.showMessageDialog(this, "Row " + targetNo + " emptied");
    }

    private void clearFields() {
        cbRoomNo.setSelectedIndex(-1);
        txtName.setText("");
        txtPrice.setText("");
        txtGuest.setText("");
        cbCategory.setSelectedIndex(0);
        updateRoomNoOptions();
        cbStatus.setSelectedIndex(0);
        cbPaymentMethod.setSelectedIndex(0);
        spBookingAt.setValue(new Date());
        spBookingAt.setEnabled(false);
        currentSelected = null;
    }

    private Room findRoomByNo(int roomNo) {
        for (Room r : rooms) {
            if (r.getRoomNo() == roomNo) return r;
        }
        return null;
    }

    private String defaultPriceForType(String type) {
        if ("VIP".equals(type)) return "100";
        if ("Family".equals(type)) return "80";
        return "60";
    }

    private String baseForType(String type) {
        if ("VIP".equals(type)) return "VIP";
        if ("Family".equals(type)) return "Family";
        return "Double";
    }

    private int[] rangeForType(String type) {
        if ("VIP".equalsIgnoreCase(type)) return new int[]{1, 10};
        if ("Family".equalsIgnoreCase(type)) return new int[]{11, 20};
        if ("Double Bed".equalsIgnoreCase(type)) return new int[]{21, 30};
        return new int[]{1, 30};
    }

    private int indexForTypeRoomNo(String type, int roomNo) {
        int[] r = rangeForType(type);
        return roomNo - r[0] + 1;
    }

    private void emptyRoom(Room room) {
        room.setGuestName("");
        room.setBookedAt(null);
        room.setStatus("Free");
        room.setPaymentMethod("Cash");
        room.setPrice(defaultPriceForType(room.getType()));
        int idx = indexForTypeRoomNo(room.getType(), room.getRoomNo());
        room.setName(baseForType(room.getType()) + " " + idx);
    }

    private void updateRoomNoOptions() {
        cbRoomNo.removeAllItems();
        Object sel = cbCategory.getSelectedItem();
        String type = sel == null ? "" : sel.toString();
        int[] range = rangeForType(type);
        for (int i = range[0]; i <= range[1]; i++) cbRoomNo.addItem(i);
        cbRoomNo.setSelectedIndex(-1);
    }

    private Font baseFont() {
        Font f = getFont();
        if (f == null) f = UIManager.getFont("Label.font");
        if (f == null) f = new Font("Dialog", Font.PLAIN, 12);
        return f;
    }
}