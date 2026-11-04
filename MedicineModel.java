package org.example.medicare;

public class MedicineModel {
    private int id;
    private String name;
    private int quantity;
    private double price;
    private String expiryDate;

    public MedicineModel(int id, String name, int quantity, double price, String expiryDate) {
        this.id = id;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.expiryDate = expiryDate;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public double getPrice() { return price; }
    public String getExpiryDate() { return expiryDate; }
}