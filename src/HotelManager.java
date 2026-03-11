import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Kini nga class nga HotelManager kay para sa pagdumala sa koleksyon sa mga kwarto ug business logic.
 * Kini ang nag-handle sa pagdugang, pag-update, pag-book, ug pag-filter sa mga kwarto sa hotel.
 */
public class HotelManager {
    // Kini nga listahan nagtipig sa tanang mga kwarto nga anaa karon sa inventory sa hotel.
    private ArrayList<Room> rooms;

    public HotelManager() {
        this.rooms = new ArrayList<>();
        // Nagbutang sa inisyal nga set sa mga kwarto sa dihang gihimo ang manager.
        seedInventory();
    }

    // Kini nga method nag-return sa kompleto nga listahan sa mga kwarto.
    public List<Room> getAllRooms() {
        return rooms;
    }

    // Kini nga method nag-return lamang sa mga kwarto nga gi-book karon sa mga bisita.
    public List<Room> getBookedRooms() {
        return rooms.stream()
                .filter(r -> "Booked".equals(r.getStatus()) && r.getGuestName() != null && !r.getGuestName().trim().isEmpty())
                .collect(Collectors.toList());
    }

    // Kini nga method nangita og piho nga kwarto gamit ang iyang talagsaon nga numero sa kwarto.
    public Room findRoomByNo(int roomNo) {
        return rooms.stream().filter(r -> r.getRoomNo() == roomNo).findFirst().orElse(null);
    }

    // Kini nga method nangita og kwarto pinaagi sa iyang deskriptibong ngalan.
    public Room findRoomByName(String name) {
        return rooms.stream().filter(r -> r.getName().equals(name)).findFirst().orElse(null);
    }

    // Kini nga method nag-generate sa sunod nga anaa nga numero sa kwarto.
    private int nextRoomNo() {
        return rooms.stream().mapToInt(Room::getRoomNo).max().orElse(0) + 1;
    }

    // Kini nga method nagpuno sa inisyal nga inventory sa mga kwarto para sa lain-laing mga kategorya.
    private void seedInventory() {
        seedType("VIP", "VIP", 10, "100");
        seedType("Family", "Family", 10, "80");
        seedType("Double Bed", "Double", 10, "60");
    }

    // Kini nga method helper naghimo og daghang mga kwarto sa usa ka piho nga klase.
    private void seedType(String type, String base, int count, String price) {
        for (int i = 1; i <= count; i++) {
            String name = base + " " + i;
            if (findRoomByName(name) == null) {
                rooms.add(new Room(nextRoomNo(), name, type, "Free", price, "Cash", ""));
            }
        }
    }

    // Kini nga method naga-update sa mga detalye sa usa ka anaa na nga kwarto.
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

    // Kini nga method nagdugang og bag-ong kwarto sa sistema nga adunay kompleto nga mga detalye.
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

    // Kini nga method nag-proseso sa aksyon nga "Book In" para sa usa ka bisita.
    public void bookInRoom(Room room, String guestName, String paymentMethod, int nights, 
                           int guestCount, double discount, Date bookedAt, double total) {
        room.setStatus("Booked");
        room.setGuestName(guestName);
        room.setPaymentMethod(paymentMethod);
        room.setNights(nights);
        room.setGuestCount(guestCount);
        room.setDiscount(discount);
        room.setBookedAt(bookedAt);
        
        // Awtomatiko nga kalkulasyon sa book out date base sa bookedAt ug nights.
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
