# Product Service Use Cases

The **Product Service** is responsible for managing product information in the system.
---

## Core Use Cases

### 1. Add Product
- Admin adds a new product with:
    - Name
    - Description
    - Price
    - SKU
    - Category
- Product service stores the product metadata in the database.

---

### 2. Update Product
- Modify existing product information.
- Supported updates include:
    - Price
    - Description
    - Images
    - Category
    - Other metadata.

---

### 3. Delete Product
- Uses **soft delete** instead of permanent deletion.
- Ensures past orders referencing the product are not broken.
- Product remains in the database but is marked as inactive.

---

### 4. Get Product Details
- Fetch complete product information.
- Used by:
    - Order Service
    - UI / Frontend
    - Search Service
    - Other microservices.

---

### 5. Search Products
- Allows users to search for products using filters such as:
    - Category
    - Price range
    - Rating
    - Keyword.

---

### 6. Product Variants
- Supports multiple variants for a single product.
- Examples:
    - Different sizes
    - Different colors
    - Different configurations.

Example:
- Product: T-Shirt
    - Variant 1: Size M, Color Black
    - Variant 2: Size L, Color White

---

### 7. Product Reviews
- Integrates with the **Review Service**.
- Displays:
    - Customer ratings
    - Customer reviews
    - Average product rating.

---

### 8. Product Availability Check
- Communicates with the **Inventory Service**.
- Checks:
    - Stock availability
    - Quantity remaining
    - Whether the product is in stock or out of stock.