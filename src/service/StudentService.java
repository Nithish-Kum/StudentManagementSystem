package service;

import dao.StudentDAO;
import model.Student;
import java.sql.SQLException;

public class StudentService {

      StudentDAO dao = new StudentDAO();

      public boolean login(String username, String password) throws SQLException {
            return dao.validateUser(username, password);
      }

      public int getNextId() throws SQLException {
            return dao.getNextStudentId();
      }

      public boolean add(Student s) throws SQLException {
            return dao.addStudent(s);
      }

      public String view() throws SQLException {
            return dao.viewStudents();
      }

      public java.util.List<Student> getAllStudents() throws SQLException {
            return dao.getAllStudents();
      }

      public boolean update(Student s) throws SQLException {
            return dao.updateStudent(s);
      }

      public boolean delete(int id) throws SQLException {
            return dao.deleteStudent(id);
      }

      public Student search(int id) throws SQLException {
            return dao.searchStudent(id);
      }

      public java.util.List<Student> searchAdvanced(String idStr, String name, String branch, String status) throws SQLException {
            return dao.searchStudentsAdvanced(idStr, name, branch, status);
      }

      public java.util.List<Student> searchByFilter(String type, String value) throws SQLException {
            return dao.searchByFilter(type, value);
      }

      public String getDashboard() throws SQLException {
            return dao.getDashboardStats();
      }

      public int getTotalStudents() throws SQLException {
            return dao.getTotalStudents();
      }

      public double getAverageMarks() throws SQLException {
            return dao.getAverageMarks();
      }

      public int getPassedStudents() throws SQLException {
            return dao.getPassedStudents();
      }

      public int getFailedStudents() throws SQLException {
            return dao.getFailedStudents();
      }

      public Student getTopPerformer() throws SQLException {
            return dao.getTopPerformer();
      }

      public Student getLowestPerformer() throws SQLException {
            return dao.getLowestPerformer();
      }
}