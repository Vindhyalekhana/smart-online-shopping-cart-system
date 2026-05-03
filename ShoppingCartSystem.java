import ui.ShoppingCartGUI;

import javax.swing.SwingUtilities;

public class ShoppingCartSystem {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {
                // Try to set system look and feel
                javax.swing.UIManager.setLookAndFeel(javax.swing.UIManager.getSystemLookAndFeelClassName());
            } catch (Exception e) {
                // Ignore, keep default
            }
            ShoppingCartGUI gui = new ShoppingCartGUI();
            gui.setVisible(true);
        });
    }
}
