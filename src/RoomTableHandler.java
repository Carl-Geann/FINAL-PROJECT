import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.stream.Collectors;

/**
 * Kini nga class nga RoomTableHandler kay para sa pagdumala sa JTable UI display.
 * Kini ang nag-handle sa pag-update sa main room table gamit ang data gikan sa HotelManager.
 */
public class RoomTableHandler {
    // Mga model reference para sa main room table.
    private DefaultTableModel model;
    // UI Label para ipakita ang kinatibuk-ang gidaghanon sa mga bisita nga anaa karon sa hotel.
    private JLabel lblTotalGuests;
    // Search field para sa main room table.
    private JTextField txtSearch;
    // Current status filter (null, "Free", or "Booked")
    private String currentStatusFilter = null;
    
    private HotelManager hotelManager;

    public RoomTableHandler(HotelReservationSystem system, HotelManager hotelManager,
                            JTable table, DefaultTableModel model, 
                            JLabel lblTotalGuests,
                            JTextField txtSearch) {
        // Pag-initialize sa mga references
        this.hotelManager = hotelManager;
        this.model = model;
        this.lblTotalGuests = lblTotalGuests;
        this.txtSearch = txtSearch;
    }

    // Kini nga method nag-apply sa search filter sa main room table ug naga-update sa gidaghanon sa mga bisita.
    public void searchRooms() {
        showRoomsByStatus(currentStatusFilter);
    }

    // Kini nga method naga-filter sa mga kwarto base sa ilang status (Free o Booked).
    public void showRoomsByStatus(String statusFilter) {
        this.currentStatusFilter = statusFilter;
        String query = txtSearch.getText().toLowerCase().trim();
        model.setRowCount(0);
        
        // Pagkalkula sa kinatibuk-ang mga bisita bisan unsa pa ang filter
        int globalTotalGuests = 0;
        for (Room r : hotelManager.getAllRooms()) {
            if ("Booked".equals(r.getStatus())) {
                globalTotalGuests += r.getGuestCount();
            }
        }

        java.util.List<Room> filteredList = hotelManager.getAllRooms().stream().filter(room -> {
            // Pag-apply sa status filter kung gi-set
            boolean matchesStatus = (statusFilter == null) || statusFilter.equals(room.getStatus());
            
            // Pag-apply sa search query kung gi-set
            boolean matchesQuery = query.isEmpty();
            if (!matchesQuery) {
                boolean matchesRoomNo = String.valueOf(room.getRoomNo()).contains(query);
                boolean matchesName = room.getName().toLowerCase().contains(query);
                boolean matchesRoomStatus = room.getStatus().toLowerCase().contains(query);
                boolean matchesType = room.getType().toLowerCase().contains(query);
                boolean matchesGuestName = room.getGuestName() != null && room.getGuestName().toLowerCase().contains(query);
                boolean matchesPrice = room.getPrice().toLowerCase().contains(query);
                boolean matchesNights = String.valueOf(room.getNights()).contains(query);
                boolean matchesDiscount = String.valueOf(room.getDiscount()).contains(query);
                boolean matchesTotal = String.valueOf(room.getTotal()).contains(query);
                boolean matchesGuestCount = String.valueOf(room.getGuestCount()).contains(query);
                boolean matchesPayment = room.getPaymentMethod().toLowerCase().contains(query);
                
                matchesQuery = matchesRoomNo || matchesName || matchesRoomStatus || matchesType || 
                               matchesGuestName || matchesPrice || matchesNights || 
                               matchesDiscount || matchesTotal || matchesGuestCount || matchesPayment;
            }
            
            return matchesStatus && matchesQuery;
        }).collect(Collectors.toList());

        for (Room room : filteredList) {
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
        lblTotalGuests.setText(" | Total Guests In: " + globalTotalGuests);
    }

    // Kini nga method naga-update sa main room table uban ang kompleto nga listahan sa inventory.
    public void roomUpdateTable() {
        showRoomsByStatus(null);
    }
}
