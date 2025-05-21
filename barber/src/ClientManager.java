package src;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class ClientManager extends JPanel {
    private JTable table;
    private DefaultTableModel model;

    public ClientManager() {
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "Name", "Email", "Phone"}, 0);
        table = new JTable(model);
        loadClients();

        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadClients() {
        model.setRowCount(0); // Clear existing data
        try (Connection conn = Database.connect();
             PreparedStatement pst = conn.prepareStatement("SELECT * FROM Clients")) {
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("client_id"),
                    rs.getString("client_name"),
                    rs.getString("email"),
                    rs.getString("phone_number"),
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
