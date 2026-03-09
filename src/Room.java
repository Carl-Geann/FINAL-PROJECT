import java.util.Date;

public class Room {
    private int roomNo;
    private String name;
    private String type;
    private String status;
    private String price;
    private String paymentMethod;
    private Date bookedAt;
    private Date bookOutAt;
    private String guestName;
    private int nights;
    private double discount;
    private double totalPayment;
    private int guestCount;

    public Room(int roomNo, String name, String type, String status, String price, String paymentMethod, Date bookedAt) {
        this.roomNo = roomNo;
        this.name = name;
        this.type = type;
        this.status = status;
        this.price = price;
        this.paymentMethod = paymentMethod;
        this.bookedAt = bookedAt;
        this.nights = 1;
        this.discount = 0.0;
        this.totalPayment = 0.0;
        this.guestCount = 0;
    }

    public int getRoomNo() { return roomNo; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getPrice() { return price; }
    public void setPrice(String price) { this.price = price; }
    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }
    public Date getBookedAt() { return bookedAt; }
    public void setBookedAt(Date bookedAt) { this.bookedAt = bookedAt; }
    public Date getBookOutAt() { return bookOutAt; }
    public void setBookOutAt(Date bookOutAt) { this.bookOutAt = bookOutAt; }

    public String getGuestName() { return guestName; }
    public void setGuestName(String guestName) { this.guestName = guestName; }

    public int getNights() { return nights; }
    public void setNights(int nights) { this.nights = nights; }
    public double getDiscount() { return discount; }
    public void setDiscount(double discount) { this.discount = discount; }
    public double getTotalPayment() { return totalPayment; }
    public void setTotalPayment(double totalPayment) { this.totalPayment = totalPayment; }
    public int getGuestCount() { return guestCount; }
    public void setGuestCount(int guestCount) { this.guestCount = guestCount; }
}