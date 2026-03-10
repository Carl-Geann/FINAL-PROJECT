import java.util.Date;

/**
 * This class Room is a data model representing a single hotel room.
 * It stores all the information related to a room's status, guest details, and pricing.
 */
public class Room {
    // Unique identifier for the room
    private int roomNo;
    // Descriptive name of the room
    private String name;
    // Category of the room (VIP, Double Bed, Family)
    private String type;
    // Current availability status (Free, Booked)
    private String status;
    // Base price per night
    private String price;
    // Selected payment method
    private String paymentMethod;
    // The date and time when the guest checked in
    private Date bookedAt;
    // The date and time when the guest is expected to check out
    private Date bookOutAt;
    // Name of the guest staying in the room
    private String guestName;
    // Number of nights the guest is staying
    private int nights;
    // Any discount amount applied to the booking
    private double discount;
    // Total payment amount calculated for the stay
    private double total;
    // Number of guests staying in the room
    private int guestCount;

    public Room(int roomNo, String name, String type, String status, String price, String paymentMethod, String guestName) {
        // Initializing the room object with core details
        this.roomNo = roomNo;
        this.name = name;
        this.type = type;
        this.status = status;
        this.price = price;
        this.paymentMethod = paymentMethod;
        this.guestName = guestName;
        // Setting default values for a new booking
        this.nights = 1;
        this.discount = 0.0;
        this.total = 0.0;
        this.guestCount = 1; 
    }

    // This section contains Getters to retrieve room information
    public int getRoomNo() { return roomNo; }
    public String getName() { return name; }
    public String getType() { return type; }
    public String getStatus() { return status; }
    public String getPrice() { return price; }
    public String getPaymentMethod() { return paymentMethod; }
    public Date getBookedAt() { return bookedAt; }
    public Date getBookOutAt() { return bookOutAt; }
    public String getGuestName() { return guestName; }
    public int getNights() { return nights; }
    public double getDiscount() { return discount; }
    public double getTotal() { return total; }
    public int getGuestCount() { return guestCount; }

    // This section contains Setters to update room information
    public void setName(String name) { this.name = name; }
    public void setType(String type) { this.type = type; }
    public void setStatus(String status) { this.status = status; }
    public void setPrice(String price) { this.price = price; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public void setBookedAt(Date bookedAt) { this.bookedAt = bookedAt; }
    public void setBookOutAt(Date bookOutAt) { this.bookOutAt = bookOutAt; }
    public void setGuestName(String guestName) { this.guestName = guestName; }
    public void setNights(int nights) { this.nights = nights; }
    public void setDiscount(double discount) { this.discount = discount; }
    public void setTotal(double total) { this.total = total; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }
}
