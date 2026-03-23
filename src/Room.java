
import java.util.Date;

/**
 * Data model representing a single hotel room, including its properties and booking information.
 * Stores all relevant information such as room status, guest details, pricing, and stay duration.
 */
public class Room {

    // Unique identifier (room number) for the room
    private int roomNo;
    // Descriptive name or label for the room
    private String name;
    // Category or type of the room (e.g., VIP, Family, Couple)
    private String type;
    // Current operational status of the room (e.g., Free, Booked)
    private String status;
    // Standard nightly rate for the room
    private String price;
    // The payment method chosen by the guest (e.g., Cash, Card)
    private String paymentMethod;
    // Timestamp indicating when the guest officially checked into the room
    private Date bookedAt;
    // Timestamp indicating the scheduled check-out date and time
    private Date bookOutAt;
    // Full name of the primary guest currently occupying the room
    private String guestName;
    // Total number of nights the guest has booked for their stay
    private int nights;
    // Discount amount applied to the total booking cost
    private double discount;
    // The final calculated total cost for the entire stay
    private double total;
    // Total number of guests occupying the room during the stay
    private int guestCount;

    public Room(int roomNo, String name, String type, String status, String price, String paymentMethod, String guestName) {
        // Constructor that initializes a new Room object with its essential information
        this.roomNo = roomNo;
        this.name = name;
        this.type = type;
        this.status = status;
        this.price = price;
        this.paymentMethod = paymentMethod;
        this.guestName = guestName;
        // Sets initial default values for booking-related fields
        this.nights = 1;
        this.discount = 0.0;
        this.total = 0.0;
        this.guestCount = 0;
    }

    // Standard getter methods to retrieve the room's current property values
    public int getRoomNo() {
        return roomNo;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }

    public String getStatus() {
        return status;
    }

    public String getPrice() {
        return price;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public Date getBookedAt() {
        return bookedAt;
    }

    public Date getBookOutAt() {
        return bookOutAt;
    }

    public String getGuestName() {
        return guestName;
    }

    public int getNights() {
        return nights;
    }

    public double getDiscount() {
        return discount;
    }

    public double getTotal() {
        return total;
    }

    public int getGuestCount() {
        return guestCount;
    }

    // Standard setter methods to update the room's property values
    public void setName(String name) {
        this.name = name;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void setBookedAt(Date bookedAt) {
        this.bookedAt = bookedAt;
    }

    public void setBookOutAt(Date bookOutAt) {
        this.bookOutAt = bookOutAt;
    }

    public void setGuestName(String guestName) {
        this.guestName = guestName;
    }

    public void setNights(int nights) {
        this.nights = nights;
    }

    public void setDiscount(double discount) {
        this.discount = discount;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public void setGuestCount(int guestCount) {
        this.guestCount = guestCount;
    }
}
