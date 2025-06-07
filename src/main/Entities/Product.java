package main.Entities;

public class Product {
    private int ref;
    private String name;
    private String category;
    private boolean criticism;

    public Product(int ref, String name, String category, boolean criticism){
        this.ref=ref;
        this.name=name;
        this.category=category;
        this.criticism=criticism;
    }

    public int getRef(){
        return(ref);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public boolean getCriticism() {
        return criticism;
    }

    public void setCriticism(boolean criticism) {
        this.criticism = criticism;
    }
}
