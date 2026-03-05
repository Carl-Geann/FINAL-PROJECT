import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import javax.imageio.ImageIO;

public class HotelBookingSystem extends JFrame {

    private JTextField txtName, txtPrice;
    private JComboBox<String> cbCategory, cbStatus, cbPaymentMethod, cbFilterType, cbFilterStatus, cbFilterPayment;
    private JTable table;
    private DefaultTableModel model;
    private ArrayList<Room> rooms;

    public HotelBookingSystem() {

        setTitle("Hotel Reservation List");
        setSize(1100, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());
        
        rooms = new ArrayList<>();

        
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(350, 550));
        leftPanel.setLayout(new GridLayout(12, 1, 5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Room Details"));

        txtName = new JTextField();
        txtPrice = new JTextField();

        cbCategory = new JComboBox<>(new String[]{"VIP", "Double Bed", "Family"});
        cbStatus = new JComboBox<>(new String[]{"Free", "Booked"});
        cbPaymentMethod = new JComboBox<>(new String[]{"Credit Card", "Debit Card", "Cash", "Online Transfer"});

        JButton btnAdd = new JButton("Add");
        JButton btnEdit = new JButton("Edit");
        JButton btnDelete = new JButton("Delete");

        leftPanel.add(new JLabel("Name"));
        leftPanel.add(txtName);
        leftPanel.add(new JLabel("Category"));
        leftPanel.add(cbCategory);
        leftPanel.add(new JLabel("Status"));
        leftPanel.add(cbStatus);
        leftPanel.add(new JLabel("Price"));
        leftPanel.add(txtPrice);
        leftPanel.add(new JLabel("Payment Method"));
        leftPanel.add(cbPaymentMethod);
        leftPanel.add(btnAdd);
        leftPanel.add(btnEdit);
        leftPanel.add(btnDelete);

        
        JPanel rightPanel = new JPanel(new BorderLayout());

        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));

        cbFilterType = new JComboBox<>(new String[]{"All", "VIP", "Double Bed", "Family"});
        cbFilterStatus = new JComboBox<>(new String[]{"All", "Free", "Booked"});
        cbFilterPayment = new JComboBox<>(new String[]{"All", "Credit Card", "Debit Card", "Cash", "Online Transfer"});
        JButton btnRefresh = new JButton("Refresh");

        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(cbFilterType);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(cbFilterStatus);
        filterPanel.add(new JLabel("Payment:"));
        filterPanel.add(cbFilterPayment);
        filterPanel.add(btnRefresh);

        String[] columns = {"Room No", "Room Name", "Type", "Status", "Price", "Payment Method"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);
        table.setRowHeight(16); 

        JScrollPane scrollPane = new JScrollPane(table);

        rightPanel.add(filterPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        
        btnAdd.addActionListener(e -> addRoom());
        btnEdit.addActionListener(e -> editRoom());
        btnDelete.addActionListener(e -> deleteRoom());
        btnRefresh.addActionListener(e -> filterRooms());
    }

    private void addRoom() {
        String name = txtName.getText();
        String type = cbCategory.getSelectedItem().toString();
        String status = cbStatus.getSelectedItem().toString();
        String price = txtPrice.getText();
        String paymentMethod = cbPaymentMethod.getSelectedItem().toString();

        if (name.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        Room room = new Room(rooms.size() + 1, name, type, status, price, paymentMethod);
        rooms.add(room);
        updateTable();
        clearFields();
    }

    private void editRoom() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            Room room = rooms.get(selectedRow);
            room.setName(txtName.getText());
            room.setType(cbCategory.getSelectedItem().toString());
            room.setStatus(cbStatus.getSelectedItem().toString());
            room.setPrice(txtPrice.getText());
            room.setPaymentMethod(cbPaymentMethod.getSelectedItem().toString());
            updateTable();
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to edit.");
        }
    }

    private void deleteRoom() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            rooms.remove(selectedRow);
            updateTable();
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
        }
    }

    private void filterRooms() {
        String typeFilter = cbFilterType.getSelectedItem().toString();
        String statusFilter = cbFilterStatus.getSelectedItem().toString();
        String paymentFilter = cbFilterPayment.getSelectedItem().toString();

        model.setRowCount(0);

        for (Room room : rooms) {
            boolean visible = true;

            if (!typeFilter.equals("All") && !room.getType().equals(typeFilter)) {
                visible = false;
            }

            if (!statusFilter.equals("All") && !room.getStatus().equals(statusFilter)) {
                visible = false;
            }

            if (!paymentFilter.equals("All") && !room.getPaymentMethod().equals(paymentFilter)) {
                visible = false;
            }

            if (visible) {
                model.addRow(new Object[]{room.getRoomNo(), room.getName(), room.getType(), 
                                          room.getStatus(), room.getPrice(), room.getPaymentMethod()});
            }
        }
    }

    private void updateTable() {
        model.setRowCount(0);
        for (Room room : rooms) {
            model.addRow(new Object[]{room.getRoomNo(), room.getName(), room.getType(), 
                                      room.getStatus(), room.getPrice(), room.getPaymentMethod()});
        }
    }

    private void clearFields() {
        txtName.setText("");
        txtPrice.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new HotelBookingSystem().setVisible(true));
    }
}

class Room {
    private int roomNo;
    private String name;
    private String type;
    private String status;
    private String price;
    private String paymentMethod;

    public Room(int roomNo, String name, String type, String status, String price, String paymentMethod) {
        this.roomNo = roomNo;
        this.name = name;
        this.type = type;
        this.status = status;
        this.price = price;
        this.paymentMethod = paymentMethod;
    }

    public int getRoomNo() {
        return roomNo;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getPaymentMethod() {
        return paymentMethod;
    }

    public void setPaymentMethod(String paymentMethod) {
        this.paymentMethod = paymentMethod;
    }
}