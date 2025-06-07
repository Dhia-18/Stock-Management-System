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
import main.Models.InventoryModel;
import main.Models.ProductModel;
import main.Models.SupplierModel;
import main.Models.WarehouseModel;

public class AddInventoryController {
    private InventoryMenuController parentController;

    @FXML
    private Button clearBtn;
    @FXML
    private DatePicker deliverInput;
    @FXML
    private DatePicker expirationInput;
    @FXML
    private Button addBtn;
    @FXML
    private Label productNameLabel;
    @FXML
    private Spinner<Integer> quantityInput;
    @FXML
    private TextField refInput;
    @FXML
    private ChoiceBox<String> supplierInput;
    @FXML
    private ChoiceBox<String> warehouseInput;

    public void setParentController(InventoryMenuController controller){
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
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if(newValue.isEmpty()){
                    productNameLabel.setText("");
                    return;
                }
                
                try{
                    int ref = Integer.parseInt(newValue);
                    Product product = model.findProductByRef(ref);
                    
                    if(product != null){
                        productNameLabel.setText(product.getName());
                        expirationInput.setDisable("Non-Consumable".equals(product.getCategory()));
                    } else{
                        productNameLabel.setText("");
                        expirationInput.setDisable(false);
                    }
                } catch(SQLException e){
                    e.printStackTrace();
                }
            }
        });

        loadSuppliers();
        loadWarehouses();
    }

    private void loadSuppliers(){
        SupplierModel model = new SupplierModel();

        try{
            ObservableList<String> suppliersList = FXCollections.observableArrayList(model.getAllSupplierNames());

            supplierInput.getItems().addAll(suppliersList);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    private void loadWarehouses(){
        WarehouseModel model = new WarehouseModel();

        try{
            ObservableList<String> warehousesList = FXCollections.observableArrayList(model.getAllStoresNames());

            warehouseInput.getItems().addAll(warehousesList);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void handleAddInventoryRow(){
        String productRef = refInput.getText().trim();
        String supplierName = supplierInput.getValue();
        String warehouseName = warehouseInput.getValue();
        LocalDate deliverDate = deliverInput.getValue();
        LocalDate expirationDate = expirationInput.getValue();
        int quantity = quantityInput.getValue();

        List<String> validationErrors = validateInputs(supplierName, warehouseName, deliverDate, expirationDate);
        if(!validationErrors.isEmpty()){
            showError(String.join("\n",validationErrors));
            return;
        }

        InventoryModel inventoryModel = new InventoryModel();
        SupplierModel supplierModel = new SupplierModel();
        WarehouseModel warehouseModel = new WarehouseModel();
        try{
            int supplierId = supplierModel.findIdByName(supplierName);
            int warehouseId = warehouseModel.findIdByName(warehouseName);
            inventoryModel.saveInventoryRow(Integer.parseInt(productRef), supplierId, warehouseId, deliverDate, expirationDate, quantity);
            showSuccess("Row added successfully!");
        } catch(SQLException e){
            e.printStackTrace();
            showError("Failed to save row");
        }
    }

    private List<String> validateInputs(String supplierName, String warehouseName, LocalDate deliverDate, LocalDate expirationDate){
        List<String> errors = new ArrayList<>();

        if(supplierName==null){
            errors.add("Supplier name is required.");
        }
        if(warehouseName==null){
            errors.add("Warehouse name is required.");
        }
        if(productNameLabel.getText().isEmpty()){
            // This means that the product reference is incorrect!
            errors.add("Product Reference is incorrect.");
        }
        if(!expirationInput.isDisable() && expirationDate==null){
            errors.add("Expiration date is required.");
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
            parentController.loadAllInventoryRows();
            stage.close();
        }
    }

    @FXML
    private void clearData(){
        refInput.clear();
        supplierInput.setValue(null);
        warehouseInput.setValue(null);
        deliverInput.setValue(null);
        expirationInput.setValue(null);
        quantityInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));
    }
}
