import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Manages the collection of hotel rooms and implements core business logic.
 * Handles operations such as adding, updating, booking, and filtering rooms within the hotel system.
 */
public class HotelManager {
    // Stores the complete list of rooms available in the hotel's inventory.
    private ArrayList<Room> rooms;

    public HotelManager() {
        this.rooms = new ArrayList<>();
        // Initializes the room inventory with a default set of rooms when the manager is instantiated.
        seedInventory();
    }

    // Retrieves the full list of all rooms managed by the hotel.
    public List<Room> getAllRooms() {
        return rooms;
    }

    // Returns a list of rooms that are currently occupied or reserved by guests.
    public List<Room> getBookedRooms() {
        return rooms.stream()
                .filter(r -> "Booked".equals(r.getStatus()) && r.getGuestName() != null && !r.getGuestName().trim().isEmpty())
                .collect(Collectors.toList());
    }

    // Searches for and returns a specific room based on its unique room number.
    public Room findRoomByNo(int roomNo) {
        return rooms.stream().filter(r -> r.getRoomNo() == roomNo).findFirst().orElse(null);
    }

    // Searches for and returns a room based on its descriptive name.
    public Room findRoomByName(String name) {
        return rooms.stream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
    }

    // Calculates and returns the next available unique room number for a new room.
    private int nextRoomNo() {
        return rooms.stream().mapToInt(Room::getRoomNo).max().orElse(0) + 1;
    }

    // Populates the hotel inventory with an initial set of rooms for various categories (e.g., VIP, Family, Couple).
    private void seedInventory() {
        seedType("VIP BED", "VIP", 20, "100");
        seedType("FAMILY BED", "Family", 20, "80");
        seedType("COUPLE BED", "Couple", 20, "60");
    }

    // A helper method that creates a specified number of rooms for a given room type.
    private void seedType(String type, String base, int count, String price) {
        for (int i = 1; i <= count; i++) {
            String name = base + " " + i;
            if (findRoomByName(name) == null) {
                rooms.add(new Room(nextRoomNo(), name, type, "Free", price, "Cash", ""));
            }
        }
    }

    // Updates the information and status of an existing room object.
    public void updateRoom(Room target, String name, String type, String status, String price, 
                           String paymentMethod, String guestName, Date bookedAt, Date bookOutAt, 
                           int nights, double discount, double total, int guestCount) {
        if (target != null) {
            target.setName(name);
            target.setType(type);
            target.setStatus(status);
            target.setPrice(price);
            target.setPaymentMethod(paymentMethod);
            target.setGuestName(guestName);
            target.setBookedAt(bookedAt);
            target.setBookOutAt(bookOutAt);
            target.setNights(nights);
            target.setDiscount(discount);
            target.setTotal(total);
            target.setGuestCount(guestCount);
        }
    }

    // Adds a new room with all its associated details to the hotel's inventory.
    public void addRoom(int roomNo, String name, String type, String status, String price, 
                        String paymentMethod, String guestName, Date bookedAt, Date bookOutAt, 
                        int nights, double discount, double total, int guestCount) {
        Room room = new Room(roomNo, name, type, status, price, paymentMethod, guestName);
        room.setBookedAt(bookedAt);
        room.setBookOutAt(bookOutAt);
        room.setNights(nights);
        room.setDiscount(discount);
        room.setTotal(total);
        room.setGuestCount(guestCount);
        rooms.add(room);
    }

    // Processes the check-in operation for a guest, updating the room's status and booking details.
    public void bookInRoom(Room room, String guestName, String paymentMethod, int nights, 
                           int guestCount, double discount, Date bookedAt, double total) {
        room.setStatus("Booked");
        room.setGuestName(guestName);
        room.setPaymentMethod(paymentMethod);
        room.setNights(nights);
        room.setGuestCount(guestCount);
        room.setDiscount(discount);
        room.setBookedAt(bookedAt);
        
        // Automatically calculates the expected check-out date based on the check-in date and number of nights.
        if (bookedAt != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(bookedAt);
            cal.add(java.util.Calendar.DATE, nights);
            room.setBookOutAt(cal.getTime());
        } else {
            room.setBookOutAt(null);
        }
        
        room.setTotal(total);
    }

    // Processes the check-out operation, resetting the room status to 'Free' and clearing guest information.
    public void bookOutRoom(Room room) {
        room.setGuestName("");
        room.setBookedAt(null);
        room.setBookOutAt(null);
        room.setStatus("Free");
        room.setPaymentMethod("Cash");
        room.setPrice(defaultPriceForType(room.getType()));
        room.setNights(1);
        room.setDiscount(0);
        room.setGuestCount(0);
        room.setTotal(0);
        int idx = indexForTypeRoomNo(room.getType(), room.getRoomNo());
        room.setName(baseForType(room.getType()) + " " + idx);
    }

    // Calculates the total booking cost considering the base price, number of nights, applied discounts, and guest count.
    public double calculateTotal(double price, int nights, double discount, int guestCount) {
        // Formula: (Price * Nights) + (Price * Guest Count) - Discount
        return (price * nights) + (price * guestCount) - discount;
    }
    // Returns the standard nightly price for a specific room category.
    public String defaultPriceForType(String type) {
        if ("VIP BED".equals(type)) return "100";
        if ("FAMILY BED".equals(type)) return "80";
        if ("COUPLE BED".equals(type)) return "60";
        return "60";
    }
    // Returns the naming prefix (e.g., "VIP", "Family") for a given room type.
    public String baseForType(String type) {
        if ("VIP BED".equals(type)) return "VIP";
        if ("FAMILY BED".equals(type)) return "Family";
        if ("COUPLE BED".equals(type)) return "Couple";
        return "Double";
    }
    // Returns the defined range of room numbers for each specific room category.
    public int[] rangeForType(String type) {
        if ("VIP BED".equalsIgnoreCase(type)) return new int[]{1, 20};
        if ("FAMILY BED".equalsIgnoreCase(type)) return new int[]{21, 40};
        if ("COUPLE BED".equalsIgnoreCase(type)) return new int[]{41, 60};
        return new int[]{1, 60};
    }

    // Calculates the relative position or index of a room within its category's range.
    public int indexForTypeRoomNo(String type, int roomNo) {
        int[] r = rangeForType(type);
        return roomNo - r[0] + 1;
    }

    // Searches through all room fields and returns a list of rooms that match the provided search query.
    public List<Room> searchRooms(String query) {
        if (query == null || query.trim().isEmpty()) {
            return getAllRooms();
        }
        
        String lowerQuery = query.toLowerCase().trim();
        return rooms.stream().filter(room -> {
            boolean matchesRoomNo = String.valueOf(room.getRoomNo()).contains(lowerQuery);
            boolean matchesName = room.getName().toLowerCase().contains(lowerQuery);
            boolean matchesStatus = room.getStatus().toLowerCase().contains(lowerQuery);
            boolean matchesType = room.getType().toLowerCase().contains(lowerQuery);
            boolean matchesGuestName = room.getGuestName() != null && room.getGuestName().toLowerCase().contains(lowerQuery);
            boolean matchesPrice = room.getPrice().toLowerCase().contains(lowerQuery);
            boolean matchesNights = String.valueOf(room.getNights()).contains(lowerQuery);
            boolean matchesDiscount = String.valueOf(room.getDiscount()).contains(lowerQuery);
            boolean matchesTotal = String.valueOf(room.getTotal()).contains(lowerQuery);
            boolean matchesGuestCount = String.valueOf(room.getGuestCount()).contains(lowerQuery);
            boolean matchesPayment = room.getPaymentMethod().toLowerCase().contains(lowerQuery);
            
            return matchesRoomNo || matchesName || matchesStatus || matchesType || 
                   matchesGuestName || matchesPrice || matchesNights || 
                   matchesDiscount || matchesTotal || matchesGuestCount || matchesPayment;
        }).collect(Collectors.toList());
    }

}