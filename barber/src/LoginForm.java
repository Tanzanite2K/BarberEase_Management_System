package src;
import javax.swing.*;
import java.awt.*;

public class LoginForm extends JFrame {

    public LoginForm() {
        setTitle("Barber Login");
        setSize(300, 200);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new GridLayout(4, 2));

        JTextField usernameField = new JTextField();
        JPasswordField passwordField = new JPasswordField();
        JButton loginButton = new JButton("Login");
        JButton registerButton = new JButton("Register");

        add(new JLabel("Username:"));
        add(usernameField);
        add(new JLabel("Password:"));
        add(passwordField);
        add(loginButton);
        add(registerButton);

        loginButton.addActionListener(_ -> {
            int barberId = UserManager.login(usernameField.getText(), new String(passwordField.getPassword()));
            if (barberId != -1) {
                new Dashboard(barberId).setVisible(true);
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid Login");
            }
        });

        registerButton.addActionListener(_ -> {
            new RegisterForm().setVisible(true);
            dispose();
        });

        setLocationRelativeTo(null);
        setVisible(true);
    }
}
