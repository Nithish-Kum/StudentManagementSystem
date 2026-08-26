package model;

public class Student {

    private int id;
    private String name;
    private String branch;
    private String email;
    private double marks;

    public Student(){}

    public Student(int id,String name,String branch,
                   String email,double marks){

        this.id=id;
        this.name=name;
        this.branch=branch;
        this.email=email;
        this.marks=marks;
    }

    public int getId(){
        return id;
    }

    public void setId(int id){
        this.id=id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name=name;
    }

    public String getBranch(){
        return branch;
    }

    public void setBranch(String branch){
        this.branch=branch;
    }

    public String getEmail(){
        return email;
    }

    public void setEmail(String email){
        this.email=email;
    }

    public double getMarks(){
        return marks;
    }

    public void setMarks(double marks){
        this.marks=marks;
    }

    public String getGrade(){
        if(marks >= 90) return "A+";
        if(marks >= 80) return "A";
        if(marks >= 70) return "B";
        if(marks >= 60) return "C";
        if(marks >= 50) return "D";
        return "F";
    }

    public String getStatus(){
        return marks >= 40 ? "PASS" : "FAIL";
    }
}