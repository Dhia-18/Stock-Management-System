package main.Models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.Database.DatabaseConnection;
import main.Entities.Inventory;

public class InventoryModel {
    public void saveInventory(Connection connection, int ref, LocalDate expirationDate, int supplierId, LocalDate deliverDate, int warehouseId, int quantity) throws SQLException{
        String sql = "INSERT INTO inventory_products (ref, expiration_date, supplier_id, deliver_date, warehouse_id, quantity) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, ref);
        if(expirationDate != null){
            statement.setDate(2, Date.valueOf(expirationDate));
        } else{
            statement.setNull(2, Types.DATE);
        }
        statement.setInt(3, supplierId);
        statement.setDate(4, Date.valueOf(deliverDate));
        statement.setInt(5, warehouseId);
        statement.setInt(6, quantity);

        statement.executeUpdate();
    }

    public void saveInventoryRow(int ref, int supplier_id, int warehouse_id, LocalDate deliverDate, LocalDate expirationDate, int quantity) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "INSERT INTO inventory_products (ref, supplier_id, warehouse_id, deliver_date, expiration_date, quantity) VALUES (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, ref);
        statement.setInt(2, supplier_id);
        statement.setInt(3, warehouse_id);
        statement.setDate(4, Date.valueOf(deliverDate));
        if(expirationDate != null){
            statement.setDate(5, Date.valueOf(expirationDate));
        } else{
            statement.setNull(5, Types.DATE);
        }
        statement.setInt(6, quantity);
        statement.executeUpdate();
    
        connection.close();
    }

    public void deleteInventoryProduct(Connection connection, int ref) throws SQLException{
        String sql = "DELETE FROM inventory_products WHERE ref = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, ref);

        statement.executeUpdate();
    }

    public Date getExpirationDate(int reference) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();
        
        String sql = "SELECT expiration_date FROM inventory_products WHERE ref = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, reference);
        ResultSet result = statement.executeQuery();
        result.next();
        return(result.getDate("expiration_date"));
    }

    public void updateExpirationDate(Connection connection, int reference, LocalDate expirationDate) throws SQLException{
        String sql = "UPDATE inventory_products SET expiration_date = ? WHERE ref=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        if(expirationDate != null){
            statement.setDate(1, Date.valueOf(expirationDate));
        } else{
            statement.setNull(1, Types.DATE);
        }
        statement.setInt(2, reference);

        statement.executeUpdate();
    }

    public void deleteInventory(int id) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "DELETE FROM inventory_products WHERE id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, id);
        statement.executeUpdate();

        connection.close();
    }

    public List<Inventory> getAllInventoryRows() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT * FROM inventory_products";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        List<Inventory> list = new ArrayList<>();
        while(result.next()){
            list.add(new Inventory(result.getInt("id"), result.getInt("ref"), result.getDate("expiration_date"), result.getInt("supplier_id"), result.getDate("deliver_date"), result.getInt("warehouse_id"), result.getInt("quantity")));
        }
        connection.close();
        return(list);
    }

    public void updateInventory(int id, int supplierId, int warehouseId, LocalDate deliverDate, LocalDate expirationDate, int quantity) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "UPDATE inventory_products SET supplier_id = ?, warehouse_id = ?, deliver_date = ?, expiration_date = ?, quantity = ? WHERE id=?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, supplierId);
        statement.setInt(2, warehouseId);
        statement.setDate(3, Date.valueOf(deliverDate));
        if(expirationDate != null){
            statement.setDate(4, Date.valueOf(expirationDate));
        } else{
            statement.setNull(4, Types.DATE);
        }
        statement.setInt(5, quantity);
        statement.setInt(6, id);
        statement.executeUpdate();

        connection.close();
    }

    public int getAvailableQuantity(int ref, int warehouseId) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT COALESCE(SUM(quantity), 0) AS available FROM inventory_products WHERE ref = ? AND warehouse_id = ?";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, ref);
        statement.setInt(2, warehouseId);
        ResultSet result = statement.executeQuery();

        connection.close();
        return(result.next() ? result.getInt("available") : 0);
    }

    public int countLowStock(int min) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT COUNT(DISTINCT ref) AS count FROM inventory_products WHERE quantity < ? AND warehouse_id IN ( SELECT id FROM warehouses WHERE service = 'Magasin') ";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, min);
        ResultSet result = statement.executeQuery();

        connection.close();
        return(result.next() ? result.getInt("count") : 0);
    }

    public int countOutOfStock() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT COUNT(DISTINCT ref) AS count FROM inventory_products WHERE quantity = 0 AND warehouse_id IN ( SELECT id FROM warehouses WHERE service = 'Magasin') ";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        connection.close();
        return(result.next() ? result.getInt("count") : 0);
    }
}
