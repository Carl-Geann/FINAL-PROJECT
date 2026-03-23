import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.util.stream.Collectors;

/**
 * Manages the synchronization and display of room data within the main JTable UI component.
 * Responsible for updating the main room table's content based on the data provided by the HotelManager.
 */
public class RoomTableHandler {
    // Mga model reference para sa main room table
    private DefaultTableModel model;
    // Label component that displays the total count of guests currently checked into the hotel.
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
        // Initializes the handler with references to necessary UI components and the HotelManager.
        this.hotelManager = hotelManager;
        this.model = model;
        this.lblTotalGuests = lblTotalGuests;
        this.txtSearch = txtSearch;
    }

    // Applies the current search criteria and filters the room table while updating the total guest count display.
    public void searchRooms() {
        showRoomsByStatus(currentStatusFilter);
    }

    // Filters and displays rooms in the table based on their operational status (e.g., 'Free' or 'Booked').
    public void showRoomsByStatus(String statusFilter) {
        this.currentStatusFilter = statusFilter;
        String query = txtSearch.getText().toLowerCase().trim();
        model.setRowCount(0);
        
        // Calculates the global total number of guests across all rooms, independent of any active table filters.
        int globalTotalGuests = 0;
        for (Room r : hotelManager.getAllRooms()) {
            if ("Booked".equals(r.getStatus())) {
                globalTotalGuests += r.getGuestCount();
            }
        }

        java.util.List<Room> filteredList = hotelManager.getAllRooms().stream().filter(room -> {
            // Checks if the room matches the currently active status filter (Free/Booked/All).
            boolean matchesStatus = (statusFilter == null) || statusFilter.equals(room.getStatus());
            
            // Checks if any of the room's attributes match the provided search text.
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

    // Refreshes the table to show all rooms in the hotel's inventory.
    public void roomUpdateTable() {
        showRoomsByStatus(null);
    }
}

