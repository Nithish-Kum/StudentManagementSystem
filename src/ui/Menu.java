package ui;

import java.util.Scanner;
import model.Student;
import service.StudentService;

public class Menu {

      public void display() {

            Scanner sc = new Scanner(System.in);
            StudentService service = new StudentService();

            while (true) {
                  try {
                        System.out.println("\nStudent Management System");
                        System.out.println("1. Add Student");
                        System.out.println("2. View Students");
                        System.out.println("3. Update Student");
                        System.out.println("4. Delete Student");
                        System.out.println("5. Exit");
                        System.out.print("Enter choice: ");

                        int choice = sc.nextInt();

                        switch (choice) {
                              case 1:
                                    sc.nextLine();
                                    System.out.print("Enter Name: ");
                                    String name = sc.nextLine();
                                    System.out.print("Enter Branch: ");
                                    String branch = sc.nextLine();
                                    System.out.print("Enter Email: ");
                                    String email = sc.nextLine();
                                    System.out.print("Enter Marks: ");
                                    double marks = sc.nextDouble();

                                    Student s = new Student(0, name, branch, email, marks);
                                    if (service.add(s)) {
                                          System.out.println("Student Added Successfully!");
                                    } else {
                                          System.out.println("Failed to add student.");
                                    }
                                    break;

                              case 2:
                                    System.out.println("\n--- Student Records ---");
                                    System.out.println(service.view());
                                    break;

                              case 3:
                                    System.out.print("Enter Student ID: ");
                                    int id = sc.nextInt();

                                    Student existingStudent = service.search(id);
                                    if (existingStudent == null) {
                                          System.out.println("No student found with ID: " + id);
                                          break;
                                    }

                                    System.out.println("\nWhat do you want to update?");
                                    System.out.println("1. Name");
                                    System.out.println("2. Branch");
                                    System.out.println("3. Email");
                                    System.out.println("4. Marks");
                                    System.out.print("Enter choice: ");
                                    int updateChoice = sc.nextInt();
                                    sc.nextLine();

                                    switch (updateChoice) {
                                          case 1:
                                                System.out.print("Enter New Name: ");
                                                existingStudent.setName(sc.nextLine());
                                                break;
                                          case 2:
                                                System.out.print("Enter New Branch: ");
                                                existingStudent.setBranch(sc.nextLine());
                                                break;
                                          case 3:
                                                System.out.print("Enter New Email: ");
                                                existingStudent.setEmail(sc.nextLine());
                                                break;
                                          case 4:
                                                System.out.print("Enter New Marks: ");
                                                existingStudent.setMarks(sc.nextDouble());
                                                break;
                                          default:
                                                System.out.println("Invalid Choice");
                                                break;
                                    }

                                    if (service.update(existingStudent)) {
                                          System.out.println("Student Updated Successfully!");
                                    } else {
                                          System.out.println("Failed to update student.");
                                    }
                                    break;

                              case 4:
                                    System.out.print("Enter Student ID to delete: ");
                                    int deleteId = sc.nextInt();
                                    if (service.delete(deleteId)) {
                                          System.out.println("Student Deleted Successfully!");
                                    } else {
                                          System.out.println("No student found with ID: " + deleteId);
                                    }
                                    break;

                              case 5:
                                    System.exit(0);

                              default:
                                    System.out.println("Invalid Choice. Please enter 1-5.");
                        }
                  } catch (Exception e) {
                        System.err.println("Error: " + e.getMessage());
                        sc.nextLine(); // Clear buffer
                  }
            }
      }
}