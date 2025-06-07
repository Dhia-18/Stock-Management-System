package main.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.Database.DatabaseConnection;
import main.Entities.Customer;

public class CustomerModel {
    public void saveCustomer(String name, String service, String email) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "INSERT INTO customers (name, service, email) VALUES (?, CAST(? AS services), ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        statement.setString(2, service);
        statement.setString(3, email);
        statement.executeUpdate();

        connection.close();
    }

    public List<Customer> getAllCustomer() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT * FROM customers";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        List<Customer> list = new ArrayList<>();
        while(result.next()){
            list.add(new Customer(result.getInt("id"), result.getString("name"), result.getString("service"), result.getString("email")));
        }
        connection.close();
        return(list);
    }

    public List<String> getAllCustomerNames() throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
    
        String sql = "SELECT name FROM customers";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();
    
        List<String> names = new ArrayList<>();
        while (result.next()) {
            names.add(result.getString("name"));
        }
    
        connection.close();
        return names;
    }
    
    public int findIdByName(String name) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
    
        String sql = "SELECT id FROM customers WHERE name = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, name);
    
        ResultSet result = statement.executeQuery();
    
        connection.close();
        if (result.next()) {
            return(result.getInt("id"));
        } else{
            throw new SQLException("Order not found: "+ name);
        }
    }

    public void updateCustomer(int id, String name, String service, String email) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "UPDATE customers SET name = ?, service = CAST(? AS services), email = ? WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        statement.setString(2, service);
        statement.setString(3, email);
        statement.setInt(4, id);
        statement.executeUpdate();

        connection.close();
    }

    public void deleteCustomer(int id) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "DELETE FROM customers WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeUpdate();

        connection.close();
    }

    public String findServiceByName(String name) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT service::text AS service FROM customers WHERE name=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        ResultSet resultSet = statement.executeQuery();
        
        connection.close();
        return(resultSet.next() ? resultSet.getString("service"): null);
    }

    public int countCustomers() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT COUNT(*) AS count FROM customers";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        connection.close();
        return(result.next() ? result.getInt("count") : 0);
    }
}
