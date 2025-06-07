package main.Controllers;

import java.sql.SQLException;
import java.util.Optional;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import main.Entities.Supplier;
import main.Models.SupplierModel;

public class ModifySupplierController {
    @FXML
    private TextField addressInput;
    @FXML
    private Button clearButton;
    @FXML
    private TextField emailInput;
    @FXML
    private Button modifySupplier;
    @FXML
    private TextField nameInput;
    @FXML
    private TextField phoneInput;

    private SuppliersMenuController parentController; // Later, it will help me to refresh the suppliers table after modifying a supplier.
    private Supplier supplier;

    public void setParentController(SuppliersMenuController controller){
        this.parentController = controller;
    }

    public void setSupplier(Supplier supplier){
        this.supplier = supplier;

        if(supplier == null) return;
        nameInput.setText(supplier.getName());
        addressInput.setText(supplier.getAddress());
        emailInput.setText(supplier.getEmail());
        phoneInput.setText(supplier.getPhone());
    }

    @FXML
    private void handleModifySupplier(){
        String supplierName = nameInput.getText().trim();
        String address = addressInput.getText().trim();
        String email = emailInput.getText().trim();
        String phone = phoneInput.getText().trim();

        if(!validateInputs(supplierName, address, email, phone)){
            showError("Please fill in all the required fields!");
            return;
        }

        SupplierModel model = new SupplierModel();
        try{
            model.updateSupplier(supplier.getId(), supplierName, address, email, phone);
            showSuccess("Supplier updated Successfully");
        } catch(SQLException e){
            e.printStackTrace();
            showError("Failed to update supplier!");
        }
    }

    private boolean validateInputs(String name, String address, String email, String phone) {
        if (name == null || name.isEmpty() || address == null || address.isEmpty() || email   == null || email.isEmpty() || phone   == null || phone.isEmpty()) {
            return false;
        }

        String emailRegex = "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$";
        if (!email.matches(emailRegex)) {
            return false;
        }

        String phoneRegex = "^\\+?[0-9\\s\\-()]+$";
        if (!phone.matches(phoneRegex)) {
            return false;
        }

        return true;
    }

    private void showError(String msg){
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(msg);
        
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
