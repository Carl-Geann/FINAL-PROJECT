import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.DefaultListCellRenderer;
import java.awt.Component;

/**
 * UI panel responsible for displaying and editing guest profile information and booking details.
 * Displays comprehensive booking details including check-in/out times, room assignment, and payment status.
 */
public class GuestProfilePanel extends JPanel {
    // UI Components para sa Inputs
    private JTextField txtGuestName, txtRoomName, txtBookInTime, txtBookOutTime, txtTotalAmount, txtDiscount;
    private JComboBox<Integer> cbGuestCount, cbNights;
    private JComboBox<String> cbPaymentMethod;
    private JButton btnUpdate, btnBookOut;
    private JList<Room> guestList;
    private DefaultListModel<Room> listModel;
    
    // Logic Managers ug Data
    private HotelReservationSystem system;
    private HotelManager hotelManager;
    private RoomFormHandler formHandler;
    private Room currentRoom;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private boolean isLoading = false;
    private final Color THEME_RED = new Color(150, 0, 0), LIGHT_YELLOW = new Color(255, 255, 224);

    // admin[Method Group: Initialization
    /**
     * Constructor to initialize the guest profile panel with necessary managers and handlers.
     */
    public GuestProfilePanel(HotelReservationSystem system, HotelManager hotelManager, RoomFormHandler formHandler) {
        this.system = system; this.hotelManager = hotelManager; this.formHandler = formHandler;
        initComponents(); // Initializes all Swing UI components used in the panel
        initLayout();     // Sets up the visual layout and positioning of components within the panel
        initListeners();  // Attaches event listeners to handle user interactions
    }

    private Font getFont(int size) {
        Font f = new Font("Trade Gothic", Font.BOLD, size);
        return f.getFamily().equals("Dialog") ? new Font("Arial", Font.BOLD, size) : f;
    }

    private void initComponents() {
        Font fontField = getFont(14); 
        listModel = new DefaultListModel<>();
        guestList = new JList<>(listModel); 
        guestList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        guestList.setFont(fontField); guestList.setForeground(THEME_RED); guestList.setFixedCellHeight(30); 
        
        // Custom list cell renderer to format how guest information is displayed in the JList
        guestList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                label.setFont(fontField); // Sets the font to bold for items within the guest list
                if (value instanceof Room) {
                    Room r = (Room) value;
                    label.setText(" " + (r.getGuestName() != null ? r.getGuestName().toUpperCase() : "") + " - " + r.getName());
                }
                label.setBackground(isSelected ? THEME_RED : Color.WHITE);
                label.setForeground(isSelected ? Color.YELLOW : THEME_RED);
                return label;
            }
        });

        // Initializes and configures the text input fields for guest information
        txtGuestName = createField(fontField, 240);
        txtRoomName = createField(fontField, 240); txtRoomName.setEditable(false);
        
        // Initializes and populates the dropdown selection boxes (combo boxes)
        cbGuestCount = createComboBox(fontField, 240);
        cbPaymentMethod = createComboBox(fontField, 240);
        cbPaymentMethod.setModel(new DefaultComboBoxModel<>(new String[]{"Credit Card", "Debit Card", "Cash", "Online Transfer"}));
        cbNights = createComboBox(fontField, 240);
        for (int i = 1; i <= 7; i++) cbNights.addItem(i);

        // Initializes additional data fields like check-in/out times and totals
        txtBookInTime = createField(fontField, 240); txtBookInTime.setEditable(false);
        txtBookOutTime = createField(fontField, 240); txtBookOutTime.setEditable(false);
        txtDiscount = createField(fontField, 240); 
        txtTotalAmount = createField(fontField, 240); txtTotalAmount.setEditable(false);

        // Applies consistent visual styling to the action buttons
        btnUpdate = styleButton(new JButton("Update Profile"), getFont(14));
        btnBookOut = styleButton(new JButton("Book Out Room"), getFont(14));
    }

    private void initLayout() {
        setLayout(new BorderLayout()); setBackground(THEME_RED); setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        // Header label para sa panel
        JLabel lblHeader = new JLabel("GUEST PROFILE", SwingConstants.CENTER);
        lblHeader.setFont(getFont(22)); lblHeader.setForeground(Color.YELLOW); 
        lblHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 5, 0));
        add(lblHeader, BorderLayout.NORTH);

        // Main container using GridBagLayout to organize the guest list and the detail form
        JPanel mainContainer = new JPanel(new GridBagLayout());
        mainContainer.setBackground(LIGHT_YELLOW);
        mainContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.YELLOW, 2), 
            BorderFactory.createEmptyBorder(10, 10, 10, 10) 
        ));

        GridBagConstraints mg = new GridBagConstraints();
        mg.gridx = 0; mg.fill = GridBagConstraints.BOTH; mg.weightx = 1.0;

        // Scrollable pane to hold and display the list of currently booked guests
        JScrollPane scrollPane = new JScrollPane(guestList);
        scrollPane.setPreferredSize(new Dimension(450, 250)); 
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(THEME_RED, 2), "Currently Booked Guests", 0, 0, getFont(16), THEME_RED));
        mg.gridy = 0; mg.weighty = 0.5;
        mainContainer.add(scrollPane, mg);

        // Sub-panel containing the input form for viewing and editing specific guest details
        JPanel formPanel = new JPanel(new GridBagLayout()); 
        formPanel.setBackground(LIGHT_YELLOW);
        GridBagConstraints fg = new GridBagConstraints();
        fg.insets = new Insets(6, 5, 6, 5); 
        fg.fill = GridBagConstraints.NONE; 
        fg.anchor = GridBagConstraints.WEST;

        // Dynamically adds descriptive labels and their corresponding input components to the form
        String[] labels = {"Guest Name : ", "Room : ", "Number of Guests : ", "Payment Method : ", "Nights : ", "Book In Time : ", "Book Out Time : ", "Discount : ", "Total Amount : "};
        JComponent[] components = {txtGuestName, txtRoomName, cbGuestCount, cbPaymentMethod, cbNights, txtBookInTime, txtBookOutTime, txtDiscount, txtTotalAmount};
        for (int i = 0; i < labels.length; i++) addLabelAndComponent(formPanel, labels[i], components[i], fg, i);

        mg.gridy = 1; mg.weighty = 0.5;
        mg.insets = new Insets(15, 0, 0, 0); 
        mainContainer.add(formPanel, mg);

        add(mainContainer, BorderLayout.CENTER);

        // Action buttons sa ubos (Update ug Book Out)
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 5)); 
        btnPanel.setBackground(THEME_RED);
        btnPanel.add(btnUpdate); btnPanel.add(btnBookOut); 
        add(btnPanel, BorderLayout.SOUTH);
    }

    private void initListeners() {
        // Handles the action event for the update button to save changes to the guest profile
        btnUpdate.addActionListener(e -> updateProfile()); 
        
        // Handles the action event for the book-out button to process a guest's check-out
        btnBookOut.addActionListener(e -> bookOut());     
        
        // Handles selection changes in the guest list to load the corresponding data into the form
        guestList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !isLoading && guestList.getSelectedValue() != null) loadGuestData(guestList.getSelectedValue());
        });
        
        // Event listeners that trigger automatic updates to the total amount and check-out date
        cbNights.addActionListener(e -> updateBookOutDateAndTotal());
        cbGuestCount.addActionListener(e -> updateBookOutDateAndTotal());
        
        // Recalculates the total payment in real-time as the user types a discount value
        txtDiscount.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyReleased(java.awt.event.KeyEvent e) {
                updateBookOutDateAndTotal();
            }
        });
    }

    // UI 

    private JTextField createField(Font font, int width) {
        JTextField f = new JTextField(); f.setFont(font); f.setForeground(THEME_RED); f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED, 2), BorderFactory.createEmptyBorder(2, 8, 2, 8))); 
        f.setPreferredSize(new Dimension(width, 32)); 
        addFocusEffect(f);
        return f;
    }

    private <T> JComboBox<T> createComboBox(Font font, int width) {
        JComboBox<T> cb = new JComboBox<>();
        cb.setUI(new javax.swing.plaf.basic.BasicComboBoxUI()); 
        cb.setFont(font); cb.setForeground(THEME_RED); cb.setBackground(Color.WHITE);
        cb.setPreferredSize(new Dimension(width, 32)); 
        cb.setBorder(BorderFactory.createLineBorder(Color.RED, 2)); 
        addFocusEffect(cb);
        return cb;
    }

    private JButton styleButton(JButton btn, Font font) {
        btn.setFont(font); btn.setBackground(Color.YELLOW); btn.setForeground(THEME_RED);
        btn.setFocusPainted(false); btn.setPreferredSize(new Dimension(150, 35)); 
        btn.setBorder(BorderFactory.createLineBorder(THEME_RED, 1));
        return btn;
    }

    private void addLabelAndComponent(JPanel p, String text, JComponent gbc_comp, GridBagConstraints gbc, int row) {
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0.5; 
        gbc.anchor = GridBagConstraints.EAST;
        JLabel lbl = new JLabel(text); lbl.setFont(getFont(14)); lbl.setForeground(THEME_RED); 
        lbl.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 15)); 
        p.add(lbl, gbc);
        
        gbc.gridx = 1; gbc.weightx = 0.5; 
        gbc.anchor = GridBagConstraints.WEST;
        p.add(gbc_comp, gbc);
    }

    private void addFocusEffect(JComponent c) {
        c.addFocusListener(new java.awt.event.FocusAdapter() {
            @Override
            public void focusGained(java.awt.event.FocusEvent e) {
                if (c instanceof JComboBox) {
                    c.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                } else {
                    c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED, 2), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                }
            }
            @Override
            public void focusLost(java.awt.event.FocusEvent e) {
                if (c instanceof JComboBox) {
                    c.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                } else {
                    c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED, 2), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                }
            }
        });
        c.addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseEntered(java.awt.event.MouseEvent e) {
                if (!c.isFocusOwner()) {
                    if (c instanceof JComboBox) {
                        c.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                    } else {
                        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED, 2), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                    }
                }
            }
            @Override
            public void mouseExited(java.awt.event.MouseEvent e) {
                if (!c.isFocusOwner()) {
                    if (c instanceof JComboBox) {
                        c.setBorder(BorderFactory.createLineBorder(Color.RED, 2));
                    } else {
                        c.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.RED, 2), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
                    }
                }
            }
        });
    }

    // Profile Code

    /**
     * Loads the details of the selected guest from the list into the form fields.
     */
    public void loadGuestData(Room room) {
        if (isLoading) return;
        isLoading = true; 
        refreshGuestList(); 
        this.currentRoom = room;
        
        if (room != null && "Booked".equals(room.getStatus())) {
            if (guestList.getSelectedValue() != room) guestList.setSelectedValue(room, true);
            txtGuestName.setText(room.getGuestName()); 
            txtRoomName.setText(room.getName() + " (" + room.getType() + ")");
            
            updateGuestCountOptions(room.getType());
            cbGuestCount.setSelectedItem(room.getGuestCount());
            cbPaymentMethod.setSelectedItem(room.getPaymentMethod());
            cbNights.setSelectedItem(room.getNights());

            txtBookInTime.setText(room.getBookedAt() != null ? sdf.format(room.getBookedAt()) : "N/A");
            txtBookOutTime.setText(room.getBookOutAt() != null ? sdf.format(room.getBookOutAt()) : "N/A");
            txtDiscount.setText(String.valueOf(room.getDiscount())); 
            txtTotalAmount.setText("P " + String.format("%,.2f", room.getTotal()));
            btnUpdate.setEnabled(true); btnBookOut.setEnabled(true);
        } else {
            // No room selected or room is not booked - show empty form
            guestList.clearSelection();
            clearFields(); 
            btnUpdate.setEnabled(false); 
            btnBookOut.setEnabled(false);
        }
        isLoading = false;
    }

    private void updateGuestCountOptions(String type) {
        cbGuestCount.removeAllItems();
        int max = type.contains("VIP") ? 10 : type.contains("FAMILY") ? 7 : 2;
        for (int i = 1; i <= max; i++) cbGuestCount.addItem(i);
    }

    private void updateBookOutDateAndTotal() {
        if (currentRoom == null || isLoading) return;
        Integer nights = (Integer) cbNights.getSelectedItem();
        if (nights != null && currentRoom.getBookedAt() != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(currentRoom.getBookedAt());
            cal.add(java.util.Calendar.DAY_OF_MONTH, nights);
            txtBookOutTime.setText(sdf.format(cal.getTime()));
            
            double price = Double.parseDouble(currentRoom.getPrice());
            int guests = (Integer) cbGuestCount.getSelectedItem();
            double discount = 0;
            try { discount = Double.parseDouble(txtDiscount.getText().trim()); } catch (Exception ignored) {}
            
            double total = hotelManager.calculateTotal(price, nights, discount, guests);
            txtTotalAmount.setText("P " + String.format("%,.2f", total));
        }
    }

    public void refreshGuestList() {
        Room selected = guestList.getSelectedValue(); listModel.clear();
        for (Room r : hotelManager.getBookedRooms()) listModel.addElement(r);
        if (selected != null && "Booked".equals(selected.getStatus())) guestList.setSelectedValue(selected, true);
    }

    private void clearFields() {
        txtGuestName.setText(""); txtRoomName.setText(""); 
        cbGuestCount.setSelectedIndex(-1); cbPaymentMethod.setSelectedIndex(-1); cbNights.setSelectedIndex(0);
        txtBookInTime.setText(""); txtBookOutTime.setText(""); txtTotalAmount.setText("");
    }

    private void updateProfile() {
        if (currentRoom == null) return;
        try {
            String newName = txtGuestName.getText().trim();
            if (newName.isEmpty()) { JOptionPane.showMessageDialog(this, "Kindly input the valid guestname."); return; }
            currentRoom.setGuestName(newName);
            currentRoom.setGuestCount((Integer) cbGuestCount.getSelectedItem());
            currentRoom.setPaymentMethod((String) cbPaymentMethod.getSelectedItem());
            currentRoom.setNights((Integer) cbNights.getSelectedItem());
            
            double discount = 0;
            try { discount = Double.parseDouble(txtDiscount.getText().trim()); } catch (Exception ignored) {}
            currentRoom.setDiscount(discount);
            
            // Invokes the HotelManager's calculation logic to determine the final total payment
            double price = Double.parseDouble(currentRoom.getPrice());
            double total = hotelManager.calculateTotal(price, currentRoom.getNights(), currentRoom.getDiscount(), currentRoom.getGuestCount());
            currentRoom.setTotal(total);
            
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(currentRoom.getBookedAt());
            cal.add(java.util.Calendar.DAY_OF_MONTH, currentRoom.getNights());
            currentRoom.setBookOutAt(cal.getTime());

            system.roomUpdateTable(); loadGuestData(currentRoom);
            JOptionPane.showMessageDialog(this, "Guest Profile updated successfully!");
        } catch (Exception e) { JOptionPane.showMessageDialog(this, "Error updating profile: " + e.getMessage()); }
    }

    private void bookOut() {
        if (currentRoom == null) return;
        if (JOptionPane.showConfirmDialog(this, "Book Confirmed ? " + currentRoom.getName() + "?", "Confirm Book Out", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            hotelManager.bookOutRoom(currentRoom); 
            system.roomUpdateTable(); 
            formHandler.clearFields(); 
            clearFields(); 
            JOptionPane.showMessageDialog(this, "Booking out process completed !");
            system.showRoomDetails(); 
        }
    }
}
