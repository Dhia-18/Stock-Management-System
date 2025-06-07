package main.Controllers;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.control.Alert.AlertType;
import main.Models.ProductModel;
import main.Models.SupplierModel;
import main.Models.WarehouseModel;

public class AddProductController {
    private ProductsMenuController parentController; // Later, it will help me to refresh the products table after adding a new product.

    @FXML
    private Button addButton;
    @FXML
    private Button clearButton;
    @FXML
    private TextField nameInput;
    @FXML
    private ChoiceBox<String> categoryInput;
    @FXML
    private CheckBox criticismInput;
    @FXML
    private DatePicker deliveredInput;
    @FXML
    private DatePicker expirationInput;
    @FXML
    private ChoiceBox<String> supplierInput;
    @FXML
    private ChoiceBox<String> warehouseInput1;
    @FXML
    private ChoiceBox<String> warehouseInput2;
    @FXML
    private ChoiceBox<String> warehouseInput3;
    @FXML
    private Spinner<Integer> quantityInput1;
    @FXML
    private Spinner<Integer> quantityInput2;
    @FXML
    private Spinner<Integer> quantityInput3;

    public void setParentController(ProductsMenuController controller){
        this.parentController = controller;
    }

    @FXML
    private void initialize() throws IOException{
        // Set category choiceBox
        categoryInput.getItems().addAll("Consumable","Non-Consumable");
        categoryInput.setValue("Consumable");
        categoryInput.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newVal)->{
            handleChoiceSelection(newVal);
        });

        // Default deliver date
        deliveredInput.setValue(LocalDate.now());

        // Set warehouse choiceBoxes and quantity spinners
        quantityInput1.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));
        quantityInput2.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));
        quantityInput3.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 1000, 1));
        setWarehouses();

        loadWarehouses();
        loadSuppliers();
    }
    
    private void handleChoiceSelection(String selected){
        if(selected.equals("Non-Consumable")){
            expirationInput.setValue(null);
            expirationInput.setDisable(true);
        } else{
            expirationInput.setDisable(false);
        }
    }

    private void setWarehouses(){
        warehouseInput2.setDisable(true);
        quantityInput2.setDisable(true);

        warehouseInput3.setDisable(true);
        quantityInput3.setDisable(true);

        ChangeListener<Object> firstListener = (obs, oldVal, newVal) -> {
            boolean firstFilled = warehouseInput1.getValue() != null && quantityInput1.getValue() >0;
            if(newVal!=null){
                warehouseInput1.getItems().remove(newVal.toString());
            }
            if(oldVal != null && oldVal.toString().matches("[a-zA-Z]+")){
                warehouseInput1.getItems().add(oldVal.toString());
            }
            warehouseInput2.getItems().setAll(warehouseInput1.getItems());
            warehouseInput2.setDisable(!firstFilled);
            quantityInput2.setDisable(!firstFilled);
        };

        warehouseInput1.valueProperty().addListener(firstListener);
        quantityInput1.valueProperty().addListener(firstListener);

        ChangeListener<Object> secondListener = (obs, oldVal, newVal) -> {
            boolean secondFilled = warehouseInput2.getValue() != null && quantityInput2.getValue() > 0;
            if(newVal!=null){
                warehouseInput2.getItems().remove(newVal.toString());
            }
            if(oldVal != null && oldVal.toString().matches("[a-zA-Z]+")){
                warehouseInput2.getItems().add(oldVal.toString());
            }
            warehouseInput3.getItems().setAll(warehouseInput2.getItems());
            warehouseInput3.setDisable(!secondFilled);
            quantityInput3.setDisable(!secondFilled);
        };
    
        warehouseInput2.valueProperty().addListener(secondListener);
        quantityInput2.valueProperty().addListener(secondListener);
    }

    private void loadWarehouses(){
        WarehouseModel model = new WarehouseModel();
        
        try{
            ObservableList<String> warehousesList = FXCollections.observableArrayList(model.getAllStoresNames());
            
            warehouseInput1.setItems(warehousesList);
        } catch(SQLException e){
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

    @FXML
    private void handleAddProduct(){
        // Inputs
        String productName = nameInput.getText().trim();
        String category = categoryInput.getValue();
        LocalDate deliverDate = deliveredInput.getValue();
        LocalDate expirationDate = expirationInput.getValue();
        String supplierName = supplierInput.getValue();
        String warehouseName1 = warehouseInput1.getValue();
        int quantity1 = quantityInput1.getValue();
        String warehouseName2 = warehouseInput2.getValue();
        int quantity2 = quantityInput2.getValue();
        String warehouseName3 = warehouseInput3.getValue();
        int quantity3 = quantityInput3.getValue();
        boolean criticism = criticismInput.isSelected();

        List<String> validationErrors = validateInputs(productName, category, expirationDate, supplierName);
        if(!validationErrors.isEmpty()){
            showError(String.join("\n", validationErrors));
            return;
        }

        ProductModel productModel = new ProductModel();
        SupplierModel supplierModel = new SupplierModel();
        WarehouseModel warehouseModel = new WarehouseModel();

        try{
            int supplierId = supplierModel.findIdByName(supplierName);
            int warehouseId1 = warehouseModel.findIdByName(warehouseName1);
            int warehouseId2 = warehouseName2 == null ? -1 : warehouseModel.findIdByName(warehouseName2);
            int warehouseId3 = warehouseName3 == null ? -1 : warehouseModel.findIdByName(warehouseName3);

            productModel.saveProduct(productName, category, deliverDate, expirationDate, supplierId, warehouseId1, quantity1, warehouseId2, quantity2, warehouseId3, quantity3, criticism);
            showSuccess("Product added Successfully!");
        } catch (SQLException e){
            e.printStackTrace();
            showError("Failed to save product!");
        }
    }

    private List<String> validateInputs(String productName, String category, LocalDate expirationDate, String supplierName){
        List<String> errors = new ArrayList<>();

        if(productName.isEmpty()){
            errors.add("Product name is required.");
        }
        if(category.equals("Consumable") && expirationDate == null){
            errors.add("Expiration date must be provided for consumable products.");
        }
        if(supplierName == null){
            errors.add("Supplier name is required.");
        }
        if(warehouseInput1.getValue() == null){
            errors.add("Warehouse name is required.");
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
            Stage stage = (Stage) nameInput.getScene().getWindow();
            parentController.loadAllProducts();
            stage.close();
        }
    }
    
    @FXML
    private void clearData(){
        nameInput.clear();
        categoryInput.setValue("Consumable");
        deliveredInput.setValue(LocalDate.now());
        expirationInput.setValue(null);
        supplierInput.setValue(null);
        warehouseInput1.setValue(null);
        quantityInput1.getValueFactory().setValue(1);
        warehouseInput2.setValue(null);
        quantityInput2.getValueFactory().setValue(1);
        warehouseInput3.setValue(null);
        quantityInput3.getValueFactory().setValue(1);
        criticismInput.setSelected(false);
    }
}