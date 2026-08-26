package ui;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.DefaultTableCellRenderer;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.sql.SQLException;
import java.util.List;
import model.Student;
import service.StudentService;

public class StudentGUI extends JFrame implements ActionListener {

    JButton addBtn, searchBtn, updateBtn, deleteBtn, viewBtn, dashBtn, logoutBtn;
    JButton submitBtn, fetchBtn, clearSearchBtn, exportCsvBtn;
    JLabel l0, l1, l2, l3, l4, titleLabel;
    JTextField t0, t1, t2, t3, t4;
    JTextArea area;
    JScrollPane scroll;
    JPanel panel;
    DashboardPanel dashboardPanel;

    // Search Selector & Dynamic Search Components
    JLabel searchByLabel, dynamicLabel;
    JComboBox<String> searchByCombo;
    JTextField searchIdText, searchNameText;
    JComboBox<String> searchBranchCombo, searchStatusCombo;

    JTable studentTable;
    DefaultTableModel tableModel;
    JScrollPane tableScroll;

    StudentService service = new StudentService();
    String currentOperation = "";

    public StudentGUI() {
        setTitle("Student Management System");
        setLayout(null);

        // Header Title
        titleLabel = new JLabel("Student Management System", SwingConstants.CENTER);
        titleLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        titleLabel.setBounds(20, 10, 600, 25);
        add(titleLabel);

        // Navigation Header Buttons
        addBtn = new JButton("Add");
        searchBtn = new JButton("Search");
        updateBtn = new JButton("Update");
        deleteBtn = new JButton("Delete");
        viewBtn = new JButton("View All");
        dashBtn = new JButton("Dashboard");
        logoutBtn = new JButton("Logout");

        addBtn.setBounds(15, 45, 75, 35);
        searchBtn.setBounds(95, 45, 75, 35);
        updateBtn.setBounds(175, 45, 75, 35);
        deleteBtn.setBounds(255, 45, 75, 35);
        viewBtn.setBounds(335, 45, 85, 35);
        dashBtn.setBounds(425, 45, 105, 35);
        logoutBtn.setBounds(535, 45, 85, 35);

        add(addBtn);
        add(searchBtn);
        add(updateBtn);
        add(deleteBtn);
        add(viewBtn);
        add(dashBtn);
        add(logoutBtn);

        addBtn.addActionListener(this);
        searchBtn.addActionListener(this);
        updateBtn.addActionListener(this);
        deleteBtn.addActionListener(this);
        viewBtn.addActionListener(this);
        dashBtn.addActionListener(this);
        logoutBtn.addActionListener(this);

        // Operation Form Panel
        panel = new JPanel();
        panel.setLayout(null);
        panel.setBounds(20, 90, 600, 250);
        panel.setBorder(BorderFactory.createTitledBorder("Operation Form"));
        panel.setVisible(false);

        l0 = new JLabel("Student ID:");
        l1 = new JLabel("Name:");
        l2 = new JLabel("Branch:");
        l3 = new JLabel("Email:");
        l4 = new JLabel("Marks:");

        t0 = new JTextField();
        t1 = new JTextField();
        t2 = new JTextField();
        t3 = new JTextField();
        t4 = new JTextField();

        // Single Search Selector Components
        searchByLabel = new JLabel("Search By:");
        String[] searchByOptions = {"Select", "Student ID", "Name", "Branch", "Status"};
        searchByCombo = new JComboBox<>(searchByOptions);
        searchByCombo.addActionListener(e -> updateSearchInputVisibility());

        dynamicLabel = new JLabel("");
        searchIdText = new JTextField();
        searchNameText = new JTextField();

        String[] branchOptions = {"Select Branch", "CSE", "ECE", "EEE", "MECH", "CIVIL", "Other"};
        searchBranchCombo = new JComboBox<>(branchOptions);

        String[] statusOptions = {"Select Status", "PASS", "FAIL"};
        searchStatusCombo = new JComboBox<>(statusOptions);

        searchByLabel.setVisible(false);
        searchByCombo.setVisible(false);
        dynamicLabel.setVisible(false);
        searchIdText.setVisible(false);
        searchNameText.setVisible(false);
        searchBranchCombo.setVisible(false);
        searchStatusCombo.setVisible(false);

        fetchBtn = new JButton("Fetch");
        fetchBtn.addActionListener(this);

        submitBtn = new JButton("Submit");
        submitBtn.addActionListener(this);

        clearSearchBtn = new JButton("Clear");
        clearSearchBtn.addActionListener(this);
        clearSearchBtn.setVisible(false);

        // Add standard form components to panel
        panel.add(l0);
        panel.add(t0);
        panel.add(fetchBtn);
        panel.add(l1);
        panel.add(t1);
        panel.add(l2);
        panel.add(t2);
        panel.add(l3);
        panel.add(t3);
        panel.add(l4);
        panel.add(t4);

        // Add search selector components to panel
        panel.add(searchByLabel);
        panel.add(searchByCombo);
        panel.add(dynamicLabel);
        panel.add(searchIdText);
        panel.add(searchNameText);
        panel.add(searchBranchCombo);
        panel.add(searchStatusCombo);

        panel.add(submitBtn);
        panel.add(clearSearchBtn);

        add(panel);

        // Output Display Area (For Add, Update, Delete action feedback logs)
        area = new JTextArea();
        area.setEditable(false);
        scroll = new JScrollPane(area);
        scroll.setBounds(20, 350, 600, 210);
        scroll.setBorder(BorderFactory.createTitledBorder("Output Display"));
        add(scroll);

        // Dashboard Panel Component
        dashboardPanel = new DashboardPanel(service);
        add(dashboardPanel);
        dashboardPanel.setVisible(false);

        // JTable for View All & Search Results
        String[] columns = {"ID", "Name", "Branch", "Email", "Marks", "Grade", "Status"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        studentTable = new JTable(tableModel);
        studentTable.setRowHeight(25);
        studentTable.setFont(new Font("SansSerif", Font.PLAIN, 12));
        studentTable.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        studentTable.getTableHeader().setBackground(new Color(235, 240, 245));

        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
        studentTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(4).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(5).setCellRenderer(centerRenderer);
        studentTable.getColumnModel().getColumn(6).setCellRenderer(centerRenderer);

        tableScroll = new JScrollPane(studentTable);
        tableScroll.setBounds(20, 90, 600, 420);
        tableScroll.setBorder(BorderFactory.createTitledBorder("🎓 Student Records Directory (JTable)"));
        add(tableScroll);
        tableScroll.setVisible(false);

        // Export CSV Button (For View All screen)
        exportCsvBtn = new JButton("📥 Export as CSV");
        exportCsvBtn.setFont(new Font("SansSerif", Font.BOLD, 12));
        exportCsvBtn.setBounds(230, 520, 180, 35);
        exportCsvBtn.addActionListener(this);
        exportCsvBtn.setVisible(false);
        add(exportCsvBtn);

        // Row Selection Listener
        studentTable.getSelectionModel().addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting() && studentTable.getSelectedRow() != -1) {
                int selectedRow = studentTable.getSelectedRow();
                t0.setText(String.valueOf(tableModel.getValueAt(selectedRow, 0)));
                t1.setText(String.valueOf(tableModel.getValueAt(selectedRow, 1)));
                t2.setText(String.valueOf(tableModel.getValueAt(selectedRow, 2)));
                t3.setText(String.valueOf(tableModel.getValueAt(selectedRow, 3)));
                t4.setText(String.valueOf(tableModel.getValueAt(selectedRow, 4)));
            }
        });

        setSize(660, 610);
        setVisible(true);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    private void clearFields() {
        t0.setText("");
        t1.setText("");
        t2.setText("");
        t3.setText("");
        t4.setText("");
        area.setText("");
    }

    private void updateSearchInputVisibility() {
        String selected = (String) searchByCombo.getSelectedItem();

        dynamicLabel.setVisible(false);
        searchIdText.setVisible(false);
        searchNameText.setVisible(false);
        searchBranchCombo.setVisible(false);
        searchStatusCombo.setVisible(false);

        if ("Student ID".equals(selected)) {
            dynamicLabel.setText("Student ID:");
            dynamicLabel.setVisible(true);
            searchIdText.setText("");
            searchIdText.setVisible(true);
        } else if ("Name".equals(selected)) {
            dynamicLabel.setText("Name:");
            dynamicLabel.setVisible(true);
            searchNameText.setText("");
            searchNameText.setVisible(true);
        } else if ("Branch".equals(selected)) {
            dynamicLabel.setText("Branch:");
            dynamicLabel.setVisible(true);
            searchBranchCombo.setSelectedIndex(0);
            searchBranchCombo.setVisible(true);
        } else if ("Status".equals(selected)) {
            dynamicLabel.setText("Status:");
            dynamicLabel.setVisible(true);
            searchStatusCombo.setSelectedIndex(0);
            searchStatusCombo.setVisible(true);
        }
    }

    private void hideSearchComponents() {
        searchByLabel.setVisible(false);
        searchByCombo.setVisible(false);
        dynamicLabel.setVisible(false);
        searchIdText.setVisible(false);
        searchNameText.setVisible(false);
        searchBranchCombo.setVisible(false);
        searchStatusCombo.setVisible(false);
    }

    private void populateTable() {
        tableModel.setRowCount(0);
        try {
            List<Student> list = service.getAllStudents();
            for (Student s : list) {
                tableModel.addRow(new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getBranch(),
                    s.getEmail(),
                    s.getMarks(),
                    s.getGrade(),
                    s.getStatus()
                });
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Connection Error: " + ex.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void exportToCSV() {
        if (tableModel.getRowCount() == 0) {
            JOptionPane.showMessageDialog(this, "No student records available in the table to export.", "Export Warning", JOptionPane.WARNING_MESSAGE);
            return;
        }

        JFileChooser fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Save Student Records as CSV");
        fileChooser.setSelectedFile(new File("students_export.csv"));

        int userSelection = fileChooser.showSaveDialog(this);
        if (userSelection == JFileChooser.APPROVE_OPTION) {
            File fileToSave = fileChooser.getSelectedFile();
            String filePath = fileToSave.getAbsolutePath();
            if (!filePath.toLowerCase().endsWith(".csv")) {
                filePath += ".csv";
                fileToSave = new File(filePath);
            }

            try (PrintWriter writer = new PrintWriter(new FileWriter(fileToSave))) {
                writer.println("ID,Name,Branch,Email,Marks,Grade,Status");
                for (int row = 0; row < tableModel.getRowCount(); row++) {
                    StringBuilder rowSb = new StringBuilder();
                    for (int col = 0; col < tableModel.getColumnCount(); col++) {
                        Object value = tableModel.getValueAt(row, col);
                        String strVal = (value == null) ? "" : value.toString();

                        if (strVal.contains(",") || strVal.contains("\"") || strVal.contains("\n")) {
                            strVal = "\"" + strVal.replace("\"", "\"\"") + "\"";
                        }
                        rowSb.append(strVal);
                        if (col < tableModel.getColumnCount() - 1) {
                            rowSb.append(",");
                        }
                    }
                    writer.println(rowSb.toString());
                }
                JOptionPane.showMessageDialog(this, "Student records exported successfully!", "Export Success", JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error exporting CSV: " + ex.getMessage(), "Export Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void setupStandardFormLayout() {
        l0.setBounds(60, 25, 100, 28);
        t0.setBounds(170, 25, 180, 28);
        fetchBtn.setBounds(360, 25, 80, 28);

        l1.setBounds(60, 65, 100, 28);
        t1.setBounds(170, 65, 270, 28);

        l2.setBounds(60, 105, 100, 28);
        t2.setBounds(170, 105, 270, 28);

        l3.setBounds(60, 145, 100, 28);
        t3.setBounds(170, 145, 270, 28);

        l4.setBounds(60, 185, 100, 28);
        t4.setBounds(170, 185, 270, 28);

        submitBtn.setBounds(240, 215, 120, 30);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addBtn) {
            currentOperation = "add";
            clearFields();
            hideSearchComponents();
            tableScroll.setVisible(false);
            exportCsvBtn.setVisible(false);
            dashboardPanel.setVisible(false);

            panel.setBounds(20, 90, 600, 250);
            panel.setBorder(BorderFactory.createTitledBorder("Add New Student"));
            setupStandardFormLayout();
            submitBtn.setText("Submit");
            panel.setVisible(true);

            l0.setVisible(true);
            t0.setVisible(true);
            t0.setEditable(true);
            fetchBtn.setVisible(false);

            l1.setVisible(true);
            t1.setVisible(true);
            l2.setVisible(true);
            t2.setVisible(true);

            l3.setText("Email:");
            l3.setVisible(true);
            t3.setVisible(true);

            l4.setVisible(true);
            t4.setVisible(true);
            clearSearchBtn.setVisible(false);

            scroll.setBounds(20, 350, 600, 210);
            scroll.setVisible(true);

        } else if (e.getSource() == searchBtn) {
            currentOperation = "search";
            clearFields();
            scroll.setVisible(false);
            dashboardPanel.setVisible(false);
            exportCsvBtn.setVisible(false);

            // Hide standard form components
            l0.setVisible(false); t0.setVisible(false); fetchBtn.setVisible(false);
            l1.setVisible(false); t1.setVisible(false);
            l2.setVisible(false); t2.setVisible(false);
            l3.setVisible(false); t3.setVisible(false);
            l4.setVisible(false); t4.setVisible(false);

            panel.setBounds(20, 90, 600, 150);
            panel.setBorder(BorderFactory.createTitledBorder("Search / Filter Students"));
            panel.setVisible(true);

            // Single Search Selector Layout
            searchByLabel.setBounds(140, 25, 100, 28);
            searchByLabel.setVisible(true);

            searchByCombo.setBounds(250, 25, 200, 28);
            searchByCombo.setSelectedIndex(0);
            searchByCombo.setVisible(true);

            dynamicLabel.setBounds(140, 65, 100, 28);
            searchIdText.setBounds(250, 65, 200, 28);
            searchNameText.setBounds(250, 65, 200, 28);
            searchBranchCombo.setBounds(250, 65, 200, 28);
            searchStatusCombo.setBounds(250, 65, 200, 28);

            updateSearchInputVisibility();

            submitBtn.setText("Search");
            submitBtn.setBounds(180, 105, 110, 30);

            clearSearchBtn.setBounds(310, 105, 110, 30);
            clearSearchBtn.setVisible(true);

            // Show JTable directly below the search form
            tableModel.setRowCount(0);
            tableScroll.setBounds(20, 250, 600, 310);
            tableScroll.setBorder(BorderFactory.createTitledBorder("🔍 Search Results Directory (JTable)"));
            tableScroll.setVisible(true);

        } else if (e.getSource() == updateBtn) {
            currentOperation = "update";
            clearFields();
            hideSearchComponents();
            tableScroll.setVisible(false);
            exportCsvBtn.setVisible(false);
            dashboardPanel.setVisible(false);

            panel.setBounds(20, 90, 600, 250);
            panel.setBorder(BorderFactory.createTitledBorder("Update Student (Enter ID & details, or click Fetch)"));
            setupStandardFormLayout();
            submitBtn.setText("Submit");
            panel.setVisible(true);

            l0.setVisible(true);
            t0.setVisible(true);
            t0.setEditable(true);
            fetchBtn.setVisible(true);

            l1.setVisible(true);
            t1.setVisible(true);
            l2.setVisible(true);
            t2.setVisible(true);

            l3.setText("Email:");
            l3.setVisible(true);
            t3.setVisible(true);

            l4.setVisible(true);
            t4.setVisible(true);
            clearSearchBtn.setVisible(false);

            scroll.setBounds(20, 350, 600, 210);
            scroll.setVisible(true);

        } else if (e.getSource() == deleteBtn) {
            currentOperation = "delete";
            clearFields();
            hideSearchComponents();
            tableScroll.setVisible(false);
            exportCsvBtn.setVisible(false);
            dashboardPanel.setVisible(false);

            panel.setBounds(20, 90, 600, 250);
            panel.setBorder(BorderFactory.createTitledBorder("Delete Student by ID"));
            setupStandardFormLayout();
            submitBtn.setText("Submit");
            panel.setVisible(true);

            l0.setVisible(true);
            t0.setVisible(true);
            t0.setEditable(true);
            fetchBtn.setVisible(false);

            l1.setVisible(false);
            t1.setVisible(false);
            l2.setVisible(false);
            t2.setVisible(false);

            l3.setText("Email:");
            l3.setVisible(false);
            t3.setVisible(false);

            l4.setVisible(false);
            t4.setVisible(false);
            clearSearchBtn.setVisible(false);

            scroll.setBounds(20, 350, 600, 210);
            scroll.setVisible(true);

        } else if (e.getSource() == viewBtn) {
            hideSearchComponents();
            dashboardPanel.setVisible(false);
            panel.setVisible(false);
            scroll.setVisible(false);

            tableScroll.setBounds(20, 90, 600, 420);
            tableScroll.setBorder(BorderFactory.createTitledBorder("🎓 All Enrolled Students Directory (JTable)"));
            populateTable();
            tableScroll.setVisible(true);

            exportCsvBtn.setBounds(230, 520, 180, 35);
            exportCsvBtn.setVisible(true);

        } else if (e.getSource() == dashBtn) {
            hideSearchComponents();
            tableScroll.setVisible(false);
            exportCsvBtn.setVisible(false);
            panel.setVisible(false);
            scroll.setVisible(false);
            dashboardPanel.setBounds(20, 90, 600, 470);
            dashboardPanel.refreshData();
            dashboardPanel.setVisible(true);

        } else if (e.getSource() == logoutBtn) {
            int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to log out?", "Confirm Logout", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                this.dispose();
                new LoginGUI();
            }

        } else if (e.getSource() == clearSearchBtn) {
            searchByCombo.setSelectedIndex(0);
            updateSearchInputVisibility();
            tableModel.setRowCount(0);

        } else if (e.getSource() == exportCsvBtn) {
            exportToCSV();

        } else if (e.getSource() == fetchBtn) {
            String idStr = t0.getText().trim();
            if (idStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a Student ID to fetch.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                return;
            }
            try {
                int id = Integer.parseInt(idStr);
                Student s = service.search(id);
                if (s != null) {
                    t1.setText(s.getName());
                    t2.setText(s.getBranch());
                    t3.setText(s.getEmail());
                    t4.setText(String.valueOf(s.getMarks()));
                    JOptionPane.showMessageDialog(this, "Student details loaded into fields.", "Fetch Successful", JOptionPane.INFORMATION_MESSAGE);
                } else {
                    JOptionPane.showMessageDialog(this, "No student found with ID: " + id, "Not Found", JOptionPane.WARNING_MESSAGE);
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "ID must be a valid integer number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "Database Error: " + sqle.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }

        } else if (e.getSource() == submitBtn) {
            if (currentOperation.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please select an operation (Add, Search, Update, Delete) first.", "No Operation Selected", JOptionPane.INFORMATION_MESSAGE);
                return;
            }

            try {
                if (currentOperation.equals("add")) {
                    String idStr = t0.getText().trim();
                    String name = t1.getText().trim();
                    String branch = t2.getText().trim();
                    String email = t3.getText().trim();
                    String marksStr = t4.getText().trim();

                    if (idStr.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Student ID cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (email.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Email cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid email address (e.g. name@gmail.com).", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (marksStr.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Marks cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int id = Integer.parseInt(idStr);
                    double marks = Double.parseDouble(marksStr);
                    Student s = new Student(id, name, branch, email, marks);

                    if (service.add(s)) {
                        JOptionPane.showMessageDialog(this, "Student Added Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                        String content = "ID     : " + s.getId() + "\n" +
                                         "Name   : " + s.getName() + "\n" +
                                         "Branch : " + s.getBranch() + "\n" +
                                         "Email  : " + s.getEmail() + "\n" +
                                         "Marks  : " + s.getMarks() + "  (Grade: " + s.getGrade() + " | Status: " + s.getStatus() + ")";
                        area.setText(content);
                    } else {
                        JOptionPane.showMessageDialog(this, "Failed to add student.", "Error", JOptionPane.ERROR_MESSAGE);
                    }

                } else if (currentOperation.equals("search")) {
                    String selectedType = (String) searchByCombo.getSelectedItem();

                    if (selectedType == null || "Select".equals(selectedType)) {
                        JOptionPane.showMessageDialog(this, "Please select a Search By option first.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    String val = "";
                    if ("Student ID".equals(selectedType)) {
                        val = searchIdText.getText().trim();
                        if (val.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Please enter a Student ID.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                        try {
                            Integer.parseInt(val);
                        } catch (NumberFormatException nfe) {
                            JOptionPane.showMessageDialog(this, "Student ID must be a valid integer number.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                            return;
                        }
                    } else if ("Name".equals(selectedType)) {
                        val = searchNameText.getText().trim();
                        if (val.isEmpty()) {
                            JOptionPane.showMessageDialog(this, "Please enter a Name to search.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    } else if ("Branch".equals(selectedType)) {
                        val = (String) searchBranchCombo.getSelectedItem();
                        if (val == null || "Select Branch".equals(val)) {
                            JOptionPane.showMessageDialog(this, "Please select a Branch.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    } else if ("Status".equals(selectedType)) {
                        val = (String) searchStatusCombo.getSelectedItem();
                        if (val == null || "Select Status".equals(val)) {
                            JOptionPane.showMessageDialog(this, "Please select a Status (PASS / FAIL).", "Validation Error", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    }

                    List<Student> results = service.searchByFilter(selectedType, val);
                    tableModel.setRowCount(0);
                    if (!results.isEmpty()) {
                        for (Student s : results) {
                            tableModel.addRow(new Object[]{
                                s.getId(), s.getName(), s.getBranch(), s.getEmail(), s.getMarks(), s.getGrade(), s.getStatus()
                            });
                        }
                    } else {
                        JOptionPane.showMessageDialog(this, "No matching student found.", "Search Result", JOptionPane.INFORMATION_MESSAGE);
                    }

                } else if (currentOperation.equals("update")) {
                    String idStr = t0.getText().trim();
                    if (idStr.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please enter Student ID to update.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int id = Integer.parseInt(idStr);
                    String name = t1.getText().trim();
                    String branch = t2.getText().trim();
                    String email = t3.getText().trim();
                    String marksStr = t4.getText().trim();

                    if (name.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Name cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (email.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Email cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
                        JOptionPane.showMessageDialog(this, "Please enter a valid email address (e.g. name@gmail.com).", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }
                    if (marksStr.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Marks cannot be empty.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    double marks = Double.parseDouble(marksStr);
                    Student s = new Student(id, name, branch, email, marks);

                    if (service.update(s)) {
                        JOptionPane.showMessageDialog(this, "Student Updated Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                        clearFields();
                        String content = "ID     : " + s.getId() + "\n" +
                                         "Name   : " + s.getName() + "\n" +
                                         "Branch : " + s.getBranch() + "\n" +
                                         "Email  : " + s.getEmail() + "\n" +
                                         "Marks  : " + s.getMarks() + "  (Grade: " + s.getGrade() + " | Status: " + s.getStatus() + ")";
                        area.setText(content);
                    } else {
                        JOptionPane.showMessageDialog(this, "No student found with ID: " + id + " to update.", "Update Failed", JOptionPane.WARNING_MESSAGE);
                    }

                } else if (currentOperation.equals("delete")) {
                    String idStr = t0.getText().trim();
                    if (idStr.isEmpty()) {
                        JOptionPane.showMessageDialog(this, "Please enter Student ID to delete.", "Validation Error", JOptionPane.WARNING_MESSAGE);
                        return;
                    }

                    int id = Integer.parseInt(idStr);
                    int confirm = JOptionPane.showConfirmDialog(this, "Are you sure you want to delete student with ID: " + id + "?", "Confirm Delete", JOptionPane.YES_NO_OPTION);
                    if (confirm == JOptionPane.YES_OPTION) {
                        if (service.delete(id)) {
                            JOptionPane.showMessageDialog(this, "Student Deleted Successfully!", "Success", JOptionPane.INFORMATION_MESSAGE);
                            clearFields();
                            area.setText("Student with ID " + id + " deleted successfully.");
                        } else {
                            JOptionPane.showMessageDialog(this, "No student found with ID: " + id, "Delete Failed", JOptionPane.WARNING_MESSAGE);
                        }
                    }
                }
            } catch (NumberFormatException nfe) {
                JOptionPane.showMessageDialog(this, "ID and Marks must be valid numbers.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            } catch (SQLException sqle) {
                JOptionPane.showMessageDialog(this, "Database Error: " + sqle.getMessage(), "DB Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }
}