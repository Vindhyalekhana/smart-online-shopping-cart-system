package model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Order {
    private String orderId;
    private List<CartItem> items;
    private double subtotal;
    private double tax;
    private double discount;
    private double finalTotal;
    private Date orderDate;

    public Order(String orderId, List<CartItem> items, double subtotal, double tax, double discount, double finalTotal) {
        this.orderId = orderId;
        // Deep copy items to preserve state at checkout time
        this.items = new ArrayList<>();
        for (CartItem item : items) {
            this.items.add(new CartItem(item.getProduct(), item.getQuantity()));
        }
        this.subtotal = subtotal;
        this.tax = tax;
        this.discount = discount;
        this.finalTotal = finalTotal;
        this.orderDate = new Date();
    }

    public String getOrderId() { return orderId; }
    public List<CartItem> getItems() { return items; }
    public double getSubtotal() { return subtotal; }
    public double getTax() { return tax; }
    public double getDiscount() { return discount; }
    public double getFinalTotal() { return finalTotal; }
    public Date getOrderDate() { return orderDate; }
}
