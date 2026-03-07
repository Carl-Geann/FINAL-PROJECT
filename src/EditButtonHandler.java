import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Date;

public class EditButtonHandler implements java.awt.event.ActionListener {
    private final JComboBox<Integer> cbRoomNo;
    private final JTextField txtName, txtPrice, txtGuest;
    private final JComboBox<String> cbCategory, cbStatus, cbPaymentMethod;
    private final JSpinner spBookingAt;
    private final JTable table;
    private final ArrayList<Room> rooms;
    private final DefaultTableModel model;
    private final Runnable refreshMapIfVisible;

    public EditButtonHandler(JComboBox<Integer> cbRoomNo,
                             JTextField txtName, JTextField txtPrice, JTextField txtGuest,
                             JComboBox<String> cbCategory, JComboBox<String> cbStatus, JComboBox<String> cbPaymentMethod,
                             JSpinner spBookingAt,
                             JTable table,
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
        this.table = table;
        this.rooms = rooms;
        this.model = model;
        this.refreshMapIfVisible = refreshMapIfVisible;
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        Integer prefill = (Integer) cbRoomNo.getSelectedItem();
        if (prefill == null && table.getSelectedRow() != -1) {
            try {
                prefill = Integer.parseInt(table.getValueAt(table.getSelectedRow(), 0).toString());
            } catch (Exception ignored) {}
        }
        String input = (String) JOptionPane.showInputDialog(
                null,
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
            JOptionPane.showMessageDialog(null, "Invalid Room No");
            return;
        }

        Room room = RoomHelpers.findRoomByNo(rooms, targetNo);
        if (room == null) {
            JOptionPane.showMessageDialog(null, "Room No " + targetNo + " not found");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                null,
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
        Date bookedAt = "Booked".equals(cbStatus.getSelectedItem().toString()) ? RoomHelpers.commitSpinner(spBookingAt) : null;
        room.setBookedAt(bookedAt);

        RoomHelpers.refreshTable(model, rooms);
        JOptionPane.showMessageDialog(null, "Edit confirmed");
        if (refreshMapIfVisible != null) refreshMapIfVisible.run();
    }
}