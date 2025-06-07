package main.Controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Entities.Product;
import main.Models.CustomerModel;
import main.Models.InventoryModel;
import main.Models.OrderModel;
import main.Models.ProductModel;
import main.Models.WarehouseModel;

public class AddOrderController {
    private OrdersMenuController parentController;

    @FXML
    private Button addBtn;
    @FXML
    private Button clearBtn;
    @FXML
    private ChoiceBox<String> customerInput;
    @FXML
    private DatePicker deliverInput;
    @FXML
    private Label productNameLabel;
    @FXML
    private Label customerServiceLabel;
    @FXML
    private Spinner<Integer> quantityInput;
    @FXML
    private TextField refInput;
    @FXML
    private ChoiceBox<String> warehouseDestinationInput;
    @FXML
    private ChoiceBox<String> warehouseSourceInput;

    public void setParentController(OrdersMenuController controller){
        this.parentController = controller;
    }

    @FXML
    private void initialize() throws IOException{
        deliverInput.setValue(LocalDate.now());
        quantityInput.setEditable(true);
        quantityInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));
        refInput.textProperty().addListener(new ChangeListener<String>() {
            ProductModel model = new ProductModel();

            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue){
                if(newValue.isEmpty()){
                    productNameLabel.setText("");
                    return;
                }

                try{
                    int ref = Integer.parseInt(newValue);
                    Product product = model.findProductByRef(ref);

                    if(product != null){
                        productNameLabel.setText(product.getName());
                    } else{
                        productNameLabel.setText("");
                    }
                } catch(SQLException e){
                    e.printStackTrace();
                }
            }
        });

        customerInput.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue == null || newValue.isEmpty()){
                customerServiceLabel.setText("");
                return;
            }

            CustomerModel model = new CustomerModel();
            try{
                String service = model.findServiceByName(newValue);
                if(!service.isEmpty()){
                    customerServiceLabel.setText(service);
                }else{
                    customerServiceLabel.setText("");
                }
            } catch (SQLException e){
                e.printStackTrace();
            }
        });

        loadWarehouses();
        loadCustomers();
    }

    private void loadWarehouses(){
        WarehouseModel model = new WarehouseModel();

        try{
            ObservableList<String> warehousesList = FXCollections.observableArrayList(model.getAllWarehouseNames());

            warehouseDestinationInput.getItems().addAll(warehousesList);
            warehouseSourceInput.getItems().addAll(warehousesList);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void loadCustomers(){
        CustomerModel model = new CustomerModel();

        try{
            ObservableList<String> customersList = FXCollections.observableArrayList(model.getAllCustomerNames());
            
            customerInput.getItems().addAll(customersList);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddOrder(){
        String productRef = refInput.getText().trim();
        String customerName = customerInput.getValue();
        String warehouseSource = warehouseSourceInput.getValue();
        String warehouseDestination = warehouseDestinationInput.getValue();
        LocalDate deliverDate = deliverInput.getValue();
        int quantity = quantityInput.getValue();

        List<String> validationErrors = validateInputs(customerName, warehouseSource, warehouseDestination);
        if(!validationErrors.isEmpty()){
            showError(String.join("\n", validationErrors));
            return;
        }

        OrderModel orderModel = new OrderModel();
        CustomerModel customerModel = new CustomerModel();
        WarehouseModel warehouseModel = new WarehouseModel();
        InventoryModel inventoryModel = new InventoryModel();

        try{
            int customerId = customerModel.findIdByName(customerName);
            int warehouseSourceId = warehouseModel.findIdByName(warehouseSource);
            int warehouseDestinationId = warehouseModel.findIdByName(warehouseDestination);

            int available = inventoryModel.getAvailableQuantity(Integer.parseInt(productRef), warehouseSourceId);
            if(quantity > available){
                showError("Not enough stock in source warehouse. Available: "+ available);
                return;
            }

            String customerService = customerModel.findServiceByName(customerName);
            String warehouseService = warehouseModel.findServiceByName(warehouseDestination);
            if(customerService == null || warehouseService == null || !customerService.equals(warehouseService)){
                showError("Customer's service ("+customerService+") does not match destination warehouse's service ("+warehouseService+").");
                return;
            }

            orderModel.saveOrder(Integer.parseInt(productRef), customerId, warehouseSourceId, warehouseDestinationId, deliverDate, quantity);
            showSuccess("Order added successfully!");
        } catch(SQLException e){
            e.printStackTrace();
            showError("Failed to save order");
        }
    }

    private List<String> validateInputs(String customerName, String warehouseSource, String warehouseDestination){
        List<String> errors = new ArrayList<>();

        if(customerName==null){
            errors.add("Customer name is required.");
        }
        if(warehouseSource==null){
            errors.add("Warehouse Source name is required.");
        }
        if(warehouseDestination==null){
            errors.add("Warehouse Destination is required.");
        }
        if(productNameLabel.getText().isEmpty()){
            // This means that the product reference is incorrect!
            errors.add("Product Reference is incorrect.");
        }
        if (warehouseSource.equals(warehouseDestination)) {
            errors.add("Source and destination warehouses must be different.");
        }
        
        return(errors);
    }
    
    private void showError(String msg){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("ops...");
        alert.setContentText(msg);
        
        alert.show();
    }

    private void showSuccess(String msg){
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(msg);
        
        Optional<ButtonType> result = alert.showAndWait();
        if(result.isPresent() && result.get()==ButtonType.OK){
            Stage stage = (Stage) refInput.getScene().getWindow();
            parentController.loadAllOrders();
            stage.close();
        }
    }

    @FXML
    private void clearData(){
        refInput.clear();
        customerInput.setValue(null);
        warehouseDestinationInput.setValue(null);
        warehouseSourceInput.setValue(null);
        deliverInput.setValue(LocalDate.now());
        quantityInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));
    }
}
