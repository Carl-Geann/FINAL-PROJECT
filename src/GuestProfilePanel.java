import javax.swing.*;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.swing.DefaultListCellRenderer;
import java.awt.Component;

/**
 * Kini nga class nga GuestProfilePanel kay para sa pagpakita ug pag-edit sa profile sa bisita.
 * Kini nagpakita sa mga detalye sa booking sama sa oras sa check-in/out, kwarto, ug bayad.
 */
public class GuestProfilePanel extends JPanel {
    private JTextField txtGuestName, txtRoomName, txtGuestCount, txtPaymentMethod, txtBookInTime, txtBookOutTime, txtTotalAmount;
    private JButton btnUpdate, btnBookOut;
    private JList<Room> guestList;
    private DefaultListModel<Room> listModel;
    private HotelReservationSystem system;
    private HotelManager hotelManager;
    private RoomFormHandler formHandler;
    private Room currentRoom;
    private SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
    private boolean isLoading = false;
    private final Color THEME_RED = new Color(150, 0, 0), LIGHT_YELLOW = new Color(255, 255, 240);

    public GuestProfilePanel(HotelReservationSystem system, HotelManager hotelManager, RoomFormHandler formHandler) {
        this.system = system; this.hotelManager = hotelManager; this.formHandler = formHandler;
        initComponents(); initLayout(); initListeners();
    }

    private Font getFont(int size) {
        Font f = new Font("Trade Gothic", Font.BOLD, size);
        return f.getFamily().equals("Dialog") ? new Font("Arial", Font.BOLD, size) : f;
    }

    private void initComponents() {
        Font fontField = getFont(16);
        listModel = new DefaultListModel<>();
        guestList = new JList<>(listModel);
        guestList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        guestList.setFont(fontField); guestList.setForeground(THEME_RED); guestList.setFixedCellHeight(35);
        guestList.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                JLabel label = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value instanceof Room) {
                    Room r = (Room) value;
                    label.setText(" " + (r.getGuestName() != null ? r.getGuestName().toUpperCase() : "") + " - " + r.getName());
                    label.setFont(fontField);
                }
                label.setBackground(isSelected ? THEME_RED : Color.WHITE);
                label.setForeground(isSelected ? Color.YELLOW : THEME_RED);
                return label;
            }
        });

        txtGuestName = createField(fontField);
        txtRoomName = createField(fontField); txtRoomName.setEditable(false);
        txtGuestCount = createField(fontField);
        txtPaymentMethod = createField(fontField);
        txtBookInTime = createField(fontField); txtBookInTime.setEditable(false);
        txtBookOutTime = createField(fontField); txtBookOutTime.setEditable(false);
        txtTotalAmount = createField(fontField); txtTotalAmount.setEditable(false);

        btnUpdate = styleButton(new JButton("Update Profile"), getFont(16));
        btnBookOut = styleButton(new JButton("Book Out Room"), getFont(16));
    }

    private void initLayout() {
        setLayout(new BorderLayout()); setBackground(THEME_RED); setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JLabel lblHeader = new JLabel("GUEST PROFILE", SwingConstants.CENTER);
        lblHeader.setFont(getFont(28)); lblHeader.setForeground(Color.YELLOW); lblHeader.setBorder(BorderFactory.createEmptyBorder(0, 0, 10, 0));
        add(lblHeader, BorderLayout.NORTH);

        JPanel mainContainer = new JPanel(new BorderLayout(0, 10));
        mainContainer.setBackground(LIGHT_YELLOW);
        mainContainer.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(Color.YELLOW, 2), BorderFactory.createEmptyBorder(10, 10, 10, 10)));

        JScrollPane scrollPane = new JScrollPane(guestList);
        scrollPane.setPreferredSize(new Dimension(0, 180));
        scrollPane.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(THEME_RED), "Currently Booked Guests", 0, 0, getFont(16).deriveFont(14f), THEME_RED));
        mainContainer.add(scrollPane, BorderLayout.NORTH);

        JPanel formPanel = new JPanel(new GridBagLayout()); formPanel.setBackground(LIGHT_YELLOW);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5); gbc.fill = GridBagConstraints.HORIZONTAL; gbc.anchor = GridBagConstraints.WEST;

        String[] labels = {"Guest Name : ", "Room : ", "Number of Guests : ", "Payment Method : ", "Book In Time : ", "Book Out Time : ", "Total Amount : "};
        JTextField[] fields = {txtGuestName, txtRoomName, txtGuestCount, txtPaymentMethod, txtBookInTime, txtBookOutTime, txtTotalAmount};
        for (int i = 0; i < labels.length; i++) addLabelAndField(formPanel, labels[i], fields[i], gbc, i);

        mainContainer.add(formPanel, BorderLayout.CENTER); add(mainContainer, BorderLayout.CENTER);

        JPanel btnPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10)); btnPanel.setBackground(THEME_RED);
        btnPanel.add(btnUpdate); btnPanel.add(btnBookOut); add(btnPanel, BorderLayout.SOUTH);
    }

    private void initListeners() {
        btnUpdate.addActionListener(e -> updateProfile());
        btnBookOut.addActionListener(e -> bookOut());
        guestList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && !isLoading && guestList.getSelectedValue() != null) loadGuestData(guestList.getSelectedValue());
        });
    }

    private JTextField createField(Font font) {
        JTextField f = new JTextField(20); f.setFont(font); f.setForeground(THEME_RED); f.setBackground(Color.WHITE);
        f.setBorder(BorderFactory.createCompoundBorder(BorderFactory.createLineBorder(THEME_RED, 2), BorderFactory.createEmptyBorder(5, 10, 5, 10)));
        f.setPreferredSize(new Dimension(300, 45));
        return f;
    }

    private void addLabelAndField(JPanel p, String text, JTextField f, GridBagConstraints gbc, int row) {
        gbc.gridy = row; gbc.gridx = 0; gbc.weightx = 0.3;
        JLabel lbl = new JLabel(text); lbl.setFont(getFont(16)); lbl.setForeground(THEME_RED);
        p.add(lbl, gbc);
        gbc.gridx = 1; gbc.weightx = 0.7; p.add(f, gbc);
    }

    private JButton styleButton(JButton btn, Font font) {
        btn.setFont(font); btn.setBackground(Color.YELLOW); btn.setForeground(THEME_RED);
        btn.setFocusPainted(false); btn.setPreferredSize(new Dimension(160, 40));
        btn.setBorder(BorderFactory.createLineBorder(THEME_RED, 1));
        return btn;
    }

    public void loadGuestData(Room room) {
        if (isLoading) return;
        isLoading = true; refreshGuestList(); this.currentRoom = room;
        if (room != null && "Booked".equals(room.getStatus())) {
            if (guestList.getSelectedValue() != room) guestList.setSelectedValue(room, true);
            txtGuestName.setText(room.getGuestName()); txtRoomName.setText(room.getName() + " (" + room.getType() + ")");
            txtGuestCount.setText(String.valueOf(room.getGuestCount())); txtPaymentMethod.setText(room.getPaymentMethod());
            txtBookInTime.setText(room.getBookedAt() != null ? sdf.format(room.getBookedAt()) : "N/A");
            txtBookOutTime.setText(room.getBookOutAt() != null ? sdf.format(room.getBookOutAt()) : "N/A");
            txtTotalAmount.setText("P " + String.format("%,.2f", room.getTotal()));
            btnUpdate.setEnabled(true); btnBookOut.setEnabled(true);
        } else {
            if (room == null && !listModel.isEmpty()) {
                guestList.setSelectedIndex(0);
                if (guestList.getSelectedValue() != null) { isLoading = false; loadGuestData(guestList.getSelectedValue()); return; }
            }
            clearFields(); btnUpdate.setEnabled(false); btnBookOut.setEnabled(false);
        }
        isLoading = false;
    }

    public void refreshGuestList() {
        Room selected = guestList.getSelectedValue(); listModel.clear();
        for (Room r : hotelManager.getBookedRooms()) listModel.addElement(r);
        if (selected != null && "Booked".equals(selected.getStatus())) guestList.setSelectedValue(selected, true);
    }

    private void clearFields() {
        txtGuestName.setText(""); txtRoomName.setText(""); txtGuestCount.setText("");
        txtPaymentMethod.setText(""); txtBookInTime.setText(""); txtBookOutTime.setText(""); txtTotalAmount.setText("");
    }

    private void updateProfile() {
        if (currentRoom == null) return;
        try {
            String newName = txtGuestName.getText().trim();
            if (newName.isEmpty()) { JOptionPane.showMessageDialog(this, "Kindly input the valid guestname."); return; }
            currentRoom.setGuestName(newName);
            currentRoom.setGuestCount(Integer.parseInt(txtGuestCount.getText().trim()));
            currentRoom.setPaymentMethod(txtPaymentMethod.getText().trim());
            currentRoom.setTotal(hotelManager.calculateTotal(Double.parseDouble(currentRoom.getPrice()), currentRoom.getNights(), currentRoom.getDiscount(), currentRoom.getGuestCount()));
            system.roomUpdateTable(); loadGuestData(currentRoom);
            JOptionPane.showMessageDialog(this, "Guest Profile updated successfully!");
        } catch (NumberFormatException e) { JOptionPane.showMessageDialog(this, "Invalid guest name !."); }
    }

    private void bookOut() {
        if (currentRoom == null) return;
        if (JOptionPane.showConfirmDialog(this, "Are you sure you gotta book this : " + currentRoom.getName() + "?", "Confirm Book Out", JOptionPane.YES_NO_OPTION) == JOptionPane.YES_OPTION) {
            hotelManager.bookOutRoom(currentRoom); system.roomUpdateTable(); formHandler.clearFields(); clearFields();
            JOptionPane.showMessageDialog(this, "Room has been booked out successfully."); system.showRoomDetails();
        }
    }
}
