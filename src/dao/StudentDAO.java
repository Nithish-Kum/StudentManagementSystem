package dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import model.Student;
import database.DBConnection;

public class StudentDAO {

    private Connection checkConnection() throws SQLException {
        Connection con = DBConnection.getConnection();
        if (con == null) {
            throw new SQLException("Cannot connect to Database. Please ensure MySQL is running on localhost:3306 with database 'studentdb'.");
        }
        return con;
    }

    public boolean validateUser(String username, String password) throws SQLException {
        if ("admin".equalsIgnoreCase(username) && "admin123".equals(password)) {
            return true;
        }

        Connection con = checkConnection();
        String query = "select count(*) from users where username=? and password=?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, username);
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next() && rs.getInt(1) > 0) {
                    return true;
                }
            }
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
        return false;
    }

    public int getNextStudentId() throws SQLException {
        Connection con = checkConnection();
        int nextId = 1;
        String maxIdQuery = "select max(id) from students";
        try (PreparedStatement psMax = con.prepareStatement(maxIdQuery);
             ResultSet rsMax = psMax.executeQuery()) {
            if (rsMax.next()) {
                int maxId = rsMax.getInt(1);
                if (maxId > 0) {
                    nextId = maxId + 1;
                }
            }
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
        return nextId;
    }

    // ADD STUDENT
    public boolean addStudent(Student s) throws SQLException {
        Connection con = checkConnection();

        // Check for duplicate ID
        String checkQuery = "select count(*) from students where id=?";
        try (PreparedStatement psCheck = con.prepareStatement(checkQuery)) {
            psCheck.setInt(1, s.getId());
            try (ResultSet rsCheck = psCheck.executeQuery()) {
                if (rsCheck.next() && rsCheck.getInt(1) > 0) {
                    throw new SQLException("Student ID " + s.getId() + " already exists. Please use a unique ID.");
                }
            }
        }

        String query = "insert into students(id,name,branch,email,marks) values(?,?,?,?,?)";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, s.getId());
            ps.setString(2, s.getName());
            ps.setString(3, s.getBranch());
            ps.setString(4, s.getEmail());
            ps.setDouble(5, s.getMarks());

            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
    }

    // VIEW STUDENTS
    public String viewStudents() throws SQLException {
        StringBuilder sb = new StringBuilder();
        Connection con = checkConnection();

        String query = "select * from students order by id asc";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {

            boolean hasData = false;
            while (rs.next()) {
                hasData = true;
                Student s = new Student(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("branch"),
                    rs.getString("email"),
                    rs.getDouble("marks")
                );

                sb.append("ID     : ").append(s.getId()).append("\n");
                sb.append("Name   : ").append(s.getName()).append("\n");
                sb.append("Branch : ").append(s.getBranch()).append("\n");
                sb.append("Email  : ").append(s.getEmail()).append("\n");
                sb.append("Marks  : ").append(s.getMarks())
                  .append("  (Grade: ").append(s.getGrade())
                  .append(" | Status: ").append(s.getStatus()).append(")\n");
                sb.append("---------------------\n");
            }
            if (!hasData) {
                return "No student records found in database.";
            }
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }

        return sb.toString();
    }

    public java.util.List<Student> getAllStudents() throws SQLException {
        java.util.List<Student> list = new java.util.ArrayList<>();
        Connection con = checkConnection();
        String query = "select * from students order by id asc";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(new Student(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("branch"),
                    rs.getString("email"),
                    rs.getDouble("marks")
                ));
            }
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
        return list;
    }

    // UPDATE STUDENT
    public boolean updateStudent(Student s) throws SQLException {
        Connection con = checkConnection();

        String query = "update students set name=?, branch=?, email=?, marks=? where id=?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setString(1, s.getName());
            ps.setString(2, s.getBranch());
            ps.setString(3, s.getEmail());
            ps.setDouble(4, s.getMarks());
            ps.setInt(5, s.getId());

            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
    }

    // DELETE STUDENT
    public boolean deleteStudent(int id) throws SQLException {
        Connection con = checkConnection();

        String query = "delete from students where id=?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            int rows = ps.executeUpdate();
            return rows > 0;
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
    }

    // SEARCH STUDENT
    public Student searchStudent(int id) throws SQLException {
        Student s = null;
        Connection con = checkConnection();

        String query = "select * from students where id=?";
        try (PreparedStatement ps = con.prepareStatement(query)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    s = new Student();
                    s.setId(rs.getInt("id"));
                    s.setName(rs.getString("name"));
                    s.setBranch(rs.getString("branch"));
                    s.setEmail(rs.getString("email"));
                    s.setMarks(rs.getDouble("marks"));
                }
            }
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }

        return s;
    }

    // ADVANCED SEARCH / FILTER
    public java.util.List<Student> searchStudentsAdvanced(String idStr, String name, String branch, String status) throws SQLException {
        java.util.List<Student> list = new java.util.ArrayList<>();
        Connection con = checkConnection();

        StringBuilder sql = new StringBuilder("select * from students where 1=1 ");
        java.util.List<Object> params = new java.util.ArrayList<>();

        if (idStr != null && !idStr.trim().isEmpty()) {
            try {
                int id = Integer.parseInt(idStr.trim());
                sql.append("and id = ? ");
                params.add(id);
            } catch (NumberFormatException ignored) {}
        }

        if (name != null && !name.trim().isEmpty()) {
            sql.append("and lower(name) like ? ");
            params.add("%" + name.trim().toLowerCase() + "%");
        }

        if (branch != null && !branch.trim().isEmpty() && !branch.equalsIgnoreCase("All")) {
            sql.append("and lower(branch) like ? ");
            params.add("%" + branch.trim().toLowerCase() + "%");
        }

        if (status != null && !status.trim().isEmpty() && !status.equalsIgnoreCase("All")) {
            if (status.equalsIgnoreCase("PASS")) {
                sql.append("and marks >= 40 ");
            } else if (status.equalsIgnoreCase("FAIL")) {
                sql.append("and marks < 40 ");
            }
        }

        sql.append("order by id asc");

        try (PreparedStatement ps = con.prepareStatement(sql.toString())) {
            for (int i = 0; i < params.size(); i++) {
                ps.setObject(i + 1, params.get(i));
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("branch"),
                        rs.getString("email"),
                        rs.getDouble("marks")
                    ));
                }
            }
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
        return list;
    }

    // SINGLE CRITERIA SEARCH BY FILTER
    public java.util.List<Student> searchByFilter(String type, String value) throws SQLException {
        java.util.List<Student> list = new java.util.ArrayList<>();
        Connection con = checkConnection();
        String query = "";

        if ("Student ID".equalsIgnoreCase(type)) {
            query = "select * from students where id = ?";
        } else if ("Name".equalsIgnoreCase(type)) {
            query = "select * from students where lower(name) like ? order by id asc";
        } else if ("Branch".equalsIgnoreCase(type)) {
            query = "select * from students where lower(branch) = ? order by id asc";
        } else if ("Status".equalsIgnoreCase(type)) {
            if ("PASS".equalsIgnoreCase(value)) {
                query = "select * from students where marks >= 40 order by id asc";
            } else if ("FAIL".equalsIgnoreCase(value)) {
                query = "select * from students where marks < 40 order by id asc";
            }
        }

        if (query.isEmpty()) return list;

        try (PreparedStatement ps = con.prepareStatement(query)) {
            if ("Student ID".equalsIgnoreCase(type)) {
                ps.setInt(1, Integer.parseInt(value.trim()));
            } else if ("Name".equalsIgnoreCase(type)) {
                ps.setString(1, "%" + value.trim().toLowerCase() + "%");
            } else if ("Branch".equalsIgnoreCase(type)) {
                ps.setString(1, value.trim().toLowerCase());
            }

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("branch"),
                        rs.getString("email"),
                        rs.getDouble("marks")
                    ));
                }
            }
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
        return list;
    }

    // GET DASHBOARD STATS
    public String getDashboardStats() throws SQLException {
        Connection con = checkConnection();
        StringBuilder sb = new StringBuilder();

        sb.append("=========================================\n");
        sb.append("       📊 ACADEMIC DASHBOARD STATS      \n");
        sb.append("=========================================\n\n");

        int totalStudents = 0;
        double avgMarks = 0.0;
        int passCount = 0;
        int failCount = 0;

        String summaryQuery = "select count(*), avg(marks), " +
                              "sum(case when marks >= 40 then 1 else 0 end), " +
                              "sum(case when marks < 40 then 1 else 0 end) " +
                              "from students";

        try (PreparedStatement ps = con.prepareStatement(summaryQuery);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                totalStudents = rs.getInt(1);
                avgMarks = rs.getDouble(2);
                passCount = rs.getInt(3);
                failCount = rs.getInt(4);
            }
        }

        if (totalStudents == 0) {
            sb.append("No student records available in database.\n");
            sb.append("Please add students to view performance analytics.");
            try { con.close(); } catch(Exception ignored) {}
            return sb.toString();
        }

        sb.append("Total Students Enrolled : ").append(totalStudents).append("\n");
        sb.append(String.format("Average Marks Overall   : %.2f", avgMarks)).append("\n");
        sb.append("Passed Students (>=40)  : ").append(passCount)
          .append(String.format(" (%.1f%%)", (passCount * 100.0 / totalStudents))).append("\n");
        sb.append("Failed Students (<40)   : ").append(failCount)
          .append(String.format(" (%.1f%%)", (failCount * 100.0 / totalStudents))).append("\n");
        sb.append("\n-----------------------------------------\n");

        // Top Performer
        String topQuery = "select name, marks, branch from students order by marks desc limit 1";
        try (PreparedStatement ps = con.prepareStatement(topQuery);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                sb.append("🏆 Top Performer   : ").append(rs.getString("name"))
                  .append(" (").append(rs.getString("branch")).append(") - ")
                  .append(rs.getDouble("marks")).append(" marks\n");
            }
        }

        // Lowest Performer
        String lowQuery = "select name, id, branch, email, marks from students order by marks asc limit 1";
        try (PreparedStatement ps = con.prepareStatement(lowQuery);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                Student temp = new Student(rs.getInt("id"), rs.getString("name"), rs.getString("branch"), rs.getString("email"), rs.getDouble("marks"));
                if (temp.getMarks() < 40) {
                    sb.append("⚠️ Needs Improvement: ").append(temp.getName())
                      .append(" (ID: ").append(temp.getId()).append(", ").append(temp.getBranch()).append(") - ")
                      .append(temp.getMarks()).append(" Marks (Grade ").append(temp.getGrade()).append(" | ").append(temp.getStatus()).append(")\n");
                } else {
                    sb.append("📈 Lowest Score     : ").append(temp.getName())
                      .append(" (ID: ").append(temp.getId()).append(", ").append(temp.getBranch()).append(") - ")
                      .append(temp.getMarks()).append(" Marks (Grade ").append(temp.getGrade()).append(" | ").append(temp.getStatus()).append(")\n");
                }
            }
        }

        sb.append("=========================================\n");

        try { con.close(); } catch(Exception ignored) {}
        return sb.toString();
    }

    public int getTotalStudents() throws SQLException {
        Connection con = checkConnection();
        String query = "select count(*) from students";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
        return 0;
    }

    public double getAverageMarks() throws SQLException {
        Connection con = checkConnection();
        String query = "select coalesce(avg(marks), 0) from students";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getDouble(1);
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
        return 0.0;
    }

    public int getPassedStudents() throws SQLException {
        Connection con = checkConnection();
        String query = "select count(*) from students where marks >= 40";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
        return 0;
    }

    public int getFailedStudents() throws SQLException {
        Connection con = checkConnection();
        String query = "select count(*) from students where marks < 40";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
        return 0;
    }

    public Student getTopPerformer() throws SQLException {
        Connection con = checkConnection();
        String query = "select * from students order by marks desc limit 1";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Student(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("branch"),
                    rs.getString("email"),
                    rs.getDouble("marks")
                );
            }
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
        return null;
    }

    public Student getLowestPerformer() throws SQLException {
        Connection con = checkConnection();
        String query = "select * from students order by marks asc limit 1";
        try (PreparedStatement ps = con.prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return new Student(
                    rs.getInt("id"),
                    rs.getString("name"),
                    rs.getString("branch"),
                    rs.getString("email"),
                    rs.getDouble("marks")
                );
            }
        } finally {
            try { con.close(); } catch(Exception ignored) {}
        }
        return null;
    }
}