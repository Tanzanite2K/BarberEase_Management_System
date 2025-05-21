package src;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class UserManager {

    public static boolean register(String username, String password) {
        try (Connection conn = Database.connect()) {
            String query = "INSERT INTO Barbers (username, password) VALUES (?, ?)";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            pst.executeUpdate();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int login(String username, String password) {
        try (Connection conn = Database.connect()) {
            String query = "SELECT id FROM Barbers WHERE username = ? AND password = ?";
            PreparedStatement pst = conn.prepareStatement(query);
            pst.setString(1, username);
            pst.setString(2, password);
            ResultSet rs = pst.executeQuery();
            if (rs.next()) {
                return rs.getInt("id"); // Return barber_id
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1; // Invalid login
    }
}
