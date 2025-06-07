package main.Entities;

public class Warehouse {
    private int id;
    private String name;
    private String service;

    public Warehouse(int id, String name, String service){
        this.id = id;
        this.name = name;
        this.service = service;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getService() {
        return service;
    }
    
    public void setService(String service) {
        this.service = service;
    }
}
