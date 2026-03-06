import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class HotelReservationSystem extends JFrame {

    private JTextField txtName, txtPrice;
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
        JPanel headerButtons = new JPanel(new GridLayout(1, 2, 8, 0));
        headerButtons.add(btnRoomDetails);
        headerButtons.add(btnReservationList);

        detailsPanel = new JPanel(new GridLayout(12, 1, 5, 5));

        txtName = new JTextField();
        txtPrice = new JTextField();

        cbCategory = new JComboBox<>(new String[]{"VIP", "Double Bed", "Family"});
        cbStatus = new JComboBox<>(new String[]{"Free", "Booked"});
        cbPaymentMethod = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash", "Online Transfer"});

        spBookingAt = new JSpinner(new SpinnerDateModel(new Date(), null, null, java.util.Calendar.MINUTE));
        spBookingAt.setEditor(new JSpinner.DateEditor(spBookingAt, "yyyy-MM-dd HH:mm"));

        detailsPanel.add(new JLabel("Name"));
        detailsPanel.add(txtName);
        detailsPanel.add(new JLabel("Category"));
        detailsPanel.add(cbCategory);
        detailsPanel.add(new JLabel("Status"));
        detailsPanel.add(cbStatus);
        detailsPanel.add(new JLabel("Price"));
        detailsPanel.add(txtPrice);
        detailsPanel.add(new JLabel("Payment Method"));
        detailsPanel.add(cbPaymentMethod);
        detailsPanel.add(new JLabel("Booking Date/Time"));
        detailsPanel.add(spBookingAt);

        actionsPanel = new JPanel(new GridBagLayout());
        GridBagConstraints agc = new GridBagConstraints();
        agc.insets = new Insets(4, 0, 4, 0);
        agc.fill = GridBagConstraints.HORIZONTAL;

        btnAdd = new JButton("Add");
        btnEdit = new JButton("Edit");
        btnDelete = new JButton("Delete");

        JPanel row1 = new JPanel(new GridLayout(1, 2, 8, 0));
        row1.add(btnAdd);
        row1.add(btnEdit);

        agc.gridx = 0; agc.gridy = 0;
        actionsPanel.add(row1, agc);

        agc.gridx = 0; agc.gridy = 1;
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

        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(cbFilterType);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(cbFilterStatus);
        filterPanel.add(new JLabel("Payment:"));
        filterPanel.add(cbFilterPayment);
        filterPanel.add(btnRefresh);

        String[] columns = {"Room No", "Room Name", "Type", "Status", "Price", "Payment Method", "Booked At"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(16);
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
            if (booked) spBookingAt.setValue(new Date());
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
        updateTable();
        updateAuthUI();
        setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
    }

    public void setUsers(java.util.Map<String, String> creds) {
        users.clear();
        users.putAll(creds);
    }

    public void setSignedIn(boolean v) { signedIn = v; }

    public void setCurrentUser(String u) { currentUser = u; }

    public void updateAuthUI() {
        boolean enabled = signedIn;
        txtName.setEnabled(enabled);
        txtPrice.setEnabled(enabled);
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

    private void refreshMapIfVisible() {
        if ("map".equals(currentRightView)) {
            mapPanel = buildMapPanel();
            rightCards.add(mapPanel, "map");
            ((CardLayout) rightCards.getLayout()).show(rightCards, "map");
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

    private JPanel buildMapPanel() {
        JPanel overlay = new JPanel(new GridBagLayout());
        overlay.setBackground(Color.WHITE);
        GridBagConstraints g = new GridBagConstraints();
        g.insets = new Insets(10, 10, 10, 10);
        g.gridx = 0; g.gridy = 0; overlay.add(buildRow("VIP", "VIP"), g);
        g.gridy = 1; overlay.add(buildRow("Family", "Family"), g);
        g.gridy = 2; overlay.add(buildRow("Double Bed", "Double"), g);
        return overlay;
    }

    private JPanel buildRow(String type, String base) {
        JPanel row = new JPanel(new BorderLayout());
        JLabel lbl = new JLabel(type);
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 6, 0));
        row.add(lbl, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(1, 10, 8, 8));
        for (int i = 1; i <= 10; i++) {
            String label = base + " " + i;
            Room r = findRoomByName(label);
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
        txtName.setText(r.getName());
        cbCategory.setSelectedItem(r.getType());
        cbStatus.setSelectedItem(r.getStatus());
        txtPrice.setText(r.getPrice());
        cbPaymentMethod.setSelectedItem(r.getPaymentMethod());
        if (r.getBookedAt() != null) spBookingAt.setValue(r.getBookedAt());
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
            setExtendedState(getExtendedState() | JFrame.MAXIMIZED_BOTH);
            setVisible(true);
            JOptionPane.showMessageDialog(this, "Signed in as " + email);
        } else {
            System.exit(0);
        }
    }

    private Room findRoomByNo(int roomNo) {
        for (Room r : rooms) if (r.getRoomNo() == roomNo) return r;
        return null;
    }

    private Room findRoomByName(String name) {
        for (Room r : rooms) if (r.getName().equals(name)) return r;
        return null;
    }

    private int nextRoomNo() {
        int max = 0;
        for (Room r : rooms) if (r.getRoomNo() > max) max = r.getRoomNo();
        return max + 1;
    }

    private void addRoom() {
        String name = txtName.getText();
        String type = cbCategory.getSelectedItem().toString();
        String status = cbStatus.getSelectedItem().toString();
        String price = txtPrice.getText();
        String paymentMethod = cbPaymentMethod.getSelectedItem().toString();

        if (name.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try { spBookingAt.commitEdit(); } catch (ParseException ignored) {}
        Date bookedAt = "Booked".equals(status) ? (Date) spBookingAt.getValue() : null;

        Room room = new Room(nextRoomNo(), name, type, status, price, paymentMethod, bookedAt);
        rooms.add(room);
        updateTable();
        clearFields();
    }

    private void editRoom() {
        Room room = null;
        int viewRow = table.getSelectedRow();
        if (viewRow != -1) {
            int roomNo = Integer.parseInt(table.getValueAt(viewRow, 0).toString());
            room = findRoomByNo(roomNo);
        } else if (currentSelected != null) {
            room = currentSelected;
        } else {
            Room byName = findRoomByName(txtName.getText().trim());
            if (byName != null) room = byName;
        }

        if (room == null) {
            JOptionPane.showMessageDialog(this, "Select a room from table or map.");
            return;
        }

        room.setName(txtName.getText());
        room.setType(cbCategory.getSelectedItem().toString());
        room.setStatus(cbStatus.getSelectedItem().toString());
        room.setPrice(txtPrice.getText());
        room.setPaymentMethod(cbPaymentMethod.getSelectedItem().toString());

        try { spBookingAt.commitEdit(); } catch (ParseException ignored) {}
        Date bookedAt = "Booked".equals(cbStatus.getSelectedItem().toString()) ? (Date) spBookingAt.getValue() : null;
        room.setBookedAt(bookedAt);

        updateTable();
    }

    private void deleteRoom() {
        int viewRow = table.getSelectedRow();
        if (viewRow == -1) {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
            return;
        }
        int roomNo = Integer.parseInt(table.getValueAt(viewRow, 0).toString());
        Room target = findRoomByNo(roomNo);
        if (target != null) {
            rooms.remove(target);
            updateTable();
        } else {
            JOptionPane.showMessageDialog(this, "Room not found.");
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
                model.addRow(new Object[]{room.getRoomNo(), room.getName(), room.getType(),
                        room.getStatus(), room.getPrice(), room.getPaymentMethod(), bookedStr});
            }
        }
    }

    private void updateTable() {
        model.setRowCount(0);
        SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Room room : rooms) {
            String bookedStr = room.getBookedAt() != null ? fmt.format(room.getBookedAt()) : "";
            model.addRow(new Object[]{room.getRoomNo(), room.getName(), room.getType(),
                    room.getStatus(), room.getPrice(), room.getPaymentMethod(), bookedStr});
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtPrice.setText("");
        cbCategory.setSelectedIndex(0);
        cbStatus.setSelectedIndex(0);
        cbPaymentMethod.setSelectedIndex(0);
        spBookingAt.setValue(new Date());
        spBookingAt.setEnabled(false);
        currentSelected = null;
    }

    void setUsers(Map<String, String> creds) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
}