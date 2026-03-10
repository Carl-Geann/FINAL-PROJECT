import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.text.SimpleDateFormat;

/**
 * Kini nga class nga RoomTableHandler kay para sa pagdumala sa JTable UI display.
 * Kini ang nag-handle sa pag-update sa main room table ug sa guest list table gamit ang data gikan sa HotelManager.
 */
public class RoomTableHandler {
    // Mga model reference para sa main room table ug guest list table.
    private DefaultTableModel model, guestModel;
    // UI Label para ipakita ang kinatibuk-ang gidaghanon sa mga bisita nga anaa karon sa hotel.
    private JLabel lblTotalGuests;
    // Mga filter component para sa main room table.
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

    // Kini nga method naga-refresh sa Guest List table uban ang mga kwarto nga gi-book karon.
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

    // Kini nga method nag-apply og mga filter sa main room table ug naga-update sa gidaghanon sa mga bisita.
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

    // Kini nga method naga-update sa main room table uban ang kompleto nga listahan sa inventory.
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
