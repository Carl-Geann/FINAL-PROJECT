import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Kini nga class nga GuestProfilePanel kay para sa pagpakita ug pag-edit sa profile sa bisita.
 * Kini nagpakita sa mga detalye sa booking sama sa oras sa check-in/out, kwarto, ug bayad.
 */
public class GuestProfilePanel extends JPanel {
    private JTextField txtGuestName, txtRoomName, txtGuestCount, txtPaymentMethod;
    private JTextField txtBookInTime, txtBookOutTime, txtTotalAmount;
    private JButton btnUpdate, btnBookOut;
    private JList<Room> guestList;
    private DefaultListModel<Room> listModel;
    
    private HotelReservationSystem system;
    private HotelManager hotelManager;
    private RoomFormHandler formHandler;
    private Room currentRoom;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private boolean isLoading = false;

    public GuestProfilePanel(HotelReservationSystem system, HotelManager hotelManager, RoomFormHandler formHandler) {
        this.system = system;
        this.hotelManager = hotelManager;
        this.formHandler = formHandler;
        
        setLayout(new BorderLayout());
        setBackground(new Color(150, 0, 0)); // Theme Red
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Trade Gothic Fonts
        Font fontHeader = new Font("Trade Gothic", Font.BOLD, 22);
        if (fontHeader.getFamily().equals("Dialog")) fontHeader = new Font("Arial", Font.BOLD, 22);
        
        Font fontLabel = new Font("Trade Gothic", Font.BOLD, 14);
        if (fontLabel.getFamily().equals("Dialog")) fontLabel = new Font("Arial", Font.BOLD, 14);
        
        Font fontField = new Font("Trade Gothic", Font.PLAIN, 14);
        if (fontField.getFamily().equals("Dialog")) fontField = new Font("Arial", Font.PLAIN, 14);
        
        Font fontButton = new Font("Trade Gothic", Font.BOLD, 14);
        if (fontButton.getFamily().equals("Dialog")) fontButton = new Font("Arial", Font.BOLD, 14);

        // Header
        JLabel lblHeader = new JLabel("GUEST PROFILE");
        lblHeader.setFont(fontHeader);
        lblHeader.setForeground(Color.YELLOW);
        lblHeader.setHorizontalAlignment(SwingConstants.CENTER);
        lblHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        // Main Container Panel (White box)
        JPanel mainContainer = new JPanel(new BorderLayout(0, 10));
        mainContainer.setBackground(new Color(255, 255, 240)); // lightYellow
        mainContainer.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(Color.YELLOW, 2),
            BorderFactory.createEmptyBorder(10, 10, 10, 10)
        ));

        // Guest List Section
        listModel = new DefaultListModel<>();
        guestList = new JList<>(listModel);
        guestList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        guestList.setFont(fontField);
        guestList.setForeground(new Color(150, 0, 0));
        guestList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Room) {
                    Room r = (Room) value;
                    label.setText(r.getGuestName() + " - " + r.getName() + " (" + r.getType() + ")");
                    label.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
                }
                return label;
            }
        });

        JScrollPane scrollPane = new JScrollPane(guestList);
        scrollPane.setPreferredSize(new Dimension(0, 180)); // Increased height slightly
        scrollPane.setBorder(BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(150, 0, 0)), 
            "Currently Booked Guests", 
            0, 0, fontLabel.deriveFont(12f), new Color(150, 0, 0)
        ));
        mainContainer.add(scrollPane, BorderLayout.NORTH);

        // Form Panel
        JPanel formPanel = new JPanel(new GridBagLayout());
        formPanel.setBackground(new Color(255, 255, 240)); 

        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.anchor = GridBagConstraints.WEST;

        Color themeRed = new Color(150, 0, 0);

        // Fields
        txtGuestName = createField(fontField, themeRed);
        txtRoomName = createField(fontField, themeRed);
        txtRoomName.setEditable(false);
        txtGuestCount = createField(fontField, themeRed);
        txtPaymentMethod = createField(fontField, themeRed);
        txtBookInTime = createField(fontField, themeRed);
        txtBookInTime.setEditable(false);
        txtBookOutTime = createField(fontField, themeRed);
        txtBookOutTime.setEditable(false);
        txtTotalAmount = createField(fontField, themeRed);
        txtTotalAmount.setEditable(false);

        addLabelAndField(formPanel, "Guest Name:", txtGuestName, fontLabel, themeRed, gbc, 0);
        addLabelAndField(formPanel, "Room:", txtRoomName, fontLabel, themeRed, gbc, 1);
        addLabelAndField(formPanel, "Number of Guests:", txtGuestCount, fontLabel, themeRed, gbc, 2);
        addLabelAndField(formPanel, "Payment Method:", txtPaymentMethod, fontLabel, themeRed, gbc, 3);
        addLabelAndField(formPanel, "Book In Time:", txtBookInTime, fontLabel, themeRed, gbc, 4);
        addLabelAndField(formPanel, "Book Out Time:", txtBookOutTime, fontLabel, themeRed, gbc, 5);
        addLabelAndField(formPanel, "Total Amount:", txtTotalAmount, fontLabel, themeRed, gbc, 6);

        mainContainer.add(formPanel, BorderLayout.CENTER);
        add(mainContainer, BorderLayout.CENTER);

        // Button Panel
        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
        btnPanel.setBackground(new Color(150, 0, 0));

        btnUpdate = new JButton("Update Profile");
        btnBookOut = new JButton("Book Out Room");

        styleButton(btnUpdate, fontButton);
        styleButton(btnBookOut, fontButton);

        btnUpdate.addActionListener(e -> updateProfile());
        btnBookOut.addActionListener(e -> bookOut());

        guestList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !isLoading) {
                Room selected = guestList.getSelectedValue();
                if (selected != null) {
                    loadGuestData(selected);
                }
            }
        });

        btnPanel.add(btnUpdate);
        btnPanel.add(btnBookOut);
        add(btnPanel, BorderLayout.SOUTH);
    }

    private JTextField createField(Font font, Color color) {
        JTextField field = new JTextField(20);
        field.setFont(font);
        field.setForeground(color);
        field.setBackground(Color.WHITE);
        field.setBorder(BorderFactory.createLineBorder(color, 1));
        return field;
    }

    private void addLabelAndField(JPanel p, String text, JTextField f, Font font, Color color, GridBagConstraints gbc, int row) {
        gbc.gridy = row;
        gbc.gridx = 0;
        gbc.weightx = 0.3;
        JLabel lbl = new JLabel(text);
        lbl.setFont(font);
        lbl.setForeground(color);
        p.add(lbl, gbc);

        gbc.gridx = 1;
        gbc.weightx = 0.7;
        p.add(f, gbc);
    }

    private void styleButton(JButton btn, Font font) {
        btn.setFont(font);
        btn.setBackground(Color.YELLOW);
        btn.setForeground(new Color(150, 0, 0));
        btn.setFocusPainted(false);
        btn.setPreferredSize(new Dimension(160, 40));
        btn.setBorder(BorderFactory.createLineBorder(new Color(150, 0, 0), 1));
    }

    public void loadGuestData(Room room) {
        isLoading = true;
        refreshGuestList();
        
        this.currentRoom = room;
        if (room != null && "Booked".equals(room.getStatus())) {
            // Select in list if not already selected
            if (guestList.getSelectedValue() != room) {
                guestList.setSelectedValue(room, true);
            }
            
            txtGuestName.setText(room.getGuestName());
            txtRoomName.setText(room.getName() + " (" + room.getType() + ")");
            txtGuestCount.setText(String.valueOf(room.getGuestCount()));
            txtPaymentMethod.setText(room.getPaymentMethod());
            txtBookInTime.setText(room.getBookedAt() != null ? sdf.format(room.getBookedAt()) : "N/A");
            txtBookOutTime.setText(room.getBookOutAt() != null ? sdf.format(room.getBookOutAt()) : "N/A");
            txtTotalAmount.setText(String.format("%.2f", room.getTotal()));
            
            btnUpdate.setEnabled(true);
            btnBookOut.setEnabled(true);
        } else {
            if (room == null && !listModel.isEmpty()) {
                guestList.setSelectedIndex(0);
                isLoading = false; // Need to allow the recursive call to loadGuestData(selected)
                loadGuestData(guestList.getSelectedValue());
                return;
            } else {
                clearFields();
                btnUpdate.setEnabled(false);
                btnBookOut.setEnabled(false);
            }
        }
        isLoading = false;
    }

    public void refreshGuestList() {
        Room selected = guestList.getSelectedValue();
        listModel.clear();
        for (Room r : hotelManager.getBookedRooms()) {
            listModel.addElement(r);
        }
        if (selected != null && "Booked".equals(selected.getStatus())) {
            guestList.setSelectedValue(selected, true);
        }
    }

    private void clearFields() {
        txtGuestName.setText("");
        txtRoomName.setText("");
        txtGuestCount.setText("");
        txtPaymentMethod.setText("");
        txtBookInTime.setText("");
        txtBookOutTime.setText("");
        txtTotalAmount.setText("");
    }

    private void updateProfile() {
        if (currentRoom == null) return;
        
        try {
            String newName = txtGuestName.getText().trim();
            int newCount = Integer.parseInt(txtGuestCount.getText().trim());
            String newPayment = txtPaymentMethod.getText().trim();

            if (newName.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Palihug butangi og ngalan ang bisita.");
                return;
            }

            currentRoom.setGuestName(newName);
            currentRoom.setGuestCount(newCount);
            currentRoom.setPaymentMethod(newPayment);
            
            // Recalculate total if needed (using default manager logic)
            double price = Double.parseDouble(currentRoom.getPrice());
            double total = hotelManager.calculateTotal(price, currentRoom.getNights(), currentRoom.getDiscount(), newCount);
            currentRoom.setTotal(total);
            
            system.roomUpdateTable();
            loadGuestData(currentRoom);
            JOptionPane.showMessageDialog(this, "Guest Profile updated successfully!");
        } catch (NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "Sayop ang format sa gidaghanon sa bisita.");
        }
    }

    private void bookOut() {
        if (currentRoom == null) return;
        
        int ok = JOptionPane.showConfirmDialog(this, 
            "Sigurado ka nga mo-Book Out kini nga kwarto: " + currentRoom.getName() + "?", 
            "Confirm Book Out", JOptionPane.YES_NO_OPTION);
            
        if (ok == JOptionPane.YES_OPTION) {
            hotelManager.bookOutRoom(currentRoom);
            system.roomUpdateTable();
            formHandler.clearFields();
            clearFields();
            JOptionPane.showMessageDialog(this, "Room has been booked out successfully.");
            system.showRoomDetails(); // Switch back to details
        }
    }
}
