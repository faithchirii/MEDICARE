package org.example.medicare;

public class Appointment {
    private int id;
    private String patientName, doctorName, date, time, reason, status;

    public Appointment(int id, String patientName, String doctorName, String date, String time, String reason, String status) {
        this.id = id;
        this.patientName = patientName;
        this.doctorName = doctorName;
        this.date = date;
        this.time = time;
        this.reason = reason;
        this.status = status;
    }

    public int getId() { return id; }
    public String getPatientName() { return patientName; }
    public String getDoctorName() { return doctorName; }
    public String getDate() { return date; }
    public String getTime() { return time; }
    public String getReason() { return reason; }
    public String getStatus() { return status; }
}