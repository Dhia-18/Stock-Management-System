package main.Controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Entities.Inventory;
import main.Models.InventoryModel;
import main.Models.SupplierModel;
import main.Models.WarehouseModel;

public class ModifyInventoryController {
    @FXML
    private Button clearBtn;
    @FXML
    private DatePicker deliverInput;
    @FXML
    private DatePicker expirationInput;
    @FXML
    private Button modifyBtn;
    @FXML
    private Spinner<Integer> quantityInput;
    @FXML
    private TextField refInput;
    @FXML
    private ChoiceBox<String> supplierInput;
    @FXML
    private ChoiceBox<String> warehouseInput;

    private InventoryMenuController parentController; // Later it will help me to refresh the inventory table after modifying a row.
    private Inventory inventory;

    @FXML
    private void initialize() throws IOException{
        refInput.setDisable(true);
        quantityInput.setEditable(true);

        loadSuppliers();
        loadWarehouses();
    }

    public void setParentController(InventoryMenuController controller){
        this.parentController = controller;
    }

    public void setInventory(Inventory inventory){
        this.inventory = inventory;
        setInputs();
    }

    private void setInputs(){
        WarehouseModel warehouseModel = new WarehouseModel();
        SupplierModel supplierModel = new SupplierModel();

        if(inventory == null) return;
        refInput.setText(String.valueOf(inventory.getRef()));
        deliverInput.setValue(inventory.getDeliver_date().toLocalDate());
        quantityInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, inventory.getQuantity()));
        if(inventory.getExpiration_date()==null){
            expirationInput.setDisable(true);
        } else{
            expirationInput.setValue(inventory.getExpiration_date().toLocalDate());
        }

        try{
            warehouseInput.setValue(warehouseModel.findNameById(inventory.getWarehouse_id()));
            supplierInput.setValue(supplierModel.findNameById(inventory.getSupplier_id()));
        } catch (SQLException e){
            e.printStackTrace();
        }
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
            ObservableList<String> warehousesList = FXCollections.observableArrayList(model.getAllWarehouseNames());

            warehouseInput.getItems().addAll(warehousesList);
        } catch(SQLException e){
            e.printStackTrace();
        }
    }

    @FXML
    private void handleModifyInventory(){
        String supplierName = supplierInput.getValue();
        String warehouseName = warehouseInput.getValue();
        LocalDate deliverDate = deliverInput.getValue();
        LocalDate expirationDate = expirationInput.getValue();
        int quantity = quantityInput.getValue();
        
        List<String> validationErrors = validateInputs(supplierName, warehouseName, deliverDate, expirationDate);
        if(!validationErrors.isEmpty()){
            showError(String.join("\n", validationErrors));
            return;
        }

        InventoryModel inventoryModel = new InventoryModel();
        SupplierModel supplierModel = new SupplierModel();
        WarehouseModel warehouseModel = new WarehouseModel();
        try{
            int supplierId = supplierModel.findIdByName(supplierName);
            int warehouseId = warehouseModel.findIdByName(warehouseName);
            inventoryModel.updateInventory(inventory.getId(), supplierId, warehouseId, deliverDate, expirationDate, quantity);
            showSuccess("Row updated successfully!");
        } catch (SQLException e){
            e.printStackTrace();
            showError("Failed to update row!");
        }
    }

    private List<String> validateInputs(String supplierName, String warehouseName, LocalDate deliverDate, LocalDate expirationDate){
        List<String> errors = new ArrayList<>();

        if(supplierName == null){
            errors.add("Supplier name is required.");
        }
        if(warehouseName == null){
            errors.add("Warehouse name is required.");
        }
        if(deliverDate == null){
            errors.add("Deliver date is required.");
        }
        if(!expirationInput.isDisabled() && expirationDate==null){
            errors.add("Expiration date is required");
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
        supplierInput.setValue(null);
        warehouseInput.setValue(null);
        deliverInput.setValue(null);
        expirationInput.setValue(null);
        quantityInput.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(0, 1000, 0));
    }
}
