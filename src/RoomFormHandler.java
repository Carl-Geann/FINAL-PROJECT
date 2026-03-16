import javax.swing.*;
import java.util.Date;
import java.text.ParseException;

/**
 * Kini nga class nga RoomFormHandler kay para sa pagdumala sa logic sa Room Details form
 * Kini ang nag-handle sa field validation, pagkalkula, ug pakig-uban tali sa form UI ug sa HotelManager
 */
public class RoomFormHandler {

    // UI Components gikan sa Form
    private JComboBox<Integer> cbRoomNo, cbNights, cbGuestCount;
    private JTextField txtName, txtPrice, txtGuest, txtDiscount, txtTotalPayment, txtStatus;
    private JComboBox<String> cbCategory, cbPaymentMethod;
    private JSpinner spBookingAt, spBookOutAt;

    // Logic Managers & References
    private HotelManager hotelManager;
    private HotelReservationSystem system;
    private Room currentSelected;

    public RoomFormHandler(HotelReservationSystem system, HotelManager hotelManager,
            JComboBox<Integer> cbRoomNo, JComboBox<Integer> cbNights, JComboBox<Integer> cbGuestCount,
            JTextField txtName, JTextField txtPrice, JTextField txtGuest, JTextField txtDiscount,
            JTextField txtTotalPayment, JComboBox<String> cbCategory, JTextField txtStatus,
            JComboBox<String> cbPaymentMethod, JSpinner spBookingAt, JSpinner spBookOutAt) {
        
        this.system = system;
        this.hotelManager = hotelManager;
        this.cbRoomNo = cbRoomNo;
        this.cbNights = cbNights;
        this.cbGuestCount = cbGuestCount;
        this.txtName = txtName;
        this.txtPrice = txtPrice;
        this.txtGuest = txtGuest;
        this.txtDiscount = txtDiscount;
        this.txtTotalPayment = txtTotalPayment;
        this.cbCategory = cbCategory;
        this.txtStatus = txtStatus;
        this.cbPaymentMethod = cbPaymentMethod;
        this.spBookingAt = spBookingAt;
        this.spBookOutAt = spBookOutAt;
    }

    // UI Updates & Reset

    // Kini nga method nag-reset sa tanang field sa form ngadto sa ilang default nga walay sulod.
    public void clearFields() {
        cbRoomNo.setSelectedIndex(-1);
        txtName.setText("");
        txtPrice.setText("");
        txtGuest.setText("");
        cbNights.setSelectedIndex(0);
        txtDiscount.setText("0");
        txtTotalPayment.setText("0.00");
        cbGuestCount.setSelectedIndex(0);
        cbCategory.setSelectedIndex(0);
        
        updateRoomNoOptions();
        updateGuestCountOptions();
        
        txtStatus.setText("Free");
        cbPaymentMethod.setSelectedIndex(0);
        spBookingAt.setValue(new Date());
        spBookingAt.setEnabled(false);
        spBookOutAt.setValue(new Date());
        spBookOutAt.setEnabled(false);
        
        currentSelected = null;
        system.roomUpdateTable(); // I-refresh ang table selection
        system.updateActionButtons();
    }

    // Kini nga method nagpuno sa form og data gikan sa napili nga Room object
    public void selectRoomInForm(Room r) {
        currentSelected = r;
        cbCategory.setSelectedItem(r.getType());
        updateRoomNoOptions();
        updateGuestCountOptions();
        cbRoomNo.setSelectedItem(r.getRoomNo());
        txtName.setText(r.getName());
        txtStatus.setText(r.getStatus());
        txtPrice.setText(r.getPrice());
        cbNights.setSelectedItem(r.getNights());
        txtDiscount.setText(String.valueOf(r.getDiscount()));
        cbGuestCount.setSelectedItem(r.getGuestCount());
        calculateTotal();
        cbPaymentMethod.setSelectedItem(r.getPaymentMethod());
        txtGuest.setText(r.getGuestName() != null ? r.getGuestName() : "");
        
        if (r.getBookedAt() != null) spBookingAt.setValue(r.getBookedAt());
        if (r.getBookOutAt() != null) spBookOutAt.setValue(r.getBookOutAt());
        
        system.updateActionButtons();
    }

    // Kini nga method naga-update sa room number dropdown base sa napili nga kategorya
    public void updateRoomNoOptions() {
        if (cbRoomNo == null || cbCategory == null) return;
        cbRoomNo.removeAllItems();
        Object sel = cbCategory.getSelectedItem();
        String type = sel == null ? "" : sel.toString();
        int[] range = hotelManager.rangeForType(type);
        for (int i = range[0]; i <= range[1]; i++) cbRoomNo.addItem(i);
        cbRoomNo.setSelectedIndex(-1);
    }

    // Kini nga method naga-update sa guest count dropdown base sa kapasidad sa kategorya sa kwarto.
    public void updateGuestCountOptions() {
        if (cbGuestCount == null || cbCategory == null) return;
        int maxGuests = 2;
        Object sel = cbCategory.getSelectedItem();
        String category = sel == null ? "" : sel.toString();
        
        if ("VIP BED".equals(category)) maxGuests = 10;
        else if ("FAMILY BED".equals(category)) maxGuests = 7;
        else if ("COUPLE BED".equals(category)) maxGuests = 2;

        Integer currentVal = (Integer) cbGuestCount.getSelectedItem();
        cbGuestCount.removeAllItems();
        for (int i = 1; i <= maxGuests; i++) cbGuestCount.addItem(i);
        
        if (currentVal != null && currentVal <= maxGuests) cbGuestCount.setSelectedItem(currentVal);
        else cbGuestCount.setSelectedIndex(0);
    }

    // Kini nga method naga-update sa price field base sa default price sa kategorya.
    public void updatePriceFromCategory() {
        if (cbCategory == null || txtPrice == null) return;
        Object sel = cbCategory.getSelectedItem();
        String category = sel == null ? "" : sel.toString();
        txtPrice.setText(hotelManager.defaultPriceForType(category));
        calculateTotal();
    }

    // --- [Method Group: Core Logic & Calculations] ---

    // Kini nga method nagkalkula sa total nga bayad base sa presyo, gidaghanon sa gabii, ug mga discount.
    public void calculateTotal() {
        try {
            double price = Double.parseDouble(txtPrice.getText().isEmpty() ? "0" : txtPrice.getText());
            int nights = (Integer) cbNights.getSelectedItem();
            double discount = Double.parseDouble(txtDiscount.getText().isEmpty() ? "0" : txtDiscount.getText());
            int guestCount = (Integer) cbGuestCount.getSelectedItem();
            double total = hotelManager.calculateTotal(price, nights, discount, guestCount);
            txtTotalPayment.setText(String.format("%.2f", total));
        } catch (Exception e) {
            txtTotalPayment.setText("0.00");
        }
    }

    // Kini nga method awtomatiko nga naga-update sa gitagna nga check-out date base sa nights.
    public void updateBookOutDate() {
        Date bookingAt = (Date) spBookingAt.getValue();
        Integer nights = (Integer) cbNights.getSelectedItem();
        if (bookingAt != null && nights != null) {
            java.util.Calendar cal = java.util.Calendar.getInstance();
            cal.setTime(bookingAt);
            cal.add(java.util.Calendar.DAY_OF_MONTH, nights);
            spBookOutAt.setValue(cal.getTime());
        }
    }

    //  Booking Operations

    // Kini nga method nag-proseso sa "Book In" nga request para sa napili nga kwarto
    public void bookInRoom() {
        if (currentSelected == null) {
            JOptionPane.showMessageDialog(system, "Kindly choose a room in the table ! ", "You can't choose", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!"Free".equals(currentSelected.getStatus())) {
            JOptionPane.showMessageDialog(system, "This room is occupied ! Find a 'Free' Room to occupied ! ", "Not allowed", JOptionPane.WARNING_MESSAGE);
            return;
        }

        String guestName = txtGuest.getText().trim();
        if (guestName.isEmpty()) {
            JOptionPane.showMessageDialog(system, "Kindly input the valid guest name ! ", "Guest Name Requirement ! ", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(system, "Booking in '" + guestName + "' in room of " + currentSelected.getRoomNo() + "?", "Confirm Book In", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        String paymentMethod = cbPaymentMethod.getSelectedItem().toString();
        int nights = (Integer) cbNights.getSelectedItem();
        int guestCount = (Integer) cbGuestCount.getSelectedItem();
        double discount = 0;
        try { discount = Double.parseDouble(txtDiscount.getText()); } catch (Exception ex) { discount = 0; }

        Date bookedAt;
        try { spBookingAt.commitEdit(); bookedAt = (Date) spBookingAt.getValue(); } catch (ParseException ex) { bookedAt = new Date(); }

        calculateTotal();
        double total = 0;
        try { total = Double.parseDouble(txtTotalPayment.getText()); } catch (Exception ex) { total = 0; }

        hotelManager.bookInRoom(currentSelected, guestName, paymentMethod, nights, guestCount, discount, bookedAt, total);
        system.roomUpdateTable();
        clearFields();
        JOptionPane.showMessageDialog(system, "This room is booking in " + currentSelected.getRoomNo() + ".", "Book In Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    // Kini nga method nag-proseso sa "Book Out" nga request, naglimpyo sa data sa bisita
    public void bookOutRoom() {
        Room roomToBookOut = currentSelected;
        if (roomToBookOut == null) {
            Integer roomNoVal = (Integer) cbRoomNo.getSelectedItem();
            if (roomNoVal != null) roomToBookOut = hotelManager.findRoomByNo(roomNoVal);
        }

        if (roomToBookOut == null) {
            JOptionPane.showMessageDialog(system, "Kindly choose a room ! ", "No room you choose", JOptionPane.WARNING_MESSAGE);
            return;
        }

        if (!"Booked".equals(roomToBookOut.getStatus())) {
            JOptionPane.showMessageDialog(system, "This room is not occupied ! ", "Error", JOptionPane.WARNING_MESSAGE);
            return;
        }

        int ok = JOptionPane.showConfirmDialog(system, "Booking out the " + roomToBookOut.getRoomNo() + "? This will clear the guest data !", "Confirm Book Out", JOptionPane.OK_CANCEL_OPTION);
        if (ok != JOptionPane.OK_OPTION) return;

        hotelManager.bookOutRoom(roomToBookOut);
        system.roomUpdateTable();
        clearFields();
        JOptionPane.showMessageDialog(system, "Booking out process completed ! ", "Book Out Successful", JOptionPane.INFORMATION_MESSAGE);
    }

    // Kini nga method nag-save o nag-update sa data sa kwarto gikan sa form ngadto sa inventory
    public void saveRoom() {
        String name = txtName.getText();
        String type = cbCategory.getSelectedItem().toString();
        String status = txtStatus.getText();
        String price = txtPrice.getText();
        String paymentMethod = cbPaymentMethod.getSelectedItem().toString();
        String guestName = txtGuest.getText().trim();
        int nights = (Integer) cbNights.getSelectedItem();
        double discount = 0;
        int guestCount = (Integer) cbGuestCount.getSelectedItem();
        try { discount = Double.parseDouble(txtDiscount.getText()); } catch (Exception ignored) {}

        double priceVal = Double.parseDouble(price.isEmpty() ? "0" : price);
        double total = hotelManager.calculateTotal(priceVal, nights, discount, guestCount);

        Integer roomNoVal = (Integer) cbRoomNo.getSelectedItem();
        if (name.isEmpty() || price.isEmpty() || roomNoVal == null) {
            JOptionPane.showMessageDialog(system, "Kindly fill up the details !");
            return;
        }

        try { spBookingAt.commitEdit(); spBookOutAt.commitEdit(); } catch (ParseException ignored) {}

        Date bookedAt = "Booked".equals(status) ? (Date) spBookingAt.getValue() : null;
        Date bookOutAt = "Booked".equals(status) ? (Date) spBookOutAt.getValue() : null;

        Room target = hotelManager.findRoomByNo(roomNoVal);
        if (target != null) {
            int ok = JOptionPane.showConfirmDialog(system, "Kindly update the details ! " + target.getRoomNo() + "?", "Confirm Update", JOptionPane.OK_CANCEL_OPTION);
            if (ok != JOptionPane.OK_OPTION) return;
            hotelManager.updateRoom(target, name, type, status, price, paymentMethod, guestName, bookedAt, bookOutAt, nights, discount, total, guestCount);
            JOptionPane.showMessageDialog(system, "Update Process Completed !", "Update Successful", JOptionPane.INFORMATION_MESSAGE);
        } else {
            hotelManager.addRoom(roomNoVal, name, type, status, price, paymentMethod, guestName, bookedAt, bookOutAt, nights, discount, total, guestCount);
            JOptionPane.showMessageDialog(system, "Booking In Process Completed !", "Room Added", JOptionPane.INFORMATION_MESSAGE);
        }

        system.roomUpdateTable();
        clearFields();
    }

    //  Getters & Setters
    public Room getCurrentSelected() { return currentSelected; }
    public void setCurrentSelected(Room r) { this.currentSelected = r; }
}
