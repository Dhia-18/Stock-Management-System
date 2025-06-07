# University Stock Management System: StockWise
![Dashboard's Page](src/ressources/Images/Application%20Screenshoots/Dashboard_Page.png)

## Project Overview
This project aims to create a highly reliable stock management application for university facilities. Designed for daily intensive use in critical university operations, the system ensures robustness with zero tolerance for failures.

## Core Objectives
- Provide real-time stock status
- Track article lifecycle (traceability)
- Manage daily distributions and purchases
- Handle clients (university services) and suppliers
- Conduct inventories
- Ensure rapid response to user needs

***Primary Focus**: Stock Management*

## Key Features

### 1. 📦 Article Management (Products)
**Types**:
- **Consumables** (Office supplies, IT consumables, Cleaning products)
- **Durables** (Furniture, IT equipment, Technology assets)

**Functionalities**:
- Create/modify/delete articles
- Search by characteristics (reference, expiration date, etc)
- Assign articles to storage locations (full/partial)
- Alert system for near-expiry items
- Identify critical consumables

![Products Page](src/ressources/Images/Application%20Screenshoots/Products_Page.png)

### 2. 🏢 Warehouse Management
- Create/modify/delete storage warehouses
- Search locations by characteristics  
*(See Appendix 2 for Warehouse examples)*

![Warehouses Page](src/ressources/Images/Application%20Screenshoots/Warehouses_Page.png)

### 3. 🤝 Supplier Management
- Create/modify/delete suppliers
- Search suppliers by attributes

![Suppliers Page](src/ressources/Images/Application%20Screenshoots/Suppliers_Page.png)

### 4. 🏫 University Service Management (Customers)
*Services include*: Administration, Printing Services, Library, etc.
- Create/modify/delete customer services
- Search services by characteristics

![Customers Page](src/ressources/Images/Application%20Screenshoots/Customers_Page.png)

### 5. 📤 Internal Order Management (Orders)
- Automatic stock reduction upon validation
- Full transaction auditing
- Managed by storekeeper

![Orders Page](src/ressources/Images/Application%20Screenshoots/Orders_Page.png)

### 6. 📊 Inventory Management
- Real-time stock status for annual reporting
- Service/location-specific durable asset tracking

![Inventory Page](src/ressources/Images/Application%20Screenshoots/Inventory_Page.png)

---

## Appendices

### Appendix 1: Product Classification
| Consumables              | Durables               |
|--------------------------|------------------------|
| Office Supplies          | Furniture              |
| IT Consumables           | IT Equipment           |
| Technology Supplies      | Technology Assets      |
| Printing Materials       | Miscellaneous Assets   |
| Cleaning Products        |                        |
| Maintenance Items        |                        |
| Gardening Supplies       |                        |
| Miscellaneous Consumables|                        |

### Appendix 2: Storage Warehouses
| Location Type           | Examples                          |
|-------------------------|-----------------------------------|
| Libraries               | Lib-1, Lib-2...                   |
| Lecture Halls           | Hall-A, Hall-B...                 |
| Classrooms              | A-11, A-12, B-15...               |
| Administration Offices  | MainSecretariat, Office1, Office2 |
| ...                     | ...                               |

---

## Technology Used:
* Javafx
* PostgreSQL

## Notes:
*   This app gets the job done, but it’s not perfect! You might run into a few rough edges, like slower searches with huge inventories or occasional UI quirks. If you spot something wrong or have ideas to improve it, let me know!