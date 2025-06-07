package main.Controllers;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Models.SupplierModel;

public class AddSupplierController {
    private SuppliersMenuController parentController; // Later, it will help me to refresh the suppliers table after adding a new supplier.

    @FXML
    private Button addButton;
    @FXML
    private TextField addressInput;
    @FXML
    private Button clearButton;
    @FXML
    private TextField emailInput;
    @FXML
    private TextField nameInput;
    @FXML
    private TextField phoneInput;

    public void setParentController(SuppliersMenuController controller){
        this.parentController = controller;
    }

    @FXML
    private void handleAddSupplier(){
        // Inputs
        String supplierName = nameInput.getText().trim();
        String address = addressInput.getText().trim();
        String email = emailInput.getText().trim();
        String phone = phoneInput.getText().trim();

        List<String> validationErrors = validateInputs(supplierName, address, email, phone);
        if(!validationErrors.isEmpty()){
            showError(String.join("\n", validationErrors));
            return;
        }

        SupplierModel model = new SupplierModel();

        try{
            model.saveSupplier(supplierName, address, email, phone);
            showSuccess("Supplier added successfully");
        } catch (SQLException e){
            e.printStackTrace();
            showError("Failed to save Supplier");
        }
    }

private List<String> validateInputs(String name, String address, String email, String phone) {
    List<String> errors = new ArrayList<>();

    if (name == null || name.trim().isEmpty()) {
        errors.add("Name is required.");
    }
    if (address == null || address.trim().isEmpty()) {
        errors.add("Address is required.");
    }
    if (email == null || email.trim().isEmpty()) {
        errors.add("Email is required.");
    }
    if (phone == null || phone.trim().isEmpty()) {
        errors.add("Phone number is required.");
    }

    if (email != null && !email.trim().isEmpty()) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            errors.add("Please enter a valid email address.");
        }
    }

    if (phone != null && !phone.trim().isEmpty()) {
        String phoneRegex = "^\\+?[0-9\\s\\-()]+$";
        if (!phone.matches(phoneRegex)) {
            errors.add("Please enter a valid phone number.");
        }
    }

    return errors;
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
            parentController.loadAllSuppliers();
            stage.close();
        }
    }

    @FXML
    private void clearData(){
        nameInput.clear();
        emailInput.clear();
        addressInput.clear();
        phoneInput.clear();
    }
}
