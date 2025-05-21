package src;
import javax.swing.*;

public class Dashboard extends JFrame {

    public Dashboard(int barberId) {
        setTitle("Barber Dashboard");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JTabbedPane tabs = new JTabbedPane();
        tabs.addTab("Services", new ServiceManager(barberId));
        tabs.addTab("Appointments", new AppointmentManager(barberId));
        tabs.addTab("Clients", new ClientManager()); 

        add(tabs);
    }
}
