import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;

/**
 * This class RoomTableHandler is for managing the JTable UI displays.
 * It handles updating the main room table and the guest list table with data from HotelManager.
 */
public class RoomTableHandler {
    // Model references for the main room table and guest list table
    private DefaultTableModel model, guestModel;
    // UI Label to display the total number of guests currently in the hotel
    private JLabel lblTotalGuests;
    // Filter components for the main room table
    private JComboBox<String> cbFilterType, cbFilterStatus, cbFilterPayment;
    
    private HotelManager hotelManager;

    public RoomTableHandler(HotelReservationSystem system, HotelManager hotelManager,
                            JTable table, DefaultTableModel model, 
                            JTable guestTable, DefaultTableModel guestModel,
                            JLabel lblTotalGuests,
                            JComboBox<String> cbFilterType, JComboBox<String> cbFilterStatus, JComboBox<String> cbFilterPayment) {
        // Initializing references
        this.hotelManager = hotelManager;
        this.model = model;
        this.guestModel = guestModel;
        this.lblTotalGuests = lblTotalGuests;
        this.cbFilterType = cbFilterType;
        this.cbFilterStatus = cbFilterStatus;
        this.cbFilterPayment = cbFilterPayment;
    }

    // This method refreshes the Guest List table with currently booked rooms
    public void updateGuestListTable() {
        guestModel.setRowCount(0);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        for (Room room : hotelManager.getBookedRooms()) {
            Object[] row = {
                room.getGuestName(),
                room.getName(),
                room.getBookedAt() != null ? sdf.format(room.getBookedAt()) : "",
                room.getBookOutAt() != null ? sdf.format(room.getBookOutAt()) : ""
            };
            guestModel.addRow(row);
        }
    }

    // This method applies filters to the main room table and updates the guest count
    public void filterRooms() {
        String typeFilter = cbFilterType.getSelectedItem().toString();
        String statusFilter = cbFilterStatus.getSelectedItem().toString();
        String paymentFilter = cbFilterPayment.getSelectedItem().toString();

        model.setRowCount(0);
        int totalGuestsIn = 0;

        for (Room room : hotelManager.filterRooms(typeFilter, statusFilter, paymentFilter)) {
            if ("Booked".equals(room.getStatus())) {
                totalGuestsIn += room.getGuestCount();
            }
            model.addRow(new Object[]{
                room.getRoomNo(),
                room.getName(),
                room.getStatus(),
                room.getType(),
                room.getGuestName(),
                room.getPrice(),
                room.getNights(),
                room.getDiscount(),
                room.getTotal(),
                room.getGuestCount(),
                room.getPaymentMethod()
            });
        }
        lblTotalGuests.setText(" | Total Guests In: " + totalGuestsIn);
    }

    // This method updates the main room table with the full inventory list
    public void roomUpdateTable() {
        model.setRowCount(0);
        int totalGuestsIn = 0;
        for (Room room : hotelManager.getAllRooms()) {
            if ("Booked".equals(room.getStatus())) {
                totalGuestsIn += room.getGuestCount();
            }
            model.addRow(new Object[]{
                room.getRoomNo(),
                room.getName(),
                room.getStatus(),
                room.getType(),
                room.getGuestName(),
                room.getPrice(),
                room.getNights(),
                room.getDiscount(),
                room.getTotal(),
                room.getGuestCount(),
                room.getPaymentMethod()
            });
        }
        lblTotalGuests.setText(" | Total Guests In: " + totalGuestsIn);
    }
}
