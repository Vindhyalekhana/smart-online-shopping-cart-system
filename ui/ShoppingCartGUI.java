package ui;

import model.CartItem;
import model.Order;
import model.Product;
import service.BillingService;
import service.CartService;
import service.ProductService;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.util.List;

public class ShoppingCartGUI extends JFrame {
    private ProductService productService;
    private CartService cartService;
    private BillingService billingService;

    // UI Components
    private JTable productTable;
    private DefaultTableModel productTableModel;
    private JTextField searchField;
    private JComboBox<String> categoryFilter;

    private JLabel prodNameLabel;
    private JTextArea prodDescArea;
    private JLabel prodPriceLabel;
    private JLabel prodStockLabel;
    private JSpinner quantitySpinner;
    private JButton addToCartBtn;

    private JTable cartTable;
    private DefaultTableModel cartTableModel;
    
    private JLabel subtotalLabel;
    private JLabel taxLabel;
    private JLabel totalLabel;

    public ShoppingCartGUI() {
        productService = new ProductService();
        cartService = new CartService();
        billingService = new BillingService();

        setTitle("Smart Online Shopping Cart System");
        setSize(1100, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        initUI();
        refreshProductTable();
        refreshCartTable();
    }

    private void initUI() {
        // Title
        JLabel titleLabel = new JLabel("SMART ONLINE SHOPPING CART", SwingConstants.CENTER);
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setBorder(BorderFactory.createEmptyBorder(15, 0, 15, 0));
        add(titleLabel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout(10, 10));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(0, 10, 10, 10));
        add(mainPanel, BorderLayout.CENTER);

        // --- LEFT PANEL: Catalog ---
        JPanel leftPanel = new JPanel(new BorderLayout(5, 5));
        leftPanel.setPreferredSize(new Dimension(350, 0));
        leftPanel.setBorder(BorderFactory.createTitledBorder("Product Catalog"));

        JPanel searchPanel = new JPanel(new BorderLayout(5, 5));
        searchField = new JTextField();
        searchField.addActionListener(e -> refreshProductTable());
        
        categoryFilter = new JComboBox<>(new String[]{"All Categories", "Electronics", "Fashion", "Grocery"});
        categoryFilter.addActionListener(e -> refreshProductTable());

        searchPanel.add(new JLabel("Search:"), BorderLayout.WEST);
        searchPanel.add(searchField, BorderLayout.CENTER);
        searchPanel.add(categoryFilter, BorderLayout.SOUTH);
        leftPanel.add(searchPanel, BorderLayout.NORTH);

        productTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Price"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        productTable = new JTable(productTableModel);
        productTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        productTable.getSelectionModel().addListSelectionListener(this::onProductSelected);
        leftPanel.add(new JScrollPane(productTable), BorderLayout.CENTER);

        // --- MIDDLE PANEL: Product Details ---
        JPanel middlePanel = new JPanel();
        middlePanel.setLayout(new BoxLayout(middlePanel, BoxLayout.Y_AXIS));
        middlePanel.setBorder(BorderFactory.createTitledBorder("Product Details"));
        middlePanel.setPreferredSize(new Dimension(300, 0));

        prodNameLabel = new JLabel("Select a product...");
        prodNameLabel.setFont(new Font("Arial", Font.BOLD, 16));
        prodDescArea = new JTextArea(4, 20);
        prodDescArea.setEditable(false);
        prodDescArea.setLineWrap(true);
        prodDescArea.setWrapStyleWord(true);
        prodPriceLabel = new JLabel("Price: ");
        prodStockLabel = new JLabel("Stock: ");
        
        JPanel spinnerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        spinnerPanel.add(new JLabel("Qty:"));
        quantitySpinner = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        spinnerPanel.add(quantitySpinner);

        addToCartBtn = new JButton("Add to Cart");
        addToCartBtn.setEnabled(false);
        addToCartBtn.addActionListener(e -> handleAddToCart());

        middlePanel.add(prodNameLabel);
        middlePanel.add(Box.createVerticalStrut(10));
        middlePanel.add(new JScrollPane(prodDescArea));
        middlePanel.add(Box.createVerticalStrut(10));
        middlePanel.add(prodPriceLabel);
        middlePanel.add(Box.createVerticalStrut(5));
        middlePanel.add(prodStockLabel);
        middlePanel.add(Box.createVerticalStrut(10));
        middlePanel.add(spinnerPanel);
        middlePanel.add(Box.createVerticalStrut(10));
        middlePanel.add(addToCartBtn);

        // --- RIGHT PANEL: Cart ---
        JPanel rightPanel = new JPanel(new BorderLayout(5, 5));
        rightPanel.setBorder(BorderFactory.createTitledBorder("Shopping Cart"));
        rightPanel.setPreferredSize(new Dimension(350, 0));

        cartTableModel = new DefaultTableModel(new Object[]{"ID", "Name", "Qty", "Total"}, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };
        cartTable = new JTable(cartTableModel);
        cartTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        rightPanel.add(new JScrollPane(cartTable), BorderLayout.CENTER);

        JPanel cartActionsPanel = new JPanel(new GridLayout(1, 3, 5, 0));
        JButton updateQtyBtn = new JButton("Update Qty");
        JButton removeBtn = new JButton("Remove");
        JButton clearCartBtn = new JButton("Clear Cart");
        
        updateQtyBtn.addActionListener(e -> handleUpdateQuantity());
        removeBtn.addActionListener(e -> handleRemoveFromCart());
        clearCartBtn.addActionListener(e -> handleClearCart());

        cartActionsPanel.add(updateQtyBtn);
        cartActionsPanel.add(removeBtn);
        cartActionsPanel.add(clearCartBtn);
        rightPanel.add(cartActionsPanel, BorderLayout.SOUTH);

        // --- Add Panels to Center ---
        JPanel horizontalSplit = new JPanel(new BorderLayout(10, 0));
        horizontalSplit.add(leftPanel, BorderLayout.WEST);
        horizontalSplit.add(middlePanel, BorderLayout.CENTER);
        horizontalSplit.add(rightPanel, BorderLayout.EAST);
        mainPanel.add(horizontalSplit, BorderLayout.CENTER);

        // --- BOTTOM PANEL: Checkout ---
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBorder(BorderFactory.createTitledBorder(BorderFactory.createLineBorder(Color.GRAY), "Checkout summary", TitledBorder.RIGHT, TitledBorder.TOP));
        
        JPanel totalsPanel = new JPanel(new GridLayout(3, 1));
        subtotalLabel = new JLabel("Subtotal: ₹0.00", SwingConstants.RIGHT);
        taxLabel = new JLabel("Tax (18%): ₹0.00", SwingConstants.RIGHT);
        totalLabel = new JLabel("Grand Total: ₹0.00", SwingConstants.RIGHT);
        totalLabel.setFont(new Font("Arial", Font.BOLD, 18));
        
        totalsPanel.add(subtotalLabel);
        totalsPanel.add(taxLabel);
        totalsPanel.add(totalLabel);

        JButton checkoutBtn = new JButton("Proceed to Checkout");
        checkoutBtn.setFont(new Font("Arial", Font.BOLD, 16));
        checkoutBtn.setBackground(new Color(34, 139, 34));
        checkoutBtn.setForeground(Color.WHITE);
        checkoutBtn.setFocusPainted(false);
        checkoutBtn.addActionListener(e -> handleCheckout());

        JPanel checkoutWrapper = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        checkoutWrapper.add(checkoutBtn);

        bottomPanel.add(totalsPanel, BorderLayout.CENTER);
        bottomPanel.add(checkoutWrapper, BorderLayout.EAST);
        add(bottomPanel, BorderLayout.SOUTH);
    }

    private void refreshProductTable() {
        String query = searchField.getText();
        String category = (String) categoryFilter.getSelectedItem();
        List<Product> products = productService.searchProducts(query, category);

        productTableModel.setRowCount(0);
        for (Product p : products) {
            productTableModel.addRow(new Object[]{p.getId(), p.getName(), String.format("₹%.2f", p.getDiscountedPrice())});
        }
    }

    private void refreshCartTable() {
        List<CartItem> items = cartService.getCartItems();
        cartTableModel.setRowCount(0);
        for (CartItem item : items) {
            cartTableModel.addRow(new Object[]{
                item.getProduct().getId(),
                item.getProduct().getName(),
                item.getQuantity(),
                String.format("₹%.2f", item.getTotalPrice())
            });
        }
        updateTotals();
    }

    private void updateTotals() {
        double subtotal = cartService.getSubtotal();
        double tax = subtotal * 0.18;
        double total = subtotal + tax;

        subtotalLabel.setText(String.format("Subtotal: ₹%.2f", subtotal));
        taxLabel.setText(String.format("Tax (18%%): ₹%.2f", tax));
        totalLabel.setText(String.format("Grand Total: ₹%.2f", total));
    }

    private void onProductSelected(ListSelectionEvent e) {
        if (!e.getValueIsAdjusting()) {
            int row = productTable.getSelectedRow();
            if (row != -1) {
                int id = (int) productTableModel.getValueAt(row, 0);
                Product p = productService.getProductById(id);
                if (p != null) {
                    prodNameLabel.setText(p.getName());
                    prodDescArea.setText(p.getDescription());
                    if (p.getDiscountPercent() > 0) {
                        prodPriceLabel.setText(String.format("Price: ₹%.2f (%.0f%% off)", p.getDiscountedPrice(), p.getDiscountPercent()));
                    } else {
                        prodPriceLabel.setText(String.format("Price: ₹%.2f", p.getPrice()));
                    }
                    prodStockLabel.setText("Stock: " + p.getStockQuantity());
                    quantitySpinner.setValue(1);
                    addToCartBtn.setEnabled(true);
                }
            } else {
                addToCartBtn.setEnabled(false);
            }
        }
    }

    private void handleAddToCart() {
        int row = productTable.getSelectedRow();
        if (row != -1) {
            int id = (int) productTableModel.getValueAt(row, 0);
            Product p = productService.getProductById(id);
            int qty = (int) quantitySpinner.getValue();

            try {
                cartService.addToCart(p, qty);
                refreshCartTable();
                refreshProductTable();
                // re-select to update stock view
                productTable.setRowSelectionInterval(row, row);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    private void handleUpdateQuantity() {
        int row = cartTable.getSelectedRow();
        if (row != -1) {
            int id = (int) cartTableModel.getValueAt(row, 0);
            Product p = productService.getProductById(id);
            
            String input = JOptionPane.showInputDialog(this, "Enter new quantity:", cartTableModel.getValueAt(row, 2));
            if (input != null) {
                try {
                    int newQty = Integer.parseInt(input);
                    cartService.updateQuantity(p, newQty);
                    refreshCartTable();
                    refreshProductTable();
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this, "Please enter a valid number.", "Error", JOptionPane.ERROR_MESSAGE);
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Select an item to update.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleRemoveFromCart() {
        int row = cartTable.getSelectedRow();
        if (row != -1) {
            int id = (int) cartTableModel.getValueAt(row, 0);
            Product p = productService.getProductById(id);
            cartService.removeFromCart(p);
            refreshCartTable();
            refreshProductTable();
        } else {
            JOptionPane.showMessageDialog(this, "Select an item to remove.", "Warning", JOptionPane.WARNING_MESSAGE);
        }
    }

    private void handleClearCart() {
        cartService.clearCart();
        refreshCartTable();
        refreshProductTable();
    }

    private void handleCheckout() {
        try {
            Order order = billingService.generateOrder(cartService);
            
            // Show confirmation
            String message = String.format("Order placed successfully!\nOrder ID: %s\nTotal Amount: ₹%.2f\n\nGenerating invoice...", 
                                           order.getOrderId(), order.getFinalTotal());
            JOptionPane.showMessageDialog(this, message, "Checkout Success", JOptionPane.INFORMATION_MESSAGE);
            
            // Generate Invoice
            billingService.generateInvoiceFile(order);
            
            JOptionPane.showMessageDialog(this, "Invoice saved to 'invoices' folder.", "Invoice Generated", JOptionPane.INFORMATION_MESSAGE);
            
            refreshCartTable();
            refreshProductTable();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, ex.getMessage(), "Checkout Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
