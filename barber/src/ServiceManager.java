package src;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ServiceManager extends JPanel {
    private int barberId;
    private JTable table;
    private DefaultTableModel model;

    public ServiceManager(int barberId) {
        this.barberId = barberId;
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "Service", "Price"}, 0);
        table = new JTable(model);
        loadServices();

        JButton addButton = new JButton("Add Service");
        addButton.addActionListener(_-> addService());

        JButton deleteButton = new JButton("Delete Service");
        deleteButton.addActionListener(_ -> deleteService());

        JPanel top = new JPanel();
        top.add(addButton);
        top.add(deleteButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadServices() {
        model.setRowCount(0);
        try (Connection conn = Database.connect();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM Services WHERE barber_id = ?")) {
            pst.setInt(1, barberId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{rs.getInt("id"), rs.getString("service_name"), rs.getDouble("price")});
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void addService() {
        String name = JOptionPane.showInputDialog("Service Name:");
        String priceStr = JOptionPane.showInputDialog("Service Price:");
        try (Connection conn = Database.connect();
             PreparedStatement pst = conn.prepareStatement("INSERT INTO Services (barber_id, service_name, price) VALUES (?, ?, ?)")) {
            pst.setInt(1, barberId);
            pst.setString(2, name);
            pst.setDouble(3, Double.parseDouble(priceStr));
            pst.executeUpdate();
            loadServices();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void deleteService() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int serviceId = (int) model.getValueAt(selectedRow, 0);
            try (Connection conn = Database.connect();
                 PreparedStatement pst = conn.prepareStatement("DELETE FROM Services WHERE id = ?")) {
                pst.setInt(1, serviceId);
                pst.executeUpdate();
                loadServices();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
