package main.Entities;

public class Customer {
    private int id;
    private String name;
    private String service;
    private String email;

    public Customer(int id, String name, String service, String email){
        this.id = id;
        this.name=name;
        this.service=service;
        this.email=email;
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
    
    public String getEmail() {
        return email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    
}
