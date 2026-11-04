package org.example.medicare;

public class DoctorModel {
    private int id;
    private String name, specialization, contact;

    public DoctorModel(int id, String name, String specialization, String contact) {
        this.id = id;
        this.name = name;
        this.specialization = specialization;
        this.contact = contact;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getSpecialization() { return specialization; }
    public String getContact() { return contact; }

    @Override
    public String toString() {
        return name + " (" + specialization + ")";
    }
}