import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class RoomHelpers {
    public static Room findRoomByNo(ArrayList<Room> rooms, int roomNo) {
        for (Room r : rooms) if (r.getRoomNo() == roomNo) return r;
        return null;
    }

    public static String defaultPriceForType(String type) {
        if ("VIP".equals(type)) return "100";
        if ("Family".equals(type)) return "80";
        return "60";
    }

    public static String baseForType(String type) {
        if ("VIP".equals(type)) return "VIP";
        if ("Family".equals(type)) return "Family";
        return "Double";
    }

    public static int[] rangeForType(String type) {
        if ("VIP".equalsIgnoreCase(type)) return new int[]{1,10};
        if ("Family".equalsIgnoreCase(type)) return new int[]{11,20};
        if ("Double Bed".equalsIgnoreCase(type)) return new int[]{21,30};
        return new int[]{1,30};
    }

    public static int indexForTypeRoomNo(String type, int roomNo) {
        int[] r = rangeForType(type);
        return roomNo - r[0] + 1;
    }

    public static void emptyRoom(Room room) {
        room.setGuestName("");
        room.setBookedAt(null);
        room.setStatus("Free");
        room.setPaymentMethod("Cash");
        room.setPrice(defaultPriceForType(room.getType()));
        int idx = indexForTypeRoomNo(room.getType(), room.getRoomNo());
        room.setName(baseForType(room.getType()) + " " + idx);
    }

    public static void refreshTable(DefaultTableModel model, ArrayList<Room> rooms) {
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

    public static void updateRoomNoOptions(JComboBox<String> cbCategory, JComboBox<Integer> cbRoomNo) {
        cbRoomNo.removeAllItems();
        Object sel = cbCategory.getSelectedItem();
        String type = sel == null ? "" : sel.toString();
        int[] range = rangeForType(type);
        for (int i = range[0]; i <= range[1]; i++) cbRoomNo.addItem(i);
        cbRoomNo.setSelectedIndex(-1);
    }

    public static Date commitSpinner(JSpinner spinner) {
        try { spinner.commitEdit(); } catch (ParseException ignored) {}
        return (Date) spinner.getValue();
    }

    public static void clearFields(JComboBox<Integer> cbRoomNo,
                                   JTextField txtName, JTextField txtPrice, JTextField txtGuest,
                                   JComboBox<String> cbCategory, JComboBox<String> cbStatus, JComboBox<String> cbPaymentMethod,
                                   JSpinner spBookingAt) {
        cbRoomNo.setSelectedIndex(-1);
        txtName.setText("");
        txtPrice.setText("");
        txtGuest.setText("");
        cbCategory.setSelectedIndex(0);
        updateRoomNoOptions(cbCategory, cbRoomNo);
        cbStatus.setSelectedIndex(0);
        cbPaymentMethod.setSelectedIndex(0);
        spBookingAt.setValue(new Date());
        spBookingAt.setEnabled(false);
    }
}