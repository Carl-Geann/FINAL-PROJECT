import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;

public class DeleteButtonHandler implements java.awt.event.ActionListener {
    private final JComboBox<Integer> cbRoomNo;
    private final JTable table;
    private final ArrayList<Room> rooms;
    private final DefaultTableModel model;
    private final Runnable refreshMapIfVisible;

    public DeleteButtonHandler(JComboBox<Integer> cbRoomNo,
                               JTable table,
                               ArrayList<Room> rooms,
                               DefaultTableModel model,
                               Runnable refreshMapIfVisible) {
        this.cbRoomNo = cbRoomNo;
        this.table = table;
        this.rooms = rooms;
        this.model = model;
        this.refreshMapIfVisible = refreshMapIfVisible;
    }

    @Override
    public void actionPerformed(java.awt.event.ActionEvent e) {
        int viewRow = table.getSelectedRow();
        Integer roomNoVal = (Integer) cbRoomNo.getSelectedItem();
        int targetNo = -1;

        if (roomNoVal != null) {
            targetNo = roomNoVal;
        } else if (viewRow != -1) {
            try { targetNo = Integer.parseInt(table.getValueAt(viewRow, 0).toString()); }
            catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "Select a valid row");
                return;
            }
        } else {
            String input = (String) JOptionPane.showInputDialog(
                    null,
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
                JOptionPane.showMessageDialog(null, "Invalid Room No");
                return;
            }
        }

        Room room = RoomHelpers.findRoomByNo(rooms, targetNo);
        if (room == null) {
            JOptionPane.showMessageDialog(null, "Room No " + targetNo + " not found");
            return;
        }

        int ok = JOptionPane.showConfirmDialog(
                null,
                "Empty row " + targetNo + " (" + room.getName() + ")?",
                "Confirm Empty",
                JOptionPane.OK_CANCEL_OPTION
        );
        if (ok != JOptionPane.OK_OPTION) return;

        RoomHelpers.emptyRoom(room);
        RoomHelpers.refreshTable(model, rooms);
        cbRoomNo.setSelectedItem(targetNo);
        JOptionPane.showMessageDialog(null, "Row " + targetNo + " emptied");
        if (refreshMapIfVisible != null) refreshMapIfVisible.run();
    }
}