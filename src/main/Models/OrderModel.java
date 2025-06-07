package main.Models;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import main.Database.DatabaseConnection;
import main.Entities.Order;

public class OrderModel {
    public List<Order> getAllOrders() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();
        
        String sql = """
            SELECT 
                o.id,
                o.ref,
                o.customer_id,
                o.deliver_date,
                o.warehouse_from_id,
                o.warehouse_to_id,
                o.quantity,
                o.status,
                p.name   AS product_name,
                c.name   AS customer_name,
                wf.name  AS warehouse_from_name,
                wt.name  AS warehouse_to_name
            FROM orders o
            JOIN products p  ON o.ref               = p.ref
            JOIN customers c ON o.customer_id       = c.id
            JOIN warehouses wf ON o.warehouse_from_id = wf.id
            JOIN warehouses wt ON o.warehouse_to_id   = wt.id
            ORDER BY o.id
        """;
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        List<Order> list = new ArrayList<>();
        while(result.next()){
            list.add(new Order(
                    result.getInt("id"),
                    result.getInt("ref"),
                    result.getInt("customer_id"),
                    result.getDate("deliver_date"),
                    result.getInt("warehouse_from_id"),
                    result.getInt("warehouse_to_id"),
                    result.getInt("quantity"),
                    result.getString("status"),
                    result.getString("product_name"),
                    result.getString("customer_name"),
                    result.getString("warehouse_from_name"),
                    result.getString("warehouse_to_name")
            ));
        }
        connection.close();
        return(list);
    }

    public void cancelOrder(int id) throws SQLException {
        Connection connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false);

        String selectSql = "SELECT ref, quantity, warehouse_from_id FROM orders WHERE id = ? AND status = 'Pending'";
        PreparedStatement selecStatement = connection.prepareStatement(selectSql);
        selecStatement.setInt(1, id);
        ResultSet selectResult = selecStatement.executeQuery();
        if(!selectResult.next()){
            connection.rollback();
            return;
        }
        int ref = selectResult.getInt("ref");
        int quantity = selectResult.getInt("quantity");
        int warehouseSource = selectResult.getInt("warehouse_from_id");

        String updateStockSql = "UPDATE inventory_products SET quantity = quantity + ? WHERE ref = ? AND warehouse_id = ?";
        PreparedStatement updateStock = connection.prepareStatement(updateStockSql);
        updateStock.setInt(1, quantity);
        updateStock.setInt(2, ref);
        updateStock.setInt(3, warehouseSource);
        updateStock.executeUpdate();

        String updateOrderSql = "UPDATE orders SET status = 'Cancelled' WHERE id = ?";
        PreparedStatement updateOrder = connection.prepareStatement(updateOrderSql);
        updateOrder.setInt(1, id);
        updateOrder.executeUpdate();
        
        connection.commit();
        connection.setAutoCommit(true);
        connection.close();
    }

    public void saveOrder(int ref, int customerId, int sourceWarehouseId, int destinationWarehouseId, LocalDate deliverDate, int quantity) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false);

        String sql = "INSERT INTO orders (ref, customer_id, deliver_date, warehouse_from_id, warehouse_to_id, quantity, status) VALUES (?, ?, ?, ?, ?, ?, CAST(? AS order_status))";
        PreparedStatement statement = connection.prepareStatement(sql);
        statement.setInt(1, ref);
        statement.setInt(2, customerId);
        statement.setDate(3, java.sql.Date.valueOf(deliverDate));
        statement.setInt(4, sourceWarehouseId);
        statement.setInt(5, destinationWarehouseId);
        statement.setInt(6, quantity);
        statement.setString(7, "Pending");
        statement.executeUpdate();

        String updateStockSql = "UPDATE inventory_products SET quantity = quantity - ? WHERE ref = ? AND warehouse_id = ?";
        PreparedStatement updateStatement = connection.prepareStatement(updateStockSql);
        updateStatement.setInt(1, quantity);
        updateStatement.setInt(2, ref);
        updateStatement.setInt(3, sourceWarehouseId);
        updateStatement.executeUpdate();

        connection.commit();
        connection.setAutoCommit(true);
        connection.close();
    }

    public void shipOrder(int orderId) throws SQLException{
        Connection connection = DatabaseConnection.getConnection();
        connection.setAutoCommit(false);

        String selectSql = "SELECT o.ref, o.quantity, o.warehouse_from_id, o.warehouse_to_id, ip.expiration_date FROM orders o JOIN inventory_products ip ON o.ref = ip.ref AND o.warehouse_from_id = ip.warehouse_id WHERE o.id = ? AND o.status = 'Pending'";
        PreparedStatement selecStatement = connection.prepareStatement(selectSql);
        selecStatement.setInt(1, orderId);
        ResultSet result = selecStatement.executeQuery();

        if(!result.next()){
            connection.rollback();
            return;
        }
        int ref = result.getInt("ref");
        int quantity = result.getInt("quantity");
        int destinationWarehouse = result.getInt("warehouse_to_id");
        Date expDate = result.getDate("expiration_date");

        String checkSql = "SELECT 1 FROM inventory_products WHERE ref = ? AND warehouse_id = ?";
        PreparedStatement statement = connection.prepareStatement(checkSql);
        statement.setInt(1, ref);
        statement.setInt(2, destinationWarehouse);
        ResultSet rs = statement.executeQuery();
        boolean exists = rs.next();

        if(exists){
            String incSql = "UPDATE inventory_products SET quantity = quantity + ? WHERE ref = ? AND warehouse_id = ?";
            PreparedStatement incStatement = connection.prepareStatement(incSql);
            incStatement.setInt(1, quantity);
            incStatement.setInt(2, ref);
            incStatement.setInt(3, destinationWarehouse);
            incStatement.executeUpdate();
        } else{
            String insSql = "INSERT INTO inventory_products (ref, warehouse_id, expiration_date, deliver_date, quantity) VALUES (?, ?, ?, CURRENT_DATE, ?)";
            PreparedStatement insStatement = connection.prepareStatement(insSql);
            insStatement.setInt(1, ref);
            insStatement.setInt(2, destinationWarehouse);
            insStatement.setDate(3, expDate);
            insStatement.setInt(4, quantity);
            insStatement.executeUpdate();
        }
        String updOrderSql = "UPDATE orders SET status = 'Shipped' WHERE id = ?";
        PreparedStatement ps = connection.prepareStatement(updOrderSql);
        ps.setInt(1, orderId);
        ps.executeUpdate();

        connection.commit();
        connection.setAutoCommit(true);
        connection.close();
    }

    public int countOrders() throws SQLException{
        Connection connection = DatabaseConnection.getConnection();

        String sql = "SELECT COUNT(*) AS count FROM orders WHERE DATE(deliver_date) = CURRENT_DATE";
        PreparedStatement statement = connection.prepareStatement(sql);
        ResultSet result = statement.executeQuery();

        connection.close();
        return(result.next() ? result.getInt("count") : 0);
    }
}
