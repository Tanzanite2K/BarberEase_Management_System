package src;
import javax.swing.*;
import java.awt.*;

public class RegisterForm extends JFrame {

    public RegisterForm() {
        setTitle("Barber Register");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton registerButton = new JButton("Register");

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(registerButton);

        registerButton.addActionListener(_ -> {
            boolean success = UserManager.register(usernameField.getText(), new String(passwordField.getPassword()));
            if (success) {
                JOptionPane.showMessageDialog(this, "Registered successfully!");
                new LoginForm().setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration Failed!");
            }
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
