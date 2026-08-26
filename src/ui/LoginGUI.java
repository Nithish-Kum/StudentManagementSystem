package ui;

import javax.swing.*;
import service.StudentService;
import java.awt.event.*;
import java.sql.SQLException;

public class LoginGUI extends JFrame implements ActionListener {

    JLabel userLabel, passLabel, titleLabel;
    JTextField userText;
    JPasswordField passText;
    JButton loginBtn, exitBtn;

    StudentService service = new StudentService();

    public LoginGUI() {
        setTitle("Login System - Student Management");
        setLayout(null);

        titleLabel = new JLabel("🔐 Admin Login System", SwingConstants.CENTER);
        titleLabel.setBounds(30, 20, 320, 30);
        add(titleLabel);

        userLabel = new JLabel("Username:");
        userLabel.setBounds(40, 70, 90, 28);
        add(userLabel);

        userText = new JTextField();
        userText.setBounds(130, 70, 200, 28);
        add(userText);

        passLabel = new JLabel("Password:");
        passLabel.setBounds(40, 115, 90, 28);
        add(passLabel);

        passText = new JPasswordField();
        passText.setBounds(130, 115, 200, 28);
        add(passText);

        loginBtn = new JButton("Login");
        loginBtn.setBounds(80, 165, 100, 32);
        loginBtn.addActionListener(this);
        add(loginBtn);

        exitBtn = new JButton("Exit");
        exitBtn.setBounds(200, 165, 100, 32);
        exitBtn.addActionListener(this);
        add(exitBtn);

        setSize(390, 260);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == loginBtn) {
            String username = userText.getText().trim();
            String password = new String(passText.getPassword()).trim();

            if (username.isEmpty() || password.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter both Username and Password.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }

            try {
                if (service.login(username, password)) {
                    JOptionPane.showMessageDialog(this, "Login Successful! Welcome, " + username + ".", "Success", JOptionPane.INFORMATION_MESSAGE);
                    this.dispose();
                    new StudentGUI();
                } else {
                    JOptionPane.showMessageDialog(this, "Invalid Username or Password. Please try again.", "Login Failed", JOptionPane.ERROR_MESSAGE);
                }
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "Database Connection Error: " + sqle.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        } else if (e.getSource() == exitBtn) {
            System.exit(0);
        }
    }
}
