package src;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;

public class AppointmentManager extends JPanel {
    private int barberId;
    private JTable table;
    private DefaultTableModel model;

    public AppointmentManager(int barberId) {
        this.barberId = barberId;
        setLayout(new BorderLayout());

        model = new DefaultTableModel(new String[]{"ID", "Client Name", "Date", "Service", "Status"}, 0);
        table = new JTable(model);
        loadAppointments();

        JButton addButton = new JButton("Add Appointment");
        addButton.addActionListener(_ -> addAppointment());

        JButton deleteButton = new JButton("Delete Appointment");
        deleteButton.addActionListener(_-> deleteAppointment());

        JButton markDoneButton = new JButton("Mark as Done");
        markDoneButton.addActionListener(_ -> markAppointmentAsDone());

        JButton markUndoneButton = new JButton("Mark as Undone");
        markUndoneButton.addActionListener(_ -> markAppointmentAsUndone());

        JPanel top = new JPanel();
        top.add(addButton);
        top.add(deleteButton);
        top.add(markDoneButton);
        top.add(markUndoneButton);

        add(top, BorderLayout.NORTH);
        add(new JScrollPane(table), BorderLayout.CENTER);
    }

    private void loadAppointments() {
        model.setRowCount(0);
        try (Connection conn = Database.connect();
             PreparedStatement pst = conn.prepareStatement(
                 "SELECT a.id, c.client_name, a.appointment_date, a.service_name, a.status " +
                 "FROM Appointments a " +
                 "JOIN Clients c ON a.client_id = c.client_id " +
                 "WHERE a.barber_id = ?"
             )) {
            pst.setInt(1, barberId);
            ResultSet rs = pst.executeQuery();
            while (rs.next()) {
                model.addRow(new Object[]{
                    rs.getInt("id"),
                    rs.getString("client_name"),
                    rs.getString("appointment_date"),
                    rs.getString("service_name"),
                    rs.getString("status")
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    private void addAppointment() {
        String clientName = JOptionPane.showInputDialog("Client Name:");
        String email = JOptionPane.showInputDialog("Client Email:");
        String phoneNumber = JOptionPane.showInputDialog("Client Phone Number:");
        String date = JOptionPane.showInputDialog("Appointment Date (YYYY-MM-DD):");
        String serviceName = JOptionPane.showInputDialog("Service Name:");
    
        try (Connection conn = Database.connect()) {
            // Check if the service exists for this barber
            String checkServiceQuery = "SELECT id FROM Services WHERE barber_id = ? AND service_name = ?";
            PreparedStatement pstCheckService = conn.prepareStatement(checkServiceQuery);
            pstCheckService.setInt(1, barberId);
            pstCheckService.setString(2, serviceName);
            ResultSet rsService = pstCheckService.executeQuery();
    
            if (!rsService.next()) {
                JOptionPane.showMessageDialog(this, "Please select a service from the Services list.", "Invalid Service", JOptionPane.ERROR_MESSAGE);
                return; // Exit without adding appointment
            }
    
            // Check if client exists
            String checkClientQuery = "SELECT client_id FROM Clients WHERE email = ? OR phone_number = ?";
            PreparedStatement pstCheckClient = conn.prepareStatement(checkClientQuery);
            pstCheckClient.setString(1, email);
            pstCheckClient.setString(2, phoneNumber);
            ResultSet rsClient = pstCheckClient.executeQuery();
    
            int clientId = -1;
            if (rsClient.next()) {
                clientId = rsClient.getInt("client_id");
            } else {
                // Insert new client
                String insertClientQuery = "INSERT INTO Clients (client_name, email, phone_number) VALUES (?, ?, ?)";
                PreparedStatement pstInsertClient = conn.prepareStatement(insertClientQuery, Statement.RETURN_GENERATED_KEYS);
                pstInsertClient.setString(1, clientName);
                pstInsertClient.setString(2, email);
                pstInsertClient.setString(3, phoneNumber);
                pstInsertClient.executeUpdate();
                ResultSet generatedKeys = pstInsertClient.getGeneratedKeys();
                if (generatedKeys.next()) {
                    clientId = generatedKeys.getInt(1);
                }
            }
    
            // Insert appointment
            String insertAppointmentQuery = "INSERT INTO Appointments (barber_id, client_id, appointment_date, service_name, status) VALUES (?, ?, ?, ?, 'Undone')";
            PreparedStatement pstInsertAppointment = conn.prepareStatement(insertAppointmentQuery);
            pstInsertAppointment.setInt(1, barberId);
            pstInsertAppointment.setInt(2, clientId);
            pstInsertAppointment.setString(3, date);
            pstInsertAppointment.setString(4, serviceName);
            pstInsertAppointment.setString(5, clientName );
            pstInsertAppointment.executeUpdate();
    
            loadAppointments();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private void deleteAppointment() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) model.getValueAt(selectedRow, 0);
            try (Connection conn = Database.connect();
                 PreparedStatement pst = conn.prepareStatement("DELETE FROM Appointments WHERE id = ?")) {
                pst.setInt(1, id);
                pst.executeUpdate();
                loadAppointments();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void markAppointmentAsDone() {
        changeAppointmentStatus("Done");
    }

    private void markAppointmentAsUndone() {
        changeAppointmentStatus("Undone");
    }

    private void changeAppointmentStatus(String newStatus) {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            int id = (int) model.getValueAt(selectedRow, 0);
            try (Connection conn = Database.connect();
                 PreparedStatement pst = conn.prepareStatement("UPDATE Appointments SET status = ? WHERE id = ?")) {
                pst.setString(1, newStatus);
                pst.setInt(2, id);
                pst.executeUpdate();
                loadAppointments();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
