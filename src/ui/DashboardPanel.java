package ui;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import java.awt.*;
import java.sql.SQLException;
import model.Student;
import service.StudentService;

public class DashboardPanel extends JPanel {

    private JLabel totalValLabel, avgValLabel, passValLabel, failValLabel;
    private JLabel topLabel, lowestLabel;
    private JProgressBar passBar, failBar, avgBar;
    private StudentService service;

    public DashboardPanel(StudentService service) {
        this.service = service;
        setLayout(null);
        setBounds(20, 90, 580, 440);
        setBorder(BorderFactory.createTitledBorder(
                BorderFactory.createEtchedBorder(),
                "📊 Academic Performance Dashboard",
                TitledBorder.LEFT,
                TitledBorder.TOP,
                new Font("SansSerif", Font.BOLD, 14),
                new Color(41, 128, 185)
        ));

        // 1. STATISTIC CARDS (TOP ROW)
        JPanel card1 = createCard("📚 Total Students", new Color(235, 245, 251), new Color(52, 152, 219));
        card1.setBounds(15, 25, 125, 80);
        totalValLabel = createCardValueLabel(new Color(27, 79, 114), 22);
        card1.add(totalValLabel, BorderLayout.CENTER);
        add(card1);

        JPanel card2 = createCard("📊 Average Marks", new Color(245, 238, 248), new Color(155, 89, 182));
        card2.setBounds(150, 25, 125, 80);
        avgValLabel = createCardValueLabel(new Color(81, 46, 95), 22);
        card2.add(avgValLabel, BorderLayout.CENTER);
        add(card2);

        JPanel card3 = createCard("🏆 Passed (>=40)", new Color(232, 248, 245), new Color(46, 204, 113));
        card3.setBounds(285, 25, 135, 80);
        passValLabel = createCardValueLabel(new Color(17, 122, 101), 15);
        card3.add(passValLabel, BorderLayout.CENTER);
        add(card3);

        JPanel card4 = createCard("⚠️ Failed (<40)", new Color(253, 237, 236), new Color(231, 76, 60));
        card4.setBounds(430, 25, 135, 80);
        failValLabel = createCardValueLabel(new Color(120, 40, 31), 15);
        card4.add(failValLabel, BorderLayout.CENTER);
        add(card4);

        // 2. PROGRESS BARS PANEL (MIDDLE ROW)
        JPanel progressPanel = new JPanel(null);
        progressPanel.setBounds(15, 115, 550, 165);
        progressPanel.setBorder(BorderFactory.createTitledBorder("📈 Visual Analytics & Rates"));

        JLabel pLabel1 = new JLabel("Pass Percentage Rate:");
        pLabel1.setFont(new Font("SansSerif", Font.BOLD, 12));
        pLabel1.setBounds(20, 25, 160, 25);
        progressPanel.add(pLabel1);

        passBar = new JProgressBar(0, 100);
        passBar.setBounds(180, 25, 340, 25);
        passBar.setStringPainted(true);
        passBar.setForeground(new Color(39, 174, 96));
        progressPanel.add(passBar);

        JLabel pLabel2 = new JLabel("Fail Percentage Rate:");
        pLabel2.setFont(new Font("SansSerif", Font.BOLD, 12));
        pLabel2.setBounds(20, 70, 160, 25);
        progressPanel.add(pLabel2);

        failBar = new JProgressBar(0, 100);
        failBar.setBounds(180, 70, 340, 25);
        failBar.setStringPainted(true);
        failBar.setForeground(new Color(192, 57, 43));
        progressPanel.add(failBar);

        JLabel pLabel3 = new JLabel("Overall Avg Marks (%):");
        pLabel3.setFont(new Font("SansSerif", Font.BOLD, 12));
        pLabel3.setBounds(20, 115, 160, 25);
        progressPanel.add(pLabel3);

        avgBar = new JProgressBar(0, 100);
        avgBar.setBounds(180, 115, 340, 25);
        avgBar.setStringPainted(true);
        avgBar.setForeground(new Color(41, 128, 185));
        progressPanel.add(avgBar);

        add(progressPanel);

        // 3. HIGHLIGHTS PANEL (BOTTOM ROW)
        JPanel highlightsPanel = new JPanel(null);
        highlightsPanel.setBounds(15, 290, 550, 135);
        highlightsPanel.setBorder(BorderFactory.createTitledBorder("⭐ Performance Highlights"));

        topLabel = new JLabel("🏆 Top Performer: Loading...");
        topLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        topLabel.setForeground(new Color(30, 132, 73));
        topLabel.setBounds(20, 30, 510, 30);
        highlightsPanel.add(topLabel);

        lowestLabel = new JLabel("⚠️ Needs Improvement: Loading...");
        lowestLabel.setFont(new Font("SansSerif", Font.BOLD, 13));
        lowestLabel.setForeground(new Color(169, 50, 38));
        lowestLabel.setBounds(20, 75, 510, 30);
        highlightsPanel.add(lowestLabel);

        add(highlightsPanel);
    }

    private JPanel createCard(String title, Color bgColor, Color borderColor) {
        JPanel card = new JPanel(new BorderLayout());
        card.setBackground(bgColor);
        card.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(borderColor, 2),
                BorderFactory.createEmptyBorder(5, 5, 5, 5)
        ));

        JLabel titleLbl = new JLabel(title, SwingConstants.CENTER);
        titleLbl.setFont(new Font("SansSerif", Font.BOLD, 11));
        titleLbl.setForeground(borderColor.darker());
        card.add(titleLbl, BorderLayout.NORTH);

        return card;
    }

    private JLabel createCardValueLabel(Color textColor, int fontSize) {
        JLabel lbl = new JLabel("-", SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, fontSize));
        lbl.setForeground(textColor);
        return lbl;
    }

    public void refreshData() {
        try {
            int total = service.getTotalStudents();
            double avg = service.getAverageMarks();
            int passed = service.getPassedStudents();
            int failed = service.getFailedStudents();
            Student top = service.getTopPerformer();
            Student lowest = service.getLowestPerformer();

            totalValLabel.setText(String.valueOf(total));
            avgValLabel.setText(String.format("%.1f", avg));

            if (total == 0) {
                passValLabel.setText("0 (0%)");
                failValLabel.setText("0 (0%)");
                passBar.setValue(0);
                failBar.setValue(0);
                avgBar.setValue(0);
                topLabel.setText("🏆 Top Performer: No student records available");
                lowestLabel.setText("⚠️ Needs Improvement: No student records available");
                return;
            }

            double passPct = (passed * 100.0) / total;
            double failPct = (failed * 100.0) / total;

            passValLabel.setText(String.format("%d (%.1f%%)", passed, passPct));
            failValLabel.setText(String.format("%d (%.1f%%)", failed, failPct));

            passBar.setValue((int) Math.round(passPct));
            passBar.setString(String.format("%.1f%% Passed", passPct));

            failBar.setValue((int) Math.round(failPct));
            failBar.setString(String.format("%.1f%% Failed", failPct));

            int avgVal = (int) Math.min(100, Math.max(0, Math.round(avg)));
            avgBar.setValue(avgVal);
            avgBar.setString(String.format("%.1f Avg Marks", avg));

            if (top != null) {
                topLabel.setText(String.format("🏆 Top Performer: %s (ID: %d, %s) - %.1f Marks (Grade %s | %s)",
                        top.getName(), top.getId(), top.getBranch(), top.getMarks(), top.getGrade(), top.getStatus()));
            } else {
                topLabel.setText("🏆 Top Performer: N/A");
            }

            if (lowest != null) {
                if (lowest.getMarks() < 40) {
                    lowestLabel.setForeground(new Color(169, 50, 38));
                    lowestLabel.setText(String.format("⚠️ Needs Improvement: %s (ID: %d, %s) - %.1f Marks (Grade %s | %s)",
                            lowest.getName(), lowest.getId(), lowest.getBranch(), lowest.getMarks(), lowest.getGrade(), lowest.getStatus()));
                } else {
                    lowestLabel.setForeground(new Color(41, 128, 185));
                    lowestLabel.setText(String.format("📈 Lowest Score: %s (ID: %d, %s) - %.1f Marks (Grade %s | %s)",
                            lowest.getName(), lowest.getId(), lowest.getBranch(), lowest.getMarks(), lowest.getGrade(), lowest.getStatus()));
                }
            } else {
                lowestLabel.setText("📈 Lowest Score: N/A");
            }

        } catch (SQLException sqle) {
            totalValLabel.setText("Err");
            avgValLabel.setText("Err");
            passValLabel.setText("Err");
            failValLabel.setText("Err");
            topLabel.setText("Error loading dashboard data: " + sqle.getMessage());
            lowestLabel.setText("");
        }
    }
}
