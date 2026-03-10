import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class HotelManager is for managing the collection of rooms and business logic.
 * It handles adding, updating, booking, and filtering rooms in the hotel.
 */
public class HotelManager {
    // This list stores all the rooms currently in the hotel inventory
    private ArrayList<Room> rooms;

    public HotelManager() {
        this.rooms = new ArrayList<>();
        // Seeds the initial set of rooms when the manager is created
        seedInventory();
    }

    // This method returns the complete list of rooms
    public List<Room> getAllRooms() {
        return rooms;
    }

    // This method returns only the rooms that are currently booked by guests
    public List<Room> getBookedRooms() {
        return rooms.stream()
                .filter(r -> "Booked".equals(r.getStatus()) && r.getGuestName() != null && !r.getGuestName().trim().isEmpty())
                .collect(Collectors.toList());
    }

    // This method finds a specific room using its unique room number
    public Room findRoomByNo(int roomNo) {
        return rooms.stream().filter(r -> r.getRoomNo() == roomNo).findFirst().orElse(null);
    }

    // This method finds a room by its descriptive name
    public Room findRoomByName(String name) {
        return rooms.stream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
    }

    // This method generates the next available room number
    private int nextRoomNo() {
        return rooms.stream().mapToInt(Room::getRoomNo).max().orElse(0) + 1;
    }

    // This method populates the initial inventory of rooms for different categories
    private void seedInventory() {
        seedType("VIP", "VIP", 10, "100");
        seedType("Family", "Family", 10, "80");
        seedType("Double Bed", "Double", 10, "60");
    }

    // This method helper creates multiple rooms of a specific type
    private void seedType(String type, String base, int count, String price) {
        for (int i = 1; i <= count; i++) {
            String name = base + " " + i;
            if (findRoomByName(name) == null) {
                rooms.add(new Room(nextRoomNo(), name, type, "Free", price, "Cash", ""));
            }
        }
    }

    // This method updates the details of an existing room
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

    // This method adds a new room to the system with full details
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

    // This method processes a "Book In" action for a guest
    public void bookInRoom(Room room, String guestName, String paymentMethod, int nights, 
                           int guestCount, double discount, Date bookedAt, double total) {
        room.setStatus("Booked");
        room.setGuestName(guestName);
        room.setPaymentMethod(paymentMethod);
        room.setNights(nights);
        room.setGuestCount(guestCount);
        room.setDiscount(discount);
        room.setBookedAt(bookedAt);
        room.setBookOutAt(null);
        room.setTotal(total);
    }

    // This method processes a "Book Out" action, resetting the room to Free
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

    // This method calculates the total payment based on price, nights, discount and guest count
    public double calculateTotal(double price, int nights, double discount, int guestCount) {
        return (price * nights) + (price * guestCount) - discount;
    }

    // This method provides the default price for each room category
    public String defaultPriceForType(String type) {
        if ("VIP".equals(type)) return "100";
        if ("Family".equals(type)) return "80";
        return "60";
    }

    // This method provides the base naming prefix for each room type
    public String baseForType(String type) {
        if ("VIP".equals(type)) return "VIP";
        if ("Family".equals(type)) return "Family";
        return "Double";
    }

    // This method defines the room number ranges for each category
    public int[] rangeForType(String type) {
        if ("VIP".equalsIgnoreCase(type)) return new int[]{1, 10};
        if ("Family".equalsIgnoreCase(type)) return new int[]{11, 20};
        if ("Double Bed".equalsIgnoreCase(type)) return new int[]{21, 30};
        return new int[]{1, 30};
    }

    // This method calculates the relative index within a room type range
    public int indexForTypeRoomNo(String type, int roomNo) {
        int[] r = rangeForType(type);
        return roomNo - r[0] + 1;
    }

    // This method filters the list of rooms based on type, status, and payment method
    public List<Room> filterRooms(String typeFilter, String statusFilter, String paymentFilter) {
        return rooms.stream().filter(room -> {
            boolean matchesType = typeFilter.equals("All") || room.getType().equals(typeFilter);
            boolean matchesStatus = statusFilter.equals("All") || room.getStatus().equals(statusFilter);
            boolean matchesPayment = paymentFilter.equals("All") || room.getPaymentMethod().equals(paymentFilter);
            return matchesType && matchesStatus && matchesPayment;
        }).collect(Collectors.toList());
    }
}
