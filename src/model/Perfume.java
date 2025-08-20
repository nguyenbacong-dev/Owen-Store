package model;

public class Perfume {
    private String code;
    private String name;
    private int quantity;
    private double price;
    private String scent;
    private String imagePath;

    public Perfume(String code, String name, int quantity, double price, String scent, String imagePath) {
        this.code = code;
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.scent = scent;
        this.imagePath = imagePath;
    }

    public String getCode() { return code; }
    public void setCode(String code) { this.code = code; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getScent() { return scent; }
    public void setScent(String scent) { this.scent = scent; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }
}
