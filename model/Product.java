package model;

public class Product {
    private int id;
    private String name;
    private String category;
    private String description;
    private double price;
    private int stockQuantity;
    private double discountPercent;
    
    public Product(int id, String name, String category, String description, double price, int stockQuantity, double discountPercent) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.description = description;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.discountPercent = discountPercent;
    }

    public int getId() { return id; }
    public String getName() { return name; }
    public String getCategory() { return category; }
    public String getDescription() { return description; }
    public double getPrice() { return price; }
    public int getStockQuantity() { return stockQuantity; }
    public double getDiscountPercent() { return discountPercent; }

    public double getDiscountedPrice() {
        return price * (1 - (discountPercent / 100.0));
    }

    public void reduceStock(int quantity) {
        if (quantity <= stockQuantity) {
            this.stockQuantity -= quantity;
        }
    }

    public void restoreStock(int quantity) {
        this.stockQuantity += quantity;
    }

    @Override
    public String toString() {
        return name;
    }
}
