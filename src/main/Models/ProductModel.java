package main.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.Database.DatabaseConnection;
import main.Entities.Product;

public class ProductModel {

    public List<Product> getAllProduct() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT * FROM products";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        List<Product> list = new ArrayList<>();
        while(result.next()){
            list.add(new Product(result.getInt("ref"), result.getString("name"), result.getString("category"), result.getBoolean("criticism")));
        }
        connection.close();
        return(list);
    }

    public void saveProduct(String productName, String category, LocalDate deliverDate, LocalDate expirationDate, int supplierId, int warehouseId1, int quantity1, int warehouseId2, int quantity2, int warehouseId3, int quantity3, boolean criticism) throws SQLException{
        InventoryModel inventoryModel = new InventoryModel();
        Connection connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false);

        String sql = "INSERT INTO products (name, category, criticism) VALUES (?, CAST(? AS product_category), ?) RETURNING ref";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, productName);
        statement.setString(2, category);
        statement.setBoolean(3, criticism);
        ResultSet result = statement.executeQuery();
        result.next();
        int ref = result.getInt("ref");

        inventoryModel.saveInventory(connection, ref, expirationDate, supplierId, deliverDate, warehouseId1, quantity1);
        if(warehouseId2 != -1){
            inventoryModel.saveInventory(connection, ref, expirationDate, supplierId, deliverDate, warehouseId2, quantity2);
        }
        if(warehouseId3 != -1){
            inventoryModel.saveInventory(connection, ref, expirationDate, supplierId, deliverDate, warehouseId3, quantity3);
        }

        connection.commit();
        connection.close();
    }

    public void deleteProduct(int reference) throws SQLException{
        InventoryModel inventoryModel = new InventoryModel();
        Connection connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false);

        // Delete from inventory's table first
        inventoryModel.deleteInventoryProduct(connection, reference);

        String sql = "DELETE FROM products WHERE ref = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1,reference);
        statement.executeUpdate();
    
        connection.commit();
        connection.close();
    }

    public void updateProduct(int reference, String productName, String category, LocalDate expirationDate, boolean criticism) throws SQLException{
        InventoryModel inventoryModel = new InventoryModel();
        Connection connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false);

        String sql = "UPDATE products SET name = ?, category = CAST(? AS product_category), criticism = ? WHERE ref = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, productName);
        statement.setString(2, category);
        statement.setBoolean(3, criticism);
        statement.setInt(4, reference);
        statement.executeUpdate();

        inventoryModel.updateExpirationDate(connection, reference, expirationDate);

        connection.commit();
        connection.close();
    }

    public Product findProductByRef(int ref) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql="SELECT * FROM products WHERE ref = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, ref);
        ResultSet result = statement.executeQuery();
        if(result.next()){
            return(new Product(ref, result.getString("name"), result.getString("category"), result.getBoolean("criticism")));
        }
        return(null);
    }

    public int countProducts() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT COUNT(*) AS count FROM products";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        connection.close();
        return(result.next() ? result.getInt("count") : 0);
    }
}
