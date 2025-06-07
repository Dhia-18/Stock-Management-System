package main.Entities;

import java.sql.Date;

public class Order {
    private int id;
    private int ref;
    private int customer_id;
    private Date deliver_date;
    private int warehouse_from_id;
    private int warehouse_to_id;
    private int quantity;
    private String status;
    private String productName;
    private String customerName;
    private String warehouseFromName;
    private String warehouseToName;


    public Order(int id, int ref, int customer_id, Date deliver_date, int warehouse_from_id, int warehouse_to_id, int quantity, String status, String productName, String customerName, String warehouseFromName, String warehouseToName) {
        this.id = id;
        this.ref = ref;
        this.customer_id = customer_id;
        this.deliver_date = deliver_date;
        this.warehouse_from_id = warehouse_from_id;
        this.warehouse_to_id = warehouse_to_id;
        this.quantity = quantity;
        this.status = status;
        this.productName = productName;
        this.customerName = customerName;
        this.warehouseFromName = warehouseFromName;
        this.warehouseToName = warehouseToName;
    }


    public int getId() {
        return id;
    }
    public int getRef() {
        return ref;
    }
    public int getCustomer_id() {
        return customer_id;
    }
    public Date getDeliver_date() {
        return deliver_date;
    }
    public int getWarehouse_from_id() {
        return warehouse_from_id;
    }
    public int getWarehouse_to_id() {
        return warehouse_to_id;
    }
    public int getQuantity() {
        return quantity;
    }
    public String getStatus() {
        return status;
    }
    public String getProductName() {
        return productName;
    }
    public void setProductName(String productName) {
        this.productName = productName;
    }
    public String getCustomerName() {
        return customerName;
    }
    public void setId(int id) {
        this.id = id;
    }
    public void setRef(int ref) {
        this.ref = ref;
    }
    public void setCustomer_id(int customer_id) {
        this.customer_id = customer_id;
    }
    public void setDeliver_date(Date deliver_date) {
        this.deliver_date = deliver_date;
    }
    public void setWarehouse_from_id(int warehouse_from_id) {
        this.warehouse_from_id = warehouse_from_id;
    }
    public void setWarehouse_to_id(int warehouse_to_id) {
        this.warehouse_to_id = warehouse_to_id;
    }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    public void setStatus(String status) {
        this.status = status;
    }
    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }
    public String getWarehouseFromName() {
        return warehouseFromName;
    }
    public void setWarehouseFromName(String warehouseFromName) {
        this.warehouseFromName = warehouseFromName;
    }
    public String getWarehouseToName() {
        return warehouseToName;
    }
    public void setWarehouseToName(String warehouseToName) {
        this.warehouseToName = warehouseToName;
    }
}
