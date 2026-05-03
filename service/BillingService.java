package service;

import model.CartItem;
import model.Order;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.UUID;

public class BillingService {
    private static final double TAX_RATE = 0.18; // 18% tax

    public Order generateOrder(CartService cartService) throws Exception {
        if (cartService.getCartItems().isEmpty()) {
            throw new Exception("Cannot checkout an empty cart.");
        }

        String orderId = "ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        double subtotal = cartService.getSubtotal();
        double tax = subtotal * TAX_RATE;
        double discount = 0; // Global discount could go here
        double finalTotal = subtotal + tax - discount;

        Order order = new Order(orderId, cartService.getCartItems(), subtotal, tax, discount, finalTotal);
        
        // Clear cart for the next order (without restoring stock)
        cartService.checkoutClear();

        return order;
    }

    public void generateInvoiceFile(Order order) throws IOException {
        File invoiceDir = new File("invoices");
        if (!invoiceDir.exists()) {
            invoiceDir.mkdir();
        }

        File invoiceFile = new File(invoiceDir, order.getOrderId() + ".txt");
        try (FileWriter writer = new FileWriter(invoiceFile)) {
            writer.write("=========================================\n");
            writer.write("          SMART SHOPPING INVOICE         \n");
            writer.write("=========================================\n");
            writer.write("Order ID: " + order.getOrderId() + "\n");
            writer.write("Date: " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(order.getOrderDate()) + "\n");
            writer.write("-----------------------------------------\n");
            writer.write(String.format("%-20s %-5s %-10s\n", "Item", "Qty", "Total"));
            writer.write("-----------------------------------------\n");

            for (CartItem item : order.getItems()) {
                writer.write(String.format("%-20s %-5d %.2f\n", 
                    item.getProduct().getName(), 
                    item.getQuantity(), 
                    item.getTotalPrice()));
            }

            writer.write("-----------------------------------------\n");
            writer.write(String.format("Subtotal: %.2f\n", order.getSubtotal()));
            writer.write(String.format("Tax (18%%): %.2f\n", order.getTax()));
            if (order.getDiscount() > 0) {
                writer.write(String.format("Discount: -%.2f\n", order.getDiscount()));
            }
            writer.write("-----------------------------------------\n");
            writer.write(String.format("GRAND TOTAL: ₹%.2f\n", order.getFinalTotal()));
            writer.write("=========================================\n");
            writer.write("      Thank you for shopping with us!    \n");
            writer.write("=========================================\n");
        }
    }
}
