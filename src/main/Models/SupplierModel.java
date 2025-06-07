package main.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.Database.DatabaseConnection;
import main.Entities.Supplier;


public class SupplierModel {
    public List<Supplier> getAllSupplier() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT * FROM suppliers";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        List<Supplier> list = new ArrayList<>();
        while(result.next()){
            list.add(new Supplier(result.getInt("id"), result.getString("name"), result.getString("address"), result.getString("email"), result.getString("phone")));
        }
        connection.close();
        return(list);
    }

    public List<String> getAllSupplierNames() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT name FROM suppliers";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        List<String> list = new ArrayList<>();
        while(result.next()){
            list.add(result.getString("name"));
        }
        connection.close();
        return(list);
    }

    public int findIdByName(String name) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT id FROM suppliers WHERE name = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        ResultSet result = statement.executeQuery();

        connection.close();
        if(result.next()){
            return(result.getInt("id"));
        } else{
            throw new SQLException("Supplier not found: " + name);
        }
    }

    public void saveSupplier(String name, String address, String email, String phone) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "INSERT INTO suppliers (name, address, email, phone) VALUES (?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        statement.setString(2, address);
        statement.setString(3, email);
        statement.setString(4, phone);
        statement.executeUpdate();

        connection.close();
    }

    public void updateSupplier(int id, String supplierName, String address, String email, String phone) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "UPDATE suppliers SET name = ?, address = ?, email = ?, phone = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, supplierName);
        statement.setString(2, address);
        statement.setString(3, email);
        statement.setString(4, phone);
        statement.setInt(5, id);
        statement.executeUpdate();

        connection.close();
    }

    public void deleteSupplier(int id) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();
        
        String sql = "DELETE FROM suppliers WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeUpdate();

        connection.close();
    }

    public String findNameById(int id) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT name FROM suppliers WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        result.next();
        return(result.getString("name"));
    }

    public int countSuppliers() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT COUNT(*) AS count FROM suppliers";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        connection.close();
        return(result.next() ? result.getInt("count") : 0);
    }
}
