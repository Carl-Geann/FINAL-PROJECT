import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class HotelReservationSystem extends JFrame {

    private JComboBox<Integer> cbRoomNo, cbNights, cbGuestCount;
    private JTextField txtName, txtPrice, txtGuest, txtDiscount, txtTotalPayment;
    private JComboBox<String> cbCategory, cbStatus, cbPaymentMethod, cbFilterType, cbFilterStatus, cbFilterPayment;
    private JTable table;
    private DefaultTableModel model;
    private ArrayList<Room> rooms;

    private boolean signedIn = false;
    private String currentUser;
    private java.util.Map<String, String> users = new java.util.LinkedHashMap<>();

    private JButton btnAdd, btnEdit, btnDelete, btnBookOut;
    private JLabel lblUser, lblTotalGuests;
    private JButton btnSignOut;

    private JSpinner spBookingAt, spBookOutAt;

    private JButton btnRoomDetails;
    private JPanel detailsPanel;
    private JPanel actionsPanel;
    private JPanel leftStack;

    private JSplitPane mainSplit;

    private Room currentSelected;

    public HotelReservationSystem() {
        setTitle("Hotel Reservation List");
        setSize(1200, 700);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        rooms = new ArrayList<>();

        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        btnRoomDetails = new JButton("Room Details");
        btnRoomDetails.setPreferredSize(new Dimension(150, 35));
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        headerPanel.add(btnRoomDetails);

        detailsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(8, 0, 8, 15);
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.HORIZONTAL;

        cbRoomNo = new JComboBox<>();
        txtName = new JTextField();
        txtPrice = new JTextField();
        txtPrice.setEditable(false);
        txtGuest = new JTextField();
        cbNights = new JComboBox<>();
        for (int i = 1; i <= 7; i++) cbNights.addItem(i);
        txtDiscount = new JTextField("0");
        txtTotalPayment = new JTextField();
        txtTotalPayment.setEditable(false);
        cbGuestCount = new JComboBox<>();
        updateGuestCountOptions();

        cbCategory = new JComboBox<>(new String[]{"VIP", "Double Bed", "Family"});
        cbStatus = new JComboBox<>(new String[]{"Free", "Booked"});
        cbPaymentMethod = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash", "Online Transfer"});

        spBookingAt = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE));
        spBookingAt.setEditor(new JSpinner.DateEditor(spBookingAt, "yyyy-MM-dd HH:mm"));
        
        spBookOutAt = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE));
        spBookOutAt.setEditor(new JSpinner.DateEditor(spBookOutAt, "yyyy-MM-dd HH:mm"));

        String[] labels = {"Room No", "Room", "Category", "Status", "Guest Name", "Guests In", "Price", "Payment Method", "Discount", "Nights", "Total Payment", "Booking Date/Time", "Book Out"};
        JComponent[] fields = {cbRoomNo, txtName, cbCategory, cbStatus, txtGuest, cbGuestCount, txtPrice, cbPaymentMethod, txtDiscount, cbNights, txtTotalPayment, spBookingAt, spBookOutAt};

        // Add listeners for auto-calculation
        java.awt.event.KeyAdapter calcKeyListener = new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent e) { calculateTotal(); }
        };
        java.awt.event.ActionListener calcActionListener = e -> calculateTotal();
        
        txtPrice.addKeyListener(calcKeyListener);
        cbNights.addActionListener(calcActionListener);
        txtDiscount.addKeyListener(calcKeyListener);
        cbGuestCount.addActionListener(calcActionListener);

        for (int i = 0; i < labels.length; i++) {
            gbc.gridx = 0;
            gbc.gridy = i;
            gbc.weightx = 0;
            detailsPanel.add(new JLabel(labels[i]), gbc);

            gbc.gridx = 1;
            gbc.weightx = 1.0;
            fields[i].setPreferredSize(new Dimension(200, 30));
            detailsPanel.add(fields[i], gbc);
        }

        actionsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints agc = new GridBagConstraints();
        agc.insets = new Insets(8, 0, 8, 15);
        agc.fill = GridBagConstraints.HORIZONTAL;
        agc.weightx = 1.0;

        btnAdd = new JButton("Add");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Delete");
        btnBookOut = new JButton("Book Out");

        Dimension btnSize = new Dimension(200, 45);
        btnAdd.setPreferredSize(btnSize);
        btnEdit.setPreferredSize(btnSize);
        btnDelete.setPreferredSize(btnSize);
        btnBookOut.setPreferredSize(btnSize);

        Font btnFont = new Font("SansSerif", Font.BOLD, 14);
        btnAdd.setFont(btnFont);
        btnEdit.setFont(btnFont);
        btnDelete.setFont(btnFont);
        btnBookOut.setFont(btnFont);

        agc.gridx = 0;
        agc.gridy = 0;
        actionsPanel.add(btnAdd, agc);

        agc.gridy = 1;
        actionsPanel.add(btnEdit, agc);

        agc.gridy = 2;
        actionsPanel.add(btnDelete, agc);
        
        agc.gridy = 3;
        actionsPanel.add(btnBookOut, agc);

        leftStack = new JPanel(new BorderLayout(0, 5));
        leftStack.add(detailsPanel, BorderLayout.NORTH);
        leftStack.add(actionsPanel, BorderLayout.CENTER);

        // Added JScrollPane with fast scroll speed
        JScrollPane leftScroll = new JScrollPane(leftStack);
        leftScroll.setBorder(null);
        leftScroll.getVerticalScrollBar().setUnitIncrement(25); // Set fast scroll speed

        leftPanel.add(headerPanel, BorderLayout.NORTH);
        leftPanel.add(leftScroll, BorderLayout.CENTER);

        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JPanel filterPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        cbFilterType = new JComboBox<>(new String[]{"All", "VIP", "Double Bed", "Family"});
        cbFilterStatus = new JComboBox<>(new String[]{"All", "Free", "Booked"});
        cbFilterPayment = new JComboBox<>(new String[]{"All", "Credit Card", "Debit Card", "Cash", "Online Transfer"});
        JButton btnRefresh = new JButton("Refresh");

        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(cbFilterType);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(cbFilterStatus);
        filterPanel.add(new JLabel("Payment:"));
        filterPanel.add(cbFilterPayment);
        filterPanel.add(btnRefresh);

        lblTotalGuests = new JLabel(" | Total Guests In: 0");
        lblTotalGuests.setFont(new Font("SansSerif", Font.BOLD, 12));
        filterPanel.add(lblTotalGuests);

        String[] columns = {"Room No", "Room Name", "Guest Name", "Type", "Status", "Price", "Nights", "Discount", "Total", "Guests In", "Payment Method", "Booked At", "Booked Out"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(25);
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setShowGrid(true);
        table.setGridColor(Color.LIGHT_GRAY);

        JScrollPane scrollPane = new JScrollPane(table);
        rightPanel.add(filterPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, leftPanel, rightPanel);
        mainSplit.setDividerLocation(400);
        mainSplit.setContinuousLayout(true);
        add(mainSplit, BorderLayout.CENTER);

        btnRoomDetails.addActionListener(e -> {
            cbFilterType.setSelectedIndex(0);
            cbFilterStatus.setSelectedItem("All");
            cbFilterPayment.setSelectedIndex(0);
            filterRooms();
        });

        btnAdd.addActionListener(e -> addRoom());
        btnEdit.addActionListener(e -> editRoom());
        btnDelete.addActionListener(e -> deleteRoom());
        btnBookOut.addActionListener(e -> bookOutRoom());
        btnRefresh.addActionListener(e -> filterRooms());

        cbStatus.addActionListener(e -> {
            boolean booked = "Booked".equals(cbStatus.getSelectedItem().toString());
            spBookingAt.setEnabled(booked && signedIn);
            spBookOutAt.setEnabled(booked && signedIn);
            if (booked) {
                spBookingAt.setValue(new Date());
                spBookOutAt.setValue(new Date());
            }
        });

        cbCategory.addActionListener(e -> {
            updateRoomNoOptions();
            updateGuestCountOptions();
        });
        cbRoomNo.addActionListener(e -> {
            Integer val = (Integer) cbRoomNo.getSelectedItem();
            if (val != null) {
                Room r = findRoomByNo(val);
                if (r != null) {
                    selectRoomInForm(r);
                }
            }
        });

        JPanel footer = new JPanel(new BorderLayout());
        footer.setBorder(BorderFactory.createEmptyBorder(10, 15, 10, 15));
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
        cbNights.setEnabled(enabled);
        txtDiscount.setEnabled(enabled);
        cbGuestCount.setEnabled(enabled);
        cbCategory.setEnabled(enabled);
        cbStatus.setEnabled(enabled);
        cbPaymentMethod.setEnabled(enabled);
        boolean booked = cbStatus.getSelectedItem() != null && "Booked".equals(cbStatus.getSelectedItem().toString());
        spBookingAt.setEnabled(enabled && booked);
        spBookOutAt.setEnabled(enabled && booked);
        btnAdd.setEnabled(enabled);
        btnEdit.setEnabled(enabled);
        btnDelete.setEnabled(enabled);
        btnBookOut.setEnabled(enabled);
        lblUser.setText(enabled ? ("Signed in as " + currentUser) : "Signed out");
        btnSignOut.setEnabled(true);
        btnRoomDetails.setEnabled(true);
    }

    private void updateGuestCountOptions() {
        if (cbGuestCount == null || cbCategory == null) return;
        int maxGuests = 2;
        String category = cbCategory.getSelectedItem().toString();
        if ("VIP".equals(category)) {
            maxGuests = 10;
        } else if ("Family".equals(category)) {
            maxGuests = 7;
        }
        
        Integer currentVal = (Integer) cbGuestCount.getSelectedItem();
        cbGuestCount.removeAllItems();
        for (int i = 1; i <= maxGuests; i++) {
            cbGuestCount.addItem(i);
        }
        if (currentVal != null && currentVal <= maxGuests) {
            cbGuestCount.setSelectedItem(currentVal);
        } else {
            cbGuestCount.setSelectedIndex(0);
        }
    }

    private void calculateTotal() {
        try {
            double price = Double.parseDouble(txtPrice.getText().isEmpty() ? "0" : txtPrice.getText());
            int nights = (Integer) cbNights.getSelectedItem();
            double discount = Double.parseDouble(txtDiscount.getText().isEmpty() ? "0" : txtDiscount.getText());
            int guestCount = (Integer) cbGuestCount.getSelectedItem();
            double total = (price * nights) + (price * guestCount) - discount;
            txtTotalPayment.setText(String.format("%.2f", total));
        } catch (Exception e) {
            txtTotalPayment.setText("0.00");
        }
    }

    private void seedInventory() {
        seedType("VIP", "VIP", 10, "100");
        seedType("Family", "Family", 10, "80");
        seedType("Double Bed", "Double", 10, "60");
    }

    private void seedType(String type, String base, int count, String price) {
        for (int i = 1; i <= count; i++) {
            String name = base + " " + i;
            if (findRoomByName(name) == null) {
                rooms.add(new Room(nextRoomNo(), name, type, "Free", price, "Cash", null));
            }
        }
    }

    private void selectRoomInForm(Room r) {
        currentSelected = r;
        cbCategory.setSelectedItem(r.getType());
        updateRoomNoOptions();
        updateGuestCountOptions();
        cbRoomNo.setSelectedItem(r.getRoomNo());
        txtName.setText(r.getName());
        cbStatus.setSelectedItem(r.getStatus());
        txtPrice.setText(r.getPrice());
        cbNights.setSelectedItem(r.getNights());
        txtDiscount.setText(String.valueOf(r.getDiscount()));
        cbGuestCount.setSelectedItem(r.getGuestCount());
        calculateTotal();
        cbPaymentMethod.setSelectedItem(r.getPaymentMethod());
        txtGuest.setText(r.getGuestName() != null ? r.getGuestName() : "");
        if (r.getBookedAt() != null) {
            spBookingAt.setValue(r.getBookedAt());
        }
        if (r.getBookOutAt() != null) {
            spBookOutAt.setValue(r.getBookOutAt());
        }
    }

    private Room findRoomByNo(int roomNo) {
        for (Room r : rooms) {
            if (r.getRoomNo() == roomNo) return r;
        }
        return null;
    }

    private Room findRoomByName(String name) {
        for (Room r : rooms) {
            if (r.getName().equals(name)) return r;
        }
        return null;
    }

    private int nextRoomNo() {
        int max = 0;
        for (Room r : rooms) {
            if (r.getRoomNo() > max) max = r.getRoomNo();
        }
        return max + 1;
    }

    private void addRoom() {
        String name = txtName.getText();
        String type = cbCategory.getSelectedItem().toString();
        String status = cbStatus.getSelectedItem().toString();
        String price = txtPrice.getText();
        String paymentMethod = cbPaymentMethod.getSelectedItem().toString();
        String guestName = txtGuest.getText().trim();
        int nights = (Integer) cbNights.getSelectedItem();
        double discount = 0;
        int guestCount = (Integer) cbGuestCount.getSelectedItem();
        try {
            discount = Double.parseDouble(txtDiscount.getText());
        } catch (NumberFormatException ignored) {}
        
        double priceVal = Double.parseDouble(price.isEmpty() ? "0" : price);
        double total = (priceVal * nights) + (priceVal * guestCount) - discount;

        Integer roomNoVal = (Integer) cbRoomNo.getSelectedItem();

        if (name.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }
        if (roomNoVal == null) {
            JOptionPane.showMessageDialog(this, "Select a Room No.");
            return;
        }

        try {
            spBookingAt.commitEdit();
            spBookOutAt.commitEdit();
        } catch (ParseException ignored) {}
        Date bookedAt = "Booked".equals(status) ? (Date) spBookingAt.getValue() : null;
        Date bookOutAt = "Booked".equals(status) ? (Date) spBookOutAt.getValue() : null;

        Room target = findRoomByNo(roomNoVal);
        if (target != null) {
            target.setName(name);
            target.setType(type);
            target.setStatus(status);
            target.setPrice(price);
            target.setPaymentMethod(paymentMethod);
            target.setGuestName(guestName);
            target.setBookedAt(bookedAt);
            target.setBookOutAt(bookOutAt);
            target.setNights(nights);
            target.setDiscount(discount);
            target.setTotalPayment(total);
            target.setGuestCount(guestCount);
        } else {
            Room room = new Room(roomNoVal, name, type, status, price, paymentMethod, bookedAt);
            room.setBookOutAt(bookOutAt);
            room.setGuestName(guestName);
            room.setNights(nights);
            room.setDiscount(discount);
            room.setTotalPayment(total);
            room.setGuestCount(guestCount);
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
        String input = (String) JOptionPane.showInputDialog(this, "Enter Room No to edit:", "Edit Which Room", JOptionPane.QUESTION_MESSAGE, null, null, prefill != null ? prefill.toString() : "");
        if (input == null) return;
        int targetNo;
        try {
            targetNo = Integer.parseInt(input.trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Invalid Room No");
            return;
        }

        Room room = findRoomByNo(targetNo);
        if (room == null) {
            JOptionPane.showMessageDialog(this, "Room No " + targetNo + " not found");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this, "Edit room " + targetNo + " (" + room.getName() + ")?", "Confirm Edit", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        room.setName(txtName.getText());
        room.setType(cbCategory.getSelectedItem().toString());
        room.setStatus(cbStatus.getSelectedItem().toString());
        room.setPrice(txtPrice.getText());
        room.setPaymentMethod(cbPaymentMethod.getSelectedItem().toString());
        room.setGuestName(txtGuest.getText().trim());
        try {
            room.setNights((Integer) cbNights.getSelectedItem());
            room.setDiscount(Double.parseDouble(txtDiscount.getText()));
            room.setGuestCount((Integer) cbGuestCount.getSelectedItem());
            calculateTotal();
            room.setTotalPayment(Double.parseDouble(txtTotalPayment.getText()));
        } catch (NumberFormatException ignored) {}

        try {
            spBookingAt.commitEdit();
            spBookOutAt.commitEdit();
        } catch (ParseException ignored) {}
        Date bookedAt = "Booked".equals(cbStatus.getSelectedItem().toString()) ? (Date) spBookingAt.getValue() : null;
        Date bookOutAt = "Booked".equals(cbStatus.getSelectedItem().toString()) ? (Date) spBookOutAt.getValue() : null;
        room.setBookedAt(bookedAt);
        room.setBookOutAt(bookOutAt);

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
            String input = (String) JOptionPane.showInputDialog(this, "Enter Room No to delete:", "Delete Which Room", JOptionPane.QUESTION_MESSAGE, null, null, "");
            if (input == null) return;
            try {
                targetNo = Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Room No");
                return;
            }
        }

        Room room = findRoomByNo(targetNo);
        if (room == null) {
            JOptionPane.showMessageDialog(this, "Room No " + targetNo + " not found");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this, "Delete room " + targetNo + " (" + room.getName() + ")? This removes it from inventory.", "Confirm Delete", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        rooms.remove(room);
        updateTable();
        clearFields();
        JOptionPane.showMessageDialog(this, "Room " + targetNo + " deleted");
    }

    private void bookOutRoom() {
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
            String input = (String) JOptionPane.showInputDialog(this, "Enter Room No to Book Out:", "Book Out Which Room", JOptionPane.QUESTION_MESSAGE, null, null, "");
            if (input == null) return;
            try {
                targetNo = Integer.parseInt(input.trim());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this, "Invalid Room No");
                return;
            }
        }

        Room room = findRoomByNo(targetNo);
        if (room == null) {
            JOptionPane.showMessageDialog(this, "Room No " + targetNo + " not found");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(this, "Book Out room " + targetNo + " (" + room.getName() + ")? This clears guest data.", "Confirm Book Out", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        emptyRoom(room);
        updateTable();
        clearFields();
        JOptionPane.showMessageDialog(this, "Room " + targetNo + " booked out");
    }

    private void filterRooms() {
        String typeFilter = cbFilterType.getSelectedItem().toString();
        String statusFilter = cbFilterStatus.getSelectedItem().toString();
        String paymentFilter = cbFilterPayment.getSelectedItem().toString();

        model.setRowCount(0);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int totalGuestsIn = 0;

        for (Room room : rooms) {
            boolean visible = true;
            if (!typeFilter.equals("All") && !room.getType().equals(typeFilter)) visible = false;
            if (!statusFilter.equals("All") && !room.getStatus().equals(statusFilter)) visible = false;
            if (!paymentFilter.equals("All") && !room.getPaymentMethod().equals(paymentFilter)) visible = false;

            if (visible) {
                if ("Booked".equals(room.getStatus())) totalGuestsIn += room.getGuestCount();
                String bookedStr = room.getBookedAt() != null ? fmt.format(room.getBookedAt()) : "";
                String outStr = room.getBookOutAt() != null ? fmt.format(room.getBookOutAt()) : "";
                model.addRow(new Object[]{ room.getRoomNo(), room.getName(), room.getGuestName(), room.getType(), room.getStatus(), room.getPrice(), room.getNights(), room.getDiscount(), room.getTotalPayment(), room.getGuestCount(), room.getPaymentMethod(), bookedStr, outStr });
            }
        }
        lblTotalGuests.setText(" | Total Guests In: " + totalGuestsIn);
    }

    private void updateTable() {
        model.setRowCount(0);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        int totalGuestsIn = 0;
        for (Room room : rooms) {
            if ("Booked".equals(room.getStatus())) totalGuestsIn += room.getGuestCount();
            String bookedStr = room.getBookedAt() != null ? fmt.format(room.getBookedAt()) : "";
            String outStr = room.getBookOutAt() != null ? fmt.format(room.getBookOutAt()) : "";
            model.addRow(new Object[]{ room.getRoomNo(), room.getName(), room.getGuestName(), room.getType(), room.getStatus(), room.getPrice(), room.getNights(), room.getDiscount(), room.getTotalPayment(), room.getGuestCount(), room.getPaymentMethod(), bookedStr, outStr });
        }
        lblTotalGuests.setText(" | Total Guests In: " + totalGuestsIn);
    }

    private void clearFields() {
        cbRoomNo.setSelectedIndex(-1);
        txtName.setText("");
        txtPrice.setText("");
        txtGuest.setText("");
        cbNights.setSelectedIndex(0);
        txtDiscount.setText("0");
        txtTotalPayment.setText("0.00");
        cbGuestCount.setSelectedIndex(0);
        cbCategory.setSelectedIndex(0);
        updateRoomNoOptions();
        updateGuestCountOptions();
        cbStatus.setSelectedIndex(0);
        cbPaymentMethod.setSelectedIndex(0);
        spBookingAt.setValue(new Date());
        spBookingAt.setEnabled(false);
        spBookOutAt.setValue(new Date());
        spBookOutAt.setEnabled(false);
        currentSelected = null;
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

    private void updateRoomNoOptions() {
        cbRoomNo.removeAllItems();
        Object sel = cbCategory.getSelectedItem();
        String type = sel == null ? "" : sel.toString();
        int[] range = rangeForType(type);
        for (int i = range[0]; i <= range[1]; i++) cbRoomNo.addItem(i);
        cbRoomNo.setSelectedIndex(-1);
    }

    private int indexForTypeRoomNo(String type, int roomNo) {
        int[] r = rangeForType(type);
        return roomNo - r[0] + 1;
    }

    private void emptyRoom(Room room) {
        room.setGuestName("");
        room.setBookedAt(null);
        room.setBookOutAt(null);
        room.setStatus("Free");
        room.setPaymentMethod("Cash");
        room.setPrice(defaultPriceForType(room.getType()));
        room.setNights(1);
        room.setDiscount(0);
        room.setGuestCount(0);
        room.setTotalPayment(0);
        int idx = indexForTypeRoomNo(room.getType(), room.getRoomNo());
        room.setName(baseForType(room.getType()) + " " + idx);
    }

    private void signOut() {
        setSignedIn(false);
        setCurrentUser(null);
        updateAuthUI();
        clearFields();
        String user = LoginDialog.show(this, users);
        if (user != null) {
            setSignedIn(true);
            setCurrentUser(user);
            updateAuthUI();
        } else {
            System.exit(0);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            HotelReservationSystem system = new HotelReservationSystem();
            String user = LoginDialog.show(system, system.users);
            if (user != null) {
                system.setSignedIn(true);
                system.setCurrentUser(user);
                system.updateAuthUI();
                system.setVisible(true);
            } else {
                System.exit(0);
            }
        });
    }
}