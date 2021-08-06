package kr.ac.dgd.db;

import kr.ac.dgd.input.InputUtil;

public class Student {
    private int id;
    private String name;
    private int age;
    private String phone;
    private String email;

    public Student() {}
    public Student(int id, String name, int age, String phone, String email) {
        this.id = id;
        this.name = name;
        this.age = age;
        this.phone = phone;
        this.email = email;
    }

    public static Student buildStudent() {
        Student s = new Student();
        System.out.println("이름은?");
        s.name = InputUtil.getStringFromConsole("no name");
        System.out.println("나이는?");
        s.age = InputUtil.getIntFromConsole();
        System.out.println("번호는?");
        s.phone = InputUtil.getStringFromConsole("no phone number");
        System.out.println("이메일은?");
        s.email = InputUtil.getStringFromConsole("no email");
        return s;
    }

    public int getId(){
        return this.id;
    }

    public String getName(){
        return this.name;
    }

    public int getAge() {
        return this.age;
    }

    public String getPhone() {
        return this.phone;
    }

    public String getEmail() {
        return this.email;
    }
}