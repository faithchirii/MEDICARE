package org.example.medicare;

public class PatientModel {
    private int id;
    private String name, dob, gender, disease, contact, address;

    public PatientModel(int id, String name, String dob, String gender, String disease, String contact, String address) {
        this.id = id;
        this.name = name;
        this.dob = dob;
        this.gender = gender;
        this.disease = disease;
        this.contact = contact;
        this.address = address;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getDob() { return dob; }
    public String getGender() { return gender; }
    public String getDisease() { return disease; }
    public String getContact() { return contact; }
    public String getAddress() { return address; }
}