import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;

public class HotelBookingSystem extends JFrame {

    private JTextField txtName, txtPrice;
    private JComboBox<String> cbCategory, cbStatus, cbFilterType, cbFilterStatus;
    private JTable table;
    private DefaultTableModel model;

    public HotelBookingSystem() {

        setTitle("Hotel Management System - Manage Rooms");
        setSize(900, 500);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // ===== LEFT PANEL =====
        JPanel leftPanel = new JPanel();
        leftPanel.setPreferredSize(new Dimension(300, 500));
        leftPanel.setLayout(new GridLayout(10, 1, 5, 5));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Room Details"));

        txtName = new JTextField();
        txtPrice = new JTextField();

        cbCategory = new JComboBox<>(new String[]{"VIP", "Double Bed", "Family"});
        cbStatus = new JComboBox<>(new String[]{"Free", "Booked"});

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
        leftPanel.add(btnAdd);
        leftPanel.add(btnEdit);
        leftPanel.add(btnDelete);

        // ===== RIGHT PANEL =====
        JPanel rightPanel = new JPanel(new BorderLayout());

        JPanel filterPanel = new JPanel();

        cbFilterType = new JComboBox<>(new String[]{"All", "VIP", "Double Bed", "Family"});
        cbFilterStatus = new JComboBox<>(new String[]{"All", "Free", "Booked"});
        JButton btnRefresh = new JButton("Refresh");

        filterPanel.add(new JLabel("Type:"));
        filterPanel.add(cbFilterType);
        filterPanel.add(new JLabel("Status:"));
        filterPanel.add(cbFilterStatus);
        filterPanel.add(btnRefresh);

        String[] columns = {"Room No", "Room Name", "Type", "Status", "Price"};
        model = new DefaultTableModel(columns, 0);
        table = new JTable(model);

        JScrollPane scrollPane = new JScrollPane(table);

        rightPanel.add(filterPanel, BorderLayout.NORTH);
        rightPanel.add(scrollPane, BorderLayout.CENTER);

        add(leftPanel, BorderLayout.WEST);
        add(rightPanel, BorderLayout.CENTER);

        // ===== SAMPLE DATA =====
        model.addRow(new Object[]{1, "Heaven Room", "VIP", "Free", 2000});
        model.addRow(new Object[]{2, "King Palace", "Double Bed", "Booked", 1800});
        model.addRow(new Object[]{3, "Fam Suite 1", "Family", "Free", 2200});

        // ===== BUTTON FUNCTIONS =====
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

        model.addRow(new Object[]{rowCount + 1, name, type, status, price});
        clearFields();
    }

    private void editRoom() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            model.setValueAt(txtName.getText(), selectedRow, 1);
            model.setValueAt(cbCategory.getSelectedItem(), selectedRow, 2);
            model.setValueAt(cbStatus.getSelectedItem(), selectedRow, 3);
            model.setValueAt(txtPrice.getText(), selectedRow, 4);
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

        for (int i = 0; i < table.getRowCount(); i++) {
            boolean visible = true;

            String type = table.getValueAt(i, 2).toString();
            String status = table.getValueAt(i, 3).toString();

            if (!typeFilter.equals("All") && !type.equals(typeFilter)) {
                visible = false;
            }

            if (!statusFilter.equals("All") && !status.equals(statusFilter)) {
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