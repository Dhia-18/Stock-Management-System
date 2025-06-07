package main.Models;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.Database.DatabaseConnection;
import main.Entities.Warehouse;

public class WarehouseModel {
    public List<String> getAllWarehouseNames() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT name FROM warehouses";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        List<String> list = new ArrayList<>();
        while(result.next()){
            list.add(result.getString("name"));
        }
        connection.close();
        return(list);
    }

    public List<String> getAllStoresNames() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT name FROM warehouses WHERE service = 'Magasin'";
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

        String sql = "SELECT id FROM warehouses WHERE name = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        ResultSet result = statement.executeQuery();
        
        connection.close();
        if(result.next()){
            return(result.getInt("id"));
        } else{
            throw new SQLException("Warehouse not found: " + name);
        }
    }

    public List<Warehouse> getAllWarehouse() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT * FROM warehouses";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        List<Warehouse> list = new ArrayList<>();
        while(result.next()){
            list.add(new Warehouse(result.getInt("id"), result.getString("name"), result.getString("service")));
        }
        connection.close();
        return(list);
    }

    public void saveWarehouse(String warehouseName, String service) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "INSERT INTO warehouses (name, service) VALUES (?, CAST(? AS services))";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, warehouseName);
        statement.setString(2, service);
        statement.executeUpdate();

        connection.close();
    }

    public void deleteWarehouse(int id) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();
        
        String sql = "DELETE FROM warehouses WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeUpdate();

        connection.close();
    }

    public void updateWarehouse(int id, String warehouseName, String service) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "UPDATE warehouses SET name = ?, service = CAST(? AS services) WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, warehouseName);
        statement.setString(2, service);
        statement.setInt(3, id);
        statement.executeUpdate();

        connection.close();
    }

    public String findNameById(int id) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT name FROM warehouses WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        ResultSet result = statement.executeQuery();
        result.next();
        return(result.getString("name"));
    }

    public String findServiceByName(String name) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT service::text AS service FROM warehouses WHERE name=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setString(1, name);
        ResultSet resultSet = statement.executeQuery();
        return(resultSet.next() ? resultSet.getString("service"): null);
    }
}
