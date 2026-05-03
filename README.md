# 🛒 Smart Cart OS - Java Swing E-Commerce Application

Welcome to the **Smart Cart OS**! This is a robust, feature-rich desktop application built using Java and Java Swing. It simulates a modern shopping cart system with real-time stock management, interactive user interfaces, and modular architecture.

## 🌟 Key Features

- **Interactive GUI**: Built entirely with Java Swing, offering a clean, user-friendly interface with `JTable` components for seamless data viewing.
- **Real-Time Stock Management**: Products automatically update their available stock when added to or removed from the cart. 
- **Validation Mechanisms**: Built-in safeguards against over-ordering (insufficient stock) and negative quantities.
- **Cart Management**: Users can dynamically add items, remove them, and adjust quantities. The total price is calculated on the fly.
- **Modular Architecture**: Strictly follows the Model-View-Controller (MVC) pattern principles, separating the user interface from the underlying business logic.

---

## 🏗️ System Architecture

The application is structured into three main layers: the View (GUI), the Controller (Manager), and the Data Models. This ensures clean separation of concerns and maintainability.

```mermaid
graph TD
    User([User]) -->|Interacts| GUI[ShoppingCartGUI]
    GUI -->|Method Calls| Controller[ShoppingCartManager]
    Controller -->|Manages| Models[Models: Product, CartItem]
    
    subgraph "View Layer (UI)"
    GUI
    end
    
    subgraph "Business Logic Layer (Controller)"
    Controller
    end
    
    subgraph "Data Layer (Models)"
    Models
    end
    
    style User fill:#f9f,stroke:#333,stroke-width:2px
    style GUI fill:#bbf,stroke:#333,stroke-width:2px
    style Controller fill:#bfb,stroke:#333,stroke-width:2px
    style Models fill:#fbb,stroke:#333,stroke-width:2px
```

---

## 🧩 Class Diagram

The following class diagram details the relationships between the core components of the Smart Cart OS.

```mermaid
classDiagram
    class ShoppingCartSystem {
        +main(String[] args)
    }

    class ShoppingCartGUI {
        -ShoppingCartManager manager
        -JTable productTable
        -JTable cartTable
        -JLabel totalLabel
        +ShoppingCartGUI()
        +updateTables()
    }

    class ShoppingCartManager {
        -List~Product~ products
        -List~CartItem~ cart
        +ShoppingCartManager()
        +getAvailableProducts() List~Product~
        +getCartItems() List~CartItem~
        +addToCart(int productId, int quantity) boolean
        +removeFromCart(int productId) boolean
        +calculateTotal() double
        +checkout()
    }

    class Product {
        -int id
        -String name
        -double price
        -int stock
        +Product(int id, String name, double price, int stock)
        +reduceStock(int quantity)
        +restoreStock(int quantity)
    }

    class CartItem {
        -Product product
        -int quantity
        +CartItem(Product product, int quantity)
        +getTotalPrice() double
    }

    ShoppingCartSystem ..> ShoppingCartGUI : Launches
    ShoppingCartGUI --> ShoppingCartManager : Uses
    ShoppingCartManager *-- Product : Maintains Inventory
    ShoppingCartManager *-- CartItem : Maintains Cart
    CartItem o-- Product : References
```

---

## 🔄 User Flows & Logic

### 1. Adding an Item to the Cart

When a user attempts to add an item to their cart, several checks are performed to ensure data integrity and stock availability.

```mermaid
sequenceDiagram
    actor User
    participant GUI as ShoppingCartGUI
    participant Manager as ShoppingCartManager
    participant Product as Product Model

    User->>GUI: Select Product & Quantity -> "Add to Cart"
    GUI->>Manager: addToCart(productId, quantity)
    Manager->>Manager: Verify product exists
    alt Product not found
        Manager-->>GUI: Throw Exception("Invalid Product ID")
        GUI-->>User: Show Error Dialog
    else Product found
        Manager->>Product: Check available stock
        alt Stock < quantity
            Manager-->>GUI: Throw Exception("Insufficient stock")
            GUI-->>User: Show Error Dialog
        else Stock sufficient
            Manager->>Product: reduceStock(quantity)
            Manager->>Manager: Add to / Update CartList
            Manager-->>GUI: Return true
            GUI->>GUI: updateTables()
            GUI-->>User: UI updates & Shows Success
        end
    end
```

### 2. Checkout Process

The checkout flow is streamlined to ensure the cart isn't empty before finalizing the purchase.

```mermaid
flowchart TD
    A([User clicks 'Checkout']) --> B{Is Cart Empty?}
    
    B -- Yes --> C[Show Error: 'Cart is empty!']
    C --> Z([End])
    
    B -- No --> D[Calculate Final Total]
    D --> E[Process Payment Simulation]
    E --> F[Clear Cart Data]
    F --> G[Show Success Message: 'Checkout Complete']
    G --> H[Refresh GUI Tables & Totals]
    H --> Z
    
    style A fill:#a2d2ff,stroke:#333,stroke-width:2px
    style Z fill:#ffc8dd,stroke:#333,stroke-width:2px
    style B fill:#fcf6bd,stroke:#333,stroke-width:2px
```

---

## 🛠️ Technology Stack

- **Language**: Java 8+
- **UI Framework**: Java Swing (AWT/Swing components)
- **Design Pattern**: MVC (Model-View-Controller)
- **Architecture**: Monolithic Desktop Application

---

## 🚀 Setup & Installation

Follow these steps to run the application on your local machine.

### Prerequisites
- Ensure you have the **Java Development Kit (JDK)** installed (Version 8 or higher).
- An IDE (like IntelliJ IDEA, Eclipse, VS Code) or command-line interface.

### Running from Command Line

1. **Navigate to the Project Directory**:
   ```bash
   cd path/to/project
   ```

2. **Compile the Java Files**:
   ```bash
   javac *.java
   ```

3. **Run the Application**:
   ```bash
   java ShoppingCartSystem
   ```

### Running from an IDE
1. Open your preferred IDE.
2. Open the directory containing the project files.
3. Locate `ShoppingCartSystem.java`.
4. Right-click and select **Run 'ShoppingCartSystem.main()'**.

---

## 📖 Usage Guide

1. **Browse Products**: Upon launching, the left panel displays all available products, their prices, and current stock levels.
2. **Add to Cart**: 
   - Enter the Product ID and desired Quantity in the input fields at the bottom left.
   - Click **Add to Cart**.
   - If stock is sufficient, the item will appear in your Cart (right panel), and the product's stock will dynamically decrease.
3. **Remove from Cart**:
   - To remove an item, enter its Product ID in the input field on the bottom right.
   - Click **Remove from Cart**. 
   - The item will be deleted from the cart, and its stock will be restored in the inventory.
4. **Checkout**:
   - Once you are happy with your selections, click the **Checkout** button.
   - A summary of your total will be finalized, the cart will clear, and you will be ready for a new transaction.

---

## 🔮 Future Enhancements

- **Database Integration**: Migrate from in-memory arrays to a SQL database (e.g., MySQL, SQLite) for persistent inventory and user data.
- **User Authentication**: Add login/register functionality to save cart states across different sessions.
- **Receipt Generation**: Implement PDF generation to create downloadable invoices upon checkout.
- **Dynamic Theming**: Upgrade the UI with modern LookAndFeel libraries (like FlatLaf) to provide dark/light modes and contemporary aesthetics.
