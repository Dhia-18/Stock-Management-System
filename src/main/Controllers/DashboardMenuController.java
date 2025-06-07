package main.Controllers;

import java.sql.SQLException;

import javafx.fxml.FXML;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import main.Models.CustomerModel;
import main.Models.InventoryModel;
import main.Models.OrderModel;
import main.Models.ProductModel;
import main.Models.SupplierModel;

public class DashboardMenuController {
    @FXML
    private Label customersLabel;
    @FXML
    private Hyperlink linkLabel;
    @FXML
    private Label lowStockLabel;
    @FXML
    private Label ordersLabel;
    @FXML
    private Label outOfStockLabel;
    @FXML
    private Label productsLabel;
    @FXML
    private Label suppliersLabel;

    @FXML
    public void initialize(){
        refreshDashboard();

        linkLabel.setOnAction(e -> {
            try{
                java.awt.Desktop.getDesktop().browse(new java.net.URI("https://www.linkedin.com/in/dhia-sarraj-850840284"));
            } catch (Exception ex){
                ex.printStackTrace();
            }
        });
    }

    public void refreshDashboard(){
        try{
            int custCount   = new CustomerModel().countCustomers();
            int prodCount   = new ProductModel().countProducts();
            int suppCount   = new SupplierModel().countSuppliers();
            int orderCount  = new OrderModel().countOrders();
            int lowCount    = new InventoryModel().countLowStock(5);
            int outCount    = new InventoryModel().countOutOfStock();

            customersLabel.setText(String.valueOf(custCount));
            productsLabel.setText(String.valueOf(prodCount));
            suppliersLabel.setText(String.valueOf(suppCount));
            ordersLabel.setText(String.valueOf(orderCount));
            lowStockLabel.setText(String.valueOf(lowCount));
            outOfStockLabel.setText(String.valueOf(outCount));
        } catch (SQLException e){
            e.printStackTrace();
        }
    }
}
