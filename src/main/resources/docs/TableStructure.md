Product
------
id: 1
name: iPhone 14
brand: Apple
category: Mobile

ProductVariant
--------------
id | product_id | sku                 | model | storage | color
1  | 1          | IP14-PRO-128-BLK    | Pro   | 128GB   | Black
2  | 1          | IP14-PRO-256-BLK    | Pro   | 256GB   | Black
3  | 1          | IP14-MAX-256-BLU    | Max   | 256GB   | Blue


Product → Catalog item
ProductVariant → Sellable item (has SKU)

inventory
---------
id
sku
quantity
reserved_quantity
updated_at

sku                 quantity
IP14-PRO-128-BLK      50
IP14-PRO-256-BLK      30
IP14-MAX-256-BLU      20

Product
↓
ProductVariant (SKU)
↓
Inventory (quantity)