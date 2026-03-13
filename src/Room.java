
import java.util.Date;

/**
 * Kini nga class nga Room kay usa ka data model nga nagrepresentar sa usa ka
 * kwarto sa hotel. Kini nagtipig sa tanang impormasyon nga may kalabutan sa
 * status sa kwarto, mga detalye sa bisita, ug presyo.
 */
public class Room {

    // Talagsaon nga identifier para sa kwarto.
    private int roomNo;
    // Deskriptibong ngalan sa kwarto.
    private String name;
    // Kategorya sa kwarto (VIP, Double Bed, Family).
    private String type;
    // Status sa pagka-anaa karon (Free, Booked).
    private String status;
    // Base nga presyo kada gabii.
    private String price;
    // Napili nga pamaagi sa pagbayad.
    private String paymentMethod;
    // Ang petsa ug oras kung kanus-a ni-check in ang bisita.
    private Date bookedAt;
    // Ang petsa ug oras kung kanus-a gilauman nga mo-check out ang bisita.
    private Date bookOutAt;
    // Ngalan sa bisita nga nagpuyo sa kwarto.
    private String guestName;
    // Gidaghanon sa mga gabii nga magpuyo ang bisita.
    private int nights;
    // Bisan unsang kantidad sa discount nga gi-apply sa booking.
    private double discount;
    // Kinatibuk-ang kantidad sa bayad nga gikalkula para sa pagpuyo.
    private double total;
    // Gidaghanon sa mga bisita nga nagpuyo sa kwarto.
    private int guestCount;

    public Room(int roomNo, String name, String type, String status, String price, String paymentMethod, String guestName) {
        // Nag-initialize sa room object uban ang mga core nga detalye.
        this.roomNo = roomNo;
        this.name = name;
        this.type = type;
        this.status = status;
        this.price = price;
        this.paymentMethod = paymentMethod;
        this.guestName = guestName;
        // Nag-set sa mga default nga value para sa usa ka bag-ong booking.
        this.nights = 1;
        this.discount = 0.0;
        this.total = 0.0;
        this.guestCount = 0;
    }

    // Kini nga seksyon adunay mga Getter para makuha ang impormasyon sa kwarto.
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

    // Kini nga seksyon adunay mga Setter para ma-update ang impormasyon sa kwarto.
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
