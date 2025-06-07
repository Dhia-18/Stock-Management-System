package main.Entities;

import java.sql.Date;

public class Inventory {
    private int id;
    private int ref;
    private Date expiration_Date;
    private int supplier_id;
    private Date deliver_date;
    private int warehouse_id;
    private int quantity;

    public Inventory(int id, int ref, Date expirationDate, int supplier_id, Date deliverDate, int warehouse_id, int quantity){
        this.id = id;
        this.expiration_Date=expirationDate;
        this.supplier_id= supplier_id;
        this.deliver_date = deliverDate;
        this.warehouse_id= warehouse_id;
        this.quantity= quantity;
        this.ref=ref;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getRef() {
        return ref;
    }
    
    public void setRef(int ref) {
        this.ref = ref;
    }
    
    public Date getExpiration_date() {
        return expiration_Date;
    }
    
    public void setExpiration_date(Date expiration_Date) {
        this.expiration_Date = expiration_Date;
    }

    public Date getDeliver_date(){
        return deliver_date;
    }

    public void setDeliver_date(Date deliverDate){
        deliver_date=deliverDate;
    }
    
    public int getSupplier_id() {
        return supplier_id;
    }
    
    public void setSupplier_id(int supplier_id) {
        this.supplier_id = supplier_id;
    }
    
    public int getWarehouse_id() {
        return warehouse_id;
    }
    
    public void setWarehouse_id(int warehouse_id) {
        this.warehouse_id = warehouse_id;
    }
    
    public int getQuantity() {
        return quantity;
    }
    
    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }
    
}
