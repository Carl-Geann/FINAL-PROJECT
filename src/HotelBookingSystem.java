import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class HotelBookingSystem extends JFrame {

    private JTextField txtName, txtPrice;
    private JComboBox<String> cbCategory, cbStatus, cbPaymentMethod, cbFilterType, cbFilterStatus, cbFilterPayment;
    private JTable table;
    private DefaultTableModel model;

    public HotelBookingSystem() {

        setTitle("Hotel Reservation List");
        setSize(1100, 550);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        
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
        table.setRowHeight(16); // Set default row height

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
        int rowCount = model.getRowCount();
        String name = txtName.getText();
        String type = cbCategory.getSelectedItem().toString();
        String status = cbStatus.getSelectedItem().toString();
        String price = txtPrice.getText();
        String paymentMethod = cbPaymentMethod.getSelectedItem().toString();

        if (name.isEmpty() || price.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        model.addRow(new Object[]{rowCount + 1, name, type, status, price, paymentMethod});
        clearFields();
    }

    private void editRoom() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            model.setValueAt(txtName.getText(), selectedRow, 1);
            model.setValueAt(cbCategory.getSelectedItem(), selectedRow, 2);
            model.setValueAt(cbStatus.getSelectedItem(), selectedRow, 3);
            model.setValueAt(txtPrice.getText(), selectedRow, 4);
            model.setValueAt(cbPaymentMethod.getSelectedItem(), selectedRow, 5);
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to edit.");
        }
    }

    private void deleteRoom() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            model.removeRow(selectedRow);
        } else {
            JOptionPane.showMessageDialog(this, "Select a row to delete.");
        }
    }

    private void filterRooms() {
        String typeFilter = cbFilterType.getSelectedItem().toString();
        String statusFilter = cbFilterStatus.getSelectedItem().toString();
        String paymentFilter = cbFilterPayment.getSelectedItem().toString();

        // Reset all rows to visible first
        for (int i = 0; i < table.getRowCount(); i++) {
            table.setRowHeight(i, 16);
        }

        // Apply filters
        for (int i = 0; i < table.getRowCount(); i++) {
            boolean visible = true;

            String type = table.getValueAt(i, 2).toString();
            String status = table.getValueAt(i, 3).toString();
            String payment = table.getValueAt(i, 5).toString();

            if (!typeFilter.equals("All") && !type.equals(typeFilter)) {
                visible = false;
            }

            if (!statusFilter.equals("All") && !status.equals(statusFilter)) {
                visible = false;
            }

            if (!paymentFilter.equals("All") && !payment.equals(paymentFilter)) {
                visible = false;
            }

            table.setRowHeight(i, visible ? 16 : 0);
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