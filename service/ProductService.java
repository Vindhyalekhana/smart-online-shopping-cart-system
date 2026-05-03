package service;

import model.Product;
import java.util.ArrayList;
import java.util.List;

public class ProductService {
    private List<Product> catalog;

    public ProductService() {
        catalog = new ArrayList<>();
        loadMockData();
    }

    private void loadMockData() {
        catalog.add(new Product(1, "MacBook Pro M3", "Electronics", "Apple 14-inch Laptop", 150000, 10, 5));
        catalog.add(new Product(2, "iPhone 15", "Electronics", "Apple Smartphone", 80000, 15, 0));
        catalog.add(new Product(3, "Sony WH-1000XM5", "Electronics", "Noise Cancelling Headphones", 30000, 50, 10));
        catalog.add(new Product(4, "Nike Air Max", "Fashion", "Running Shoes", 12000, 20, 15));
        catalog.add(new Product(5, "Levi's Jeans", "Fashion", "Blue Denim", 3500, 40, 5));
        catalog.add(new Product(6, "Almond Milk", "Grocery", "1L Unsweetened", 300, 100, 0));
        catalog.add(new Product(7, "Whole Wheat Bread", "Grocery", "Freshly baked", 60, 30, 0));
    }

    public List<Product> getAllProducts() {
        return new ArrayList<>(catalog);
    }

    public List<Product> searchProducts(String query, String category) {
        List<Product> results = new ArrayList<>();
        for (Product p : catalog) {
            boolean matchesSearch = query.isEmpty() || p.getName().toLowerCase().contains(query.toLowerCase()) || p.getDescription().toLowerCase().contains(query.toLowerCase());
            boolean matchesCategory = category.equals("All Categories") || p.getCategory().equalsIgnoreCase(category);
            
            if (matchesSearch && matchesCategory) {
                results.add(p);
            }
        }
        return results;
    }

    public Product getProductById(int id) {
        for (Product p : catalog) {
            if (p.getId() == id) {
                return p;
            }
        }
        return null;
    }
}
