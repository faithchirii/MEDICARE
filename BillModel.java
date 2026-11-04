package org.example.medicare;

public class BillModel {
    private int id;
    private String patientName, status;
    private double totalAmount, amountPaid, balance;

    public BillModel(int id, String patientName, double totalAmount, double amountPaid, double balance, String status) {
        this.id = id;
        this.patientName = patientName;
        this.totalAmount = totalAmount;
        this.amountPaid = amountPaid;
        this.balance = balance;
        this.status = status;
    }

    public int getId() { return id; }
    public String getPatientName() { return patientName; }
    public double getTotalAmount() { return totalAmount; }
    public double getAmountPaid() { return amountPaid; }
    public double getBalance() { return balance; }
    public String getStatus() { return status; }
}