package service;

import model.CartItem;
import model.Product;
import java.util.ArrayList;
import java.util.List;

public class CartService {
    private List<CartItem> cart;

    public CartService() {
        cart = new ArrayList<>();
    }

    public List<CartItem> getCartItems() {
        return cart;
    }

    public void addToCart(Product product, int quantity) throws Exception {
        if (quantity <= 0) {
            throw new Exception("Quantity must be greater than zero.");
        }
        if (product.getStockQuantity() < quantity) {
            throw new Exception("Insufficient stock! Only " + product.getStockQuantity() + " left.");
        }

        // Check if already in cart
        for (CartItem item : cart) {
            if (item.getProduct().getId() == product.getId()) {
                item.setQuantity(item.getQuantity() + quantity);
                product.reduceStock(quantity);
                return;
            }
        }

        cart.add(new CartItem(product, quantity));
        product.reduceStock(quantity);
    }

    public void updateQuantity(Product product, int newQuantity) throws Exception {
        if (newQuantity <= 0) {
            removeFromCart(product);
            return;
        }

        for (CartItem item : cart) {
            if (item.getProduct().getId() == product.getId()) {
                int diff = newQuantity - item.getQuantity();
                if (diff > 0 && product.getStockQuantity() < diff) {
                    throw new Exception("Insufficient stock for update!");
                }
                
                if (diff > 0) {
                    product.reduceStock(diff);
                } else if (diff < 0) {
                    product.restoreStock(-diff);
                }
                
                item.setQuantity(newQuantity);
                return;
            }
        }
        throw new Exception("Product not in cart.");
    }

    public void removeFromCart(Product product) {
        for (int i = 0; i < cart.size(); i++) {
            if (cart.get(i).getProduct().getId() == product.getId()) {
                CartItem item = cart.get(i);
                product.restoreStock(item.getQuantity());
                cart.remove(i);
                break;
            }
        }
    }

    public void clearCart() {
        for (CartItem item : cart) {
            item.getProduct().restoreStock(item.getQuantity());
        }
        cart.clear();
    }

    // Force clear after checkout without restoring stock
    public void checkoutClear() {
        cart.clear();
    }

    public double getSubtotal() {
        double total = 0;
        for (CartItem item : cart) {
            total += item.getTotalPrice();
        }
        return total;
    }
}
