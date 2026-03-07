import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Date;

public class AddButtonHandler implements java.awt.event.ActionListener {
    private final JComboBox<Integer> cbRoomNo;
    private final JTextField txtName, txtPrice, txtGuest;
    private final JComboBox<String> cbCategory, cbStatus, cbPaymentMethod;
    private final JSpinner spBookingAt;
    private final ArrayList<Room> rooms;
    private final DefaultTableModel model;
    private final Runnable refreshMapIfVisible;

    public AddButtonHandler(JComboBox<Integer> cbRoomNo,
                            JTextField txtName, JTextField txtPrice, JTextField txtGuest,
                            JComboBox<String> cbCategory, JComboBox<String> cbStatus, JComboBox<String> cbPaymentMethod,
                            JSpinner spBookingAt,
                            ArrayList<Room> rooms,
                            DefaultTableModel model,
                            Runnable refreshMapIfVisible) {
        this.cbRoomNo = cbRoomNo;
        this.txtName = txtName;
        this.txtPrice = txtPrice;
        this.txtGuest = txtGuest;
        this.cbCategory = cbCategory;
        this.cbStatus = cbStatus;
        this.cbPaymentMethod = cbPaymentMethod;
        this.spBookingAt = spBookingAt;
        this.rooms = rooms;
        this.model = model;
        this.refreshMapIfVisible = refreshMapIfVisible;
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        String name = txtName.getText();
        String type = cbCategory.getSelectedItem().toString();
        String status = cbStatus.getSelectedItem().toString();
        String price = txtPrice.getText();
        String paymentMethod = cbPaymentMethod.getSelectedItem().toString();
        String guestName = txtGuest.getText().trim();
        Integer roomNoVal = (Integer) cbRoomNo.getSelectedItem();

        if (name.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(null, "Please fill in all fields.");
            return;
        }
        if (roomNoVal == null) {
            JOptionPane.showMessageDialog(null, "Select a Room No.");
            return;
        }

        Date bookedAt = "Booked".equals(status) ? RoomHelpers.commitSpinner(spBookingAt) : null;

        Room target = RoomHelpers.findRoomByNo(rooms, roomNoVal);
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

        RoomHelpers.refreshTable(model, rooms);
        RoomHelpers.clearFields(cbRoomNo, txtName, txtPrice, txtGuest, cbCategory, cbStatus, cbPaymentMethod, spBookingAt);
        if (refreshMapIfVisible != null) refreshMapIfVisible.run();
    }
}